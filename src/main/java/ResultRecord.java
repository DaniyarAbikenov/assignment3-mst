import java.util.List;

/**
 * Extended record that includes MST edge list.
 */
public class ResultRecord {
    public final String graphName;
    public final String algorithm;
    public final int vertices;
    public final int edges;
    public final double mstWeight;
    public final double timeMs;
    public final List<String> mstEdges;

    public ResultRecord(String graphName, String algorithm,
                        int vertices, int edges, double mstWeight, double timeMs,
                        List<String> mstEdges) {
        this.graphName = graphName;
        this.algorithm = algorithm;
        this.vertices = vertices;
        this.edges = edges;
        this.mstWeight = mstWeight;
        this.timeMs = timeMs;
        this.mstEdges = mstEdges;
    }
}
