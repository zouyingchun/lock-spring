package org.lock.spring.config;

import static org.springframework.context.annotation.AnnotationConfigUtils.CACHE_ADVISOR_BEAN_NAME;

import org.lock.spring.interceptor.LockAdvisior;
import org.lock.spring.interceptor.LockAttributeSource;
import org.lock.spring.interceptor.LockInterceptor;
import org.lock.spring.interceptor.LockPointCut;
import org.springframework.aop.config.AopNamespaceUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class AnnotationDrivenBeanDefinitionParser implements
	BeanDefinitionParser {
	
	private static final String LOCK_ADVISOR_BEAN_NAME = "com.test.lock.interceptor.LockAdvisior";

	public BeanDefinition parse(Element element, ParserContext parserContext) {
		AopAutoProxyConfigurer.configureAutoProxyCreator(element,
			parserContext);
		return null;
	}


	/**
	 * Inner class to just introduce an AOP framework dependency when
	 * actually in proxy mode.
	 */
	private static class AopAutoProxyConfigurer {

		public static void configureAutoProxyCreator(Element element,
			ParserContext parserContext) {
			AopNamespaceUtils.registerAutoProxyCreatorIfNecessary(parserContext, element);

			if (!parserContext.getRegistry().containsBeanDefinition(LOCK_ADVISOR_BEAN_NAME)) {
				Object eleSource = parserContext.extractSource(element);

				// Create the CacheOperationSource definition.
				RootBeanDefinition sourceDef = new RootBeanDefinition(
					LockAttributeSource.class);
				sourceDef.setSource(eleSource);
				sourceDef
					.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
				String sourceName = parserContext.getReaderContext().registerWithGeneratedName(sourceDef);

				// Create the LockInterceptor definition.
				RootBeanDefinition interceptorDef = new RootBeanDefinition(LockInterceptor.class);
				interceptorDef.setSource(eleSource);
				interceptorDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
				interceptorDef.getPropertyValues().add("lockManager",new RuntimeBeanReference("lockManager"));
				interceptorDef.getPropertyValues().add("lockAttributeSource",new RuntimeBeanReference(sourceName));
				// register lock interceptor
				String interceptorName = parserContext.getReaderContext()
					.registerWithGeneratedName(interceptorDef);

				// Create the LockPointCut definition
				RootBeanDefinition lockPointCutDef = new RootBeanDefinition(LockPointCut.class);
				lockPointCutDef.setSource(eleSource);
				lockPointCutDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
				lockPointCutDef.getPropertyValues().add("lockAttributeSource",new RuntimeBeanReference(sourceName));
				// register lockPointCut interceptor
				String lockPointCutName = parserContext.getReaderContext()
					.registerWithGeneratedName(lockPointCutDef);
				
				// Create the CacheAdvisor definition.
				RootBeanDefinition advisorDef = new RootBeanDefinition(LockAdvisior.class);
				advisorDef.setSource(eleSource);
				advisorDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
				advisorDef.getPropertyValues().add("pointcut",new RuntimeBeanReference(lockPointCutName));
				advisorDef.getPropertyValues().add("adviceBeanName", interceptorName);
				parserContext.getRegistry().registerBeanDefinition(LOCK_ADVISOR_BEAN_NAME,advisorDef);

				CompositeComponentDefinition compositeDef = new CompositeComponentDefinition(element.getTagName(), eleSource);
				compositeDef.addNestedComponent(new BeanComponentDefinition(sourceDef, sourceName));
				compositeDef.addNestedComponent(new BeanComponentDefinition(interceptorDef, interceptorName));
				compositeDef.addNestedComponent(
					new BeanComponentDefinition(advisorDef,CACHE_ADVISOR_BEAN_NAME));
				parserContext.registerComponent(compositeDef);
			}
		}
	}

}
