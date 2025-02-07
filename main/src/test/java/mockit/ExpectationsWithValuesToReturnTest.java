package mockit;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.concurrent.Callable;

import org.junit.Test;

/**
 * The Class ExpectationsWithValuesToReturnTest.
 */
public final class ExpectationsWithValuesToReturnTest {

    /**
     * The Class Collaborator.
     */
    static class Collaborator {

        /**
         * Do internal.
         *
         * @return the string
         */
        static String doInternal() {
            return "123";
        }

        /**
         * Gets the value.
         *
         * @return the value
         */
        int getValue() {
            return -1;
        }

        /**
         * Gets the integer.
         *
         * @return the integer
         */
        Integer getInteger() {
            return -1;
        }

        /**
         * Gets the byte value.
         *
         * @return the byte value
         */
        byte getByteValue() {
            return -1;
        }

        /**
         * Gets the byte wrapper.
         *
         * @return the byte wrapper
         */
        Byte getByteWrapper() {
            return -1;
        }

        /**
         * Gets the short value.
         *
         * @return the short value
         */
        short getShortValue() {
            return -1;
        }

        /**
         * Gets the short wrapper.
         *
         * @return the short wrapper
         */
        Short getShortWrapper() {
            return -1;
        }

        /**
         * Gets the long value.
         *
         * @return the long value
         */
        long getLongValue() {
            return -1;
        }

        /**
         * Gets the long wrapper.
         *
         * @return the long wrapper
         */
        Long getLongWrapper() {
            return -1L;
        }

        /**
         * Gets the float value.
         *
         * @return the float value
         */
        float getFloatValue() {
            return -1.0F;
        }

        /**
         * Gets the float wrapper.
         *
         * @return the float wrapper
         */
        Float getFloatWrapper() {
            return -1.0F;
        }

        /**
         * Gets the double value.
         *
         * @return the double value
         */
        double getDoubleValue() {
            return -1.0;
        }

        /**
         * Gets the double wrapper.
         *
         * @return the double wrapper
         */
        Double getDoubleWrapper() {
            return -1.0;
        }

        /**
         * Gets the char value.
         *
         * @return the char value
         */
        char getCharValue() {
            return '1';
        }

        /**
         * Gets the character.
         *
         * @return the character
         */
        Character getCharacter() {
            return '1';
        }

        /**
         * Gets the boolean value.
         *
         * @return the boolean value
         */
        boolean getBooleanValue() {
            return true;
        }

        /**
         * Gets the boolean wrapper.
         *
         * @return the boolean wrapper
         */
        Boolean getBooleanWrapper() {
            return true;
        }

        /**
         * Gets the string.
         *
         * @return the string
         */
        String getString() {
            return "";
        }

        /**
         * Gets the object.
         *
         * @return the object
         */
        Object getObject() {
            return null;
        }

        /**
         * Gets the items.
         *
         * @return the items
         */
        Collection<?> getItems() {
            return null;
        }

        /**
         * Gets the list items.
         *
         * @return the list items
         */
        List<?> getListItems() {
            return null;
        }

        /**
         * Gets the a list.
         *
         * @return the a list
         */
        List<?> getAList() {
            return null;
        }

        /**
         * Gets the sets the items.
         *
         * @return the sets the items
         */
        Set<?> getSetItems() {
            return null;
        }

        /**
         * Gets the sorted set items.
         *
         * @return the sorted set items
         */
        SortedSet<?> getSortedSetItems() {
            return null;
        }

        /**
         * Gets the map items.
         *
         * @return the map items
         */
        Map<?, ?> getMapItems() {
            return null;
        }

        /**
         * Gets the sorted map items.
         *
         * @return the sorted map items
         */
        SortedMap<?, ?> getSortedMapItems() {
            return null;
        }

        /**
         * Gets the list iterator.
         *
         * @return the list iterator
         */
        ListIterator<?> getListIterator() {
            return null;
        }

        /**
         * Gets the iterator.
         *
         * @return the iterator
         */
        Iterator<?> getIterator() {
            return null;
        }

        /**
         * Gets the iterable.
         *
         * @return the iterable
         */
        Iterable<?> getIterable() {
            return null;
        }

        /**
         * Gets the int array.
         *
         * @return the int array
         */
        int[] getIntArray() {
            return null;
        }

        /**
         * Gets the int 2 array.
         *
         * @return the int 2 array
         */
        int[][] getInt2Array() {
            return null;
        }

        /**
         * Gets the byte array.
         *
         * @return the byte array
         */
        byte[] getByteArray() {
            return null;
        }

        /**
         * Gets the byte wrapper array.
         *
         * @return the byte wrapper array
         */
        Byte[] getByteWrapperArray() {
            return null;
        }

        /**
         * Gets the short array.
         *
         * @return the short array
         */
        short[] getShortArray() {
            return null;
        }

        /**
         * Gets the short wrapper array.
         *
         * @return the short wrapper array
         */
        Short[] getShortWrapperArray() {
            return null;
        }

        /**
         * Gets the long array.
         *
         * @return the long array
         */
        long[] getLongArray() {
            return null;
        }

        /**
         * Gets the long 2 array.
         *
         * @return the long 2 array
         */
        long[][] getLong2Array() {
            return null;
        }

        /**
         * Gets the float array.
         *
         * @return the float array
         */
        float[] getFloatArray() {
            return null;
        }

        /**
         * Gets the double array.
         *
         * @return the double array
         */
        double[] getDoubleArray() {
            return null;
        }

        /**
         * Gets the char array.
         *
         * @return the char array
         */
        char[] getCharArray() {
            return null;
        }

        /**
         * Gets the boolean array.
         *
         * @return the boolean array
         */
        boolean[] getBooleanArray() {
            return null;
        }

        /**
         * Gets the string array.
         *
         * @return the string array
         */
        String[] getStringArray() {
            return null;
        }

        /**
         * Gets the string 2 array.
         *
         * @return the string 2 array
         */
        String[][] getString2Array() {
            return null;
        }
    }

    /**
     * Returns default values for primitive and wrapper return types.
     *
     * @param mock
     *            the mock
     */
    @Test
    public void returnsDefaultValuesForPrimitiveAndWrapperReturnTypes(@Mocked final Collaborator mock) {
        new Expectations() {
            {
                mock.getValue();
                mock.getInteger();
                mock.getByteValue();
                mock.getByteWrapper();
                mock.getShortValue();
                mock.getShortWrapper();
                mock.getLongValue();
                mock.getLongWrapper();
                mock.getFloatValue();
                mock.getFloatWrapper();
                mock.getDoubleValue();
                mock.getDoubleWrapper();
                mock.getCharValue();
                mock.getCharacter();
                mock.getBooleanValue();
                mock.getBooleanWrapper();
                Collaborator.doInternal();
            }
        };

        assertEquals(0, mock.getValue());
        assertEquals(0, mock.getInteger().intValue());
        assertEquals((byte) 0, mock.getByteValue());
        assertEquals((byte) 0, mock.getByteWrapper().byteValue());
        assertEquals((short) 0, mock.getShortValue());
        assertEquals((short) 0, mock.getShortWrapper().shortValue());
        assertEquals(0L, mock.getLongValue());
        assertEquals(0L, mock.getLongWrapper().longValue());
        assertEquals(0.0F, mock.getFloatValue(), 0.0);
        assertEquals(0, mock.getFloatWrapper(), 0);
        assertEquals(0.0, mock.getDoubleValue(), 0.0);
        assertEquals(0, mock.getDoubleWrapper(), 0);
        assertEquals('\0', mock.getCharValue());
        assertEquals('\0', mock.getCharacter().charValue());
        assertFalse(mock.getBooleanValue());
        assertFalse(mock.getBooleanWrapper());
        assertNull(Collaborator.doInternal());
    }

    /**
     * Returns default values for collection valued return types.
     *
     * @param mock
     *            the mock
     */
    @Test
    public void returnsDefaultValuesForCollectionValuedReturnTypes(@Mocked final Collaborator mock) {
        new Expectations() {
            {
                mock.getItems();
                mock.getListItems();
                mock.getSetItems();
                mock.getSortedSetItems();
                mock.getMapItems();
                mock.getSortedMapItems();
            }
        };

        assertSame(Collections.emptyList(), mock.getItems());
        assertSame(Collections.emptyList(), mock.getListItems());
        assertSame(Collections.emptySet(), mock.getSetItems());
        assertEquals(Collections.emptySet(), mock.getSortedSetItems());
        assertSame(Collections.emptyMap(), mock.getMapItems());
        assertEquals(Collections.emptyMap(), mock.getSortedMapItems());
    }

    /**
     * Returns default values for array valued return types.
     *
     * @param mock
     *            the mock
     */
    @Test
    public void returnsDefaultValuesForArrayValuedReturnTypes(@Mocked final Collaborator mock) {
        new Expectations() {
            {
                mock.getIntArray();
                mock.getInt2Array();
                mock.getByteArray();
                mock.getShortArray();
                mock.getShortWrapperArray();
                mock.getLongArray();
                mock.getLong2Array();
                mock.getFloatArray();
                mock.getDoubleArray();
                mock.getCharArray();
                mock.getBooleanArray();
                mock.getStringArray();
                mock.getString2Array();
            }
        };

        assertArrayEquals(new int[0], mock.getIntArray());
        assertArrayEquals(new int[0][0], mock.getInt2Array());
        assertArrayEquals(new byte[0], mock.getByteArray());
        assertArrayEquals(new short[0], mock.getShortArray());
        assertArrayEquals(new Short[0], mock.getShortWrapperArray());
        assertArrayEquals(new long[0], mock.getLongArray());
        assertArrayEquals(new long[0][0], mock.getLong2Array());
        assertArrayEquals(new float[0], mock.getFloatArray(), 0.0F);
        assertArrayEquals(new double[0], mock.getDoubleArray(), 0.0);
        assertArrayEquals(new char[0], mock.getCharArray());
        assertEquals(0, mock.getBooleanArray().length);
        assertArrayEquals(new String[0], mock.getStringArray());
        assertArrayEquals(new String[0][0], mock.getString2Array());
    }

    /**
     * Returns multiple values in sequence using varargs.
     */
    @Test
    public void returnsMultipleValuesInSequenceUsingVarargs() {
        final Collaborator collaborator = new Collaborator();

        new Expectations(collaborator) {
            {
                collaborator.getBooleanValue();
                returns(true, false);
                collaborator.getShortValue();
                returns((short) 1, (short) 2, (short) 3);
                collaborator.getShortWrapper();
                returns((short) 5, (short) 6, (short) -7, (short) -8);
                collaborator.getCharArray();
                returns(new char[0], new char[] { 'x' });
            }
        };

        assertTrue(collaborator.getBooleanValue());
        assertFalse(collaborator.getBooleanValue());

        assertEquals(1, collaborator.getShortValue());
        assertEquals(2, collaborator.getShortValue());
        assertEquals(3, collaborator.getShortValue());

        assertEquals(5, collaborator.getShortWrapper().shortValue());
        assertEquals(6, collaborator.getShortWrapper().shortValue());
        assertEquals(-7, collaborator.getShortWrapper().shortValue());
        assertEquals(-8, collaborator.getShortWrapper().shortValue());

        assertArrayEquals(new char[0], collaborator.getCharArray());
        assertArrayEquals(new char[] { 'x' }, collaborator.getCharArray());
    }

    /**
     * Returns non null value followed by null using varargs.
     *
     * @param collaborator
     *            the collaborator
     */
    @Test
    public void returnsNonNullValueFollowedByNullUsingVarargs(@Mocked final Collaborator collaborator) {
        new Expectations() {
            {
                collaborator.getString();
                // noinspection NullArgumentToVariableArgMethod
                returns("non-null", null);
            }
        };

        assertEquals("non-null", collaborator.getString());
        assertNull(collaborator.getString());
        assertNull(collaborator.getString());
    }

    /**
     * Returns multiple values from method with return type of object.
     *
     * @param collaborator
     *            the collaborator
     */
    @Test
    public void returnsMultipleValuesFromMethodWithReturnTypeOfObject(@Mocked final Collaborator collaborator) {
        new Expectations() {
            {
                collaborator.getObject();
                returns(1, 2);
                returns("test", 'X');
            }
        };

        assertEquals(1, collaborator.getObject());
        assertEquals(2, collaborator.getObject());
        assertEquals("test", collaborator.getObject());
        assertEquals('X', collaborator.getObject());
    }

    /**
     * Returns multiple values from generic method.
     *
     * @param callable
     *            the callable
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void returnsMultipleValuesFromGenericMethod(@Mocked final Callable<Integer> callable) throws Exception {
        new Expectations() {
            {
                callable.call();
                returns(3, 2, 1);
            }
        };

        assertEquals(3, callable.call().intValue());
        assertEquals(2, callable.call().intValue());
        assertEquals(1, callable.call().intValue());
    }

    /**
     * Record results for collection and list returning methods using varargs.
     *
     * @param mock
     *            the mock
     */
    @Test
    public void recordResultsForCollectionAndListReturningMethodsUsingVarargs(@Mocked final Collaborator mock) {
        new Expectations() {
            {
                mock.getItems();
                returns(1, "2", 3.0);
                mock.getListItems();
                returns("a", true);
            }
        };

        Collaborator collaborator = new Collaborator();
        assertArrayEquals(new Object[] { 1, "2", 3.0 }, collaborator.getItems().toArray());
        assertArrayEquals(new Object[] { "a", true }, collaborator.getListItems().toArray());
    }

    /**
     * Record results for iterable returning method using varargs.
     *
     * @param mock
     *            the mock
     */
    @Test
    public void recordResultsForIterableReturningMethodUsingVarargs(@Mocked final Collaborator mock) {
        new Expectations() {
            {
                mock.getIterable();
                returns(true, "Xyz", 3.6);
            }
        };

        int i = 0;
        Object[] expectedValues = { true, "Xyz", 3.6 };

        for (Object value : mock.getIterable()) {
            assertEquals(expectedValues[i], value);
            i++;
        }
    }

    /**
     * Record results for iterator returning method using varargs.
     *
     * @param mock
     *            the mock
     */
    @Test
    public void recordResultsForIteratorReturningMethodUsingVarargs(@Mocked final Collaborator mock) {
        new Expectations() {
            {
                mock.getIterator();
                returns("ab", "cde", 1, 3);
            }
        };

        Iterator<?> itr = new Collaborator().getIterator();
        assertEquals("ab", itr.next());
        assertEquals("cde", itr.next());
        assertEquals(1, itr.next());
        assertEquals(3, itr.next());
    }

    /**
     * Record results for set returning method using varargs.
     *
     * @param mock
     *            the mock
     */
    @Test
    public void recordResultsForSetReturningMethodUsingVarargs(@Mocked final Collaborator mock) {
        new Expectations() {
            {
                mock.getSetItems();
                returns(4.0, "aB", 2);
                mock.getSortedSetItems();
                returns(1, 5, 123);
            }
        };

        assertArrayEquals(new Object[] { 4.0, "aB", 2 }, mock.getSetItems().toArray());
        assertArrayEquals(new Object[] { 1, 5, 123 }, mock.getSortedSetItems().toArray());
    }

    /**
     * Record results for array returning methods using varargs.
     */
    @Test
    public void recordResultsForArrayReturningMethodsUsingVarargs() {
        final Collaborator collaborator = new Collaborator();

        new Expectations(collaborator) {
            {
                collaborator.getIntArray();
                returns(1, 2, 3, 4);
                collaborator.getLongArray();
                returns(1023, 20234L, 354);
                collaborator.getByteArray();
                returns(0, -4, 5);
                collaborator.getByteWrapperArray();
                returns(0, -4, 5);
                collaborator.getCharArray();
                returns('a', 'B');
                collaborator.getShortArray();
                returns(-1, 3, 0);
                collaborator.getShortWrapperArray();
                returns(-1, 3, 0);
                collaborator.getFloatArray();
                returns(-0.1F, 5.6F, 7);
                collaborator.getDoubleArray();
                returns(4.1, 15, -7.0E2);
                collaborator.getStringArray();
                returns("aX", null, "B2 m");
            }
        };

        assertArrayEquals(new int[] { 1, 2, 3, 4 }, collaborator.getIntArray());
        assertArrayEquals(new long[] { 1023, 20234, 354 }, collaborator.getLongArray());
        assertArrayEquals(new byte[] { 0, -4, 5 }, collaborator.getByteArray());
        assertArrayEquals(new Byte[] { 0, -4, 5 }, collaborator.getByteWrapperArray());
        assertArrayEquals(new char[] { 'a', 'B' }, collaborator.getCharArray());
        assertArrayEquals(new short[] { -1, 3, 0 }, collaborator.getShortArray());
        assertArrayEquals(new Short[] { -1, 3, 0 }, collaborator.getShortWrapperArray());
        assertArrayEquals(new float[] { -0.1F, 5.6F, 7 }, collaborator.getFloatArray(), 0.0F);
        assertArrayEquals(new double[] { 4.0, 15, -7.0001E2 }, collaborator.getDoubleArray(), 0.1);
        assertArrayEquals(new String[] { "aX", null, "B2 m" }, collaborator.getStringArray());
    }

    /**
     * Record multiple iterators to be returned from method that returns iterator.
     *
     * @param mock
     *            the mock
     */
    @Test
    public void recordMultipleIteratorsToBeReturnedFromMethodThatReturnsIterator(@Mocked final Collaborator mock) {
        final Iterator<?> firstResult = new ArrayList<>().listIterator();
        final ListIterator<?> secondResult = new LinkedList<>().listIterator();
        final Iterator<?> thirdResult = new ArrayList<>().iterator();

        new Expectations() {
            {
                mock.getListIterator();
                returns(firstResult, secondResult);

                mock.getIterator();
                returns(firstResult, secondResult, thirdResult);
            }
        };

        assertSame(firstResult, mock.getListIterator());
        assertSame(secondResult, mock.getListIterator());

        assertSame(firstResult, mock.getIterator());
        assertSame(secondResult, mock.getIterator());
        assertSame(thirdResult, mock.getIterator());
    }

    /**
     * Record multiple lists to be returned from method that returns list.
     *
     * @param mock
     *            the mock
     */
    @Test
    public void recordMultipleListsToBeReturnedFromMethodThatReturnsList(@Mocked final Collaborator mock) {
        final List<?> firstResult = new ArrayList<>();
        final List<?> secondResult = new ArrayList<>();
        final List<?> thirdResult = new LinkedList<>();

        new Expectations() {
            {
                mock.getAList();
                returns(firstResult, secondResult);

                mock.getListItems();
                returns(firstResult, secondResult, thirdResult);
            }
        };

        assertSame(firstResult, mock.getAList());
        assertSame(secondResult, mock.getAList());

        assertSame(firstResult, mock.getListItems());
        assertSame(secondResult, mock.getListItems());
        assertSame(thirdResult, mock.getListItems());
    }
}
