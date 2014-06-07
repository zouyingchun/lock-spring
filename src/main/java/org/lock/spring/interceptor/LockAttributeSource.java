package org.lock.spring.interceptor;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.lock.spring.annotation.Lock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.ClassUtils;

/**
 *  <p>This class reads Spring's JDK 1.5+ {@link Lock} annotation and
 * exposes corresponding lock attributes to Spring's lock infrastructure.
 * This class may also serve as base class for a custom LockAttributeSource,
 * @author chun
 *
 */
public class LockAttributeSource {
	
	private Logger logger = LoggerFactory.getLogger(LockAttributeSource.class);
	/**
	 * method lock attribute cache
	 */
	final Map<String, LockAttribute> attributeCache = new ConcurrentHashMap<String, LockAttribute>(1024);
	
	public LockAttribute getLockAttribute(Method method, Class<?> targetClass){
		String key  = computeClassMethodUnqiueKey(method, targetClass);
		LockAttribute lockAttribute= null;
		if(attributeCache.containsKey(key)){
			lockAttribute = attributeCache.get(key);
			// 判断是否为NULL_LOCK_ATTRIBUTE
			if(lockAttribute.equals(LockAttribute.NULL_LOCK_ATTRIBUTE)){
				return null;
			}
			return lockAttribute;
		}else{
			lockAttribute = computeLockAttribute(method, targetClass);
			if(lockAttribute == null){
				attributeCache.put(key, LockAttribute.NULL_LOCK_ATTRIBUTE);
			}else{
				attributeCache.put(key, lockAttribute);
			}
		}
		return lockAttribute;
	}
	
	/**
	 * compute the class method unqiue key
	 * @param method
	 * @param targetClass
	 * @return
	 */
	private String computeClassMethodUnqiueKey(Method method, Class<?> targetClass){
		StringBuilder key = new StringBuilder();
		key.append(targetClass.getName()+".");
		key.append(method.getName());
		key.append("[");
		for(@SuppressWarnings("rawtypes") Class paramClass : method.getParameterTypes()){
			key.append(paramClass.getName()+",");
		}
		key.append("]");
		return key.toString();
	}
	
	/**
	 * 
	 * @param method
	 * @param targetClass
	 * @return
	 */
	private LockAttribute computeLockAttribute(Method method, Class<?> targetClass){
		// Ignore CGLIB subclasses - introspect the actual user class.
		Class<?> userClass = ClassUtils.getUserClass(targetClass);
		// The method may be on an interface, but we need attributes from the target class.
		// If the target class is null, the method will be unchanged.
		Method specificMethod = ClassUtils.getMostSpecificMethod(method, userClass);
		// If we are dealing with method with generic parameters, find the original method.
		specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
		
		// First try is the method in the target class.
		LockAttribute txAtt = findLockAttribute(specificMethod, userClass);
		if (txAtt != null) {
			return txAtt;
		}

		if (specificMethod != method) {
			// Fallback is to look at the original method.
			txAtt = findLockAttribute(method, targetClass);
			if (txAtt != null) {
				return txAtt;
			}
		}
		return null;
	}
	
	/**
	 * get lock annontation attribute
	 * @param method
	 * @param targetClass
	 * @return
	 */
	private LockAttribute findLockAttribute(Method method,Class<?>  targetClass){
		AnnotationAttributes ann = AnnotatedElementUtils.getAnnotationAttributes(method, Lock.class.getName());
		if(ann != null){
			LockAttribute lockAttribute = new LockAttribute();
			// check lock time
			long lockTime = -1;
			try{
				lockTime = ann.getNumber("lockTime").longValue();
			}catch(Exception e){
				logger.info("parse method ["+method.getName()+"] lockTime error.",e);
			}
			lockAttribute.setLockTime(lockTime);
			
			// check name 
			String name = ann.getString("name");
			if(name == null || "".equals(name)){
				name  = targetClass.getName()+"/"+method.getName();
			}
			lockAttribute.setName(name);
			return lockAttribute;
		}else{
			return null;
		}
	}
}
