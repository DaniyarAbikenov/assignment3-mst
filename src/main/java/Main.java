import com.google.gson.*;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.KruskalMST;
import edu.princeton.cs.algs4.PrimMST;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Main program for Assignment 3: MST algorithms comparison.
 *
 * Reads input JSON in the format:
 * {
 *   "graphs": [
 *     {
 *       "id": 1,
 *       "nodes": ["A","B","C","D"],
 *       "edges": [
 *         {"from":"A","to":"B","weight":4},
 *         {"from":"A","to":"C","weight":3}
 *       ]
 *     }
 *   ]
 * }
 *
 * Produces output JSON in the format:
 * {
 *   "results": [
 *     {
 *       "graph_id": 1,
 *       "input_stats": {"vertices":5,"edges":7},
 *       "prim": {...},
 *       "kruskal": {...}
 *     }
 *   ]
 * }
 */
public class Main {

    public static void main(String[] args) throws IOException {
        String inputFile = "data/small.json";
        String outputFile = "results/small_output.json";

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject root = gson.fromJson(Files.newBufferedReader(Paths.get(inputFile)), JsonObject.class);
        JsonArray graphs = root.getAsJsonArray("graphs");

        JsonArray results = new JsonArray();

        for (JsonElement graphEl : graphs) {
            JsonObject gObj = graphEl.getAsJsonObject();
            int id = gObj.get("id").getAsInt();
            JsonArray nodes = gObj.getAsJsonArray("nodes");
            JsonArray edges = gObj.getAsJsonArray("edges");

            // --- Create node index mapping (A->0, B->1, etc.)
            Map<String, Integer> nodeIndex = new HashMap<>();
            for (int i = 0; i < nodes.size(); i++) {
                nodeIndex.put(nodes.get(i).getAsString(), i);
            }

            // --- Build EdgeWeightedGraph
            EdgeWeightedGraph graph = new EdgeWeightedGraph(nodes.size());
            for (JsonElement eEl : edges) {
                JsonObject eObj = eEl.getAsJsonObject();
                int u = nodeIndex.get(eObj.get("from").getAsString());
                int v = nodeIndex.get(eObj.get("to").getAsString());
                double w = eObj.get("weight").getAsDouble();
                graph.addEdge(new Edge(u, v, w));
            }

            // --- Prepare output structure for this graph
            JsonObject result = new JsonObject();
            result.addProperty("graph_id", id);

            JsonObject stats = new JsonObject();
            stats.addProperty("vertices", graph.V());
            stats.addProperty("edges", graph.E());
            result.add("input_stats", stats);

            // === Run Prim's algorithm ===
            long startPrim = System.nanoTime();
            PrimMST prim = new PrimMST(graph);
            long endPrim = System.nanoTime();
            double timePrim = (endPrim - startPrim) / 1_000_000.0;

            JsonObject primResult = new JsonObject();
            primResult.add("mst_edges", mstEdgesToJson(prim.edges(), nodeIndex));
            primResult.addProperty("total_cost", prim.weight());
            primResult.addProperty("operations_count", 0);
            primResult.addProperty("execution_time_ms", timePrim);
            result.add("prim", primResult);

            // === Run Kruskal's algorithm ===
            long startKruskal = System.nanoTime();
            KruskalMST kruskal = new KruskalMST(graph);
            long endKruskal = System.nanoTime();
            double timeKruskal = (endKruskal - startKruskal) / 1_000_000.0;

            JsonObject kruskalResult = new JsonObject();
            kruskalResult.add("mst_edges", mstEdgesToJson(kruskal.edges(), nodeIndex));
            kruskalResult.addProperty("total_cost", kruskal.weight());
            kruskalResult.addProperty("operations_count", 0);
            kruskalResult.addProperty("execution_time_ms", timeKruskal);
            result.add("kruskal", kruskalResult);

            results.add(result);
        }

        // --- Write to output file
        JsonObject outputRoot = new JsonObject();
        outputRoot.add("results", results);
        Files.createDirectories(Paths.get("results"));
        Files.writeString(Paths.get(outputFile), gson.toJson(outputRoot));

        System.out.println("✅ Results saved to: " + outputFile);
    }

    /**
     * Converts MST edges to JSON with "from"/"to"/"weight" fields,
     * restoring node labels (A, B, C...) from index map.
     */
    private static JsonArray mstEdgesToJson(Iterable<Edge> edges, Map<String, Integer> labelMap) {
        JsonArray arr = new JsonArray();

        // Reverse mapping index → label
        Map<Integer, String> reverseMap = new HashMap<>();
        for (Map.Entry<String, Integer> e : labelMap.entrySet()) {
            reverseMap.put(e.getValue(), e.getKey());
        }

        for (Edge e : edges) {
            JsonObject o = new JsonObject();
            int v = e.either();
            int w = e.other(v);
            o.addProperty("from", reverseMap.get(v));
            o.addProperty("to", reverseMap.get(w));
            o.addProperty("weight", e.weight());
            arr.add(o);
        }

        return arr;
    }
}
