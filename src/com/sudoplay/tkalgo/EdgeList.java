package com.sudoplay.tkalgo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import com.sudoplay.math.delaunay.DT_Point;
import com.sudoplay.math.delaunay.DT_Triangle;
import com.sudoplay.math.delaunay.DelaunayTriangulation;

public class EdgeList {

  public class Edge implements Comparable<Edge> {

    private final DisjointSetForest<DT_Point>.Node n1;
    private final DisjointSetForest<DT_Point>.Node n2;
    private final int distanceSquared;

    public Edge(DisjointSetForest<DT_Point>.Node n1, DisjointSetForest<DT_Point>.Node n2) {
      this.n1 = n1;
      this.n2 = n2;
      int dx = (int) (n2.ref.x() - n1.ref.x());
      int dy = (int) (n2.ref.y() - n1.ref.y());
      distanceSquared = dx * dx + dy * dy;
    }

    public int getDistanceSquared() {
      return distanceSquared;
    }

    public DT_Point getP1() {
      return n1.ref;
    }

    public DT_Point getP2() {
      return n2.ref;
    }

    public DisjointSetForest<DT_Point>.Node getN1() {
      return n1;
    }

    public DisjointSetForest<DT_Point>.Node getN2() {
      return n2;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((n1 == null) ? 0 : n1.hashCode());
      result = prime * result + ((n2 == null) ? 0 : n2.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      Edge other = (Edge) obj;
      if (!n1.equals(other.n1)) {
        if (!(n1.equals(other.n2) && n2.equals(other.n1))) {
          return false;
        }
      }
      if (!n2.equals(other.n2)) {
        if (!(n1.equals(other.n2) && n2.equals(other.n1))) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int compareTo(Edge o) {
      if (distanceSquared < o.distanceSquared) {
        return 1;
      } else if (distanceSquared > o.distanceSquared) {
        return -1;
      }
      return 0;
    }

  }

  private ArrayList<Edge> list = new ArrayList<Edge>();

  public EdgeList(DelaunayTriangulation dt, DisjointSetForest<DT_Point> forest) {
    Iterator<DT_Point> itp = dt.verticesIterator();
    HashMap<DT_Point, DisjointSetForest<DT_Point>.Node> nodeMap = new HashMap<DT_Point, DisjointSetForest<DT_Point>.Node>();
    DT_Point p;
    while (itp.hasNext()) {
      p = itp.next();
      nodeMap.put(p, forest.makeSet(p));
    }

    Edge e;
    DisjointSetForest<DT_Point>.Node n1, n2, n3;
    DT_Triangle tri;
    Iterator<DT_Triangle> itt = dt.trianglesIterator();
    while (itt.hasNext()) {
      tri = itt.next();
      n1 = nodeMap.get(tri.p1());
      n2 = nodeMap.get(tri.p2());
      e = new Edge(n1, n2);
      if (!list.contains(e)) {
        list.add(e);
      }
      if (!tri.isHalfplane()) {
        n3 = nodeMap.get(tri.p3());
        e = new Edge(n2, n3);
        if (!list.contains(e)) {
          list.add(e);
        }
        e = new Edge(n3, n1);
        if (!list.contains(e)) {
          list.add(e);
        }
      }
    }
    Collections.sort(list);
  }

  public ArrayList<Edge> getList() {
    return list;
  }

}
