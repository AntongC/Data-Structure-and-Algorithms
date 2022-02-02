package arrayutils;

import java.util.Arrays;
import java.util.Comparator;
/**
 * Make sure to check out the interface for more method details:
 *
 * @see ArraySearcher
 */
public class BinaryRangeSearcher<T, U> implements ArraySearcher<T, U> {
    T[] array;
    Matcher<T, U> matcher;

    /**
     * Creates a BinaryRangeSearcher for the given array of items that matches items using the
     * Matcher matchUsing.
     *
     * First sorts the array in place using the Comparator sortUsing. (Assumes that the given array
     * will not be used externally afterwards.)
     *
     * Requires that sortUsing sorts the array such that for any possible reference item U,
     * calling matchUsing.match(T, U) on each T in the sorted array will result in all negative
     * values first, then all 0 values, then all positive.
     *
     * For example:
     * sortUsing lexicographic string sort: [  aaa,  abc,   ba,  bzb, cdef ]
     * matchUsing T is prefixed by U
     * matchUsing.match for prefix "b":     [   -1,   -1,    0,    0,    1 ]
     *
     * @throws IllegalArgumentException if array is null or contains null
     * @throws IllegalArgumentException if sortUsing or matchUsing is null
     */
    public static <T, U> BinaryRangeSearcher<T, U> forUnsortedArray(T[] array,
                                                                    Comparator<T> sortUsing,
                                                                    Matcher<T, U> matchUsing) {
        /*
        Tip: To reduce redundancy, you can let the BinaryRangeSearcher constructor throw some of
        the exceptions mentioned in this method's documentation. The caller doesn't care which
        method exactly causes the exception, as long as it's something that happens while
        executing this method.
        */
        if (array == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        if (array.length == 0) {
            throw new IllegalArgumentException("Array cannot be of length 0");
        }
        for (T element : array) {
            if (element == null) {
                throw new IllegalArgumentException("Array element cannot be null");
            }
        }
        if (sortUsing == null) {
            throw new IllegalArgumentException("SortUsing cannot be null");
        }
        if (matchUsing == null) {
            throw new IllegalArgumentException("MatchUsing cannot be null");
        }
        Arrays.sort(array, sortUsing);

        return new BinaryRangeSearcher<>(array, matchUsing);
    }

    /**
     * Requires that array is sorted such that for any possible reference item U,
     * calling matchUsing.match(T, U) on each T in the sorted array will result in all negative
     * values first, then all 0 values, then all positive.
     *
     * Assumes that the given array will not be used externally afterwards (and thus may directly
     * store and mutate the array).
     * @throws IllegalArgumentException if array is null or contains null
     * @throws IllegalArgumentException if matcher is null
     */
    protected BinaryRangeSearcher(T[] array, Matcher<T, U> matcher) {
        if (array == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        for (T element : array) {
            if (element == null) {
                throw new IllegalArgumentException("Array element cannot be null");
            }
        }
        if (matcher == null) {
            throw new IllegalArgumentException("Matcher cannot be null");
        }

        this.array = array;
        this.matcher = matcher;
    }

    /**
     * Calls the recursive matching function with initial index parameters
     */
    public MatchResult<T> findAllMatches(U target) {
        if (target == null) {
            throw new IllegalArgumentException("Target cannot be null");
        }
        return findAllMatchesRec(target, 0, array.length - 1);
    }

    /**
     * Added in to assist findAllMatches with recursion.
     */
    private MatchResult<T> findAllMatchesRec(U target, int head, int tail) {
        int mid = (head + tail) / 2;
        if (matcher.match(array[mid], target) == 0) {
            return findAllMatchesBase(target, head, mid, tail);
        } else if (head == tail) {
            return new MatchResult<>(array);
        } else if (matcher.match(array[mid], target) > 0) {
            tail = Integer.max(mid, 0);
        } else {
            head = Integer.min(mid + 1, array.length - 1);
        }
        return findAllMatchesRec(target, head, tail);
    }

    private MatchResult<T> findAllMatchesBase(U target, int head, int match, int tail) {
        //head and tail of the maximum possible bound
        //start and end of the tightest bound
        int start = (head + match) / 2;
        int end = (match + tail) / 2;
        while (true) {
            if (matcher.match(array[start], target) != 0) {
                head = start + 1;
                start = (head + match) / 2;
            } else {
                if (start == 0) {
                    break;
                }
                if (matcher.match(array[start - 1], target) == 0) {
                    start = (head + start) / 2;
                } else {
                    break;
                }
            }
        }

        while (true) {
            if (matcher.match(array[end], target) != 0) {
                tail = end - 1;
                end = (match + tail) / 2;
            } else {
                if (end == array.length - 1) {
                    break;
                }
                if (matcher.match(array[end + 1], target) == 0) {
                    end = (end + tail) / 2 + 1;
                } else {
                    break;
                }
            }
        }
        return new MatchResult<>(array, start, end + 1);
    }

    public static class MatchResult<T> extends AbstractMatchResult<T> {
        final T[] array;
        final int start;
        final int end;

        /**
         * Use this constructor if there are no matching results.
         * (This lets us use Arrays.copyOfRange to make a new T[], which can be difficult to
         * acquire otherwise due to the way Java handles generics.)
         */
        protected MatchResult(T[] array) {
            this(array, 0, 0);
        }

        protected MatchResult(T[] array, int startInclusive, int endExclusive) {
            this.array = array;
            this.start = startInclusive;
            this.end = endExclusive;
        }

        @Override
        public int count() {
            return this.end - this.start;
        }

        @Override
        public T[] unsorted() {
            return Arrays.copyOfRange(this.array, this.start, this.end);
        }
    }
}
