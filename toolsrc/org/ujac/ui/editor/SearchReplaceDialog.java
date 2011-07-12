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

package org.ujac.ui.editor;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.ujac.ui.BaseDialog;

/**
 * Name: SearchReplaceDialog<br>
 * Description: A search/replace dialog for the TextArea.
 * <br>Log: $Log: SearchReplaceDialog.java,v $
 * <br>Log: Revision 1.1  2006/11/05 01:22:38  haustein
 * <br>Log: 0.20
 * <br>Log:
 * <br>Log: Revision 1.2  2004/01/22 00:01:31  lauerc
 * <br>Log: Improved search dialog handling.
 * <br>Log:
 * <br>Log: Revision 1.1  2004/01/20 20:29:56  lauerc
 * <br>Log: Moved user interface components to sub project UJAC-UI.
 * <br>Log:
 * @author $Author: haustein $
 * @version $Revision: 1.1 $
 */
public class SearchReplaceDialog extends BaseDialog {
  
  /** The text area. */
  private TextArea textArea;

  /**
   * Gets the text to find.
   * @return The text to find.
   */
  public String getFindText() {
    String findText = findCombo.getEditor().getItem().toString();
    return findText;
  }
  /**
   * Sets the text to find.
   * @param findText The new text to find.
   */
  public void setFindText(String findText) {
    updateComboBox(findCombo, findText);
  }
  
  /**
   * Gets the find direction.
   * @return The current find direction.
   */
  public int getFindDirection() {
    if (backwardRadioButton.isSelected()) {
      return TextArea.DIRECTION_BACKWARD;
    }
    return TextArea.DIRECTION_FORWARD;
  }
    
  /**
   * Gets the case sensitive search flag.
   * @return The case sensitive flag specified through the find dialog.
   */
  public boolean isCaseSensitiveSearch() {
    return caseSensitiveCheck.isSelected();
  }

  /**
   * Gets the wrapped search flag.
   * @return The wrapped search flag specified through the find dialog.
   */
  public boolean isWrappedSearch() {
    return wrapSearchCheck.isSelected();
  }
  
  /**
   * @see java.awt.Dialog#show()
   */
  public void show() {
    super.show();
    // setting focus to find field
    findCombo.requestFocus();
  }

  /**
   * Constructs a SearchReplaceDialog instance with specific attributes.
   * @param textArea The text area.
   */  
  public SearchReplaceDialog(TextArea textArea) {
    super(textArea.getFrame(), false);
    setTitle("Find/Replace");
    this.textArea = textArea;
    initComponents();
    forwardRadioButton.setSelected(true);
    wrapSearchCheck.setSelected(true);

    // defining key bindings
    InputMap inputMap = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape");
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");

    ActionMap actionMap = this.getRootPane().getActionMap();
    actionMap.put("escape", new AbstractAction() {
      public void actionPerformed(ActionEvent evt) {
        closeButtonActionPerformed(evt);
      }
    });
    actionMap.put("enter", new AbstractAction() {
      public void actionPerformed(ActionEvent evt) {
        findButtonActionPerformed(evt);
      }
    });

    // centering dialog over text area
    centerDialog();
  }
  
  /** 
   * This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  private void initComponents() {//GEN-BEGIN:initComponents
    java.awt.GridBagConstraints gridBagConstraints;

    directionButtonGroup = new javax.swing.ButtonGroup();
    jPanel1 = new javax.swing.JPanel();
    findButton = new javax.swing.JButton();
    replaceFindButton = new javax.swing.JButton();
    replaceButton = new javax.swing.JButton();
    replaceAllButton = new javax.swing.JButton();
    closeButton = new javax.swing.JButton();
    mainPanel = new javax.swing.JPanel();
    findLabel = new javax.swing.JLabel();
    replaceLabel = new javax.swing.JLabel();
    findCombo = new javax.swing.JComboBox();
    replaceWithCombo = new javax.swing.JComboBox();
    directionPanel = new javax.swing.JPanel();
    forwardRadioButton = new javax.swing.JRadioButton();
    backwardRadioButton = new javax.swing.JRadioButton();
    optionsPanel = new javax.swing.JPanel();
    caseSensitiveCheck = new javax.swing.JCheckBox();
    wrapSearchCheck = new javax.swing.JCheckBox();

    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(java.awt.event.WindowEvent evt) {
        closeDialog(evt);
      }
    });

    jPanel1.setLayout(new java.awt.GridBagLayout());

    findButton.setMnemonic('n');
    findButton.setText("Find");
    findButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        findButtonActionPerformed(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.weightx = 0.5;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(findButton, gridBagConstraints);

    replaceFindButton.setMnemonic('d');
    replaceFindButton.setText("Replace/Find");
    replaceFindButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        replaceFindButtonActionPerformed(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 0.5;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(replaceFindButton, gridBagConstraints);

    replaceButton.setMnemonic('R');
    replaceButton.setText("Replace");
    replaceButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        replaceButtonActionPerformed(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.weightx = 0.5;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(replaceButton, gridBagConstraints);

    replaceAllButton.setMnemonic('A');
    replaceAllButton.setText("Replace All");
    replaceAllButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        replaceAllButtonActionPerformed(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 0.5;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(replaceAllButton, gridBagConstraints);

    closeButton.setText("Close");
    closeButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        closeButtonActionPerformed(evt);
      }
    });

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    jPanel1.add(closeButton, gridBagConstraints);

    getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

    mainPanel.setLayout(new java.awt.GridBagLayout());

    findLabel.setText("Find:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
    mainPanel.add(findLabel, gridBagConstraints);

    replaceLabel.setText("Replace with:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
    mainPanel.add(replaceLabel, gridBagConstraints);

    findCombo.setEditable(true);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    mainPanel.add(findCombo, gridBagConstraints);

    replaceWithCombo.setEditable(true);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    mainPanel.add(replaceWithCombo, gridBagConstraints);

    directionPanel.setLayout(new java.awt.GridBagLayout());

    directionPanel.setBorder(new javax.swing.border.TitledBorder("Direction"));
    forwardRadioButton.setText("Forward");
    directionButtonGroup.add(forwardRadioButton);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 0.5;
    directionPanel.add(forwardRadioButton, gridBagConstraints);

    backwardRadioButton.setText("Backward");
    directionButtonGroup.add(backwardRadioButton);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.weightx = 0.5;
    directionPanel.add(backwardRadioButton, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    mainPanel.add(directionPanel, gridBagConstraints);

    optionsPanel.setLayout(new java.awt.GridBagLayout());

    optionsPanel.setBorder(new javax.swing.border.TitledBorder("Options"));
    caseSensitiveCheck.setText("Case Sensitive");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 0.5;
    optionsPanel.add(caseSensitiveCheck, gridBagConstraints);

    wrapSearchCheck.setText("Wrap Search");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.weightx = 0.5;
    optionsPanel.add(wrapSearchCheck, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
    mainPanel.add(optionsPanel, gridBagConstraints);

    getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

    pack();
  }//GEN-END:initComponents

  private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
    this.hide();
  }//GEN-LAST:event_closeButtonActionPerformed

  private void replaceAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceAllButtonActionPerformed
    String textToFind = getFindText();
    updateComboBox(findCombo, textToFind);
    String textToReplaceWith = (String) replaceWithCombo.getSelectedItem();
    updateComboBox(replaceWithCombo, textToReplaceWith);
    textArea.replaceAll(textToFind, textToReplaceWith);
  }//GEN-LAST:event_replaceAllButtonActionPerformed

  private void replaceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceButtonActionPerformed
    String textToReplaceWith = (String) replaceWithCombo.getSelectedItem();
    updateComboBox(replaceWithCombo, textToReplaceWith);
    textArea.replace(textToReplaceWith);
  }//GEN-LAST:event_replaceButtonActionPerformed

  private void findButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findButtonActionPerformed
    String textToFind = getFindText();
    updateComboBox(findCombo, textToFind);
    textArea.find(textToFind);
  }//GEN-LAST:event_findButtonActionPerformed

  private void replaceFindButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceFindButtonActionPerformed
    // replace step
    String textToReplaceWith = (String) replaceWithCombo.getSelectedItem();
    updateComboBox(replaceWithCombo, textToReplaceWith);
    textArea.replace(textToReplaceWith);
    // find step
    String textToFind = getFindText();
    updateComboBox(findCombo, textToFind);
    textArea.find(textToFind);
  }//GEN-LAST:event_replaceFindButtonActionPerformed

  /**
   * Updates the list of the given combo box, puts the given latestEntry item on top of the list. 
   * @param combo The combo box to update.
   * @param latestEntry The lates entry to put on top of the selection list.
   */
  private void updateComboBox(JComboBox combo, String latestEntry) {
    int numItems = combo.getItemCount();
    List items = new ArrayList();
    for (int i = 0; i < numItems; i++) {
      String current = (String) combo.getItemAt(i);
      if ((current != null) && !current.equals(latestEntry)) {
        items.add(current);
      }
    }
    numItems = items.size();
    combo.removeAllItems();
    combo.addItem(latestEntry);
    for (int i = 0; i < numItems; i++) {
      combo.addItem(items.get(i));
    }
  }
  
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JRadioButton backwardRadioButton;
  private javax.swing.JCheckBox caseSensitiveCheck;
  private javax.swing.JButton closeButton;
  private javax.swing.ButtonGroup directionButtonGroup;
  private javax.swing.JPanel directionPanel;
  private javax.swing.JButton findButton;
  private javax.swing.JComboBox findCombo;
  private javax.swing.JLabel findLabel;
  private javax.swing.JRadioButton forwardRadioButton;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel mainPanel;
  private javax.swing.JPanel optionsPanel;
  private javax.swing.JButton replaceAllButton;
  private javax.swing.JButton replaceButton;
  private javax.swing.JButton replaceFindButton;
  private javax.swing.JLabel replaceLabel;
  private javax.swing.JComboBox replaceWithCombo;
  private javax.swing.JCheckBox wrapSearchCheck;
  // End of variables declaration//GEN-END:variables
  
}
