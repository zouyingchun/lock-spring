package org.lock.spring.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.springframework.aop.support.StaticMethodMatcherPointcut;

public class LockPointCut extends StaticMethodMatcherPointcut implements Serializable{

	private static final long serialVersionUID = 1378243301609228714L;
	
	private LockAttributeSource lockAttributeSource;

	@Override
	public boolean matches(Method method, Class<?> targetClass) {
		LockAttribute lockAttribute = lockAttributeSource.getLockAttribute(method, targetClass);
		if(lockAttribute != null){
			return true;
		}
		return false;
	}

	/**
	 * @return the {@link #lockAttributeSource}
	 */
	public LockAttributeSource getLockAttributeSource() {
		return lockAttributeSource;
	}

	/**
	 * @param lockAttributeSource
	 * the {@link #lockAttributeSource} to set
	 */
	public void setLockAttributeSource(LockAttributeSource lockAttributeSource) {
		this.lockAttributeSource = lockAttributeSource;
	}

}
