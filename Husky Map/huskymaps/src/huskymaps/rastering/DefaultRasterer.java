package huskymaps.rastering;

import huskymaps.graph.Coordinate;
import huskymaps.utils.Constants;
/**
 * @see Rasterer
 */
public class DefaultRasterer implements Rasterer {
    public TileGrid rasterizeMap(Coordinate ul, Coordinate lr, int depth) {
        //here we make all longitudes positive to make calculation easier and more consistent
        //that is, left edge would have a greater absolute value than right edge

        double top = ul.lat();
        double left = -ul.lon();
        double low = lr.lat();
        double right = -lr.lon();
        double tileVert = Constants.LAT_PER_TILE[depth];
        double tileHori = Constants.LON_PER_TILE[depth];
        double topEdge = Constants.ROOT_ULLAT;
        double leftEdge = -Constants.ROOT_ULLON;
        double lowEdge = Constants.ROOT_LRLAT;
        double rightEdge = -Constants.ROOT_LRLON;
        int rasTop;
        int rasLeft;
        int rasLow;
        int rasRight;
        if (top >= topEdge) {
            rasTop = 0;
        } else {
            rasTop = (int) ((topEdge - top) / tileVert);
        }
        if (left >= leftEdge) {
            rasLeft = 0;
        } else {
            rasLeft = (int) ((leftEdge - left) / tileHori);
        }
        if (low <= lowEdge) {
            rasLow = Constants.NUM_Y_TILES_AT_DEPTH[depth] - 1;
        } else {
            rasLow = (int) ((topEdge - low) / tileVert);
        }
        if (right <= rightEdge) {
            rasRight = Constants.NUM_X_TILES_AT_DEPTH[depth] - 1;
        } else {
            rasRight = (int) ((leftEdge - right) / tileHori);
        }
        Tile[][] grid = new Tile[rasLow - rasTop + 1][rasRight - rasLeft + 1];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                grid[i][j] = new Tile(depth, j + rasLeft, i + rasTop);
            }
        }
        return new TileGrid(grid);
    }
}
