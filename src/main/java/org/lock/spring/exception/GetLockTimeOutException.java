package org.lock.spring.exception;

/**
 *  get lock time out exception
 * @author chun
 *
 */
public class GetLockTimeOutException extends Exception{

	private static final long serialVersionUID = -3181617965937980279L;
	
	/**
	 * Constructor for GetLockTimeOutException.
	 * @param msg
	 */
	public GetLockTimeOutException(String msg){
		super(msg);
	}
	
	/**
	 * Constructor for GetLockTimeOutException.
	 * @param msg
	 * @param cause
	 */
	public GetLockTimeOutException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}
