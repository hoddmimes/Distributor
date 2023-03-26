package com.hoddmimes.distributor.samples.table;

import java.lang.annotation.*;

@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TableAttribute
{
    int     column();
    String header();
    String justify() default "byClass";
    int     preferedWidth() default 0;
    int     width() default 0;
    boolean editable() default false;
}
