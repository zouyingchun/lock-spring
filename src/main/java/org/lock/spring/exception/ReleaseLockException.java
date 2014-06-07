package org.lock.spring.exception;

/**
 *  release lock fail exception
 * @author chun
 *
 */
public class ReleaseLockException extends Exception{

	private static final long serialVersionUID = -3181617965937980279L;
	
	/**
	 * Constructor for ReleaseLockException.
	 * @param msg
	 */
	public ReleaseLockException(String msg){
		super(msg);
	}
	
	/**
	 * Constructor for ReleaseLockException.
	 * @param msg
	 * @param cause
	 */
	public ReleaseLockException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}
