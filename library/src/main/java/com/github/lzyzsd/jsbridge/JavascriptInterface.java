package com.github.lzyzsd.jsbridge;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 类似Android注解方式
 * Created by silen on 03/05/2018.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface JavascriptInterface {

    HandlerMode handlerMode() default HandlerMode.REGISTER;

    String[] value() default {};
}
