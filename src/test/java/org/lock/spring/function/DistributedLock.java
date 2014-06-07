package org.lock.spring.function;

import java.util.concurrent.Semaphore;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.RevocationListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.EnsurePath;

public class DistributedLock {

	private InterProcessMutex lock;// 重入的,排他的.

	private LockHolder _holder;

	private String lockPath;

	private ConnectionStateListener stateListener = new StateListener();

	private LockEvent lockEvent;

	private Semaphore semaphore = new Semaphore(0);
	private RevocationListener<InterProcessMutex> revocationListener;

	public DistributedLock(CuratorFramework client, String path,
		final LockEvent lockEvent) {
		lockPath = path;
		this.lockEvent = lockEvent;
		revocationListener = new RevocationListener<InterProcessMutex>() {
			@Override
			public void revocationRequested(
				InterProcessMutex forLock) {
				if (!forLock.isAcquiredInThisProcess()) {
					return;
				}
				try {
					lockEvent.beforeRelease();
					// 只有当前线程才可以释放
					// 其他线程,将会抛出一个错误.
					forLock.release();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		lock = createLock(client);
		lock.makeRevocable(revocationListener);
		client.getConnectionStateListenable()
			.addListener(stateListener);
	}

	public boolean lock() {
		try {
			if (_holder == null) {
				_holder = new LockHolder();
				_holder.setDaemon(true);
			}
			if (!_holder.isAlive()) {
				_holder.start();
			}
			semaphore.acquire();// 直到_holder正常运行
		} catch (Exception e) {
			//
		}
		return false;
	}

	public void unlock() {
		if (_holder.isAlive()) {
			_holder.interrupt();
		}
	}

	private InterProcessMutex createLock(CuratorFramework client) {
		lock = new InterProcessMutex(client, lockPath);
		// 协同中断,如果其他线程/进程需要此锁中断时,调用此listener.
		lock.makeRevocable(revocationListener);
		client.getConnectionStateListenable()
			.addListener(stateListener);
		return lock;
	}

	class StateListener implements ConnectionStateListener {
		@Override
		public void stateChanged(CuratorFramework client,
			ConnectionState newState) {
			switch (newState) {
			case LOST:
				// 一旦丢失链接,就意味着zk server端已经删除了锁数据
				boolean rebuild = lockEvent.lose();
				_holder.interrupt();// NUll
				if (rebuild) {
					lock();
				}
				break;
			default:
				System.out.println(newState.toString());

			}
		}
	}

	static interface LockEvent {

		public void afterAquire();

		public boolean lose();

		// 释放锁
		public void beforeRelease();
	}

	class LockHolder extends Thread {
		@Override
		public void run() {
			try {
				lock.acquire();
				semaphore.release();//
				lockEvent.afterAquire();
				synchronized (lock) {
					lock.wait();
				}
			} catch (InterruptedException e) {
				try {
					lockEvent.beforeRelease();
					lock.release();
				} catch (Exception ie) {
					ie.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	public static void main(String[] args) throws Exception {
		CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory
			.builder();
		// fluent style
		String namespace = "cluster-worker";
		CuratorFramework client = builder
			.connectString("127.0.0.1:2181")
			// FixedEnsembleProvider
			.sessionTimeoutMs(30000).connectionTimeoutMs(30000)
			.canBeReadOnly(false)
			// cant connect to one observer-instance
			.retryPolicy(
				new ExponentialBackoffRetry(1000,
					Integer.MAX_VALUE)) // auto reconnect
								// policy
			.namespace(namespace) // good method,you can specify one
						// fix prefiex-path of all
						// znode.
			.defaultData(null).build();
		client.start();
		EnsurePath ensure = client
			.newNamespaceAwareEnsurePath(namespace);
		LockEvent le = new LockEvent() {

			@Override
			public boolean lose() {
				System.out
					.println("lose,shoud be waiting or stop workers!");
				return false;
			}

			@Override
			public void beforeRelease() {
				System.out.println("Lock would be released!");

			}

			@Override
			public void afterAquire() {
				System.out
					.println("Locked success,can be running workers");

			}
		};
		DistributedLock distLock = new DistributedLock(client, "/lock",
			le);
		distLock.lock();
		distLock.unlock();
		Thread.sleep(2000);
		client.close();

	}

}