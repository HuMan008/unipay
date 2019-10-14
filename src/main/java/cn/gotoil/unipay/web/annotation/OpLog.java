package cn.gotoil.unipay.web.annotation;

import java.lang.annotation.*;

/**
 * 操作日志的
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OpLog {
    String desc() default "";
}

