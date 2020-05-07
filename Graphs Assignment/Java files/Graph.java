//NAME: Vidulash Rajaratnam
//NUMBER: 8190398

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csi2110_project;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Yahya Alaa
 */
public class Graph {
    List<Integer> nodes;
    Map<Integer, List<Integer>> edges;
    
    public Graph(List<Integer> nodes, Map<Integer, List<Integer>> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }
    
    public List<Integer> getGraphNodes() {
        return this.nodes;
    }
    
    public Map<Integer, List<Integer>> getGraphEdges() {
        return this.edges;
    }
}
