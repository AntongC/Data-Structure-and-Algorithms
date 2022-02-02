package graphpathfinding;

import priorityqueues.DoubleMapMinPQ;
import timing.Timer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @see ShortestPathFinder for more method documentation
 */
public class AStarPathFinder<VERTEX> extends ShortestPathFinder<VERTEX> {
    AStarGraph<VERTEX> graph;
    HashMap<VERTEX, Double> visited;
    HashMap<VERTEX, VERTEX> previous;
    DoubleMapMinPQ<VERTEX> unvisited;

    /**
     * Creates a new AStarPathFinder that works on the provided graph.
     */
    public AStarPathFinder(AStarGraph<VERTEX> graph) {
        this.graph = graph;
        visited = new HashMap<>();
        unvisited = new DoubleMapMinPQ<>();
        previous = new HashMap<>();
    }

    @Override
    public ShortestPathResult<VERTEX> findShortestPath(VERTEX start, VERTEX end, Duration timeout) {
        unvisited.add(start, 0);
        visited.put(start, 0.0);
        Timer clock = new Timer(timeout);
        while (!unvisited.isEmpty()) {
            VERTEX current = unvisited.removeMin();
            double currentDist = visited.get(current);
            if (current.equals(end)) {
                List<VERTEX> solution = findSolution(current);
                return new ShortestPathResult.Solved<>(solution, visited.get(end),
                    visited.size(), clock.elapsedDuration());
            }
            for (WeightedEdge<VERTEX> element : graph.neighbors(current)) {
                if (clock.isTimeUp()) {
                    return new ShortestPathResult.Timeout<>(visited.size(), clock.elapsedDuration());
                }
                VERTEX next = element.to();
                //distance from beginning calculated passing through current
                double nextDist = currentDist + element.weight();
                if (!visited.containsKey(next)) { //if next has not been visited
                    visited.put(next, nextDist);
                    unvisited.add(next, nextDist + graph.estimatedDistanceToGoal(next, end));
                    previous.put(next, current);
                /*} else if (!unvisited.contains(next)) { //if next was removed from the fringe
                    unvisited.add(next, nextDist + graph.estimatedDistanceToGoal(next, end));*/
                } else if (visited.get(next) > nextDist) { //if this path is better than known
                    visited.replace(next, nextDist);
                    unvisited.changePriority(next, nextDist + graph.estimatedDistanceToGoal(next, end));
                    previous.replace(next, current);
                } //doesn't do anything other than the above conditions
            }
        }
        return new ShortestPathResult.Unsolvable<>(visited.size(), clock.elapsedDuration());
    }

    private List<VERTEX> findSolution(VERTEX end) {
        List<VERTEX> solution = new ArrayList<>(1);
        VERTEX current = end;
        while (previous.containsKey(current)) {
            solution.add(0, current);
            current = previous.get(current);
        }
        solution.add(0, current);
        return solution;
    }

    @Override
    protected AStarGraph<VERTEX> graph() {
        return graph;
    }
}
