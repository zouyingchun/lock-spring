lock-spring 解决了分布式系统下同步以及加锁的问题。通过zookeeper+curator进行资源锁控制.以及通过annotation+spring方式去实现使使用更加方便。

 1. 下载source
    >下载[lock-spring.jar](https://github.com/zouyingchun/lock-spring/blob/developer/lib/lock-spring-1.0.0.jar)

 2. spring annotation配置
>`	<beans xmlns="http://www.springframework.org/schema/beans" 
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:context="http://www.springframework.org/schema/context" 
xmlns:jee="http://www.springframework.org/schema/jee" xmlns:jdbc="http://www.springframework.org/schema/jdbc" 
xmlns:tx="http://www.springframework.org/schema/tx" xmlns:aop="http://www.springframework.org/schema/aop" 
xmlns:lock="http://www.lock.org/schema/lock" 
xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd 
http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd 
http://www.springframework.org/schema/jee http://www.springframework.org/schema/jee/spring-jee.xsd 
http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx.xsd 
http://www.springframework.org/schema/jdbc http://www.springframework.org/schema/jdbc/spring-jdbc.xsd 
http://www.springframework.org/schema/aop 
http://www.springframework.org/schema/aop/spring-aop-3.1.xsd 
http://www.lock.org/schema/lock http://www.lock.org/schema/lock/lock-spring.xsd"> 
<lock:annotation-driven /> 
</beans>`

 3. lockManager config
>`	<bean id="curatorConnectionFactory"
		class="org.lock.spring.data.zookeeper.connection.CuratorConnectionFactory">
		<property name="host" value="127.0.0.1"></property>
		<property name="port" value="2181"></property>
	</bean>
	<bean id="genericObjectPool" class="org.apache.commons.pool.impl.GenericObjectPool">
		<constructor-arg index="0" ref="curatorConnectionFactory">
		</constructor-arg>
		<constructor-arg index="1"  value="10">
		</constructor-arg>
	</bean>
	<bean id="lockManager" class="org.lock.spring.data.zookeeper.lock.ZookeeperLockManager">
		<property name="pool" ref="genericObjectPool"></property>
	</bean>`
	
 4. annontation config
    ```java
    @Lock(name="test",lockTime=60000)
    public void testLock() {
    	logger.debug("start to test lock");
    	
    	try {
    		Thread.sleep(10000);
    	} catch (InterruptedException e) {
    		logger.info(e.getMessage(),e);
    	}
    	logger.debug("end to test lock");
    }
    ```
 5. use example：
    更加具体的使用方法请参考lock-example的使用。


