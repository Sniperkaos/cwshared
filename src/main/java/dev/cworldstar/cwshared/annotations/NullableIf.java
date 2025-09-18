package dev.cworldstar.cwshared.annotations;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This {@link Interface} is a way for me to denote
 * reasonable reasons for nullability in my IDE.
 * It does not persist over maven building.
 * @author cworldstar
 *
 */
@Retention(RetentionPolicy.SOURCE)
@Repeatable(NullableIfRepeatable.class)
public @interface NullableIf {
	/**
	 * The {@link NullableIf#Reason()} is a reason for the 
	 * denoted method to error.
	 * @author cworldstar
	 */
	String Reason() default "Nullable by default.";
}
