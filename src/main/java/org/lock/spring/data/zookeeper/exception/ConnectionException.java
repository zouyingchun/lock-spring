package org.lock.spring.data.zookeeper.exception;

/**
 * create connection fail exception
 * @author chun
 *
 */
public class ConnectionException extends Exception{

	private static final long serialVersionUID = 7076695893701336236L;

	/**
	 * Constructor for ConnectionException.
	 * @param msg
	 */
	public ConnectionException(String msg){
		super(msg);
	}
	
	/**
	 * Constructor for ConnectionException.
	 * @param msg
	 * @param cause
	 */
	public ConnectionException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
