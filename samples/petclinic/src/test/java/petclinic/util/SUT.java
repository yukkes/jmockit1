package petclinic.util;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;

import mockit.*;

/**
 * Designates a field containing an object to be tested, which gets automatically created before any @Test method as
 * needed; any fields of the object for other dependencies to be injected are also automatically created or resolved.
 */
@Target(FIELD)
@Retention(RUNTIME)
@Tested(fullyInitialized = true)
public @interface SUT {
}
