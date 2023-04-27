package integrationTests;

import java.util.*;

/**
 * The Interface InterfaceWithExecutableCode.
 */
public interface InterfaceWithExecutableCode
{
   
   /** The n. */
   int N = 1 + new Random().nextInt(10);

   /**
    * Do something.
    */
   void doSomething();
}
