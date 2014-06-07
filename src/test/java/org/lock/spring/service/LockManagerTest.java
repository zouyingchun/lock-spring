package org.lock.spring.service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryOneTime;
import org.junit.Test;
import org.lock.spring.data.zookeeper.lock.ZookeeperLockManager;
import org.springframework.beans.factory.annotation.Autowired;

public class LockManagerTest extends BaseTest{
	@Autowired
	private ZookeeperLockManager lockManager;
	CountDownLatch countDownLatch = new CountDownLatch(30);
//	private Logger logger = LoggerFactory.getLogger(LockManagerTest.class);
	
	public void testGetLock(){
		try{
			Random random = new Random();
			String originName =""+ random.nextInt(10000);
			String lockName = lockManager.getLock(originName);
			Thread.sleep(10000);
			lockManager.releaseLock(lockName);
		}catch(Exception e){
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		finally{
			countDownLatch.countDown();
		}
	}
	
	class PriThread extends Thread{
		
		public void run(){
			while(true){
//				System.out.println("lock map is "+  JSON.toJSONString(lockManager.lockMap)+", and the map size is "+lockManager.lockMap.size());
//				System.out.println("pool component is "+JSON.toJSONString(lockManager.getPool().));
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Test
	public void testThreadGetLock(){
		for(int i = 0 ;i<30;i++){
			new Thread(){

				@Override
				public void run() {
					testGetLock();
				}
				
			}.start();
		}
		new PriThread().start();
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public  void testGetConnection(){
		for(int i = 0; i < 15;i++){
			CuratorFramework client= CuratorFrameworkFactory.
	                	newClient("127.0.0.1:2181", new RetryOneTime(1000));
			client.getConnectionStateListenable().addListener(new CuratorFrameworkConnectionListener() );
			client.start();
			System.out.println("the connect client is "+connectedCount);
//			logger.info("the connect client is "+connectedCount);
		}
		try {
			Thread.sleep(100000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private AtomicInteger connectedCount = new AtomicInteger(0);

	class CuratorFrameworkConnectionListener implements ConnectionStateListener{
		
		public void stateChanged(CuratorFramework client,
			ConnectionState newState) {
			switch (newState) {
			case SUSPENDED:
			case LOST:
			case READ_ONLY:
				connectedCount.getAndDecrement();
				break;
			case CONNECTED:
			case RECONNECTED:
				connectedCount.addAndGet(1);
				break;
			default:
				break;
			}
		}
//		RedisCacheManager
	}
	
	@Test
	public void clearData() throws Exception{
		CuratorFramework client= CuratorFrameworkFactory.
                	newClient("127.0.0.1:2181", new RetryOneTime(1000));
		client.start();
		List<String> allPath = client.getChildren().forPath("/");
		try{
			for(String path : allPath){
				if("zookeeper".equals(path)){
					continue;
				}
				client.delete().forPath("/"+path);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
