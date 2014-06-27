package com.sudoplay.tkalgo;

import java.util.ArrayList;

public class CellBounds {

  private int xmin = Integer.MAX_VALUE;
  private int xmax = Integer.MIN_VALUE;
  private int ymin = Integer.MAX_VALUE;
  private int ymax = Integer.MIN_VALUE;

  private CellBounds() {
    //
  }

  public int getLeft() {
    return xmin;
  }

  public int getRight() {
    return xmax;
  }

  public int getBottom() {
    return ymin;
  }

  public int getTop() {
    return ymax;
  }

  public int getWidth() {
    return xmax - xmin;
  }

  public int getHeight() {
    return ymax - ymin;
  }

  public static CellBounds get(ArrayList<Cell> cells) {
    CellBounds cb = new CellBounds();
    int t;
    for (Cell cell : cells) {
      t = cell.getLeft();
      if (t < cb.xmin) {
        cb.xmin = t;
      }
      t = cell.getRight();
      if (t > cb.xmax) {
        cb.xmax = t;
      }
      t = cell.getBottom();
      if (t < cb.ymin) {
        cb.ymin = t;
      }
      t = cell.getTop();
      if (t > cb.ymax) {
        cb.ymax = t;
      }
    }
    return cb;
  }

  @Override
  public String toString() {
    return "CellBounds [xmin=" + xmin + ", xmax=" + xmax + ", ymin=" + ymin
        + ", ymax=" + ymax + "]";
  }

}
