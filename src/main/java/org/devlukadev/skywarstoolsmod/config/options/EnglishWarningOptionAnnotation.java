package org.devlukadev.skywarstoolsmod.config.options;

import cc.polyfrost.oneconfig.config.annotations.CustomOption;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@CustomOption(id = "englishWarningOption")
public @interface EnglishWarningOptionAnnotation {
    String category();
    String subcategory() default "";
    int size() default 2;
}