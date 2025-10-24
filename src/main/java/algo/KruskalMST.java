package algo;

import edu.princeton.cs.algs4.*;
import java.util.Arrays;

/**
 * Modified KruskalMST implementation with operation counting.
 *
 * Counts:
 *  - edge comparisons (each edge processed)
 *  - calls to find() and union() in UF
 *  - successful additions to MST
 */
public class KruskalMST {
    private static final double FLOATING_POINT_EPSILON = 1.0E-12;

    private double weight;                        // weight of MST
    private Queue<Edge> mst = new Queue<Edge>();  // edges in MST

    // new field: counter for key algorithmic operations
    private long operationCount = 0;

    /** Returns number of recorded operations */
    public long getOperationCount() {
        return operationCount;
    }

    /**
     * Compute a minimum spanning tree (or forest) of an edge-weighted graph.
     * @param G the edge-weighted graph
     */
    public KruskalMST(EdgeWeightedGraph G) {

        // create array of edges, sorted by weight
        Edge[] edges = new Edge[G.E()];
        int t = 0;
        for (Edge e: G.edges()) {
            edges[t++] = e;
        }

        // Sorting — each compare operation can be considered one logical step
        Arrays.sort(edges);
        operationCount += edges.length * (Math.log(edges.length) / Math.log(2)); // rough cost of sort

        // run greedy algorithm
        UF uf = new UF(G.V());
        for (int i = 0; i < G.E() && mst.size() < G.V() - 1; i++) {
            Edge e = edges[i];
            operationCount++; // processing edge
            int v = e.either();
            int w = e.other(v);

            // calls to UF
            operationCount += 2; // two find() calls
            if (uf.find(v) != uf.find(w)) {
                uf.union(v, w);
                operationCount++; // one union()
                mst.enqueue(e);
                weight += e.weight();
                operationCount++; // successful edge addition
            }
        }

        // check optimality conditions
        assert check(G);
    }

    /** Returns the edges in a minimum spanning tree (or forest). */
    public Iterable<Edge> edges() {
        return mst;
    }

    /** Returns the sum of the edge weights in a minimum spanning tree (or forest). */
    public double weight() {
        return weight;
    }

    // check optimality conditions (same as original)
    private boolean check(EdgeWeightedGraph G) {

        // check total weight
        double total = 0.0;
        for (Edge e : edges()) {
            total += e.weight();
        }
        if (Math.abs(total - weight()) > FLOATING_POINT_EPSILON) {
            System.err.printf("Weight of edges does not equal weight(): %f vs. %f\n", total, weight());
            return false;
        }

        // check that it is acyclic
        UF uf = new UF(G.V());
        for (Edge e : edges()) {
            int v = e.either(), w = e.other(v);
            if (uf.find(v) == uf.find(w)) {
                System.err.println("Not a forest");
                return false;
            }
            uf.union(v, w);
        }

        // check that it is a spanning forest
        for (Edge e : G.edges()) {
            int v = e.either(), w = e.other(v);
            if (uf.find(v) != uf.find(w)) {
                System.err.println("Not a spanning forest");
                return false;
            }
        }

        // check minimality
        for (Edge e : edges()) {
            uf = new UF(G.V());
            for (Edge f : mst) {
                int x = f.either(), y = f.other(x);
                if (f != e) uf.union(x, y);
            }

            for (Edge f : G.edges()) {
                int x = f.either(), y = f.other(x);
                if (uf.find(x) != uf.find(y)) {
                    if (f.weight() < e.weight()) {
                        System.err.println("Edge " + f + " violates cut optimality conditions");
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /** Unit test for manual verification */
    public static void main(String[] args) {
        In in = new In(args[0]);
        EdgeWeightedGraph G = new EdgeWeightedGraph(in);
        KruskalMST mst = new KruskalMST(G);
        for (Edge e : mst.edges()) {
            StdOut.println(e);
        }
        StdOut.printf("Weight: %.5f\n", mst.weight());
        StdOut.printf("Operations: %d\n", mst.getOperationCount());
    }
}
