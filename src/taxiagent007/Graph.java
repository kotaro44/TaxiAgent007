/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiagent007;

/**
 *
 * @author kotaro & Fabio :)
 */
import java.util.ArrayList;
import java.util.List;

public class Graph {
  private final List<Intersection> vertexes;
  private final List<Road> edges;

  public Graph(Intersection[] vertexes, List<Road> edges) {
    this.vertexes = new ArrayList<>();
    for(Intersection intersection : vertexes ){
        this.vertexes.add(intersection);
    }
    this.edges = edges;
  }

  public List<Intersection> getVertexes() {
    return vertexes;
  }

  public List<Road> getEdges() {
    return edges;
  }
 
} 

