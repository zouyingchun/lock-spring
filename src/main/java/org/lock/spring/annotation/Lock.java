package org.lock.spring.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Lock {
	
	/**
	 * lock name
	 * @return
	 */
	String name() default "";
	
	/**
	 * lock time out,time unit is millsecond
	 * @return
	 */
	long lockTime() default -1;
	
}
