package mockit;

import javax.inject.*;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * The Class StandardDI2Test.
 */
public final class StandardDI2Test
{
   
   /**
    * The Class TestedClass.
    */
   static class TestedClass {
      
      /**
       * Instantiates a new tested class.
       */
      TestedClass() { throw new RuntimeException("Must not occur"); }
      
      /**
       * Instantiates a new tested class.
       *
       * @param action the action
       */
      @Inject TestedClass(Runnable action) {}
   }

   /** The tested. */
   @Tested TestedClass tested;

   /**
    * Attempt to create tested object through annotated constructor with missing injectables.
    */
   @Test(expected = IllegalArgumentException.class)
   public void attemptToCreateTestedObjectThroughAnnotatedConstructorWithMissingInjectables() {
      fail();
   }
}
