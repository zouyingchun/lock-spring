package org.lock.spring.service;

import org.junit.Test;
import org.lock.spring.data.zookeeper.connection.CuratorConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class FactoryTest extends BaseTest{
	@Autowired
	private CuratorConnectionFactory curatorConnectionFactory;
	
	@Test
	public void testInit(){
		System.out.println("aaa");
	}

}
