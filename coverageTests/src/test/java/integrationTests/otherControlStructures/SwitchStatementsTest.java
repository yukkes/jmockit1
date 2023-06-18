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

        assertLines(49, 55, 4);
        assertLine(50, 1, 1, 2);
        assertLine(51, 1, 1, 1);
        assertLine(52, 1, 1, 1);
        assertLine(55, 1, 1, 2);
    }

    @Test
    public void switchStatementWithSparseCasesAndNoDefault() {
        tested.switchStatementWithSparseCasesAndNoDefault('f');
        tested.switchStatementWithSparseCasesAndNoDefault('b');

        assertLines(58, 66, 3);
        assertLine(58, 1, 1, 2);
        assertLine(60, 1, 0, 0);
        assertLine(61, 1, 0, 0);
        assertLine(63, 1, 1, 1);
        assertLine(64, 0, 0, 0);
        assertLine(66, 1, 1, 2);
    }

    @Test
    public void switchStatementWithCompactCasesAndNoDefault() {
        tested.switchStatementWithCompactCasesAndNoDefault(0);
        tested.switchStatementWithCompactCasesAndNoDefault(4);
        tested.switchStatementWithCompactCasesAndNoDefault(5);

        assertLines(69, 79, 4);
        assertLine(69, 1, 1, 3);
        assertLine(72, 1, 1, 3);
        assertLine(73, 1, 0, 0);
        assertLine(74, 1, 0, 0);
        assertLine(75, 1, 1, 1);
        assertLine(79, 1, 1, 3);
    }

    @Test
    public void switchStatementWithExitInAllCases() {
        tested.switchStatementWithExitInAllCases(1);
        tested.switchStatementWithExitInAllCases(2);

        assertLines(83, 89, 3);
        assertLine(83, 1, 1, 2);
        assertLine(85, 1, 1, 1);
        assertLine(87, 1, 1, 1);
        assertLine(89, 1, 0, 0);
    }

    @Test
    public void switchOnString() {
        tested.switchOnString("A", true);
        tested.switchOnString("M", false);

        assertLines(94, 98, 3);
        assertLine(94, 1, 1, 2);
        assertLine(98, 1, 1, 1);
    }
}
