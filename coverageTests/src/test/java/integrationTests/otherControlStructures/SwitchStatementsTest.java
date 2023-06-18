package integrationTests.otherControlStructures;

import org.junit.*;

import integrationTests.*;

public final class SwitchStatementsTest extends CoverageTest {
    final SwitchStatements tested = new SwitchStatements();

    @Test
    public void switchStatementWithSparseCasesAndDefault() {
        tested.switchStatementWithSparseCasesAndDefault('A');
        tested.switchStatementWithSparseCasesAndDefault('\0');

        assertLines(5, 20, 5);
        assertLine(5, 1, 1, 2);
        assertLine(7, 1, 1, 1);
        assertLine(8, 1, 1, 1);
        assertLine(15, 1, 1, 1);
        assertLine(17, 1, 0, 0);
        assertLine(19, 1, 1, 1);
    }

    @Test
    public void switchStatementWithSparseCasesAndDefaultOnDefaultCase() {
        tested.anotherSwitchStatementWithSparseCasesAndDefault('x');

        // TODO: assertions
    }

    @Test
    public void switchStatementWithCompactCasesAndDefault() {
        tested.switchStatementWithCompactCasesAndDefault(2);
        tested.switchStatementWithCompactCasesAndDefault(4);

        // TODO: assertions
    }

    @Test
    public void switchStatementWithCompactCasesAndDefaultOnDefaultCase() {
        tested.anotherSwitchStatementWithCompactCasesAndDefault(1);
        tested.anotherSwitchStatementWithCompactCasesAndDefault(5);

        assertLines(56, 60, 4);
        assertLine(56, 1, 1, 2);
        assertLine(57, 1, 1, 1);
        assertLine(58, 1, 1, 1);
        assertLine(60, 1, 1, 2);
    }

    @Test
    public void switchStatementWithSparseCasesAndNoDefault() {
        tested.switchStatementWithSparseCasesAndNoDefault('f');
        tested.switchStatementWithSparseCasesAndNoDefault('b');

        assertLines(64, 72, 3);
        assertLine(64, 1, 1, 2);
        assertLine(66, 1, 0, 0);
        assertLine(67, 1, 0, 0);
        assertLine(69, 1, 1, 1);
        assertLine(70, 0, 0, 0);
        assertLine(72, 1, 1, 2);
    }

    @Test
    public void switchStatementWithCompactCasesAndNoDefault() {
        tested.switchStatementWithCompactCasesAndNoDefault(0);
        tested.switchStatementWithCompactCasesAndNoDefault(4);
        tested.switchStatementWithCompactCasesAndNoDefault(5);

        assertLines(76, 84, 4);
        assertLine(76, 1, 1, 3);
        assertLine(78, 1, 1, 3);
        assertLine(79, 1, 0, 0);
        assertLine(80, 1, 0, 0);
        assertLine(81, 1, 1, 1);
        assertLine(84, 1, 1, 3);
    }

    @Test
    public void switchStatementWithExitInAllCases() {
        tested.switchStatementWithExitInAllCases(1);
        tested.switchStatementWithExitInAllCases(2);

        assertLines(89, 92, 3);
        assertLine(89, 1, 1, 2);
        assertLine(90, 1, 1, 1);
        assertLine(91, 1, 1, 1);
        assertLine(92, 1, 0, 0);
    }

    @Test
    public void switchOnString() {
        tested.switchOnString("A", true);
        tested.switchOnString("M", false);

        assertLines(98, 101, 3);
        assertLine(98, 1, 1, 2);
        assertLine(99, 1, 1, 1);
        // assertLine(101, 3, 2, 1, 0, 1);
    }
}
