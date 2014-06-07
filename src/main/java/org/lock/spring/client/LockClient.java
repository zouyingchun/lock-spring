package org.lock.spring.client;


/**
 * lock client operation
 * @author chun
 *
 */
public interface LockClient {
	
	/**
	 * get path lock 
	 *  Acquire the mutex - blocking until it's available.
	 * @param lockName
	 * @return
	 */
	public boolean getLock(String path);
	/**
	 * release lock
	 * @return
	 */
	public boolean releaseLock();
	
}
