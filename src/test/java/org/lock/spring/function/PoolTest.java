package org.lock.spring.function;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.curator.framework.CuratorFramework;
import org.junit.Test;
import org.lock.spring.data.zookeeper.connection.CuratorConnectionFactory;

public class PoolTest {
	
	public void testGetConnectionFormPool(){
//		DataSourceTransactionManager
	}
	
	@Test
	public void testGetPool(){
		GenericObjectPool<CuratorFramework> pool = new GenericObjectPool<CuratorFramework>(new CuratorConnectionFactory("127.0.0.1","2181"));
//		Set<Integer> hashSet = new HashSet<Integer>();
		for(int i =0;i<30;i++){
			try {
				CuratorFramework lockClient = pool.borrowObject();
				System.out.println("the borrow object is " +lockClient.hashCode()+",and the pos is "+i);
//				hash
//				pool.returnObject(lockClient);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

}
