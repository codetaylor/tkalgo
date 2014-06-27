package com.sudoplay.tkalgo;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.swing.JFrame;

import com.sudoplay.math.Vector2i;
import com.sudoplay.math.delaunay.DT_Point;
import com.sudoplay.math.delaunay.DelaunayTriangulation;
import com.sudoplay.tkalgo.EdgeList.Edge;

public class Main {

  public enum State {
    INIT, READY, NONE, GENERATE, SEPARATE, SELECT, TRIANGULATE, MINSPAN, LOOPS, FILL, CONNECTION, INTERSECTION, FINAL
  }

  private State state = State.INIT;
  private boolean ready = true;

  private static boolean continuous = true;

  private ArrayList<String> outputText = new ArrayList<String>();
  private Vector2i areaSize = new Vector2i(192, 192);
  private Vector2i areaCenter = new Vector2i(areaSize.x >> 1, areaSize.y >> 1);
  private ArrayList<Cell> cells = new ArrayList<Cell>();
  private ArrayList<Cell> fillerCells = new ArrayList<Cell>(6000);
  private ArrayList<Cell> masterCells = new ArrayList<Cell>();
  private ArrayList<RoomConnection> connections = new ArrayList<RoomConnection>();
  private DisplayPanel displayPanel;
  private DelaunayTriangulation dt;
  private ArrayList<EdgeList.Edge> minTree = new ArrayList<EdgeList.Edge>();
  private int displayGrid = 3;
  private Vector2i displaySize = new Vector2i(areaSize).multLocal(displayGrid);
  private CellBounds bounds;
  private ArrayList<Cell> rooms = new ArrayList<Cell>();
  private ArrayList<EdgeList.Edge> discardEdge = new ArrayList<EdgeList.Edge>();
  private DisjointSetForest<DT_Point> forest;
  private ArrayList<EdgeList.Edge> edgeList;

  private int roomMin = 4;
  private int roomArea = 42;
  private Random randSeed = new Random();
  private XORShiftRandom rand;
  private int cellCount = 150;
  private int radius = 16;
  private Vector2i cellSize = new Vector2i(1, 12);
  private float loopPercentage = 0.15f;

  public static void main(String[] args) {
    Main app = new Main();
    app.setup();
    app.run(State.INIT);
  }

  private void setup() {
    JFrame frame = new JFrame();
    frame.setPreferredSize(new Dimension(displaySize.x, displaySize.y));

    displayPanel = new DisplayPanel();
    displayPanel.setPreferredSize(new Dimension(displaySize.x, displaySize.y));

    if (!continuous) {
      frame.addMouseListener(new MouseClickListener() {
        @Override
        protected void onClick(MouseEvent e) {
          nextState();
        }
      });
    }

    frame.add(displayPanel);

    displayPanel.setCells(cells);
    displayPanel.setFillerCells(fillerCells);
    displayPanel.setMasterCells(masterCells);
    displayPanel.setOutText(outputText);
    displayPanel.setConnections(connections);
    displayPanel.setMinTree(minTree);

    displayPanel.setDisplayGrid(displayGrid);

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
    frame.setLocationRelativeTo(null);
  }

  private void run(State state) {

    out(state.toString());

    /*
     * Temp vars
     */
    Vector2i v = new Vector2i();

    switch (state) {

    case INIT:
      /*
       * Clear vars and repaint
       */
      cells.clear();
      fillerCells.clear();
      masterCells.clear();
      outputText.clear();
      connections.clear();
      minTree.clear();
      rooms.clear();
      discardEdge.clear();
      forest = new DisjointSetForest<DT_Point>();

      bounds = null;
      out(state.toString());
      out("   Cleared");
      rand = getRandom();
      repaint(State.INIT);
      ready = true;
      if (continuous) {
        nextState();
      } else {
        break;
      }

    case GENERATE:
      /*
       * Generate cells
       */
      out("   Target cell count: " + cellCount);
      out("   Generating cells...");
      for (int i = 0; i < cellCount; i++) {
        sleep(10);
        Generator.generateCell(rand, cellSize, areaCenter, radius, cells);
        repaint(State.GENERATE);
      }
      out("   Cells generated: " + cells.size());
      repaint(State.GENERATE);
      ready = true;
      if (continuous) {
        nextState();
      } else {
        break;
      }

    case SEPARATE:
      /*
       * Separation steering behavior
       */
      out("   Separating overlapping cells...");
      int overlap = 0;
      while (Generator.isAnyOverlap(cells)) {
        sleep(30);
        Generator.moveCells(rand, cells);
        repaint(State.SEPARATE);
        overlap++;
      }
      out("   Overlap passes: " + overlap);
      repaint(State.SEPARATE);
      ready = true;
      if (continuous) {
        nextState();
      } else {
        break;
      }

    case FILL:
      /*
       * Fill vacant area with 1x1 filler cells
       */
      out("   Filling empty space...");
      bounds = CellBounds.get(cells);
      displayPanel.setCellBounds(bounds);
      out("   " + bounds.toString());
      for (int x = bounds.getLeft(); x < bounds.getRight(); x++) {
        for (int y = bounds.getBottom(); y < bounds.getTop(); y++) {
          if (Generator.getCell(v.set(x, y), cells) == null) {
            Cell cell = new Cell();
            cell.center.set(x, y);
            cell.type = Cell.TYPE_FILL;
            fillerCells.add(cell);
            if (fillerCells.size() % 100 == 0)
              repaint(State.FILL);
          }
        }
      }
      out("   Filler cells: " + fillerCells.size());
      out("   Total cells: " + (fillerCells.size() + cells.size()));
      repaint(State.FILL);
      ready = true;
      if (continuous) {
        nextState();
      } else {
        break;
      }

    case SELECT:
      /*
       * Flag cells over an area threshold as rooms
       */
      out("   Selecting rooms...");
      for (Cell cell : cells) {
        if (cell.getArea() > roomArea) {
          rooms.add(cell);
          cell.type = Cell.TYPE_ROOM;
          repaint(State.SELECT);
          sleep(40);
        }
      }
      out("   Rooms discovered: " + rooms.size());
      out("   Non-rooms left: " + (cells.size() - rooms.size()));
      /*
       * Ensure that the minimum room count is reached
       */
      if (rooms.size() < roomMin) {
        ArrayList<Cell> list = new ArrayList<Cell>(cells);
        if (!rooms.isEmpty()) {
          list.removeAll(rooms);
        }
        Collections.sort(list, new CellAreaSorter());
        int augment = 0;
        Cell cell;
        while (rooms.size() < roomMin) {
          cell = list.remove(0);
          cell.type = Cell.TYPE_ROOM;
          rooms.add(cell);
          augment++;
        }
        out("   Extra rooms added: " + augment);
      } else {
        out("   No extra rooms added");
      }
      repaint(State.SELECT);
      ready = true;
      if (continuous) {
        nextState();
      } else {
        break;
      }

    case TRIANGULATE:
      /*
       * Generate the Delaunay Triangulation using room center points
       */
      out("   Generating DT...");
      dt = new DelaunayTriangulation();
      displayPanel.setDT(dt);
      for (Cell room : rooms) {
        dt.insertPoint(new DT_Point(room.center.x, room.center.y));
        repaint(State.TRIANGULATE);
        sleep(100);
      }
      /*
       * Generate a list of edges using a disjoint-set forest. List is sorted by
       * edge length, increasing.
       */
      edgeList = new EdgeList(dt, forest).getList();
      out("   Edges: " + edgeList.size());
      repaint(State.TRIANGULATE);
      ready = true;
      if (continuous) {
        nextState();
      } else {
        break;
      }

    case MINSPAN:
      /*
       * Generate a minimum spanning tree using the edge list
       */
      out("   Generating min-span tree...");
      while (edgeList.size() > 0) {
        EdgeList.Edge edge = edgeList.remove(edgeList.size() - 1);
        if (!forest.findSet(edge.getN1()).equals(forest.findSet(edge.getN2()))) {
          minTree.add(edge);
          forest.union(edge.getN1(), edge.getN2());
          repaint(State.MINSPAN);
          sleep(100);
        } else {
          discardEdge.add(edge);
        }
      }
      out("   Edges: " + minTree.size());
      out("   Discarded edges: " + discardEdge.size());
      repaint(State.MINSPAN);
      ready = true;
      if (continuous) {
        nextState();
      } else {
        break;
      }

    case LOOPS:
      /*
       * Add a percentage of discarded edges into to the min tree
       */
      int loopsAdded = 0;
      int loopsToAdd = (int) (loopPercentage * discardEdge.size());
      minTree.add(discardEdge.remove(discardEdge.size() - 1));
      loopsToAdd--;
      loopsAdded++;
      repaint(State.LOOPS);
      sleep(100);
      Collections.shuffle(discardEdge, rand);
      while (loopsToAdd > 0) {
        minTree.add(discardEdge.remove(discardEdge.size() - 1));
        loopsToAdd--;
        loopsAdded++;
        repaint(State.LOOPS);
        sleep(100);
      }
      out("   Loop edges added: " + loopsAdded);
      repaint(State.LOOPS);
      ready = true;
      if (continuous) {
        nextState();
      } else {
        break;
      }

    case CONNECTION:
      /*
       * Connect the rooms
       */
      Vector2i start = new Vector2i();
      Vector2i end = new Vector2i();
      int straight = 0;
      int elbow = 0;
      while (minTree.size() > 0) {
        Edge e = minTree.remove(minTree.size() - 1);
        Generator.getVectorFromDTPoint(e.getP1(), start);
        Generator.getVectorFromDTPoint(e.getP2(), end);
        if (start.x == end.x || start.y == end.y) {
          // straight connection
          connections.add(RoomConnection.get(start, end));
          straight++;
        } else {
          if (rand.nextBoolean()) {
            v.set(start.x, end.y);
          } else {
            v.set(end.x, start.y);
          }
          connections.add(RoomConnection.get(start, v, end));
          elbow++;
        }
        sleep(50);
        repaint(State.CONNECTION);
      }
      out("   Straight connections: " + straight);
      out("   L connections: " + elbow);
      out("   Total connections: " + connections.size());
      repaint(State.CONNECTION);
      ready = true;
      if (continuous) {
        nextState();
      } else {
        break;
      }

    case INTERSECTION:
      /*
       * Walk the connections, test for intersection
       */
      out("   Testing intersection...");
      ArrayList<Cell> intersecting = new ArrayList<Cell>();
      while (connections.size() > 0) {
        int extraWidth = 1;
        RoomConnection con = connections.remove(connections.size() - 1);
        ArrayList<Vector2i> p = con.getPoints();
        Generator.getIntersectingCells(p.get(0), p.get(1), cells, intersecting, extraWidth);
        Generator.getIntersectingCells(p.get(0), p.get(1), fillerCells, intersecting, extraWidth);
        if (p.size() == 3) {
          Generator.getIntersectingCells(p.get(1), p.get(2), cells, intersecting, extraWidth);
          Generator.getIntersectingCells(p.get(1), p.get(2), fillerCells, intersecting, extraWidth);
        }
        cells.removeAll(intersecting);
        fillerCells.removeAll(intersecting);
        masterCells.addAll(intersecting);
        sleep(50);
        repaint(State.INTERSECTION);
      }
      out("   Intersected cells: " + masterCells.size());
      repaint(State.INTERSECTION);
      ready = true;
      if (continuous) {
        nextState();
      } else {
        break;
      }

    case FINAL:
      /*
       * Final display
       */
      repaint(State.FINAL);
      ready = true;
      if (continuous) {
        sleep(2000);
        nextState();
      } else {
        break;
      }

    default:
      if (continuous) {
        nextState();
      } else {
        break;
      }
    }

  }

  private void repaint(State state) {
    displayPanel.setState(state);
    displayPanel.paintImmediately(0, 0, displaySize.x, displaySize.y);
  }

  private void sleep(int time) {
    try {
      Thread.sleep(time);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void out(String s) {
    System.out.println(s);
    outputText.add(s);
  }

  private XORShiftRandom getRandom() {
    long seed = randSeed.nextLong();
    seed = 42;
    out("   Seed: " + seed);
    return new XORShiftRandom(seed);
  }

  private void nextState() {
    if (ready) {
      ready = false;
      switch (state) {
      case INIT:
        state = State.GENERATE;
        break;
      case GENERATE:
        state = State.SEPARATE;
        break;
      case SEPARATE:
        state = State.FILL;
        break;
      case FILL:
        state = State.SELECT;
        break;
      case SELECT:
        state = State.TRIANGULATE;
        break;
      case TRIANGULATE:
        state = State.MINSPAN;
        break;
      case MINSPAN:
        state = State.LOOPS;
        break;
      case LOOPS:
        state = State.CONNECTION;
        break;
      case CONNECTION:
        state = State.INTERSECTION;
        break;
      case INTERSECTION:
        state = State.FINAL;
        break;
      case FINAL:
        state = State.INIT;
        break;
      default:
        break;
      }
      run(state);
    }
  }

}
