package integrationTests;

/**
 * The Class ClassInRegularPackage.
 */
public class ClassInRegularPackage
{
   
   /** The Constant CONSTANT. */
   public static final int CONSTANT = 123;
   
   /**
    * The Enum NestedEnum.
    */
   public enum NestedEnum
   {
      
      /** The First. */
      First,
      
      /** The Second. */
      Second()
      {
         @Override
         public String toString() { return "2nd"; }
      };

      static
      {
         System.out.println("test");
      }
   }

   /**
    * Do something.
    *
    * @param value the value
    * @return true, if successful
    */
   public boolean doSomething(NestedEnum value)
   {
      switch (value) {
         case First:
            return true;

         case Second:
            value.toString();
            break;
      }

      return value.ordinal() == CONSTANT;
   }
}
