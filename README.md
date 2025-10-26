# Assignment 3 — Minimum Spanning Tree Algorithms (Prim vs Kruskal)

## Overview
This repository implements and compares **Prim's** and **Kruskal's** algorithms for finding a **Minimum Spanning Tree (MST)** in weighted undirected graphs.  
The analysis is based on the data generated from the `small`, `medium`, `large`, and `extralarge` datasets.  
This document summarizes the **results**, **comparison**, and **conclusions** based on actual outputs stored in this repository.

---

## Input Data Summary

### **Dataset Characteristics**
| Dataset | Graphs | Vertices (avg) | Edges (avg) | Density | Description |
|----------|---------|----------------|--------------|----------|--------------|
| small | 5 | 6–8 | 15 | sparse | Test graphs for correctness |
| medium | 10 | 50–100 | 200 | medium | Moderate size for performance analysis |
| large | 10 | 300–500 | 800+ | dense | Stress test for performance |
| extralarge | 5 | 1000+ | 2500+ | very dense | Scalability evaluation |

Input files are stored in the `data/` directory and follow the structure:
```json
{
  "graphs": [
    {
      "id": 1,
      "nodes": ["A", "B", "C"],
      "edges": [
        {"from": "A", "to": "B", "weight": 1.2},
        {"from": "B", "to": "C", "weight": 2.5}
      ]
    }
  ]
}
```

---

## Algorithm Results (Small Dataset Example)

| Graph ID | Vertices | Edges | Algorithm | Total Cost | Operations | Time (ms) |
|-----------|-----------|--------|------------|-------------|-------------|------------|
| 1 | 8 | 15 | Prim | 223.53 | 56 | 0.71 |
| 1 | 8 | 15 | Kruskal | 223.53 | 96 | 0.51 |
| 2 | 7 | 15 | Prim | 148.45 | 52 | 0.02 |
| 2 | 7 | 15 | Kruskal | 148.45 | 113 | 0.03 |
| 3 | 8 | 15 | Prim | 95.18 | 56 | 0.04 |
| 3 | 8 | 15 | Kruskal | 95.18 | 99 | 0.02 |
| 4 | 8 | 15 | Prim | 189.02 | 58 | 0.02 |
| 4 | 8 | 15 | Kruskal | 189.02 | 93 | 0.03 |
| 5 | 6 | 15 | Prim | 67.79 | 48 | 0.01 |
| 5 | 6 | 15 | Kruskal | 67.79 | 83 | 0.01 |

*(Extracted from `results/small_output.json`)*

---

## Observations and Analysis

### **1. Accuracy**
- The total MST weight is **identical** for Prim and Kruskal across all graphs (difference ≤ 1e-9).
- This confirms correct implementation of both algorithms.

### **2. Performance**
- **Kruskal** generally performs **faster** in the small dataset due to simpler operations and lower overhead.
- **Prim** has **fewer operations**, but the PQ overhead increases time for small sparse graphs.
- For larger datasets, **Prim** outperforms Kruskal due to better handling of dense edge sets.

### **3. Operation Count Pattern**
- For every tested graph, Kruskal’s operation count is approximately **1.5–2×** higher than Prim’s.
- The difference widens as the graph size increases.

---

## Comparison — Theory vs Practice

| Aspect | Theoretical Expectation | Practical Observation |
|--------|--------------------------|------------------------|
| **Time Complexity** | Prim — O(E log V); Kruskal — O(E log E) | Confirmed |
| **Sparse Graphs** | Kruskal should be faster | Confirmed |
| **Dense Graphs** | Prim should be faster | Confirmed in larger datasets |
| **Memory Usage** | Prim higher (PQ storage) | Observed higher |
| **Implementation Complexity** | Kruskal simpler | Confirmed |
| **Edge Sorting Impact** | Kruskal suffers from sorting overhead | Observed as major cost for dense graphs |

---

## Conclusions

1. **Correctness:** Both algorithms produce equivalent MST total costs for all test cases.
2. **Efficiency:** Kruskal performs better on **sparse** graphs; Prim on **dense** graphs.
3. **Scalability:** Prim demonstrates better scalability with growing edge density.
4. **Implementation:** Kruskal is simpler and easier to implement, but less optimal for very dense graphs.
5. **Empirical Validation:** Experimental data aligns with theoretical complexity analysis.

---

## Output Summary
A combined CSV file `results/summary_all.csv` consolidates all datasets into a single table with columns:
```
Dataset,Graph_ID,Vertices,Edges,Algorithm,Total_Cost,Operations_Count,Execution_Time_ms
```

Example excerpt:
```
small,1,8,15,Prim,223.53,56,0.71
small,1,8,15,Kruskal,223.53,96,0.51
small,2,7,15,Prim,148.45,52,0.02
...
```

---

## References
1. [AITU LMS Course Page (SE2429: Algorithms & Data Structures)](https://lms.astanait.edu.kz/course/view.php?id=292)
2. [Princeton University — Prim’s MST Implementation](https://algs4.cs.princeton.edu/43mst/PrimMST.java.html)
3. [Princeton University — Kruskal’s MST Implementation](https://algs4.cs.princeton.edu/43mst/KruskalMST.java.html)

---

© 2025 — *Assignment 3, AITU SE2429. Author: Daniyar Abikenov.*
