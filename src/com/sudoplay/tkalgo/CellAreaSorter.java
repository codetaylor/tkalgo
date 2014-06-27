package com.sudoplay.tkalgo;

import java.util.Comparator;

public class CellAreaSorter implements Comparator<Cell> {

  @Override
  public int compare(Cell o1, Cell o2) {
    if (o1.getArea() < o2.getArea()) {
      return 1;
    } else if (o1.getArea() > o2.getArea()) {
      return -1;
    }
    return 0;
  }

}
