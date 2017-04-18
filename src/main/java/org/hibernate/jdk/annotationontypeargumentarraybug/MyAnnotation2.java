package org.hibernate.jdk.annotationontypeargumentarraybug;

import static java.lang.annotation.ElementType.TYPE_USE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(TYPE_USE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyAnnotation2 {
}
