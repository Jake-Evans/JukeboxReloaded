package cazzar.mods.jukeboxreloaded.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigurationOption {
	String category();
	
	String comment() default "";
	
	String key();
}
