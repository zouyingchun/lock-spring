package org.lock.spring.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class LockNamespaceHandler extends NamespaceHandlerSupport {

	public void init() {
		registerBeanDefinitionParser("annotation-driven", new AnnotationDrivenBeanDefinitionParser());
	}

}
