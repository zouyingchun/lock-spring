package org.lock.spring.interceptor;


/**
 * lock properties
 * @author chun
 *
 */
public class LockAttribute {
	/**
	 * lock name
	 */
	private String name;
	/**
	 * get lock time out,time unit is millsecond
	 */
	private long lockTime;

	public static final LockAttribute NULL_LOCK_ATTRIBUTE = new LockAttribute();
	/**
	 * @return the {@link #name}
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name
	 * the {@link #name} to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the {@link #lockTime}
	 */
	public long getLockTime() {
		return lockTime;
	}
	/**
	 * @param lockTime
	 * the {@link #lockTime} to set
	 */
	public void setLockTime(long lockTime) {
		this.lockTime = lockTime;
	}
}
