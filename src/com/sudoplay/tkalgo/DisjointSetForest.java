package com.sudoplay.tkalgo;

public class DisjointSetForest<T> {

  public class Node {

    public T ref;
    public Node parent;
    public int rank;

    public Node(T x) {
      ref = x;
      parent = null;
      rank = 0;
    }

    @Override
    public String toString() {
      return ref.toString() + "[p=" + parent.ref.toString() + ", rank=" + rank + "]";
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((parent == null) ? 0 : parent.hashCode());
      result = prime * result + rank;
      result = prime * result + ((ref == null) ? 0 : ref.hashCode());
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
      @SuppressWarnings("unchecked")
      Node other = (Node) obj;
      if (ref == null) {
        if (other.ref != null) {
          return false;
        }
      } else if (!ref.equals(other.ref)) {
        return false;
      }
      return true;
    }

  }

  public Node makeSet(T x) {
    return new Node(x);
  }

  public void union(Node x, Node y) {
    link(findSet(x), findSet(y));
  }

  public Node findSet(Node node) {
    if (node.parent != null) {
      return findSet(node.parent);
    }
    return node;
  }

  private void link(Node x, Node y) {
    if (x.rank > y.rank) {
      y.parent = x;
    } else {
      x.parent = y;
      if (x.rank == y.rank) {
        y.rank++;
      }
    }
  }
}