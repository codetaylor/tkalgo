package com.sudoplay.tkalgo;

import com.sudoplay.math.Vector2i;

public class Cell {

  public static final int TYPE_OVERLAP = -1;
  public static final int TYPE_NONE = 0;
  public static final int TYPE_ROOM = 1;
  public static final int TYPE_FILL = 2;

  /**
   * Center of the cell.
   */
  public Vector2i center = new Vector2i();

  /**
   * Cell size is distance from center to any axis edge.
   */
  public Vector2i size = new Vector2i();

  public int type = TYPE_NONE;

  public int getLeft() {
    return center.x - size.x;
  }

  public int getRight() {
    return center.x + size.x + 1;
  }

  public int getTop() {
    return center.y + size.y + 1;
  }

  public int getBottom() {
    return center.y - size.y;
  }

  public int getWidth() {
    return size.x * 2 + 1;
  }

  public int getHeight() {
    return size.y * 2 + 1;
  }

  public int getArea() {
    return size.x * size.y * 4;
  }

  @Override
  public String toString() {
    return "Cell [center=" + center + ", size=" + size + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((center == null) ? 0 : center.hashCode());
    result = prime * result + ((size == null) ? 0 : size.hashCode());
    result = prime * result + type;
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
    Cell other = (Cell) obj;
    if (center == null) {
      if (other.center != null) {
        return false;
      }
    } else if (!center.equals(other.center)) {
      return false;
    }
    if (size == null) {
      if (other.size != null) {
        return false;
      }
    } else if (!size.equals(other.size)) {
      return false;
    }
    if (type != other.type) {
      return false;
    }
    return true;
  }

}
