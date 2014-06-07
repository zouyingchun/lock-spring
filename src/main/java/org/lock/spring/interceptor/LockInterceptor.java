package org.lock.spring.interceptor;

import java.util.concurrent.TimeUnit;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.lock.spring.LockManager;
import org.lock.spring.exception.GetLockTimeOutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.InitializingBean;

/**
 * lock interceptor
 * @author chun
 *
 */
public class LockInterceptor implements MethodInterceptor,InitializingBean{
	
	private Logger logger = LoggerFactory.getLogger(LockInterceptor.class);

	private LockAttributeSource lockAttributeSource;
	private LockManager lockManager;
	
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		long startTime = System.currentTimeMillis();
		Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);
		//  get annontation lock attribute
		LockAttribute lockAttribute = lockAttributeSource.getLockAttribute(invocation.getMethod(), targetClass);
		String lockName = null;
		try{
			lockName = lockManager.getLock(lockAttribute.getName(),lockAttribute.getLockTime(),TimeUnit.MILLISECONDS);
			if(logger.isInfoEnabled()){
				logger.info("get lock "+lockName+" success");
			}
			// 反射执行结果
			Object result =  invocation.proceed();
			long endTime = System.currentTimeMillis();
			if(logger.isDebugEnabled()){
				logger.debug("the own the lock time is "+(endTime-startTime));
			}
			return result;
		}catch(GetLockTimeOutException lockTimeOutException){
			if(logger.isInfoEnabled()){
				logger.info("get lock "+lockAttribute.getName()+ " time out.");
			}
			throw lockTimeOutException;
		}catch(Exception e){
			if(logger.isInfoEnabled()){
				logger.info("get lock "+lockAttribute.getName()+ "fail.the reason is "+e.getMessage(),e);
			}
			throw e;
		}
		finally{
			if(lockName != null){
				lockManager.releaseLock(lockName);
				logger.info("release lock "+lockName+" success");
			}
		}
	}
	
	/**
	 * @return the {@link #lockAttributeSource}
	 */
	public LockAttributeSource getLockAttributeSource() {
		return lockAttributeSource;
	}

	/**
	 * @param lockAttributeSource
	 * the {@link #lockAttributeSource} to set
	 */
	public void setLockAttributeSource(LockAttributeSource lockAttributeSource) {
		this.lockAttributeSource = lockAttributeSource;
	}

	/**
	 * @return the {@link #lockManager}
	 */
	public LockManager getLockManager() {
		return lockManager;
	}

	/**
	 * @param lockManager
	 * the {@link #lockManager} to set
	 */
	public void setLockManager(LockManager lockManager) {
		this.lockManager = lockManager;
	}

	/**
	 * check lockManager、lockAttributeSource
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.lockManager == null) {
			throw new IllegalStateException(
					"Setting the property 'transactionManager' is required");
		}
		if (this.lockAttributeSource == null) {
			throw new IllegalStateException(
					"Settin the property 'lockAttributeSource' is required" );
		}
		
	}
	
}
