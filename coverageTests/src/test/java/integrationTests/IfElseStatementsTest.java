package integrationTests;

import static org.junit.Assert.*;

import org.junit.*;

public final class IfElseStatementsTest extends CoverageTest {
    IfElseStatements tested;

    @Test
    public void simpleIf() {
        tested.simpleIf(true);
        tested.simpleIf(false);

        assertLines(16, 20, 4);
        assertLine(16, 1, 1, 2);
        assertLine(17, 1, 1, 1);
        assertLine(20, 1, 1, 2);

        assertBranchingPoints(16, 2, 1); // TODO: should be 2 covered, not 1
        assertBranchingPoints(17, 0, 0);
    }

    @Test
    public void ifAndElse() {
        tested.ifAndElse(true);
        tested.ifAndElse(false);
    }

    @Test
    public void singleLineIf() {
        tested.singleLineIf(true);
        tested.singleLineIf(false);

        assertLines(43, 46, 2);
        assertLine(44, 2, 2, 2, 1);
        assertLine(46, 1, 1, 2);
    }

    @Test
    public void singleLineIfAndElse() {
        tested.singleLineIfAndElse(true);
        tested.singleLineIfAndElse(false);

        assertLines(55, 58, 2);
        assertLine(56, 3, 3, 2, 1, 1);
        assertLine(58, 1, 1, 2);
    }

    @Test
    public void singleLineIfAndElseWhereOnlyTheElseIsExecuted() {
        tested.anotherSingleLineIfAndElse(false);

        assertLines(248, 248, 1);
        assertLine(248, 3, 2, 1, 0, 1);
    }

    @Test
    public void singleLineIfAndElseWhereElseIsExecutedMoreTimes() {
        tested.yetAnotherSingleLineIfAndElse(false);
        tested.yetAnotherSingleLineIfAndElse(true);
        tested.yetAnotherSingleLineIfAndElse(false);

        assertLines(262, 262, 1);
        assertLine(262, 3, 3, 3, 1, 2);
    }

    @Test
    public void ifWithBooleanAndOperator() {
        tested.ifWithBooleanAndOperator(true, false);
        tested.ifWithBooleanAndOperator(false, true);

        assertLines(275, 278, 2);
        assertLine(275, 2, 2, 2, 1);
        assertLine(276, 1, 0, 0);
        assertLine(278, 1, 1, 2);
    }

    @Test
    @Ignore
    public void anotherIfWithBooleanAndOperator() {
        tested.anotherIfWithBooleanAndOperator(true, true);
        tested.anotherIfWithBooleanAndOperator(true, false);

        assertLines(172, 175, 3);
        assertLine(172, 3, 2, 2, 2, 0);
        assertLine(173, 1, 1, 1);
        assertLine(175, 1, 1, 2);
    }

    @Test
    public void ifWithBooleanOrOperator() {
        tested.ifWithBooleanOrOperator(false, false);
        tested.ifWithBooleanOrOperator(true, true);

        assertLines(289, 292, 3);
        assertLine(289, 2, 2, 2, 1);
        assertLine(290, 1, 1, 1);
        assertLine(292, 1, 1, 2);
    }

    @Test
    public void methodWithFourDifferentPathsAndSimpleLines_exerciseTwoOppositePaths() {
        tested.methodWithFourDifferentPathsAndSimpleLines(true, 0);
        tested.methodWithFourDifferentPathsAndSimpleLines(false, 1);

        // TODO: assertions
    }

    @Test
    public void methodWithFourDifferentPathsAndSegmentedLines_exerciseTwoOppositePaths() {
        tested.methodWithFourDifferentPathsAndSegmentedLines(false, -1);
        tested.methodWithFourDifferentPathsAndSegmentedLines(true, 1);

        // TODO: assertions
    }

    @Test
    public void ifElseWithComplexBooleanCondition() {
        tested.ifElseWithComplexBooleanCondition(true, false);

        // TODO: assertions
    }

    @Test
    public void returnInput() {
        assertEquals(2, tested.returnInput(1, true, false, false));
        assertEquals(2, tested.returnInput(2, false, false, false));
        assertEquals(2, tested.returnInput(3, false, true, false));
        assertEquals(4, tested.returnInput(4, false, false, true));
        assertEquals(5, tested.returnInput(5, true, true, false));
        assertEquals(5, tested.returnInput(6, false, true, true));
        assertEquals(7, tested.returnInput(7, true, true, true));
        assertEquals(9, tested.returnInput(8, true, false, true));
    }

    @Test
    public void nestedIf() {
        assertEquals(1, tested.nestedIf(false, false));
        assertEquals(2, tested.nestedIf(true, true));

        // TODO: assertions
    }

    @Test
    public void ifElseWithNestedIf() {
        assertEquals(1, tested.ifElseWithNestedIf(true, false));
        assertEquals(2, tested.ifElseWithNestedIf(true, true));
        assertEquals(3, tested.ifElseWithNestedIf(false, false));

        // TODO: assertions
    }

    @Test
    public void nestedIfElse() {
        assertEquals(1, tested.nestedIfElse(false, false));
        assertEquals(2, tested.nestedIfElse(true, true));
        assertEquals(3, tested.nestedIfElse(true, false));
        assertEquals(4, tested.nestedIfElse(false, true));

        // TODO: assertions
    }

    @Test
    public void infeasiblePaths() {
        tested.infeasiblePaths(true);
        tested.infeasiblePaths(false);

        // TODO: assertions
    }

    @Test
    public void ifSpanningMultipleLines() {
        tested.ifSpanningMultipleLines(true, 0);
        tested.ifSpanningMultipleLines(false, -1);
        tested.ifSpanningMultipleLines(false, 1);

        assertLine(319, 1, 1, 3);
        assertLine(320, 1, 1, 2);
        assertLine(322, 1, 1, 2);
        assertLine(325, 1, 1, 3);
    }
}
