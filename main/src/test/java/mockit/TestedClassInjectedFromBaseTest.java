package mockit;

import org.junit.*;
import static org.junit.Assert.*;

class BaseTestClass {
   static final class Dependency {}
   @Tested final Dependency dependency = new Dependency();
}

/**
 * The Class TestedClassInjectedFromBaseTest.
 */
public final class TestedClassInjectedFromBaseTest extends BaseTestClass
{
   
   /**
    * The Class TestedClass.
    */
   static final class TestedClass { 
 /** The dependency. */
 Dependency dependency; }

   /** The tested. */
   @Tested(fullyInitialized = true) TestedClass tested;

   /**
    * Verify tested object injected with tested dependency provided by base test class.
    */
   @Test
   public void verifyTestedObjectInjectedWithTestedDependencyProvidedByBaseTestClass() {
      assertSame(dependency, tested.dependency);
   }
}
