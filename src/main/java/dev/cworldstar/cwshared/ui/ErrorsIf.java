package dev.cworldstar.cwshared.ui;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This {@link Interface} is a way for me to denote
 * reasonable error reasons in my IDE. Do not use this,
 * it is removed at runtime.
 * @author cworldstar
 *
 */

@Retention(RetentionPolicy.SOURCE)
@Repeatable(ErrorsIfRepeatable.class)
public @interface ErrorsIf {
	/**
	 * The {@link ErrorsIf#Reason()} is a reason for the 
	 * denoted method to error.
	 * @author cworldstar
	 */
	
	String Reason() default "Could be any reason.";
}
