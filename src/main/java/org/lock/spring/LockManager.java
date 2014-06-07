package org.lock.spring;

import java.util.concurrent.TimeUnit;

import org.lock.spring.exception.GetLockFailException;
import org.lock.spring.exception.GetLockTimeOutException;
import org.lock.spring.exception.ReleaseLockException;

/**
 * lock service
 * @author chun
 *
 */
public interface LockManager {
	
	/**
	 * get lock
	 * @param lockName
	 * @return the real lock key
	 */
	public String getLock(String lockName)throws GetLockFailException,GetLockTimeOutException;
	
	/**
	 * get lock
	 * @param lockName
	 * @param time
	 * @param unit
	 * @return the real lock key
	 */
	public String getLock(String lockName,long time,TimeUnit unit)throws GetLockFailException,GetLockTimeOutException;
	
	/**
	 * release lock
	 * @param lockName
	 */
	public void releaseLock(String lockName)throws ReleaseLockException;

}
