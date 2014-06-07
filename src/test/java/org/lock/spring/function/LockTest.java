package org.lock.spring.function;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.utils.CloseableUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LockTest {
	private Logger logger = LoggerFactory.getLogger(LockTest.class);
	
	private AtomicInteger finishCount = new AtomicInteger(0);
	private AtomicInteger requireCount = new AtomicInteger(0);
	
	public void testGetLock(int pos){
                CuratorFramework        client =null;
                InterProcessMutex lock  = null;
                try{
                	
	                client= CuratorFrameworkFactory.
	                	newClient("127.0.0.1:2181",1000,1000, new RetryOneTime(1000));
	                client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
				
				public void stateChanged(CuratorFramework client,
					ConnectionState newState) {
					System.out.println("the client "+client.hashCode()+" status is "+newState.name());
				}
			});
	                client.start();
//	                CreateBuilder  createBuilder = client.create();
//	                createBuilder.forPath("/aab");
//	                client.create().forPath("/aa");
	                Thread.sleep(100);
//	                System.out.println(client.);
//	                client.create().forPath("/aa", "aa".getBytes());
	                lock = new InterProcessMutex(client, "/test");
//	                new  InterProcessSemaphoreMutex(client,"/test1");
	                synchronized (requireCount) {
	                	requireCount.notify();
			}
//	               System.out.println( lock.);
	                System.out.println("the client "+pos+" thread is start");
	                boolean result = lock.acquire(3,TimeUnit.MILLISECONDS);
//	                lock.acquire(time, unit)
//	                System.out.println(lock.getParticipantNodes().get);
	                System.out.println("the client "+pos +" is get lock result is "+result);
	                Thread.sleep(5000);
//	                System.out.println(client.getConnectionStateListenable().);
                }catch(Exception e){
                	e.printStackTrace();
                	System.out.println(e.getMessage());
                }finally{
                	try {
				lock.release();
				CloseableUtils.closeQuietly(client);
			} catch (Exception e) {
				e.printStackTrace();
			}
                	int currentFinishCount = finishCount.addAndGet(1);
                	if(currentFinishCount == 5){
                		synchronized (finishCount) {
					finishCount.notify();
				}
                	}
                }
	}
	
	@Test
	public void testThreadGetLock(){
//		try {
//			ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2181", 1000, new Watcher() {
//				public void process(WatchedEvent event) {
//				}
//			});
//			zooKeeper.getChildren("/test", true);
////			zooKeeper.create("/t", "aa".getBytes());
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		} catch (KeeperException e) {
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		for(int i = 0 ;i<5;i++){
			new GetLockThread(i).start();
//			try {
//				synchronized (requireCount) {
//					requireCount.wait();
//				}
////				Thread.sleep(10000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
		synchronized (finishCount) {
			try {
				finishCount.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		logger.info("end");
	}
	
	class GetLockThread extends Thread{
		private int pos;
		
		public GetLockThread(int pos){
			this.pos = pos;
		}

		@Override
		public void run() {
			testGetLock(pos);
		}
		
	}
	
	
	@Test
	public void testCountDownLatch(){
		CountDownLatch  countDownLatch = new CountDownLatch(1);
		
		try {
			new downLatchThread(countDownLatch).start();
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(countDownLatch.getCount());
	}
	
	
	class downLatchThread extends Thread{
		
		private CountDownLatch countDownLatch;
		
		public downLatchThread(CountDownLatch countDownLatch) {
			super();
			this.countDownLatch = countDownLatch;
		}


		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			countDownLatch.countDown();
			System.out.println("the thread is start");
		}
		
	}
	
	@Test
	public void testGetConnection(){
		try{
			getConnection();
		}catch(Exception e){
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
	
	
	public CuratorFramework getConnection() throws Exception{
		CuratorFramework client= CuratorFrameworkFactory.
                	newClient("127.0.0.1:2181", new RetryOneTime(1000));
		CountDownLatch countDownLatch = new CountDownLatch(1);
		client.getConnectionStateListenable().addListener(new CuratorFrameworkConnectionListener(countDownLatch));
                client.start();
                boolean isConnectSuccess = countDownLatch.await(10000, TimeUnit.MILLISECONDS);
                if(isConnectSuccess){
                	System.out.println(System.currentTimeMillis()+"the server is connect success");
                	InterProcessMutex lock = new InterProcessMutex(client, "/test");
                	lock.acquire();
                	Thread.sleep(10000);
                	lock.release();
                	System.out.println(System.currentTimeMillis()+"kdkkdkd");
                }else{
                	System.out.println("the server is connect is fail");
                }
		return client;
	}
	
	class CuratorFrameworkConnectionListener implements ConnectionStateListener{
		private CountDownLatch countDownLatch;
		public CuratorFrameworkConnectionListener(CountDownLatch countDownLatch){
			this.countDownLatch = countDownLatch;
		}
		
		public void stateChanged(CuratorFramework client,
			ConnectionState newState) {
			if(client.getState() == CuratorFrameworkState.STARTED
				&& newState == ConnectionState.CONNECTED ){
				countDownLatch.countDown();
				System.out.println("the client is connected");
			}
		}
//		RedisCacheManager
	}
	
	
}
