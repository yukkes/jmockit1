package mockit;

import java.util.*;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;
import org.junit.runners.Parameterized.*;
import static org.junit.Assert.*;

import mockit.integration.*;

import otherTests.*;

/**
 * The Class JUnit4ParametersTest.
 */
@RunWith(Parameterized.class)
public final class JUnit4ParametersTest
{
   
   /**
    * Parameters.
    *
    * @return the list
    */
   @Parameters(name = "Input squared: {0} -> {1}")
   public static List<Integer[]> parameters() {
      Integer[][] data = {{1, 1}, {2, 4}, {3, 9}};
      return Arrays.asList(data);
   }

   /** The input. */
   final int input;
   
   /** The expected. */
   final int expected;
   
   /** The cut. */
   @Tested TestedClass cut;
   
   /** The dependency. */
   @Injectable MockedClass dependency;

   /**
    * Instantiates a new j unit 4 parameters test.
    *
    * @param input the input
    * @param expected the expected
    */
   public JUnit4ParametersTest(int input, int expected) {
      this.input = input;
      this.expected = expected;
   }

   /**
    * Use parameters.
    *
    * @param mock the mock
    */
   @Test
   public void useParameters(@Mocked final Runnable mock) {
      new Expectations() {{ dependency.doSomething(anyInt); result = true; }};

      mock.run();
      boolean didSomething = cut.doSomething(input);

      assertTrue(didSomething);

      int result = input * input;
      assertEquals(expected, result);

      new Verifications() {{ mock.run(); times = 1; }};
   }
}
