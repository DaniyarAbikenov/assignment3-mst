import com.google.gson.*;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for reading weighted graphs from JSON files.
 * Each JSON file must contain an object with the structure:
 * <p>
 * {
 * "graphs": [
 * {
 * "name": "graph_name",
 * "vertices": 10,
 * "edges": [
 * {"u": 0, "v": 1, "weight": 3.5},
 * {"u": 1, "v": 2, "weight": 2.1}
 * ]
 * },
 * ...
 * ]
 * }
 * <p>
 * Compatible with EdgeWeightedGraph (algs4).
 */
public class InputLoader {

    /**
     * Loads all graphs from the specified JSON file.
     *
     * @param filePath Path to JSON file.
     * @return A list of GraphData objects (name + graph).
     * @throws IOException if the file cannot be read.
     */
    public static List<GraphData> loadGraphs(String filePath) throws IOException {
        Gson gson = new Gson();
        try (Reader reader = Files.newBufferedReader(Paths.get(filePath))) {
            JsonObject root = gson.fromJson(reader, JsonObject.class);
            JsonArray graphs = root.getAsJsonArray("graphs");
            List<GraphData> results = new ArrayList<>();

            for (JsonElement gEl : graphs) {
                JsonObject g = gEl.getAsJsonObject();
                int id = g.get("id").getAsInt();
                JsonArray nodes = g.getAsJsonArray("nodes");
                int V = nodes.size();

                EdgeWeightedGraph graph = new EdgeWeightedGraph(V);
                JsonArray edges = g.getAsJsonArray("edges");

                // Map node letters to indices
                Map<String, Integer> map = new HashMap<>();
                for (int i = 0; i < V; i++)
                    map.put(nodes.get(i).getAsString(), i);

                for (JsonElement eEl : edges) {
                    JsonObject e = eEl.getAsJsonObject();
                    int u = map.get(e.get("from").getAsString());
                    int v = map.get(e.get("to").getAsString());
                    double w = e.get("weight").getAsDouble();
                    graph.addEdge(new Edge(u, v, w));
                }

                results.add(new GraphData(id, graph, map));
            }
            return results;
        }
    }

    /**
     * Simple record (pair) to hold a graph and its name.
     */
    public static class GraphData {
        private final int id;
        private final EdgeWeightedGraph graph;
        private final Map<String, Integer> labelToIndex;

        public GraphData(int id, EdgeWeightedGraph graph, Map<String, Integer> labelToIndex) {
            this.id = id;
            this.graph = graph;
            this.labelToIndex = labelToIndex;
        }

        public int getId() {
            return id;
        }

        public EdgeWeightedGraph getGraph() {
            return graph;
        }

        public Map<String, Integer> getLabelToIndex() {
            return labelToIndex;
        }
    }


    // Example usage (for manual testing)
    public static void main(String[] args) throws Exception {
        String file = "data/small.json";
        List<GraphData> graphs = loadGraphs(file);
        System.out.println("Loaded " + graphs.size() + " graphs from " + file);

        for (GraphData gd : graphs) {
            System.out.printf("Graph '%s' has %d vertices and %d edges.%n",
                    gd.getId(), gd.getGraph().V(), gd.getGraph().E());
        }
    }
}
