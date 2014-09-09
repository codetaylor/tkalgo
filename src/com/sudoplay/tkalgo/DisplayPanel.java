package com.sudoplay.tkalgo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JPanel;

import com.sudoplay.math.Vector2i;
import com.sudoplay.math.delaunay.DT_Triangle;
import com.sudoplay.math.delaunay.DelaunayTriangulation;
import com.sudoplay.tkalgo.EdgeList.Edge;
import com.sudoplay.tkalgo.Main.State;

@SuppressWarnings("serial")
public class DisplayPanel extends JPanel {

  private int displayGrid;

  public void setDisplayGrid(int size) {
    this.displayGrid = size;
  }

  private State state = State.NONE;

  public void setState(State state) {
    this.state = state;
  }

  private ArrayList<Cell> cells;

  public void setCells(ArrayList<Cell> cells) {
    this.cells = cells;
  }

  private ArrayList<Cell> fillerCells;

  public void setFillerCells(ArrayList<Cell> cells) {
    this.fillerCells = cells;
  }

  private CellBounds bounds;

  public void setCellBounds(CellBounds bounds) {
    this.bounds = bounds;
  }

  private DelaunayTriangulation dt;

  public void setDT(DelaunayTriangulation dt) {
    this.dt = dt;
  }

  private ArrayList<Edge> minTree;

  public void setMinTree(ArrayList<Edge> minTree) {
    this.minTree = minTree;
  }

  private ArrayList<RoomConnection> connections;

  public void setConnections(ArrayList<RoomConnection> connections) {
    this.connections = connections;
  }

  private ArrayList<Cell> masterCells;

  public void setMasterCells(ArrayList<Cell> cells) {
    this.masterCells = cells;
  }

  private ArrayList<String> outText;

  public void setOutText(ArrayList<String> outText) {
    this.outText = outText;
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);

    Graphics2D g2 = (Graphics2D) g;

    clear(g2);

    switch (state) {
    case NONE:
    case INIT:
      break;
    case GENERATE:
    case SEPARATE:
      drawCells(g2, Cell.TYPE_NONE, cells);
      drawCells(g2, Cell.TYPE_OVERLAP, cells);
      break;
    case FILL:
      drawCells(g2, Cell.TYPE_FILL, fillerCells);
      drawCells(g2, Cell.TYPE_NONE, cells);
      drawCells(g2, Cell.TYPE_OVERLAP, cells);
      drawBounds(g2, bounds);
      break;
    case SELECT:
      drawCells(g2, Cell.TYPE_FILL, fillerCells);
      drawCells(g2, Cell.TYPE_NONE, cells);
      drawCells(g2, Cell.TYPE_ROOM, cells);
      drawCells(g2, Cell.TYPE_OVERLAP, cells);
      drawBounds(g2, bounds);
      break;
    case TRIANGULATE:
      drawCells(g2, Cell.TYPE_FILL, fillerCells);
      drawCells(g2, Cell.TYPE_NONE, cells);
      drawCells(g2, Cell.TYPE_ROOM, cells);
      drawCells(g2, Cell.TYPE_OVERLAP, cells);
      drawBounds(g2, bounds);
      drawDT(g2, dt);
      break;
    case MINSPAN:
      drawCells(g2, Cell.TYPE_FILL, fillerCells);
      drawCells(g2, Cell.TYPE_NONE, cells);
      drawCells(g2, Cell.TYPE_ROOM, cells);
      drawCells(g2, Cell.TYPE_OVERLAP, cells);
      drawBounds(g2, bounds);
      drawDT(g2, dt);
      drawMinTree(g2, minTree);
      break;
    case LOOPS:
      drawCells(g2, Cell.TYPE_FILL, fillerCells);
      drawCells(g2, Cell.TYPE_NONE, cells);
      drawCells(g2, Cell.TYPE_ROOM, cells);
      drawCells(g2, Cell.TYPE_OVERLAP, cells);
      drawBounds(g2, bounds);
      drawMinTree(g2, minTree);
      break;
    case CONNECTION:
      drawCells(g2, Cell.TYPE_FILL, fillerCells);
      drawCells(g2, Cell.TYPE_NONE, cells);
      drawCells(g2, Cell.TYPE_ROOM, cells);
      drawCells(g2, Cell.TYPE_OVERLAP, cells);
      drawBounds(g2, bounds);
      drawMinTree(g2, minTree);
      drawConnections(g2, connections);
      break;
    case INTERSECTION:
      drawCells(g2, Cell.TYPE_FILL, fillerCells);
      drawCells(g2, Cell.TYPE_NONE, cells);
      drawCells(g2, Cell.TYPE_ROOM, cells);
      drawCells(g2, Cell.TYPE_OVERLAP, cells);
      drawMasterCells(g2, masterCells, true);
      drawBounds(g2, bounds);
      drawConnections(g2, connections);
      break;
    case FINAL:
      drawMasterCells(g2, masterCells, false);
      break;
    default:
      break;
    }

    drawText(g2, outText);

  }

  private void drawConnections(Graphics2D g2, ArrayList<RoomConnection> connections) {
    ArrayList<Vector2i> points;
    g2.setPaint(Color.BLUE);
    g2.setStroke(new BasicStroke(3));
    for (RoomConnection con : connections) {
      points = con.getPoints();
      int x1, y1, x2, y2;
      x1 = points.get(0).x * displayGrid;
      y1 = points.get(0).y * displayGrid;
      x2 = points.get(1).x * displayGrid;
      y2 = points.get(1).y * displayGrid;
      g2.drawLine(x1, y1, x2, y2);
      if (points.size() == 3) {
        x1 = points.get(1).x * displayGrid;
        y1 = points.get(1).y * displayGrid;
        x2 = points.get(2).x * displayGrid;
        y2 = points.get(2).y * displayGrid;
        g2.drawLine(x1, y1, x2, y2);
      }
    }
    g2.setStroke(new BasicStroke(1));
  }

  private void drawBounds(Graphics2D g2, CellBounds bounds) {
    int x1, y1, x2, y2;
    g2.setPaint(Color.WHITE);
    x1 = bounds.getLeft() * displayGrid;
    y1 = bounds.getBottom() * displayGrid;
    x2 = bounds.getWidth() * displayGrid;
    y2 = bounds.getHeight() * displayGrid;
    g2.drawRect(x1, y1, x2, y2);
  }

  private void drawText(Graphics2D g2, ArrayList<String> outputText) {
    String line;
    g2.setPaint(Color.WHITE);
    g2.setFont(getFont().deriveFont(11f));
    for (int i = outputText.size() - 1; i >= 0; i--) {
      line = outputText.get(i);
      g2.drawString(line, 8, (i + 1) * 11);
    }
  }

  private void drawMinTree(Graphics2D g2, ArrayList<Edge> minTree) {
    if (minTree != null) {
      int x1, y1, x2, y2;
      g2.setPaint(Color.GREEN);
      g2.setStroke(new BasicStroke(5));
      for (EdgeList.Edge e : minTree) {
        x1 = (int) e.getP1().x() * displayGrid;
        y1 = (int) e.getP1().y() * displayGrid;
        x2 = (int) e.getP2().x() * displayGrid;
        y2 = (int) e.getP2().y() * displayGrid;
        g2.drawLine(x1, y1, x2, y2);
      }
      g2.setStroke(new BasicStroke(1));
    }
  }

  private void drawDT(Graphics2D g2, DelaunayTriangulation dt) {
    if (dt != null && dt.size() >= 4) {
      int x1, y1, x2, y2;
      g2.setPaint(Color.YELLOW);
      Iterator<DT_Triangle> it = dt.trianglesIterator();
      while (it.hasNext()) {
        DT_Triangle tri = it.next();
        x1 = (int) tri.p1().x() * displayGrid;
        y1 = (int) tri.p1().y() * displayGrid;
        x2 = (int) tri.p2().x() * displayGrid;
        y2 = (int) tri.p2().y() * displayGrid;
        g2.drawLine(x1, y1, x2, y2);
        if (!tri.isHalfplane()) {
          x1 = (int) tri.p2().x() * displayGrid;
          y1 = (int) tri.p2().y() * displayGrid;
          x2 = (int) tri.p3().x() * displayGrid;
          y2 = (int) tri.p3().y() * displayGrid;
          g2.drawLine(x1, y1, x2, y2);
          x1 = (int) tri.p3().x() * displayGrid;
          y1 = (int) tri.p3().y() * displayGrid;
          x2 = (int) tri.p1().x() * displayGrid;
          y2 = (int) tri.p1().y() * displayGrid;
          g2.drawLine(x1, y1, x2, y2);
        }
      }
    }
  }

  private void drawCells(Graphics2D g2, int type, ArrayList<Cell> cells) {
    for (Cell cell : cells) {
      if (cell.type == type) {
        drawCell(g2, cell);
      }
    }
  }

  private void drawMasterCells(Graphics2D g2, ArrayList<Cell> cells, boolean highlight) {
    if (highlight) {
      g2.setPaint(Color.GREEN);
      for (Cell cell : cells) {
        int xm, ym, x1, y1, x2, y2;

        // vertical
        for (int i = 1; i < cell.getWidth(); i++) {
          xm = (i + cell.getLeft()) * displayGrid;
          y1 = cell.getTop() * displayGrid;
          y2 = cell.getBottom() * displayGrid;
          g2.drawLine(xm, y1, xm, y2);
        }

        // horizontal
        for (int i = 1; i < cell.getHeight(); i++) {
          ym = (i + cell.getBottom()) * displayGrid;
          x1 = cell.getLeft() * displayGrid;
          x2 = cell.getRight() * displayGrid;
          g2.drawLine(x1, ym, x2, ym);
        }

        x1 = cell.getLeft() * displayGrid;
        y1 = cell.getBottom() * displayGrid;
        x2 = cell.getWidth() * displayGrid;
        y2 = cell.getHeight() * displayGrid;
        g2.drawRect(x1, y1, x2, y2);
      }
    } else {
      drawCells(g2, Cell.TYPE_FILL, cells);
      drawCells(g2, Cell.TYPE_NONE, cells);
      drawCells(g2, Cell.TYPE_ROOM, cells);
    }
  }

  private void clear(Graphics2D g2) {
    g2.setPaint(Color.BLACK);
    g2.fillRect(0, 0, getWidth(), getHeight());
  }

  private void drawCell(Graphics2D g2, Cell cell) {

    int xm, ym, x1, y1, x2, y2;

    if (cell.type == Cell.TYPE_ROOM) {
      g2.setPaint(Color.RED);
    } else {
      g2.setPaint(Color.decode("#333366"));
    }

    // vertical
    for (int i = 1; i < cell.getWidth(); i++) {
      xm = (i + cell.getLeft()) * displayGrid;
      y1 = cell.getTop() * displayGrid;
      y2 = cell.getBottom() * displayGrid;
      g2.drawLine(xm, y1, xm, y2);
    }

    // horizontal
    for (int i = 1; i < cell.getHeight(); i++) {
      ym = (i + cell.getBottom()) * displayGrid;
      x1 = cell.getLeft() * displayGrid;
      x2 = cell.getRight() * displayGrid;
      g2.drawLine(x1, ym, x2, ym);
    }

    if (cell.type == Cell.TYPE_OVERLAP) {
      g2.setPaint(Color.GREEN);
    } else if (cell.type == Cell.TYPE_FILL) {
      g2.setPaint(Color.DARK_GRAY);
    } else if (cell.type == Cell.TYPE_ROOM) {
      g2.setPaint(Color.WHITE);
    } else {
      g2.setPaint(Color.decode("#666666"));
    }

    x1 = cell.getLeft() * displayGrid;
    y1 = cell.getBottom() * displayGrid;
    x2 = cell.getWidth() * displayGrid;
    y2 = cell.getHeight() * displayGrid;
    g2.drawRect(x1, y1, x2, y2);
  }

}
