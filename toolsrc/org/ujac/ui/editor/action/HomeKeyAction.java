/*
 * Copyright (C) 2003 by Christian Lauer.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * If you didn't download this code from the following link, you should check if
 * you aren't using an obsolete version:
 * http://sourceforge.net/projects/ujac
 */

package org.ujac.ui.editor.action;

import java.awt.event.ActionEvent;

import org.ujac.ui.editor.TextArea;

/**
 * Name: HomeKeyAction<br>
 * Description: A class handling the home key action (HOME).
 * <br>Log: $Log: HomeKeyAction.java,v $
 * <br>Log: Revision 1.1  2006/11/05 01:22:38  haustein
 * <br>Log: 0.20
 * <br>Log:
 * <br>Log: Revision 1.1  2004/01/20 20:29:56  lauerc
 * <br>Log: Moved user interface components to sub project UJAC-UI.
 * <br>Log:
 * @author $Author: haustein $
 * @version $Revision: 1.1 $
 */
public class HomeKeyAction extends KeyAction {

  /**
   * Constructs a HomeKeyAction instance with no specific arguments.
   * @param textArea The text area.
   */
  public HomeKeyAction(TextArea textArea) {
    super(textArea);
  }

  /**
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(ActionEvent e) {

    if (isCtrlPressed(e)) {
      if (isShiftPressed(e)) {
	      textArea.select(textArea.getMarkPosition(), 0);
	    } else {
	      textArea.setCaretPosition(0);
	    }
    } else {
	    int caret = textArea.getCaretPosition();
	
	    int firstOfLine = textArea.getLineStartOffset(textArea.getCaretLine());
	    int firstVisible = textArea.getLineStartOffset(0);
	
	    if (caret == 0) {
	      textArea.getToolkit().beep();
	      return;
	    } else if (caret == firstVisible) {
	      caret = 0;
	    } else {
	      caret = firstOfLine;
	    }
	
      if (isShiftPressed(e)) {
	      textArea.select(textArea.getMarkPosition(), caret);
      } else {
	      textArea.setCaretPosition(caret);
      }
    }
  }

}
