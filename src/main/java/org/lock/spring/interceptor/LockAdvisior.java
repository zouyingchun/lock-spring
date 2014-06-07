package org.lock.spring.interceptor;

import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;

public class LockAdvisior extends AbstractBeanFactoryPointcutAdvisor{

	private static final long serialVersionUID = -8052867589546408930L;
	private Pointcut pointcut;
	
	public void setPointcut(Pointcut pointcut){
		this.pointcut = pointcut;
	}
	@Override
	public Pointcut getPointcut() {
		return this.pointcut;
	}

}
