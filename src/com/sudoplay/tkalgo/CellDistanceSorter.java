package com.sudoplay.tkalgo;

import java.util.Comparator;

import com.sudoplay.math.Vector2i;

public class CellDistanceSorter implements Comparator<Cell> {

  private Vector2i center = new Vector2i();

  @Override
  public int compare(Cell o1, Cell o2) {
    int d1 = (int) o1.center.getDistanceSquared(center);
    int d2 = (int) o2.center.getDistanceSquared(center);
    if (d1 < d2) {
      return 1;
    } else if (d1 > d2) {
      return -1;
    }
    return 0;
  }
  
  public void setCenter(Vector2i v) {
    center.set(v);
  }

}
