package com.github.changeche.flowable.common.annotation;

import com.github.changeche.flowable.model.enums.DBTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Chenjing
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DBType {
    DBTypeEnum value() default DBTypeEnum.FLOWABLE;
}
