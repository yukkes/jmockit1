package mockit;

import org.junit.*;
import org.junit.rules.*;

import mockit.internal.expectations.invocation.*;

/**
 * The Class VerificationsInOrderTest.
 */
public final class VerificationsInOrderTest
{
   
   /** The thrown. */
   @Rule public final ExpectedException thrown = ExpectedException.none();

   /**
    * The Class Dependency.
    */
   @SuppressWarnings("unused")
   public static class Dependency {
      
      /**
       * Sets the something.
       *
       * @param value the new something
       */
      public void setSomething(int value) {}
      
      /**
       * Sets the something else.
       *
       * @param value the new something else
       */
      public void setSomethingElse(String value) {}
      
      /**
       * Edits the A bunch more stuff.
       */
      public void editABunchMoreStuff() {}
      
      /**
       * Notify before save.
       */
      public void notifyBeforeSave() {}
      
      /**
       * Prepare.
       */
      public void prepare() {}
      
      /**
       * Save.
       */
      public void save() {}
      
      /**
       * Do something.
       *
       * @param h the h
       */
      void doSomething(ClassWithHashCode h) {}
   }

   /**
    * The Class ClassWithHashCode.
    */
   static final class ClassWithHashCode {
      @Override
      public boolean equals(Object obj) { return obj instanceof ClassWithHashCode && this == obj; }

      @Override
      public int hashCode() { return 123; }
   }

   /** The mock. */
   @Mocked Dependency mock;

   /**
    * Exercise code under test.
    */
   void exerciseCodeUnderTest() {
      mock.prepare();
      mock.setSomething(123);
      mock.setSomethingElse("anotherValue");
      mock.setSomething(45);
      mock.editABunchMoreStuff();
      mock.notifyBeforeSave();
      mock.save();
   }

   /**
    * Verify simple invocations.
    */
   @Test
   public void verifySimpleInvocations() {
      exerciseCodeUnderTest();

      new VerificationsInOrder() {{
         mock.prepare();
         mock.setSomething(45);
         mock.editABunchMoreStuff();
      }};
   }

   /**
    * Verify unrecorded invocation that should happen but does not.
    */
   @Test
   public void verifyUnrecordedInvocationThatShouldHappenButDoesNot() {
      thrown.expect(MissingInvocation.class);

      mock.setSomething(1);

      new VerificationsInOrder() {{ mock.notifyBeforeSave(); }};
   }

   /**
    * Verify unrecorded invocation that should happen exactly once but does not.
    */
   @Test
   public void verifyUnrecordedInvocationThatShouldHappenExactlyOnceButDoesNot() {
      thrown.expect(MissingInvocation.class);
      thrown.expectMessage("2");

      mock.setSomething(1);

      new VerificationsInOrder() {{ mock.setSomething(2); times = 1; }};
   }

   /**
    * Verify recorded invocation that should happen but does not.
    */
   @Test
   public void verifyRecordedInvocationThatShouldHappenButDoesNot() {
      thrown.expect(MissingInvocation.class);

      new Expectations() {{
         mock.setSomething(1);
         mock.notifyBeforeSave();
      }};

      mock.setSomething(1);

      new VerificationsInOrder() {{
         mock.setSomething(1);
         mock.notifyBeforeSave();
      }};
   }

   /**
    * Verify all invocations with some of them recorded.
    */
   @Test
   public void verifyAllInvocationsWithSomeOfThemRecorded() {
      new Expectations() {{
         mock.prepare();
         mock.editABunchMoreStuff();
      }};

      exerciseCodeUnderTest();

      new VerificationsInOrder() {{
         mock.prepare(); minTimes = 1;
         mock.setSomethingElse(anyString);
         mock.setSomething(anyInt); minTimes = 1; maxTimes = 2;
         mock.editABunchMoreStuff();
         mock.notifyBeforeSave(); maxTimes = 1;
         mock.save(); times = 1;
      }};
   }

   /**
    * Verify invocations with exact invocation counts having recorded matching expectation with argument matcher.
    */
   @Test
   public void verifyInvocationsWithExactInvocationCountsHavingRecordedMatchingExpectationWithArgumentMatcher() {
      new Expectations() {{ mock.setSomething(anyInt); }};

      mock.setSomething(1);
      mock.setSomething(2);

      new VerificationsInOrder() {{
         mock.setSomething(1); times = 1;
         mock.setSomething(2); times = 1;
      }};
   }

   /**
    * Verify invocation that is allowed to happen any number of times and happens once.
    */
   @Test
   public void verifyInvocationThatIsAllowedToHappenAnyNumberOfTimesAndHappensOnce() {
      mock.prepare();
      mock.setSomething(123);
      mock.save();

      new VerificationsInOrder() {{
         mock.prepare();
         mock.setSomething(anyInt);
         mock.save();
      }};
   }

   /**
    * Verify simple invocations when out of order.
    */
   @Test
   public void verifySimpleInvocationsWhenOutOfOrder() {
      thrown.expect(MissingInvocation.class);
      thrown.expectMessage("123");

      mock.setSomething(123);
      mock.prepare();

      new VerificationsInOrder() {{
         mock.prepare();
         mock.setSomething(123);
      }};
   }

   /**
    * Verify repeating invocation.
    */
   @Test
   public void verifyRepeatingInvocation() {
      mock.setSomething(123);
      mock.setSomething(123);

      new VerificationsInOrder() {{ mock.setSomething(123); times = 2; }};
   }

   /**
    * Verify repeating invocation that occurs one time more than expected.
    */
   @Test
   public void verifyRepeatingInvocationThatOccursOneTimeMoreThanExpected() {
      thrown.expect(UnexpectedInvocation.class);

      mock.setSomething(123);
      mock.setSomething(123);

      new VerificationsInOrder() {{ mock.setSomething(123); maxTimes = 1; }};
   }

   /**
    * Verify repeating invocation using matcher.
    */
   @Test
   public void verifyRepeatingInvocationUsingMatcher() {
      mock.setSomething(123);
      mock.setSomething(45);

      new VerificationsInOrder() {{ mock.setSomething(anyInt); times = 2; }};
   }

   /**
    * Verify invocation not expected to occur but which does.
    */
   @Test
   public void verifyInvocationNotExpectedToOccurButWhichDoes() {
      thrown.expect(UnexpectedInvocation.class);
      thrown.expectMessage("123");

      mock.prepare();
      mock.setSomething(123);

      new VerificationsInOrder() {{
         mock.prepare();
         mock.setSomething(anyInt); maxTimes = 0;
      }};
   }

   /**
    * Verify with argument matcher.
    */
   @Test
   public void verifyWithArgumentMatcher() {
      exerciseCodeUnderTest();

      new VerificationsInOrder() {{
         mock.prepare();
         mock.setSomething(anyInt);
      }};
   }

   /**
    * Verify with individual invocation counts for non consecutive invocations.
    */
   @Test
   public void verifyWithIndividualInvocationCountsForNonConsecutiveInvocations() {
      exerciseCodeUnderTest();

      new VerificationsInOrder() {{
         mock.prepare(); times = 1;
         mock.setSomething(anyInt); times = 2;
      }};
   }

   /**
    * Verify using invocation count constraint and argument matcher on object with mocked hash code.
    *
    * @param wh the wh
    */
   @Test
   public void verifyUsingInvocationCountConstraintAndArgumentMatcherOnObjectWithMockedHashCode(@Mocked ClassWithHashCode wh) {
      mock.doSomething(null);
      mock.doSomething(wh);

      new VerificationsInOrder() {{
         mock.doSomething((ClassWithHashCode) withNull()); times = 1;
         mock.doSomething((ClassWithHashCode) withNotNull());
      }};
   }

   /**
    * Verify with argument matchers when out of order.
    */
   @Test
   public void verifyWithArgumentMatchersWhenOutOfOrder() {
      thrown.expect(MissingInvocation.class);
      thrown.expectMessage("any String");

      mock.setSomething(123);
      mock.setSomethingElse("anotherValue");
      mock.setSomething(45);

      new VerificationsInOrder() {{
         mock.setSomething(anyInt);
         mock.setSomething(anyInt);
         mock.setSomethingElse(anyString);
      }};
   }

   /**
    * Verify with argument matcher and individual invocation count when out of order.
    */
   @Test
   public void verifyWithArgumentMatcherAndIndividualInvocationCountWhenOutOfOrder() {
      thrown.expect(MissingInvocation.class);
      thrown.expectMessage("Missing 1 invocation");
      thrown.expectMessage("any int");

      mock.setSomething(123);
      mock.prepare();
      mock.setSomething(45);

      new VerificationsInOrder() {{
         mock.prepare();
         mock.setSomething(anyInt); times = 2;
      }};
   }

   /**
    * Verify two independent sequences of invocations which occur separately.
    */
   @Test
   public void verifyTwoIndependentSequencesOfInvocationsWhichOccurSeparately() {
      // First sequence:
      mock.setSomething(1);
      mock.setSomething(2);

      // Second sequence:
      mock.setSomething(10);
      mock.setSomething(20);

      // Verifies first sequence:
      new VerificationsInOrder() {{
         mock.setSomething(1);
         mock.setSomething(2);
      }};

      // Verifies second sequence:
      new VerificationsInOrder() {{
         mock.setSomething(10);
         mock.setSomething(20);
      }};
   }

   /**
    * Verify two independent sequences of invocations which are mixed together.
    */
   @Test
   public void verifyTwoIndependentSequencesOfInvocationsWhichAreMixedTogether() {
      mock.setSomething(1);  // first sequence
      mock.setSomething(10); // second sequence
      mock.setSomething(2);  // first sequence
      mock.setSomething(20); // second sequence

      // Verifies second sequence:
      new VerificationsInOrder() {{
         mock.setSomething(10);
         mock.setSomething(20);
      }};

      // Verifies first sequence:
      new VerificationsInOrder() {{
         mock.setSomething(1);
         mock.setSomething(2);
      }};
   }

   /**
    * Verify second sequence of invocations with times constraint after verifying last invocation of first sequence.
    */
   @Test
   public void verifySecondSequenceOfInvocationsWithTimesConstraintAfterVerifyingLastInvocationOfFirstSequence() {
      mock.setSomething(1); // first sequence
      mock.setSomething(3); // second sequence
      mock.setSomething(4); // second sequence
      mock.setSomething(2); // first sequence

      new VerificationsInOrder() {{
         mock.setSomething(1);
         mock.setSomething(2);
      }};

      new VerificationsInOrder() {{
         mock.setSomething(3);
         mock.setSomething(4); times = 1;
      }};
   }
}
