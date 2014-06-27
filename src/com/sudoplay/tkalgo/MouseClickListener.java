package com.sudoplay.tkalgo;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public abstract class MouseClickListener implements MouseListener {

  @Override
  public void mouseClicked(MouseEvent e) {
    onClick(e);
  }

  @Override
  public void mouseEntered(MouseEvent e) {
    // TODO Auto-generated method stub

  }

  @Override
  public void mouseExited(MouseEvent e) {
    // TODO Auto-generated method stub

  }

  @Override
  public void mousePressed(MouseEvent e) {
    // TODO Auto-generated method stub

  }

  @Override
  public void mouseReleased(MouseEvent e) {
    // TODO Auto-generated method stub

  }

  protected abstract void onClick(MouseEvent e);

}
