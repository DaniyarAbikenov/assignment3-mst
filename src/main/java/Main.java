import algo.KruskalMST;
import algo.PrimMST;
import com.google.gson.*;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Main program for Assignment 3: MST algorithms comparison.
 *
 * Reads JSON input from multiple files (small, medium, large, extralarge),
 * computes MSTs using Prim's and Kruskal's algorithms,
 * writes detailed results per dataset to JSON,
 * and generates a single global summary CSV file across all inputs.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        String[] inputs = {"small", "medium", "large", "extralarge"};
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // общий список для всех результатов
        List<JsonObject> allResults = new ArrayList<>();

        for (String file : inputs) {
            String inputFile = "data/" + file + ".json";
            String outputFile = "results/" + file + "_output.json";

            JsonObject root = gson.fromJson(Files.newBufferedReader(Paths.get(inputFile)), JsonObject.class);
            JsonArray graphs = root.getAsJsonArray("graphs");

            JsonArray datasetResults = new JsonArray();

            for (JsonElement graphEl : graphs) {
                JsonObject gObj = graphEl.getAsJsonObject();
                int id = gObj.get("id").getAsInt();
                JsonArray nodes = gObj.getAsJsonArray("nodes");
                JsonArray edges = gObj.getAsJsonArray("edges");

                // --- Map node labels -> indices
                Map<String, Integer> nodeIndex = new HashMap<>();
                for (int i = 0; i < nodes.size(); i++) {
                    nodeIndex.put(nodes.get(i).getAsString(), i);
                }

                // --- Build graph
                EdgeWeightedGraph graph = new EdgeWeightedGraph(nodes.size());
                for (JsonElement eEl : edges) {
                    JsonObject eObj = eEl.getAsJsonObject();
                    int u = nodeIndex.get(eObj.get("from").getAsString());
                    int v = nodeIndex.get(eObj.get("to").getAsString());
                    double w = eObj.get("weight").getAsDouble();
                    graph.addEdge(new Edge(u, v, w));
                }

                // --- Prepare per-graph result
                JsonObject result = new JsonObject();
                result.addProperty("dataset", file);
                result.addProperty("graph_id", id);

                JsonObject stats = new JsonObject();
                stats.addProperty("vertices", graph.V());
                stats.addProperty("edges", graph.E());
                result.add("input_stats", stats);

                // === Prim
                long startPrim = System.nanoTime();
                PrimMST prim = new PrimMST(graph);
                long endPrim = System.nanoTime();
                double timePrim = (endPrim - startPrim) / 1_000_000.0;

                JsonObject primRes = new JsonObject();
                primRes.add("mst_edges", mstEdgesToJson(prim.edges(), nodeIndex));
                primRes.addProperty("total_cost", prim.weight());
                primRes.addProperty("operations_count", prim.getOperationCount());
                primRes.addProperty("execution_time_ms", timePrim);
                result.add("prim", primRes);

                // === Kruskal
                long startKruskal = System.nanoTime();
                KruskalMST kruskal = new KruskalMST(graph);
                long endKruskal = System.nanoTime();
                double timeKruskal = (endKruskal - startKruskal) / 1_000_000.0;

                JsonObject kruskalRes = new JsonObject();
                kruskalRes.add("mst_edges", mstEdgesToJson(kruskal.edges(), nodeIndex));
                kruskalRes.addProperty("total_cost", kruskal.weight());
                kruskalRes.addProperty("operations_count", kruskal.getOperationCount());
                kruskalRes.addProperty("execution_time_ms", timeKruskal);
                result.add("kruskal", kruskalRes);

                datasetResults.add(result);
                allResults.add(result);
            }

            // сохранить JSON для каждого набора
            JsonObject outputRoot = new JsonObject();
            outputRoot.add("results", datasetResults);
            Files.createDirectories(Paths.get("results"));
            Files.writeString(Paths.get(outputFile), gson.toJson(outputRoot));

            System.out.println("JSON saved for dataset: " + file);
        }

        // === записать единый CSV по всем результатам ===
        String globalCSV = "results/summary_all.csv";
        saveGlobalSummaryCSV(allResults, globalCSV);
        System.out.println("Global summary saved to: " + globalCSV);
    }

    /** Converts MST edges to JSON */
    private static JsonArray mstEdgesToJson(Iterable<Edge> edges, Map<String, Integer> labelMap) {
        JsonArray arr = new JsonArray();
        Map<Integer, String> reverse = new HashMap<>();
        for (Map.Entry<String, Integer> e : labelMap.entrySet()) reverse.put(e.getValue(), e.getKey());
        for (Edge e : edges) {
            JsonObject o = new JsonObject();
            int v = e.either(), w = e.other(v);
            o.addProperty("from", reverse.get(v));
            o.addProperty("to", reverse.get(w));
            o.addProperty("weight", e.weight());
            arr.add(o);
        }
        return arr;
    }

    /** Writes one combined CSV file across all datasets */
    public static void saveGlobalSummaryCSV(List<JsonObject> results, String path) throws IOException {
        try (FileWriter writer = new FileWriter(path)) {
            writer.write("Dataset,Graph_ID,Vertices,Edges,Algorithm,Total_Cost,Operations_Count,Execution_Time_ms\n");

            for (JsonObject result : results) {
                String dataset = result.get("dataset").getAsString();
                int id = result.get("graph_id").getAsInt();
                JsonObject stats = result.getAsJsonObject("input_stats");
                int v = stats.get("vertices").getAsInt();
                int e = stats.get("edges").getAsInt();

                for (String algo : new String[]{"prim", "kruskal"}) {
                    JsonObject algoRes = result.getAsJsonObject(algo);
                    double cost = algoRes.get("total_cost").getAsDouble();
                    long ops = algoRes.get("operations_count").getAsLong();
                    double time = algoRes.get("execution_time_ms").getAsDouble();

                    writer.write(String.format("%s,%d,%d,%d,%s,%.6f,%d,%.3f\n",
                            dataset, id, v, e, algo, cost, ops, time));
                }
            }
        }
    }
}
