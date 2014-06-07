package org.lock.spring.exception;

/**
 *  get lock fail exception
 * @author chun
 *
 */
public class GetLockFailException extends Exception{

	private static final long serialVersionUID = -3181617965937980279L;
	
	/**
	 * Constructor for GetLockFailException.
	 * @param msg
	 */
	public GetLockFailException(String msg){
		super(msg);
	}
	
	/**
	 * Constructor for GetLockFailException.
	 * @param msg
	 * @param cause
	 */
	public GetLockFailException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}
