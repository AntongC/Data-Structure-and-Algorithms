package pointsets;

import java.util.ArrayList;
import java.util.List;

/**
 * Naive nearest-neighbor implementation using a linear scan.
 */
public class NaivePointSet<T extends Point> implements PointSet<T> {
    List<T> pointList;
    /**
     * Instantiates a new NaivePointSet with the given points.
     * @param points a non-null, non-empty list of points to include
     *               Assumes that the list will not be used externally afterwards (and thus may
     *               directly store and mutate the array).
     */
    public NaivePointSet(List<T> points) {
        pointList = new ArrayList<>();
        if (points == null || points.isEmpty()) {
            throw new IllegalArgumentException("List of points can't be null or empty");
        }
        for (T element : points) {
            pointList.add(element);
        }
    }

    /**
     * Returns the point in this set closest to the given point in O(N) time, where N is the number
     * of points in this set.
     */
    @Override
    public T nearest(Point target) {
        double minDist = Integer.MAX_VALUE;
        double currentDist = Integer.MAX_VALUE;
        T bestNode = null;
        for (T element : pointList) {
            currentDist = target.distanceSquaredTo(element);
            if (currentDist < minDist) {
                minDist = currentDist;
                bestNode = element;
            }
        }
        return bestNode;
    }

    @Override
    public List<T> allPoints() {
        return pointList;
    }
}
