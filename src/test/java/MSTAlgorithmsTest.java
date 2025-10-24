import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.UF;

import algo.KruskalMST;
import algo.PrimMST;

public class MSTAlgorithmsTest {

    /** Builds a small test graph:
     *
     *    (0)A---3---(1)B
     *     | \        |
     *     1  4       2
     *     |    \     |
     *    (2)C---5---(3)D
     */
    private EdgeWeightedGraph buildSmallGraph() {
        EdgeWeightedGraph G = new EdgeWeightedGraph(4);
        G.addEdge(new Edge(0, 1, 3.0)); // A-B
        G.addEdge(new Edge(0, 2, 1.0)); // A-C
        G.addEdge(new Edge(0, 3, 4.0)); // A-D
        G.addEdge(new Edge(1, 3, 2.0)); // B-D
        G.addEdge(new Edge(2, 3, 5.0)); // C-D
        return G;
    }

    @Test
    @DisplayName("MST total weight is identical for Kruskal and Prim algorithms")
    void testSameMSTCost() {
        EdgeWeightedGraph G = buildSmallGraph();
        KruskalMST kruskal = new KruskalMST(G);
        PrimMST prim = new PrimMST(G);

        assertEquals(kruskal.weight(), prim.weight(), 1e-9,
                "Both algorithms should produce identical MST total cost.");
    }

    @Test
    @DisplayName("MST contains exactly V - 1 edges")
    void testEdgeCount() {
        EdgeWeightedGraph G = buildSmallGraph();
        PrimMST prim = new PrimMST(G);

        int edgeCount = 0;
        for (Edge e : prim.edges()) edgeCount++;

        assertEquals(G.V() - 1, edgeCount,
                "MST must contain exactly V - 1 edges.");
    }

    @Test
    @DisplayName("MST is acyclic")
    void testAcyclicProperty() {
        EdgeWeightedGraph G = buildSmallGraph();
        KruskalMST kruskal = new KruskalMST(G);

        UF uf = new UF(G.V());
        for (Edge e : kruskal.edges()) {
            int v = e.either();
            int w = e.other(v);
            // If the vertices are already connected, we found a cycle
            assertNotEquals(uf.find(v), uf.find(w),
                    "Cycle detected: MST should be acyclic.");
            uf.union(v, w);
        }
    }

    @Test
    @DisplayName("MST connects all vertices (single connected component)")
    void testConnectivity() {
        EdgeWeightedGraph G = buildSmallGraph();
        KruskalMST kruskal = new KruskalMST(G);

        UF uf = new UF(G.V());
        for (Edge e : kruskal.edges()) {
            int v = e.either();
            int w = e.other(v);
            uf.union(v, w);
        }

        int root = uf.find(0);
        for (int v = 1; v < G.V(); v++) {
            assertEquals(root, uf.find(v),
                    "All vertices must belong to a single connected component.");
        }
    }

    @Test
    @DisplayName("Disconnected graphs produce a spanning forest")
    void testDisconnectedGraph() {
        EdgeWeightedGraph G = new EdgeWeightedGraph(4);
        // Two separate components: (0–1) and (2–3)
        G.addEdge(new Edge(0, 1, 1.0));
        G.addEdge(new Edge(2, 3, 1.0));

        KruskalMST kruskal = new KruskalMST(G);

        UF uf = new UF(G.V());
        int edgeCount = 0;
        for (Edge e : kruskal.edges()) {
            int v = e.either();
            int w = e.other(v);
            uf.union(v, w);
            edgeCount++;
        }

        assertEquals(2, edgeCount,
                "For disconnected graphs, the result should be a minimum spanning forest.");
    }
}
