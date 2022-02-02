package priorityqueues;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeMap;

public class ArrayHeapMinPQ<T extends Comparable<T>> implements ExtrinsicMinPQ<T> {
    // IMPORTANT: Do not rename these fields or change their visibility.
    // We access these during grading to test your code.
    static final int START_INDEX = 1;
    private int size = 0;
    List<PriorityNode<T>> items;
    TreeMap<T, Integer> quickTree;

    public ArrayHeapMinPQ() {
        items = new ArrayList<>(1);
        items.add(null);
        quickTree = new TreeMap<>();
    }

    // Here's a method stub that may be useful. Feel free to change or remove it, if you wish.
    // You'll probably want to add more helper methods like this one to make your code easier to read.

    /**
     * A helper method for swapping the items at two indices of the array heap.
     */
    private void swap(int a, int b) {
        PriorityNode<T> temp = items.get(a);
        items.set(a, items.get(b));
        items.set(b, temp);
        update(a);
    }

    private void update(int a) {
        quickTree.replace(items.get(a).getItem(), a);
    }

    /**
     * Adds an item with the given priority value.
     * Runs in O(log N) time (except when resizing).
     *
     * @throws IllegalArgumentException if item is null or is already present in the PQ
     */
    @Override
    public void add(T item, double priority) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        } else if (contains(item)) {
            throw new IllegalArgumentException("Item cannot be already in the heap");
        }
        size += 1;
        items.add(size, new PriorityNode<>(item, priority));
        quickTree.put(item, size);
        percolateUp(size);
    }

    /**
     * Returns true if the PQ contains the given item; false otherwise.
     * Runs in O(log N) time.
     */
    @Override
    public boolean contains(T item) {
        return quickTree.containsKey(item);
    }

    /**
     * Returns the item with the least-valued priority.
     * Runs in O(log N) time.
     * @throws NoSuchElementException if the PQ is empty
     */
    @Override
    public T peekMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("Heap can't be empty");
        }
        return items.get(START_INDEX).getItem();
    }

    /**
     * Removes and returns the item with the least-valued priority.
     * Runs in O(log N) time (except when resizing).
     * @throws NoSuchElementException if the PQ is empty
     */
    @Override
    public T removeMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("Heap can't be empty");
        }
        T retVal = items.get(START_INDEX).getItem();
        swap(START_INDEX, size);
        update(START_INDEX);
        items.remove(size);
        size -= 1;
        if (!isEmpty()) {
            percolateDown(START_INDEX);
        }
        quickTree.remove(retVal);
        return retVal;
    }

    /**
     * Changes the priority of the given item.
     * Runs in O(log N) time.
     *
     * @throws NoSuchElementException if the item is not present in the PQ
     */
    @Override
    public void changePriority(T item, double priority) {
        if (isEmpty()) {
            throw new NoSuchElementException("Heap has to be non-empty");
        } else if (!contains(item)) {
            throw new NoSuchElementException("Item has to exist in the heap");
        }
        int index = quickTree.get(item);
        items.get(index).setPriority(priority);
        index = percolateUp(index);
        index = percolateDown(index);
    }

    /**
     * percolate down the item at the given index
     */
    private int percolateDown(int current) {
        int left = current * 2;
        int right = current * 2 + 1;
        double currentPrio = items.get(current).getPriority();
        double leftPrio;
        double rightPrio;
        if (size < left) {
            update(current);
            return current;
        } else if (size < right) {
            leftPrio = items.get(left).getPriority();
            if (leftPrio < currentPrio) {
                swap(current, left);
                return percolateDown(left);
            } else {
                update(current);
                return current;
            }
        } else {
            leftPrio = items.get(left).getPriority();
            rightPrio = items.get(right).getPriority();
            if (currentPrio < leftPrio && currentPrio < rightPrio) {
                update(current);
                return current;
            } else if (leftPrio < rightPrio) {
                swap(current, left);
                return percolateDown(left);
            } else {
                swap(current, right);
                return percolateDown(right);
            }
        }
    }

    /**
     * percolate up the item at the given index
     */
    private int percolateUp(int current) {
        int up = current / 2;
        if (current == START_INDEX || items.get(up).getPriority() < items.get(current).getPriority()) {
            update(current);
            return current;
        } else {
            swap(current, up);
            return percolateUp(up);
        }
    }

    /**
     * Returns the number of items in the PQ.
     * Runs in O(log N) time.
     */
    @Override
    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}
