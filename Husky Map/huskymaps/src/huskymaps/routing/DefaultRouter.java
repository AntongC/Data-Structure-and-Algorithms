package huskymaps.routing;

import graphpathfinding.AStarGraph;
import graphpathfinding.AStarPathFinder;
import graphpathfinding.ShortestPathFinder;
import graphpathfinding.ShortestPathResult;
import huskymaps.graph.Coordinate;
import huskymaps.graph.Node;
import huskymaps.graph.StreetMapGraph;
import pointsets.KDTreePointSet;
import pointsets.Point;
import pointsets.PointSet;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static huskymaps.utils.Spatial.projectToPoint;

/**
 * @see Router
 */
public class DefaultRouter extends Router {
    private StreetMapGraph graph;
    List<NodePoint> pointsList;
    private PointSet<NodePoint> pointsTree;

    public DefaultRouter(StreetMapGraph graph) {
        this.graph = graph;
        pointsList = new ArrayList<>(0);
        for (Node element : graph.allNodes()) {
            if (!graph.neighbors(element).isEmpty()) {
                pointsList.add(createNodePoint(element));
            }
        }
        pointsTree = createPointSet(pointsList);
    }

    @Override
    protected <T extends Point> PointSet<T> createPointSet(List<T> points) {
        // uncomment (and import) if you want to use WeirdPointSet instead of your own KDTreePointSet:
        // return new WeirdPointSet<>(points);
        return KDTreePointSet.createAfterShuffling(points);
    }

    @Override
    protected <VERTEX> ShortestPathFinder<VERTEX> createPathFinder(AStarGraph<VERTEX> g) {
        return new AStarPathFinder<>(g);
    }

    @Override
    protected NodePoint createNodePoint(Node node) {
        return projectToPoint(Coordinate.fromNode(node), (x, y) -> new NodePoint(x, y, node));
    }

    @Override
    protected Node closest(Coordinate c) {
        // Project to x and y coordinates instead of using raw lat and lon for finding closest points:
        Point p = projectToPoint(c, Point::new);
        NodePoint retVal = (NodePoint) pointsTree.nearest(p);
        return retVal.node();
    }

    @Override
    public List<Node> shortestPath(Coordinate start, Coordinate end) {
        Node src = closest(start);
        Node dest = closest(end);
        ShortestPathFinder<Node> finder = createPathFinder(graph);
        ShortestPathResult<Node> solution = finder.findShortestPath(src, dest, Duration.of(10, ChronoUnit.SECONDS));
        return solution.solution();
        /*
        Feel free to use any arbitrary duration for your path finding timeout; we don't expect
        queries to take more than a few seconds, so e.g. 10-30 seconds is pretty reasonable.
         */
    }

    @Override
    public List<NavigationDirection> routeDirections(List<Node> route) {
        // Optional
        return null;
    }
}
