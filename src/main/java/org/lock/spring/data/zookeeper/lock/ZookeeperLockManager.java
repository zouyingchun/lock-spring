package org.lock.spring.data.zookeeper.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.lock.spring.LockManager;
import org.lock.spring.exception.GetLockFailException;
import org.lock.spring.exception.GetLockTimeOutException;
import org.lock.spring.exception.ReleaseLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * LockManager implementation for zookeeper.
 * @author chun
 *
 */
public class ZookeeperLockManager implements LockManager,InitializingBean{
	private final Logger logger = LoggerFactory.getLogger(ZookeeperLockManager.class);
	
	public Map<String, CuratorLock> lockMap = new HashMap<String, CuratorLock>();
	/**
	 * zookeeper connection pool
	 */
	private GenericObjectPool<CuratorFramework> pool;
	
	/**
	 * default get lock time out 3000ms
	 */
	private final static int DEFAULT_LOCK_TIME_MS = 30000;
	
	public String getLock(String lockName) throws GetLockFailException,
		GetLockTimeOutException {
		return getLock(lockName, DEFAULT_LOCK_TIME_MS, TimeUnit.MILLISECONDS);
	}

	public String getLock(String lockName, long time, TimeUnit unit)
		throws GetLockFailException, GetLockTimeOutException {
		CuratorFramework curatorFramework  = null;
		try {
			curatorFramework = pool.borrowObject();
			InterProcessMutex lock = new InterProcessMutex(curatorFramework, "/"+lockName);
	        	boolean lockResult = false;
			if(time == -1){
				lock.acquire();
				lockResult = true;
			}else{
				lockResult = lock.acquire(time, unit);
			}
			// get lock fail for time out
	        	if(!lockResult){
	        		try {
	        			// 释放链接
					pool.returnObject(curatorFramework);
				} catch (Exception e) {
					throw new ReleaseLockException("return connection to pool fail  ,reason is "+e.getMessage(),e);
				}
	        		throw new GetLockTimeOutException("get lock fail for time out.");
	        	}
	        	
	        	// save lock info into map
	        	CuratorLock curatorLock = new CuratorLock();
	        	curatorLock.setCuratorFramework(curatorFramework);
	        	curatorLock.setInterProcessMutex(lock);
	        	String lockUnquieName =  UUID.randomUUID().toString();
	        	curatorLock.setLockName(lockUnquieName);
	        	
	        	lockMap.put(lockUnquieName, curatorLock);
	        	
	        	return lockUnquieName;
		} catch(GetLockTimeOutException e1){
			throw e1;
		} catch (Exception e) {
			logger.info("get lock fail,reason is "+e.getMessage(),e);
			throw new GetLockFailException("get connection from pool fail,reason is "+e.getMessage(), e);
		}
		
	}
	
	public void releaseLock(String lockName) throws ReleaseLockException{
		CuratorLock curatorLock = lockMap.get(lockName);
		if(curatorLock == null){
			throw new ReleaseLockException("release lock "+lockName +" fail.");
		}
		try {
			curatorLock.getInterProcessMutex().release();
		} catch (Exception e) {
			throw new ReleaseLockException("release lock fail ,reason is "+e.getMessage(),e);
		}finally{
			try {
				pool.returnObject(curatorLock.getCuratorFramework());
			} catch (Exception e) {
				throw new ReleaseLockException("return connection to pool fail  ,reason is "+e.getMessage(),e);
			}
			lockMap.remove(lockName);
		}
		
	}

	public void afterPropertiesSet() throws Exception {
		if(pool == null){
			logger.error("config error,please set pool property.");
		}
	}

	/**
	 * @return the {@link #pool}
	 */
	public GenericObjectPool<CuratorFramework> getPool() {
		return pool;
	}

	/**
	 * @param pool
	 * the {@link #pool} to set
	 */
	public void setPool(GenericObjectPool<CuratorFramework> pool) {
		this.pool = pool;
	}

}
