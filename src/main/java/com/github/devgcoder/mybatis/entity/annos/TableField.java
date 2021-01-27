package com.github.devgcoder.mybatis.entity.annos;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author duheng
 * @Date 2021/1/12 18:39
 */
@Inherited
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TableField {

  String value() default "";

  boolean isId() default false;

  boolean useGeneratedKeys() default false;

  boolean noneNotInsert() default false;
}
