package mockit;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.runners.MethodSorters.*;

/**
 * The Class TestedClassInGlobalScopeTest.
 */
@FixMethodOrder(NAME_ASCENDING)
public final class TestedClassInGlobalScopeTest
{
   
   /**
    * The Class TestedClass.
    */
   static class TestedClass { 
 /** The some value. */
 Integer someValue; }

   /** The tested global. */
   @Tested(fullyInitialized = true, global = true) TestedClass testedGlobal;
   
   /** The tested local. */
   @Tested(fullyInitialized = true) TestedClass testedLocal;

   /**
    * Use tested object in first step of tested scenario.
    */
   @Test
   public void useTestedObjectInFirstStepOfTestedScenario() {
      assertNull(testedGlobal.someValue);
      assertNotSame(testedGlobal, testedLocal);
      testedGlobal.someValue = 123;
   }

   /**
    * Use tested object in second step of tested scenario.
    */
   @Test
   public void useTestedObjectInSecondStepOfTestedScenario() {
      assertNotNull(testedGlobal.someValue);
      assertNull(testedLocal.someValue);
   }
}
