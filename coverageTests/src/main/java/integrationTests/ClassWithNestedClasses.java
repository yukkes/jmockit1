package integrationTests;

/**
 * The Class ClassWithNestedClasses.
 */
public class ClassWithNestedClasses
{
   
   /**
    * The Class NestedClass.
    */
   public static class NestedClass
   {
      
      /** The i. */
      private final int i;

      /**
       * Instantiates a new nested class.
       */
      public NestedClass()
      {
         i = 123;
      }

      /**
       * The Class DeeplyNestedClass.
       */
      private static final class DeeplyNestedClass
      {
         
         /**
          * Prints the.
          *
          * @param text the text
          */
         void print(String text) { System.out.println(text); }
      }

      /**
       * The Class InnerClass.
       */
      private final class InnerClass
      {
         
         /**
          * Prints the.
          *
          * @param text the text
          */
         void print(String text) { System.out.println(text + ": " + i); }
      }
   }

   /**
    * Do something.
    */
   public static void doSomething()
   {
      new NestedClass.DeeplyNestedClass().print("test");

      // Just so we have two paths:
      if (System.out != null) {
         System.out.println("Test");
      }
   }

   /**
    * Method containing anonymous class.
    *
    * @param i the i
    * @return true, if successful
    */
   public static boolean methodContainingAnonymousClass(int i)
   {
      new Cloneable() {};
      return i > 0;
   }
}
