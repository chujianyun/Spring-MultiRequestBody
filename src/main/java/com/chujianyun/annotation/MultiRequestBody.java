package com.chujianyun.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Controller中方法接收多个JSON对象
 *
 * @author 明明如月
 * @date 2018/08/27
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface MultiRequestBody {
    /**
     * 是否必须出现的参数
     */
    boolean required() default true;

    /**
     * 当value的值或者参数名不匹配时，是否允许解析最外层属性到该对象
     */
    boolean parseAllFields() default true;

    /**
     * 解析时用到的JSON的key
     */
    String value() default "";
}