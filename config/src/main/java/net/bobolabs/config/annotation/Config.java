package net.bobolabs.config.annotation;

public @interface Config {

    String path() default "";

    boolean autoSave() default false;

    String defaultResource() default "";

}
