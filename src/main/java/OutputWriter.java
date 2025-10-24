import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Utility class for writing MST algorithm results into JSON and CSV files.
 * Expected usage: record the results of Prim and Kruskal algorithms
 * for each graph dataset and output them into "results/output.json"
 * and "results/summary.csv".
 *
 * Example JSON structure:
 * [
 *   {
 *     "graph": "small_1",
 *     "algorithm": "Prim",
 *     "vertices": 10,
 *     "edges": 15,
 *     "mstWeight": 92.5,
 *     "timeMs": 1.23
 *   }
 * ]
 */
public class OutputWriter {

    /**
     * Writes results into a JSON file (pretty-printed).
     *
     * @param results list of ResultRecord objects
     * @param filePath path to JSON file
     */
    public static void writeJson(List<ResultRecord> results, String filePath) {
        try {
            // Ensure directory exists
            Files.createDirectories(Paths.get(filePath).getParent());

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (FileWriter writer = new FileWriter(filePath)) {
                gson.toJson(results, writer);
            }
            System.out.println("✅ Results saved to " + filePath);
        } catch (IOException e) {
            System.err.println("❌ Failed to write JSON file: " + e.getMessage());
        }
    }

    /**
     * Writes results into a CSV file.
     *
     * @param results list of ResultRecord objects
     * @param filePath path to CSV file
     */
    public static void writeCsv(List<ResultRecord> results, String filePath) {
        try {
            // Ensure directory exists
            Files.createDirectories(Paths.get(filePath).getParent());

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write("Graph,Algorithm,Vertices,Edges,MST_Weight,Time_ms\n");
                for (ResultRecord r : results) {
                    writer.write(String.format("%s,%s,%d,%d,%.2f,%.3f%n",
                            r.graphName, r.algorithm, r.vertices, r.edges, r.mstWeight, r.timeMs));
                }
            }
            System.out.println("✅ Results saved to " + filePath);
        } catch (IOException e) {
            System.err.println("❌ Failed to write CSV file: " + e.getMessage());
        }
    }

    /**
     * Writes both JSON and CSV outputs together.
     *
     * @param results list of results to record
     * @param jsonPath output path for JSON file
     * @param csvPath output path for CSV file
     */
    public static void writeAll(List<ResultRecord> results, String jsonPath, String csvPath) {
        writeJson(results, jsonPath);
        writeCsv(results, csvPath);
    }
}
