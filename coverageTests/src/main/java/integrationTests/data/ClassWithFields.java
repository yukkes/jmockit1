package integrationTests.data;

public class ClassWithFields {
    private static int static1;
    private static long static3;

    // Instance fields:
    // (coverage accounts for each owner instance)
    private boolean instance1 = true;
    private double instance3;

    public static int getStatic1() {
        return static1;
    }

    public static void setStatic1(int static1) {
        ClassWithFields.static1 = static1;
    }

    public static long getStatic3() {
        return static3;
    }

    public static void setStatic3(long static3) {
        ClassWithFields.static3 = static3;
    }

    /**
     * Indicates whether {@link #instance1} is <code>true</code> or <code>false</code>.
     *
     * @return true, if is instance 1
     */
    public boolean isInstance1() {
        return instance1;
    }

    public void setInstance1(boolean instance1) {
        this.instance1 = instance1;
    }

    public double getInstance3() {
        return instance3;
    }

    public void setInstance3(double instance3) {
        this.instance3 = instance3;
    }
}
