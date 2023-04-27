package integrationTests;

/**
 * The Class MultiThreadedCode.
 */
public final class MultiThreadedCode
{
   
   /**
    * Non blocking operation.
    *
    * @return the thread
    */
   public static Thread nonBlockingOperation()
   {
      Thread worker = new Thread(new Runnable()
      {
         @Override
         public void run()
         {
            new Object() // NPE only happened with this line break
            {};
         }
      });

      worker.start();
      return worker;
   }
}
