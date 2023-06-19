package integrationTests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public final class BooleanExpressionsTest extends CoverageTest {
    BooleanExpressions tested;

    @Test
    public void evalBuggyCombination() {
        // Only this combination will fail if the third condition in eval1 is changed to "z < 0",
        // which demonstrates that a more sophisticated metric than branch coverage is needed.
        assertTrue(tested.eval1(true, false, 1));

        // assertLine(7, 3, 3, 1, 1, 1);
    }

    @Test // includes executions from the previous test
    public void evalOnlySomeCombinations() {
        assertTrue(tested.eval1(true, true, 0));
        assertFalse(tested.eval1(true, false, 0));

        // assertLine(7, 3, 3, 3, 3, 2);
    }

    @Test
    public void evalAllCombinations() {
        assertTrue(tested.eval2(true, true, 0));
        assertTrue(tested.eval2(true, false, 1));
        assertFalse(tested.eval2(true, false, 0));
        assertFalse(tested.eval2(false, true, 0));

        // assertLine(12, 3, 3, 4, 3, 2);
    }

    @Test
    public void evalAllPaths() {
        assertFalse(tested.eval3(false, true, false));
        assertTrue(tested.eval3(true, true, false));
        assertTrue(tested.eval3(true, false, true));
        assertFalse(tested.eval3(true, false, false));

        // assertLine(17, 3, 3, 4, 3, 2);
    }

    @Test
    public void evalOnlyFirstAndSecondBranches() {
        assertFalse(tested.eval4(false, true, false));
        assertFalse(tested.eval4(false, false, false));
        assertFalse(tested.eval4(false, true, true));
        assertFalse(tested.eval4(false, false, true));
        assertTrue(tested.eval4(true, false, false));
        assertTrue(tested.eval4(true, false, true));

        // assertLine(22, 3, 2, 6, 2, 0);
    }

    @Test
    public void eval5() {
        assertFalse(tested.eval5(false, true, true));
        assertTrue(tested.eval5(false, false, false));

        // assertLine(30, 1, 1, 1);
    }

    @Test
    public void methodWithComplexExpressionWhichCallsAnotherInSameClass() {
        BooleanExpressions.isSameTypeIgnoringAutoBoxing(int.class, Integer.class);

        // TODO: assertions
    }

    @Test
    public void trivialMethodWhichReturnsBooleanInput() {
        assertTrue(tested.simplyReturnsInput(true));
        assertFalse(tested.simplyReturnsInput(false));

        assertLine(137, 1, 1, 2);
    }

    @Test
    public void methodWhichReturnsNegatedBoolean() {
        assertTrue(tested.returnsNegatedInput(false));

        // assertLine(58, 1, 1, 1);
    }

    @Test
    public void methodWithIfElseAndTrivialTernaryOperator() {
        assertTrue(tested.returnsTrivialResultFromInputAfterIfElse(false, 1));
        assertFalse(tested.returnsTrivialResultFromInputAfterIfElse(true, 0));
    }

    @Test
    public void methodWithTrivialTernaryOperatorAndTrivialIfElse() {
        assertTrue(tested.returnsResultPreviouslyComputedFromInput(false, 1));
        assertFalse(tested.returnsResultPreviouslyComputedFromInput(false, 0));
        assertTrue(tested.returnsResultPreviouslyComputedFromInput(true, 1));
        assertTrue(tested.returnsResultPreviouslyComputedFromInput(true, -1));

        assertLines(185, 195, 6);
        assertLine(185, 3, 3, 4, 2, 2);
        assertLine(188, 1, 1, 4, 3);
        assertLine(191, 1, 1, 1);
        assertLine(192, 1, 1, 1);
        assertLine(195, 1, 1, 4);
    }
}
