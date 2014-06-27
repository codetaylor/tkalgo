package com.sudoplay.tkalgo;

import java.util.ArrayList;
import java.util.Random;

import com.sudoplay.math.FastMath;
import com.sudoplay.math.Vector2i;
import com.sudoplay.math.delaunay.DT_Point;

public class Generator {

  private static final double MAX_GAUSS = 6.418382187553036;

  public static void generateCell(Random rand, Vector2i size, Vector2i center, int radius, ArrayList<Cell> cells) {

    int rSqr = radius * radius;

    int minx = center.x - radius;
    int maxx = center.x + radius;
    int miny = center.y - radius;
    int maxy = center.y + radius;

    int rangex = maxx - minx;
    int rangey = maxy - miny;

    int cx, cy;

    Cell cell = new Cell();

    while (cell.center.getDistanceSquared(center) > rSqr) {
      cell.center.x = rand.nextInt(rangex) + minx;
      cell.center.y = rand.nextInt(rangey) + miny;
    }

    float cRatio = (float) (1.0f - nextClampedGaussian(rand) * 0.33f);
    float cSize = (float) (nextClampedGaussian(rand) * (size.y - size.x + 1) + size.x);

    if (rand.nextBoolean()) {
      cx = (int) cSize;
      cy = (int) (cSize * cRatio);
    } else {
      cx = (int) (cSize * cRatio);
      cy = (int) cSize;
    }

    cell.size.x = cx;
    cell.size.y = cy;

    if (cell.size.x <= 0) {
      cell.size.x = size.x;
    }
    if (cell.size.y <= 0) {
      cell.size.y = size.x;
    }

    cells.add(cell);

  }

  public static void moveCells(Random rand, ArrayList<Cell> cells) {

    Cell cell;
    Vector2i v = new Vector2i();
    for (int i = 0; i < cells.size(); i++) {
      cell = cells.get(i);
      v.set(Vector2i.ZERO);
      for (int j = 0; j < cells.size(); j++) {
        if (i == j)
          continue;
        if (isOverlap(cell, cells.get(j))) {
          moveCell(rand, cell, cells.get(j), v);
        }
      }
      cell.center.x += v.x;
      cell.center.y += v.y;
    }

  }

  private static void moveCell(Random rand, Cell c1, Cell c2, Vector2i v) {
    int dx = FastMath.clamp(c1.center.x - c2.center.x, -1, 1);
    int dy = FastMath.clamp(c1.center.y - c2.center.y, -1, 1);
    if (dx == 0 && dy == 0) {
      switch (rand.nextInt(4)) {
      case 0:
        dx = 1;
        break;
      case 1:
        dx = -1;
        break;
      case 2:
        dy = 1;
        break;
      case 3:
        dy = -1;
        break;
      }
    }
    v.x += dx;
    v.y += dy;
  }

  public static boolean isAnyOverlap(ArrayList<Cell> cells) {
    Cell cell;
    for (int i = 0; i < cells.size(); i++) {
      cell = cells.get(i);
      for (int j = 0; j < cells.size(); j++) {
        if (i == j)
          continue;
        if (isOverlap(cell, cells.get(j))) {
          cell.type = Cell.TYPE_OVERLAP;
          cells.get(j).type = Cell.TYPE_OVERLAP;
          return true;
        } else {
          cell.type = Cell.TYPE_NONE;
          cells.get(j).type = Cell.TYPE_NONE;
        }
      }
    }
    return false;
  }

  public static boolean isOverlap(Cell c1, Cell c2) {
    if (c1.getLeft() < c2.getRight() && c1.getRight() > c2.getLeft() && c1.getBottom() < c2.getTop() && c1.getTop() > c2.getBottom()) {
      return true;
    } else {
      return false;
    }
  }

  public static CellBounds getBounds(ArrayList<Cell> cells) {
    return CellBounds.get(cells);
  }

  public static Cell getCell(Vector2i v, ArrayList<Cell> cells) {
    for (Cell cell : cells) {
      if (isPointInCell(v, cell)) {
        return cell;
      }
    }
    return null;
  }

  public static boolean isPointInCell(Vector2i v, Cell cell) {
    return v.x >= cell.getLeft() && v.x < cell.getRight() && v.y >= cell.getBottom() && v.y < cell.getTop();
  }

  public static Vector2i getVectorFromDTPoint(DT_Point p, Vector2i v) {
    if (v == null)
      v = new Vector2i();
    return v.set((int) p.x(), (int) p.y());
  }

  public static ArrayList<Cell> getIntersectingCells(Vector2i p1, Vector2i p2, ArrayList<Cell> cells, ArrayList<Cell> store, int extraWidth) {
    if (store == null) {
      store = new ArrayList<Cell>();
    }
    Vector2i v = new Vector2i();
    int x1, y1, x2, y2;
    if (p1.x == p2.x) {
      y1 = Math.min(p1.y, p2.y) - extraWidth;
      y2 = Math.max(p1.y, p2.y) + extraWidth;
      for (int x = p1.x - extraWidth; x <= p1.x + extraWidth; x++) {
        for (int y = y1; y <= y2; y++) {
          Cell cell = Generator.getCell(v.set(x, y), cells);
          if (cell != null && !store.contains(cell)) {
            store.add(cell);
          }
        }
      }
    } else if (p1.y == p2.y) {
      x1 = Math.min(p1.x, p2.x) - extraWidth;
      x2 = Math.max(p1.x, p2.x) + extraWidth;
      for (int y = p1.y - extraWidth; y <= p1.y + extraWidth; y++) {
        for (int x = x1; x <= x2; x++) {
          Cell cell = Generator.getCell(v.set(x, y), cells);
          if (cell != null && !store.contains(cell)) {
            store.add(cell);
          }
        }
      }
    }
    return store;
  }

  public static double nextClampedGaussian(Random rand) {
    double g = rand.nextGaussian();
    g = g < 0 ? -g : g;
    g /= MAX_GAUSS;
    return g > 1.0 ? 1.0 : g;
  }
}
