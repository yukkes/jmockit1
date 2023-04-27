package integrationTests;

import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * The Interface AnAnnotation.
 */
@Target({FIELD, METHOD, TYPE, TYPE_USE})
@Retention(RUNTIME)
public @interface AnAnnotation
{
   
   /**
    * Integers.
    *
    * @return the int[]
    */
   int[] integers() default {};
   
   /**
    * Value.
    *
    * @return the string
    */
   String value() default "test";
}
