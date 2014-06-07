package org.lock.spring.data.zookeeper.exception;

/**
 * config pool properties error
 * @author chun
 *
 */
public class PoolConfigException extends Exception{

	private static final long serialVersionUID = 7076695893701336236L;

	/**
	 * Constructor for PoolConfigException.
	 * @param msg
	 */
	public PoolConfigException(String msg){
		super(msg);
	}
	
	/**
	 * Constructor for PoolConfigException.
	 * @param msg
	 * @param cause
	 */
	public PoolConfigException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
