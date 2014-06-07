package org.lock.spring.data.zookeeper.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

class CuratorLock {
	/**
	 * the real lock
	 */
	private InterProcessMutex interProcessMutex;
	/**
	 * client connection
	 */
	private CuratorFramework curatorFramework;
	/**
	 * the lock unquie name
	 */
	private String lockName;
	/**
	 * @return the {@link #interProcessMutex}
	 */
	public InterProcessMutex getInterProcessMutex() {
		return interProcessMutex;
	}
	/**
	 * @param interProcessMutex
	 * the {@link #interProcessMutex} to set
	 */
	public void setInterProcessMutex(InterProcessMutex interProcessMutex) {
		this.interProcessMutex = interProcessMutex;
	}
	/**
	 * @return the {@link #curatorFramework}
	 */
	public CuratorFramework getCuratorFramework() {
		return curatorFramework;
	}
	/**
	 * @param curatorFramework
	 * the {@link #curatorFramework} to set
	 */
	public void setCuratorFramework(CuratorFramework curatorFramework) {
		this.curatorFramework = curatorFramework;
	}
	/**
	 * @return the {@link #lockName}
	 */
	public String getLockName() {
		return lockName;
	}
	/**
	 * @param lockName
	 * the {@link #lockName} to set
	 */
	public void setLockName(String lockName) {
		this.lockName = lockName;
	}
}
