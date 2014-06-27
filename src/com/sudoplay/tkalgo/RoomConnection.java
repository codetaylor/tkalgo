package com.sudoplay.tkalgo;

import java.util.ArrayList;

import com.sudoplay.math.Vector2i;

public class RoomConnection {

  private ArrayList<Vector2i> points = new ArrayList<Vector2i>();

  private RoomConnection() {
    //
  }

  public ArrayList<Vector2i> getPoints() {
    return points;
  }

  public static RoomConnection get(Vector2i start, Vector2i end) {
    RoomConnection con = new RoomConnection();
    con.points.add(new Vector2i(start));
    con.points.add(new Vector2i(end));
    return con;
  }

  public static RoomConnection get(Vector2i start, Vector2i elbow, Vector2i end) {
    RoomConnection con = new RoomConnection();
    con.points.add(new Vector2i(start));
    con.points.add(new Vector2i(elbow));
    con.points.add(new Vector2i(end));
    return con;
  }

}
