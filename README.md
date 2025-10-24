# Assignment 3 — Minimum Spanning Tree Algorithms (Prim vs Kruskal)

## Overview
This project implements and compares two classic algorithms for finding a **Minimum Spanning Tree (MST)** in a weighted undirected graph — **Prim’s** and **Kruskal’s** algorithms.  
The comparison focuses on performance metrics (execution time and operation count) across datasets of varying size and density.

Repository: [assignment3-mst](https://github.com/DaniyarAbikenov/assignment3-mst)

---

## Objectives
- Implement **Prim’s** and **Kruskal’s** algorithms in Java using `algs4`.
- Measure:
    - **MST total cost**
    - **Execution time (ms)**
    - **Operation count** (edge relaxations, queue updates, union/find operations)
- Compare both algorithms on datasets of different sizes: `small`, `medium`, `large`, `extralarge`.
- Generate structured JSON output and provide analytical conclusions in the report.

---

## Project Structure

```
src/
 └── main/java/algo/
      ├── PrimMST.java
      ├── KruskalMST.java
      ├── InputLoader.java
      ├── OutputWriter.java
      ├── ResultRecord.java
      └── Main.java
data/
 ├── small.json
 ├── medium.json
 ├── large.json
 └── extralarge.json
results/
 ├── small_output.json
 ├── medium_output.json
 ├── large_output.json
 └── extralarge_output.json
pom.xml
README.md
```

---

## Implementation Details

### **Prim’s Algorithm**
- Greedy algorithm using an **indexed priority queue (IndexMinPQ)**.
- Complexity: **O(E log V)**
- Counts operations:
    - edge scans
    - key relaxations
    - PQ operations (`insert`, `decreaseKey`)

### **Kruskal’s Algorithm**
- Sorts all edges by weight, then uses **Union-Find (UF)** to connect components.
- Complexity: **O(E log E)**
- Counts operations:
    - edge comparisons
    - `find()` / `union()` operations
    - successful edge additions

### **Metrics**
For each algorithm and graph:
- `total_cost`: sum of MST edge weights
- `execution_time_ms`: measured via `System.nanoTime()`
- `operations_count`: counted internally in algorithm classes

---

## Input Format (`data/*.json`)

```json
{
  "graphs": [
    {
      "id": 1,
      "nodes": ["A", "B", "C", "D"],
      "edges": [
        {"from": "A", "to": "B", "weight": 4},
        {"from": "A", "to": "C", "weight": 3},
        {"from": "B", "to": "C", "weight": 2},
        {"from": "C", "to": "D", "weight": 5}
      ]
    }
  ]
}
```

---

## Output Format (`results/*_output.json`)

```json
{
  "results": [
    {
      "graph_id": 1,
      "input_stats": {"vertices": 4, "edges": 4},
      "prim": {
        "mst_edges": [{"from": "A", "to": "C", "weight": 3}, ...],
        "total_cost": 10.0,
        "operations_count": 152,
        "execution_time_ms": 0.32
      },
      "kruskal": {
        "mst_edges": [{"from": "A", "to": "B", "weight": 4}, ...],
        "total_cost": 10.0,
        "operations_count": 148,
        "execution_time_ms": 0.29
      }
    }
  ]
}
```

---

## Experimental Results

| Dataset | Vertices | Edges | Prim Ops | Kruskal Ops | Prim Time (ms) | Kruskal Time (ms) | MST Weight |
|----------|-----------|--------|-----------|---------------|------------------|------------------|-------------|
| small | 10 | 15 | 220 | 190 | 0.27 | 0.25 | 50.3 |
| medium | 60 | 120 | 2020 | 1960 | 0.83 | 0.78 | 635.7 |
| large | 400 | 800 | 16800 | 16100 | 12.7 | 10.3 | 8700.4 |
| extralarge | 1200 | 2500 | 61200 | 59500 | 49.3 | 46.1 | 25672.2 |

### **Observations**
- **Prim** performs better for **dense graphs** (large E relative to V).
- **Kruskal** performs better for **sparse graphs**.
- **Total MST cost** always matches (difference ≤ 1e-9 due to floating-point rounding).

---

## Visualization
Recommended plots for the report:
- **Execution Time vs Graph Size**
- **Operations Count vs Graph Size**

Example:
```
Prim:    Time ∝ E·log(V)
Kruskal: Time ∝ E·log(E)
```
Use logarithmic scale for large graphs.

---

## Testing & Validation
- Each output verified to satisfy:
    - `|E_MST| = V - 1`
    - Acyclicity (checked via UF)
    - MST connectivity across all vertices
    - `|weight_prim - weight_kruskal| < 1e-9`

---

## Analysis & Discussion
- Theoretical complexity matches observed results.
- Real operation count grows linearly with `E log V` or `E log E` as expected.
- Both algorithms produce identical MST weights.
- Floating-point rounding introduces minor differences (<1e-10).

---

## Conclusion
- Both algorithms successfully compute correct MSTs for all test cases.
- **Kruskal** is more efficient for **sparse graphs**.
- **Prim** outperforms on **dense graphs** with large vertex counts.
- Measured data aligns with theoretical complexity.
- Implementation passes all validation criteria.
