package org.lock.spring.data.zookeeper.connection;

public class CuratorConnectionConfig {
	
	/**
	 * the server host
	 */
	private String host;
	/**
	 * the server port
	 */
	private String port;
	/**
	 * 建立连接超时时间
	 */
	private int connectionTimeOut;
	/**
	 * 建立连接失败尝试从次数
	 */
	private int connectionRetryTime;
	/**
	 * @return the {@link #host}
	 */
	public String getHost() {
		return host;
	}
	/**
	 * @param host
	 * the {@link #host} to set
	 */
	public void setHost(String host) {
		this.host = host;
	}
	/**
	 * @return the {@link #port}
	 */
	public String getPort() {
		return port;
	}
	/**
	 * @param port
	 * the {@link #port} to set
	 */
	public void setPort(String port) {
		this.port = port;
	}
	/**
	 * @return the {@link #connectionTimeOut}
	 */
	public int getConnectionTimeOut() {
		return connectionTimeOut;
	}
	/**
	 * @param connectionTimeOut
	 * the {@link #connectionTimeOut} to set
	 */
	public void setConnectionTimeOut(int connectionTimeOut) {
		this.connectionTimeOut = connectionTimeOut;
	}
	/**
	 * @return the {@link #connectionRetryTime}
	 */
	public int getConnectionRetryTime() {
		return connectionRetryTime;
	}
	/**
	 * @param connectionRetryTime
	 * the {@link #connectionRetryTime} to set
	 */
	public void setConnectionRetryTime(int connectionRetryTime) {
		this.connectionRetryTime = connectionRetryTime;
	}
	
}
