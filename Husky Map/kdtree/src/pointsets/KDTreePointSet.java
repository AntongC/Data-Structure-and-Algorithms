package pointsets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Fast nearest-neighbor implementation using a k-d tree.
 */
public class KDTreePointSet<T extends Point> implements PointSet<T> {
    Node root;
    List<T> points;

    class Node {
        Point value;
        Node left = null;
        Node right = null;
        boolean isHori; //yes if the node is a horizontal split

        public Node(Point value, boolean isHori) {
            this.value = value;
            this.isHori = isHori;
        }
    }

    /**
     * Instantiates a new KDTreePointSet with a shuffled version of the given points.
     * <p>
     * Randomizing the point order decreases likeliness of ending up with a spindly tree if the
     * points are sorted somehow.
     *
     * @param points a non-null, non-empty list of points to include.
     *               Assumes that the list will not be used externally afterwards (and thus may
     *               directly store and mutate the array).
     */
    public static <T extends Point> KDTreePointSet<T> createAfterShuffling(List<T> points) {
        Collections.shuffle(points);
        return new KDTreePointSet<>(points);
    }

    /**
     * Instantiates a new KDTreePointSet with the given points.
     *
     * @param points a non-null, non-empty list of points to include.
     *               Assumes that the list will not be used externally afterwards (and thus may
     *               directly store and mutate the array).
     */
    KDTreePointSet(List<T> points) {
        this.points = new ArrayList<>();
        for (T element : points) {
            this.points.add(element);
        }
        for (T element : points) {
            insert(element);
        }
    }

    private void insert(Point target) {
        if (root == null) {
            root = new Node(target, true);
        } else {
            Node current = root;
            insertRec(current, target);
        }
    }

    private void insertRec(Node current, Point target) {
        if (current.isHori) {
            if (target.x() < current.value.x()) {
                if (current.left == null) {
                    current.left = new Node(target, !current.isHori);
                } else {
                    insertRec(current.left, target);
                }
            } else {
                if (current.right == null) {
                    current.right = new Node(target, !current.isHori);
                } else {
                    insertRec(current.right, target);
                }
            }
        } else {
            if (target.y() < current.value.y()) {
                if (current.left == null) {
                    current.left = new Node(target, !current.isHori);
                } else {
                    insertRec(current.left, target);
                }
            } else {
                if (current.right == null) {
                    current.right = new Node(target, !current.isHori);
                } else {
                    insertRec(current.right, target);
                }
            }
        }
    }

    /**
     * Returns the point in this set closest to the given point in (usually) O(log N) time, where
     * N is the number of points in this set.
     */
    @Override
    public T nearest(Point target) {
        return (T) nearestRec(root, target, root).value;
    }

    private Node nearestRec(Node current, Point target, Node best) {
        Node goodSide;
        Node badSide;
        if (current == null) {
            return best;
        }
        if (distance(current.value, target) < distance(best.value, target)) {
            best = current;
        }
        if (current.isHori) {
            if (target.x() < current.value.x()) {
                goodSide = current.left;
                badSide = current.right;
            } else {
                goodSide = current.right;
                badSide = current.left;
            }
        } else {
            if (target.y() < current.value.y()) {
                goodSide = current.left;
                badSide = current.right;
            } else {
                goodSide = current.right;
                badSide = current.left;
            }
        }
        best = nearestRec(goodSide, target, best);
        if (useful(current, target, best)) {
            best = nearestRec(badSide, target, best);
        }
        return best;
    }

    private boolean useful(Node current, Point target, Node best) {
        if (current.isHori) {
            return (Math.abs(current.value.x() - target.x()) < distance(target, best.value));
        } else {
            return (Math.abs(current.value.y() - target.y()) < distance(target, best.value));
        }
    }

    private double distance(Point a, Point b) {
        return Math.sqrt(a.distanceSquaredTo(b));
    }

    @Override
    public List<T> allPoints() {
        return points;
    }
}
