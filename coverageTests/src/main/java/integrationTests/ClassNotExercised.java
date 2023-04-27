package integrationTests;

/**
 * The Class ClassNotExercised.
 */
public final class ClassNotExercised {

    /**
     * Do something.
     *
     * @param i
     *            the i
     * @param s
     *            the s
     *
     * @return true, if successful
     */
    public boolean doSomething(int i, String s) {
        if (i > 0) {
            System.out.println(s);
        }

        return s.length() > 0;
    }
}
