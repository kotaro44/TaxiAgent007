/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiagent007;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author kotaro & Fabio :)
 */
public class DijkstraAlgorithm {

  private final List<Intersection> nodes;
  private final List<Road> edges;
  private Set<Intersection> settledNodes;
  private Set<Intersection> unSettledNodes;
  private Map<Intersection, Intersection> predecessors;
  private Map<Intersection, Integer> distance;

  public DijkstraAlgorithm(Graph graph) {
    // create a copy of the array so that we can operate on this array
    this.nodes = new ArrayList<Intersection>(graph.getVertexes());
    this.edges = new ArrayList<Road>(graph.getEdges());
  }

  public void execute(Intersection source) {
    settledNodes = new HashSet<Intersection>();
    unSettledNodes = new HashSet<Intersection>();
    distance = new HashMap<Intersection, Integer>();
    predecessors = new HashMap<Intersection, Intersection>();
    distance.put(source, 0);
    unSettledNodes.add(source);
    while (unSettledNodes.size() > 0) {
      Intersection node = getMinimum(unSettledNodes);
      settledNodes.add(node);
      unSettledNodes.remove(node);
      findMinimalDistances(node);
    }
  }

  private void findMinimalDistances(Intersection node) {
    List<Intersection> adjacentNodes = getNeighbors(node);
    for (Intersection target : adjacentNodes) {
      if (getShortestDistance(target) > getShortestDistance(node)
          + getDistance(node, target)) {
        distance.put(target, (int)(getShortestDistance(node)
            + getDistance(node, target)));
        predecessors.put(target, node);
        unSettledNodes.add(target);
      }
    }

  }

  private double getDistance(Intersection node, Intersection target) {
    for (Road edge : edges) {
      if (edge.getSource().equals(node)
          && edge.getDestination().equals(target)) {
        return edge.getWeight();
      }
    }
    throw new RuntimeException("Should not happen");
  }

  private List<Intersection> getNeighbors(Intersection node) {
    List<Intersection> neighbors = new ArrayList<Intersection>();
    for (Road edge : edges) {
      if (edge.getSource().equals(node)
          && !isSettled(edge.getDestination())) {
        neighbors.add(edge.getDestination());
      }
    }
    return neighbors;
  }

  private Intersection getMinimum(Set<Intersection> vertexes) {
    Intersection minimum = null;
    for (Intersection vertex : vertexes) {
      if (minimum == null) {
        minimum = vertex;
      } else {
        if (getShortestDistance(vertex) < getShortestDistance(minimum)) {
          minimum = vertex;
        }
      }
    }
    return minimum;
  }

  private boolean isSettled(Intersection vertex) {
    return settledNodes.contains(vertex);
  }

  public int getShortestDistance(Intersection destination) {
    Integer d = distance.get(destination);
    if (d == null) {
      return Integer.MAX_VALUE;
    } else {
      return d;
    }
  }

  /*
   * This method returns the path from the source to the selected target and
   * NULL if no path exists
   */
  public LinkedList<Intersection> getPath(Intersection target) {
    LinkedList<Intersection> path = new LinkedList<Intersection>();
    Intersection step = target;
    // check if a path exists
    if (predecessors.get(step) == null) {
      return null;
    }
    path.add(step);
    while (predecessors.get(step) != null) {
      step = predecessors.get(step);
      path.add(step);
    }
    // Put it into the correct order
    Collections.reverse(path);
    return path;
  }

}
