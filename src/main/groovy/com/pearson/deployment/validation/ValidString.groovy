package com.pearson.deployment.validation

import java.lang.annotation.*

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface ValidString {
    String message() default "Invalid field"
    String regexp() default ".*"
}