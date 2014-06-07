package org.lock.spring.data.zookeeper.connection;

import java.util.NoSuchElementException;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool.Config;

public class PoolConfig extends Config {
	/**
	 * A "when exhausted action" type indicating that when the pool is
	 * exhausted (i.e., the maximum number of active objects has been
	 * reached), the {@link #borrowObject} method should fail, throwing a
	 * {@link NoSuchElementException}.
	 * 
	 * @see #WHEN_EXHAUSTED_BLOCK
	 * @see #WHEN_EXHAUSTED_GROW
	 * @see #setWhenExhaustedAction
	 */
	public static final byte WHEN_EXHAUSTED_FAIL = 0;
	/**
	 * A "when exhausted action" type indicating that when the pool is
	 * exhausted (i.e., the maximum number of active objects has been
	 * reached), the {@link #borrowObject} method should block until a new
	 * object is available, or the {@link #getMaxWait maximum wait time} has
	 * been reached.
	 * 
	 * @see #WHEN_EXHAUSTED_FAIL
	 * @see #WHEN_EXHAUSTED_GROW
	 * @see #setMaxWait
	 * @see #getMaxWait
	 * @see #setWhenExhaustedAction
	 */
	public static final byte WHEN_EXHAUSTED_BLOCK = 1;

	/**
	 * A "when exhausted action" type indicating that when the pool is
	 * exhausted (i.e., the maximum number of active objects has been
	 * reached), the {@link #borrowObject} method should simply create a new
	 * object anyway.
	 * 
	 * @see #WHEN_EXHAUSTED_FAIL
	 * @see #WHEN_EXHAUSTED_GROW
	 * @see #setWhenExhaustedAction
	 */
	public static final byte WHEN_EXHAUSTED_GROW = 2;

	/**
	 * Returns the maximum number of objects that can be allocated by the
	 * pool (checked out to clients, or idle awaiting checkout) at a given
	 * time. When non-positive, there is no limit to the number of objects
	 * that can be managed by the pool at one time.
	 * 
	 * @return the cap on the total number of object instances managed by
	 *         the pool.
	 * @see #setMaxActive
	 */
	public synchronized int getMaxActive() {
		return maxActive;
	}

	/**
	 * Sets the cap on the number of objects that can be allocated by the
	 * pool (checked out to clients, or idle awaiting checkout) at a given
	 * time. Use a negative value for no limit.
	 * 
	 * @param maxActive
	 *                The cap on the total number of object instances
	 *                managed by the pool. Negative values mean that there
	 *                is no limit to the number of objects allocated by the
	 *                pool.
	 * @see #getMaxActive
	 */
	public void setMaxActive(int maxActive) {
		synchronized (this) {
			this.maxActive = maxActive;
		}
	}

	/**
	 * Returns the action to take when the {@link #borrowObject} method is
	 * invoked when the pool is exhausted (the maximum number of "active"
	 * objects has been reached).
	 * 
	 * @return one of {@link #WHENEXHAUSTEDBLOCK},
	 *         {@link #WHENEXHAUSTEDFAIL} or {@link #WHENEXHAUSTEDGROW}
	 * @see #setWhenExhaustedAction
	 */
	public synchronized byte getWhenExhaustedAction() {
		return whenExhaustedAction;
	}

	/**
	 * Sets the action to take when the {@link #borrowObject} method is
	 * invoked when the pool is exhausted (the maximum number of "active"
	 * objects has been reached).
	 * 
	 * @param whenExhaustedAction
	 *                the action code, which must be one of
	 *                {@link #WHENEXHAUSTEDBLOCK},
	 *                {@link #WHENEXHAUSTEDFAIL}, or
	 *                {@link #WHENEXHAUSTEDGROW}
	 * @see #getWhenExhaustedAction
	 */
	public void setWhenExhaustedAction(byte whenExhaustedAction) {
		synchronized (this) {
			switch (whenExhaustedAction) {
			case WHEN_EXHAUSTED_BLOCK:
			case WHEN_EXHAUSTED_FAIL:
			case WHEN_EXHAUSTED_GROW:
				this.whenExhaustedAction = whenExhaustedAction;
				break;
			default:
				throw new IllegalArgumentException(
					"whenExhaustedAction "
						+ whenExhaustedAction
						+ " not recognized.");
			}
		}
	}

	/**
	 * Returns the maximum amount of time (in milliseconds) the
	 * {@link #borrowObject} method should block before throwing an
	 * exception when the pool is exhausted and the
	 * {@link #setWhenExhaustedAction "when exhausted" action} is
	 * {@link #WHENEXHAUSTEDBLOCK}.
	 * 
	 * When less than or equal to 0, the {@link #borrowObject} method may
	 * block indefinitely.
	 * 
	 * @return maximum number of milliseconds to block when borrowing an
	 *         object.
	 * @see #setMaxWait
	 * @see #setWhenExhaustedAction
	 * @see #WHENEXHAUSTEDBLOCK
	 */
	public synchronized long getMaxWait() {
		return maxWait;
	}

	/**
	 * Sets the maximum amount of time (in milliseconds) the
	 * {@link #borrowObject} method should block before throwing an
	 * exception when the pool is exhausted and the
	 * {@link #setWhenExhaustedAction "when exhausted" action} is
	 * {@link #WHENEXHAUSTEDBLOCK}.
	 * 
	 * When less than or equal to 0, the {@link #borrowObject} method may
	 * block indefinitely.
	 * 
	 * @param maxWait
	 *                maximum number of milliseconds to block when borrowing
	 *                an object.
	 * @see #getMaxWait
	 * @see #setWhenExhaustedAction
	 * @see #WHENEXHAUSTEDBLOCK
	 */
	public void setMaxWait(long maxWait) {
		this.maxWait = maxWait;
	}

	/**
	 * Returns the cap on the number of "idle" instances in the pool.
	 * 
	 * @return the cap on the number of "idle" instances in the pool.
	 * @see #setMaxIdle
	 */
	public synchronized int getMaxIdle() {
		return maxIdle;
	}

	/**
	 * Sets the cap on the number of "idle" instances in the pool. If
	 * maxIdle is set too low on heavily loaded systems it is possible you
	 * will see objects being destroyed and almost immediately new objects
	 * being created. This is a result of the active threads momentarily
	 * returning objects faster than they are requesting them them, causing
	 * the number of idle objects to rise above maxIdle. The best value for
	 * maxIdle for heavily loaded system will vary but the default is a good
	 * starting point.
	 * 
	 * @param maxIdle
	 *                The cap on the number of "idle" instances in the pool.
	 *                Use a negative value to indicate an unlimited number
	 *                of idle instances.
	 * @see #getMaxIdle
	 */
	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	/**
	 * Sets the minimum number of objects allowed in the pool before the
	 * evictor thread (if active) spawns new objects. Note that no objects
	 * are created when <code>numActive + numIdle >= maxActive.</code> This
	 * setting has no effect if the idle object evictor is disabled (i.e. if
	 * <code>timeBetweenEvictionRunsMillis <= 0</code>).
	 * 
	 * @param minIdle
	 *                The minimum number of objects.
	 * @see #getMinIdle
	 * @see #getTimeBetweenEvictionRunsMillis()
	 */
	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	/**
	 * Returns the minimum number of objects allowed in the pool before the
	 * evictor thread (if active) spawns new objects. (Note no objects are
	 * created when: numActive + numIdle >= maxActive)
	 * 
	 * @return The minimum number of objects.
	 * @see #setMinIdle
	 */
	public synchronized int getMinIdle() {
		return minIdle;
	}

	/**
	 * When <tt>true</tt>, objects will be
	 * {@link PoolableObjectFactory#validateObject validated} before being
	 * returned by the {@link #borrowObject} method. If the object fails to
	 * validate, it will be dropped from the pool, and we will attempt to
	 * borrow another.
	 * 
	 * @return <code>true</code> if objects are validated before being
	 *         borrowed.
	 * @see #setTestOnBorrow
	 */
	public boolean getTestOnBorrow() {
		return testOnBorrow;
	}

	/**
	 * When <tt>true</tt>, objects will be
	 * {@link PoolableObjectFactory#validateObject validated} before being
	 * returned by the {@link #borrowObject} method. If the object fails to
	 * validate, it will be dropped from the pool, and we will attempt to
	 * borrow another.
	 * 
	 * @param testOnBorrow
	 *                <code>true</code> if objects should be validated
	 *                before being borrowed.
	 * @see #getTestOnBorrow
	 */
	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

	/**
	 * When <tt>true</tt>, objects will be
	 * {@link PoolableObjectFactory#validateObject validated} before being
	 * returned to the pool within the {@link #returnObject}.
	 * 
	 * @return <code>true</code> when objects will be validated after
	 *         returned to {@link #returnObject}.
	 * @see #setTestOnReturn
	 */
	public boolean getTestOnReturn() {
		return testOnReturn;
	}

	/**
	 * When <tt>true</tt>, objects will be
	 * {@link PoolableObjectFactory#validateObject validated} before being
	 * returned to the pool within the {@link #returnObject}.
	 * 
	 * @param testOnReturn
	 *                <code>true</code> so objects will be validated after
	 *                returned to {@link #returnObject}.
	 * @see #getTestOnReturn
	 */
	public void setTestOnReturn(boolean testOnReturn) {
		this.testOnReturn = testOnReturn;
	}

	/**
	 * Returns the number of milliseconds to sleep between runs of the idle
	 * object evictor thread. When non-positive, no idle object evictor
	 * thread will be run.
	 * 
	 * @return number of milliseconds to sleep between evictor runs.
	 * @see #setTimeBetweenEvictionRunsMillis
	 */
	public synchronized long getTimeBetweenEvictionRunsMillis() {
		return timeBetweenEvictionRunsMillis;
	}

	/**
	 * Sets the number of milliseconds to sleep between runs of the idle
	 * object evictor thread. When non-positive, no idle object evictor
	 * thread will be run.
	 * 
	 * @param timeBetweenEvictionRunsMillis
	 *                number of milliseconds to sleep between evictor runs.
	 * @see #getTimeBetweenEvictionRunsMillis
	 */
	public synchronized void setTimeBetweenEvictionRunsMillis(
		long timeBetweenEvictionRunsMillis) {
		this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
	}

	/**
	 * Returns the max number of objects to examine during each run of the
	 * idle object evictor thread (if any).
	 * 
	 * @return max number of objects to examine during each evictor run.
	 * @see #setNumTestsPerEvictionRun
	 * @see #setTimeBetweenEvictionRunsMillis
	 */
	public synchronized int getNumTestsPerEvictionRun() {
		return numTestsPerEvictionRun;
	}

	/**
	 * Sets the max number of objects to examine during each run of the idle
	 * object evictor thread (if any).
	 * <p>
	 * When a negative value is supplied,
	 * <tt>ceil({@link #getNumIdle})/abs({@link #getNumTestsPerEvictionRun})</tt>
	 * tests will be run. That is, when the value is <i>-n</i>, roughly one
	 * <i>n</i>th of the idle objects will be tested per run. When the value
	 * is positive, the number of tests actually performed in each run will
	 * be the minimum of this value and the number of instances idle in the
	 * pool.
	 * 
	 * @param numTestsPerEvictionRun
	 *                max number of objects to examine during each evictor
	 *                run.
	 * @see #getNumTestsPerEvictionRun
	 * @see #setTimeBetweenEvictionRunsMillis
	 */
	public synchronized void setNumTestsPerEvictionRun(
		int numTestsPerEvictionRun) {
		this.numTestsPerEvictionRun = numTestsPerEvictionRun;
	}

	/**
	 * Returns the minimum amount of time an object may sit idle in the pool
	 * before it is eligible for eviction by the idle object evictor (if
	 * any).
	 * 
	 * @return minimum amount of time an object may sit idle in the pool
	 *         before it is eligible for eviction.
	 * @see #setMinEvictableIdleTimeMillis
	 * @see #setTimeBetweenEvictionRunsMillis
	 */
	public synchronized long getMinEvictableIdleTimeMillis() {
		return minEvictableIdleTimeMillis;
	}

	/**
	 * Sets the minimum amount of time an object may sit idle in the pool
	 * before it is eligible for eviction by the idle object evictor (if
	 * any). When non-positive, no objects will be evicted from the pool due
	 * to idle time alone.
	 * 
	 * @param minEvictableIdleTimeMillis
	 *                minimum amount of time an object may sit idle in the
	 *                pool before it is eligible for eviction.
	 * @see #getMinEvictableIdleTimeMillis
	 * @see #setTimeBetweenEvictionRunsMillis
	 */
	public synchronized void setMinEvictableIdleTimeMillis(
		long minEvictableIdleTimeMillis) {
		this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
	}

	/**
	 * Returns the minimum amount of time an object may sit idle in the pool
	 * before it is eligible for eviction by the idle object evictor (if
	 * any), with the extra condition that at least "minIdle" amount of
	 * object remain in the pool.
	 * 
	 * @return minimum amount of time an object may sit idle in the pool
	 *         before it is eligible for eviction.
	 * @since Pool 1.3
	 * @see #setSoftMinEvictableIdleTimeMillis
	 */
	public synchronized long getSoftMinEvictableIdleTimeMillis() {
		return softMinEvictableIdleTimeMillis;
	}

	/**
	 * Sets the minimum amount of time an object may sit idle in the pool
	 * before it is eligible for eviction by the idle object evictor (if
	 * any), with the extra condition that at least "minIdle" object
	 * instances remain in the pool. When non-positive, no objects will be
	 * evicted from the pool due to idle time alone.
	 * 
	 * @param softMinEvictableIdleTimeMillis
	 *                minimum amount of time an object may sit idle in the
	 *                pool before it is eligible for eviction.
	 * @since Pool 1.3
	 * @see #getSoftMinEvictableIdleTimeMillis
	 */
	public synchronized void setSoftMinEvictableIdleTimeMillis(
		long softMinEvictableIdleTimeMillis) {
		this.softMinEvictableIdleTimeMillis = softMinEvictableIdleTimeMillis;
	}

	/**
	 * When <tt>true</tt>, objects will be
	 * {@link PoolableObjectFactory#validateObject validated} by the idle
	 * object evictor (if any). If an object fails to validate, it will be
	 * dropped from the pool.
	 * 
	 * @return <code>true</code> when objects will be validated by the
	 *         evictor.
	 * @see #setTestWhileIdle
	 * @see #setTimeBetweenEvictionRunsMillis
	 */
	public synchronized boolean getTestWhileIdle() {
		return testWhileIdle;
	}

	/**
	 * When <tt>true</tt>, objects will be
	 * {@link PoolableObjectFactory#validateObject validated} by the idle
	 * object evictor (if any). If an object fails to validate, it will be
	 * dropped from the pool.
	 * 
	 * @param testWhileIdle
	 *                <code>true</code> so objects will be validated by the
	 *                evictor.
	 * @see #getTestWhileIdle
	 * @see #setTimeBetweenEvictionRunsMillis
	 */
	public synchronized void setTestWhileIdle(boolean testWhileIdle) {
		this.testWhileIdle = testWhileIdle;
	}

	/**
	 * Whether or not the idle object pool acts as a LIFO queue. True means
	 * that borrowObject returns the most recently used ("last in") idle
	 * object in the pool (if there are idle instances available). False
	 * means that the pool behaves as a FIFO queue - objects are taken from
	 * the idle object pool in the order that they are returned to the pool.
	 * 
	 * @return <code>true</true> if the pool is configured to act as a LIFO queue
	 * @since 1.4
	 */
	public synchronized boolean getLifo() {
		return lifo;
	}

	/**
	 * Sets the LIFO property of the pool. True means that borrowObject
	 * returns the most recently used ("last in") idle object in the pool
	 * (if there are idle instances available). False means that the pool
	 * behaves as a FIFO queue - objects are taken from the idle object pool
	 * in the order that they are returned to the pool.
	 * 
	 * @param lifo
	 *                the new value for the LIFO property
	 * @since 1.4
	 */
	public synchronized void setLifo(boolean lifo) {
		this.lifo = lifo;
	}

}
