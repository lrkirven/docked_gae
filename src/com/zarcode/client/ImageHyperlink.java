package com.zarcode.client;

/*
ImageHyperlink component for GWT
Copyright (C) 2006 Alexei Sokolov http://gwt.components.googlepages.com/

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA

*/

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.MouseListenerCollection;
import com.google.gwt.user.client.ui.SourcesMouseEvents;

public class ImageHyperlink extends Hyperlink implements SourcesMouseEvents {

  private MouseListenerCollection mouseListeners;

  public ImageHyperlink(Image img) {
    this(img, "");
  }

  public ImageHyperlink(Image img, String targetHistoryToken) {
    super();
    DOM.appendChild(DOM.getFirstChild(getElement()), img.getElement());
    setTargetHistoryToken(targetHistoryToken);

    img.unsinkEvents(Event.ONCLICK | Event.MOUSEEVENTS);
    sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS);
  }

  public void addMouseListener(MouseListener listener) {
    if (mouseListeners == null)
      mouseListeners = new MouseListenerCollection();
    mouseListeners.add(listener);
  }

  public void removeMouseListener(MouseListener listener) {
    if (mouseListeners != null)
      mouseListeners.remove(listener);
  }

  public void onBrowserEvent(Event event) {
    super.onBrowserEvent(event);
    switch (DOM.eventGetType(event)) {
    case Event.ONMOUSEDOWN:
    case Event.ONMOUSEUP:
    case Event.ONMOUSEMOVE:
    case Event.ONMOUSEOVER:
    case Event.ONMOUSEOUT: {
      if (mouseListeners != null)
        mouseListeners.fireMouseEvent(this, event);
      break;
    }
    }
  }
}  