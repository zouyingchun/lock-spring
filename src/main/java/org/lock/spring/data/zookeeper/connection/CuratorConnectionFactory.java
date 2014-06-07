package org.lock.spring.data.zookeeper.connection;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.utils.CloseableUtils;
import org.lock.spring.data.zookeeper.exception.PoolConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * curator connection pool
 * @author chun
 *
 */
public class CuratorConnectionFactory implements PoolableObjectFactory<CuratorFramework>,InitializingBean, DisposableBean{
	private Logger logger = LoggerFactory.getLogger(CuratorFrameworkFactory.class);
	
	private final static int DEFAULT_CONNECTION_TIMEOUT_MS = 1000;
	
	private final static int DEFAULT_SESSION_TIMEOUT_MS = 3;
	
	private final static int DEFAULT_GET_CONNECTION_TIME_OUT_MS= 30000;
	
	public Map<Integer, Boolean> connectionStateMap =new HashMap<Integer, Boolean>();
	
	/**
	 * the server host
	 */
	private String host;
	/**
	 * the server port
	 */
	private String port;
	/**
	 * connection time out
	 */
	private int connectionTimeOut;
	/**
	 * client session time out
	 */
	private int sessionTimeOut;
	
	public CuratorConnectionFactory() {
	}

	public CuratorConnectionFactory(String host, String port) {
		this(host,port,DEFAULT_CONNECTION_TIMEOUT_MS,DEFAULT_SESSION_TIMEOUT_MS);
	}
	
	public CuratorConnectionFactory(String host, String port,
		int connectionTimeOut, int sessionTimeOut) {
		this.host = host;
		this.port = port;
		this.connectionTimeOut = connectionTimeOut;
		this.sessionTimeOut = sessionTimeOut;
	}

	/**
	 * create a new zookeeper connection instance
	 */
	public CuratorFramework makeObject() throws Exception {
		return createConnection();
	}

	/**
	 * destory zookeeper connection
	 */
	public void destroyObject(CuratorFramework obj) throws Exception {
		connectionStateMap.remove(obj.hashCode());
		CloseableUtils.closeQuietly(obj);
	}
	
	/**
	 * check connection is useable
	 */
	public boolean validateObject(CuratorFramework obj) {
		return connectionStateMap.get(obj.hashCode());
	}

	public void activateObject(CuratorFramework obj) throws Exception {
	}

	public void passivateObject(CuratorFramework obj) throws Exception {
	}

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
	 * @return the {@link #sessionTimeOut}
	 */
	public int getSessionTimeOut() {
		return sessionTimeOut;
	}

	/**
	 * @param sessionTimeOut
	 * the {@link #sessionTimeOut} to set
	 */
	public void setSessionTimeOut(int sessionTimeOut) {
		this.sessionTimeOut = sessionTimeOut;
	}

	/**
	 * create new zookeeper connection instance
	 * @return
	 * @throws PoolConfigException
	 */
	private CuratorFramework createConnection()throws ConnectException{
		CuratorFramework client= CuratorFrameworkFactory.
                	newClient(host+":"+port, new RetryOneTime(1000));
		CountDownLatch countDownLatch = new CountDownLatch(1);
		client.getConnectionStateListenable().addListener(new CuratorFrameworkConnectionListener(countDownLatch));
                client.start();
                boolean isConnectSuccess = false;
                try{
	                isConnectSuccess = countDownLatch.await(
	                	DEFAULT_GET_CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                }catch(Exception e){
                	if(logger.isDebugEnabled()){
                		logger.debug(e.getMessage(),e);
                	}
                }
                if(!isConnectSuccess && countDownLatch.getCount() == 1){
                	throw new ConnectException("create connection is time out.please check the network.");
                }
		return client;
	}
	
	/**
	 * check zookeeper connection properties
	 * @return
	 * @throws PoolConfigException
	 */
	private boolean checkPoolConfig()throws PoolConfigException{
		CuratorFramework client  = null;
		try{
			client = createConnection();
			return true;
		}catch(ConnectException e){
			logger.error("please check your zookeeper connection properties,create connection is fail.");
			return false;
		}finally{
			CloseableUtils.closeQuietly(client);
		}
	}
	
	/**
	 * check the connection config
	 */
	public void afterPropertiesSet() throws Exception {
		checkPoolConfig();
	}
	
	public void destroy() throws Exception {
	}

	/**
	 * connection success listener
	 * @author chun
	 *
	 */
	class CuratorFrameworkConnectionListener implements ConnectionStateListener{
		private CountDownLatch countDownLatch;
		public CuratorFrameworkConnectionListener(CountDownLatch countDownLatch){
			this.countDownLatch = countDownLatch;
		}
		
		public void stateChanged(CuratorFramework client,
			ConnectionState newState) {
			switch (newState) {
			case SUSPENDED:
			case LOST:
			case READ_ONLY:
				connectionStateMap.put(client.hashCode(), false);
				break;
			case CONNECTED:
			case RECONNECTED:
				connectionStateMap.put(client.hashCode(), true);
				break;
			default:
				break;
			}
			if(client.getState() == CuratorFrameworkState.STARTED
				&& newState == ConnectionState.CONNECTED ){
				countDownLatch.countDown();
			}
		}
		
	}
	
}

