/**
 * Geotag
 * Copyright (C) 2007 Andreas Schneider
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.fibs.geotag.gui.settings;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.fibs.geotag.Messages;
import org.fibs.geotag.Settings;
import org.fibs.geotag.Settings.SETTING;
import org.fibs.geotag.gpsbabel.GPSBabel;
import org.fibs.geotag.util.FontUtil;

/**
 * Second version of the SettingsDialog. This time using a JTree
 * 
 * @author Andreas Schneider
 * 
 */
@SuppressWarnings("serial")
public class SettingsDialog extends JDialog implements TreeSelectionListener {

  /** The parent component, use to position this dialog */
  private JFrame parent;

  /** A list of SettingsPanels for changing settings */
  List<SettingsPanel> panelList = new ArrayList<SettingsPanel>();

  /** The panel containing the JTree and one or no SettingsPanel */
  private JPanel treeAndSettingsPanel;

  /** The SettingsPanel currently displayed (if any) */
  private SettingsPanel visibleSettingsPanel = null;

  /** The JTree displaying the available settings */
  private JTree tree;

  /**
   * @param parent
   */
  public SettingsDialog(JFrame parent) {
    // true == modal
    super(parent, Messages.getString("SettingsDialog.Settings"), true); //$NON-NLS-1$
    this.parent = parent;
    setLayout(new BorderLayout());
    treeAndSettingsPanel = new JPanel();
    treeAndSettingsPanel.setLayout(new BorderLayout());
    DefaultMutableTreeNode top = new DefaultMutableTreeNode(Messages
        .getString("SettingsDialog.Settings")); //$NON-NLS-1$
    createTreeNodes(top);
    tree = new JTree(top);
    tree.getSelectionModel().setSelectionMode(
        TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree.addTreeSelectionListener(this);
    JScrollPane treeView = new JScrollPane(tree);
    treeAndSettingsPanel.add(treeView, BorderLayout.CENTER);
    JPanel buttonPanel = createButtonPanel();
    add(treeAndSettingsPanel, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);
  }

  /**
   * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
   */
  @Override
  public void valueChanged(TreeSelectionEvent event) {
    // Get the last path element of the selection.
    // This method is only useful because our selection model
    // allows single selection only.
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
        .getLastSelectedPathComponent();

    if (node == null) {
      // Nothing is selected.
      return;
    }

    Object nodeInfo = node.getUserObject();
    if (node.isLeaf()) {
      SettingsPanel panel = (SettingsPanel) nodeInfo;
      if (visibleSettingsPanel != null) {
        treeAndSettingsPanel.remove(visibleSettingsPanel);
        treeAndSettingsPanel.validate();
      }
      treeAndSettingsPanel.add(panel, BorderLayout.SOUTH);
      visibleSettingsPanel = panel;
      pack();
    }
  }

  /**
   * @return The panel containing the OK and Cancel buttons
   */
  private JPanel createButtonPanel() {
    // Finally a Panel with OK and Cancel buttons
    // The flow layout looks very ugly, have to find something else
    JPanel buttonPanel = new JPanel(new FlowLayout());
    String ok = Messages.getString("SettingsDialog.OK"); //$NON-NLS-1$
    String cancel = Messages.getString("SettingsDialog.Cancel"); //$NON-NLS-1$
    JButton okButton = new JButton(ok);
    okButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        for (SettingsPanel settingsPanel : panelList) {
          SETTING setting = settingsPanel.getSetting();
          String value = settingsPanel.getValue();
          if (!value.equals(Settings.get(setting, settingsPanel
              .getDefaultValue()))) {
            System.out.println(setting.toString() + '=' + value);
            Settings.put(setting, value);
          }
        }
        Settings.flush();
        dispose();
      }
    });
    buttonPanel.add(okButton);
    JButton cancelButton = new JButton(cancel);
    cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // just close the dialog
        dispose();
      }
    });
    buttonPanel.add(cancelButton);
    FontMetrics fontMetrics = okButton.getFontMetrics(okButton.getFont());
    int preferredWidth = Math.max(fontMetrics.stringWidth(ok), fontMetrics
        .stringWidth(cancel)) * 2;
    Dimension preferredSize = okButton.getPreferredSize();
    preferredSize.width = preferredWidth;
    okButton.setPreferredSize(preferredSize);
    cancelButton.setPreferredSize(preferredSize);
    Dimension minimumSize = parent.getSize();
    minimumSize.height /= 2;
    minimumSize.width /= 2;
    setMinimumSize(minimumSize);
    return buttonPanel;
  }

  /**
   * Add a panel as a child of a parent node in the tree. Keeps track of panels
   * added.
   * 
   * @param parentNode
   * @param panel
   */
  private void addPanel(DefaultMutableTreeNode parentNode, SettingsPanel panel) {
    panelList.add(panel);
    DefaultMutableTreeNode leaf = new DefaultMutableTreeNode(panel);
    parentNode.add(leaf);
  }

  /**
   * Add all the nodes to the tree
   * 
   * @param top
   *          The top node of the tree
   */
  private void createTreeNodes(DefaultMutableTreeNode top) {
    DefaultMutableTreeNode general = new DefaultMutableTreeNode(Messages
        .getString("SettingsDialog.GeneralSettings")); //$NON-NLS-1$

    FontSettingsPanel font = new FontSettingsPanel(
        parent,
        Messages.getString("SettingsDialog.Font"), SETTING.FONT, FontUtil.fontToID(UIManager.getLookAndFeel().getDefaults().getFont("Table.font"))); //$NON-NLS-1$ //$NON-NLS-2$
    addPanel(general, font);

    BooleanSettingsPanel tooltipThumbnails = new BooleanSettingsPanel(
        parent,
        Messages.getString("SettingsDialog.ThumbnailsInTooltips"), SETTING.TUMBNAILS_IN_TOOLTIPS, true); //$NON-NLS-1$
    addPanel(general, tooltipThumbnails);

    BooleanSettingsPanel xmpOnly = new BooleanSettingsPanel(
        parent,
        Messages.getString("SettingsDialog.XmpFilesOnly"), SETTING.XMP_FILES_ONLY, false); //$NON-NLS-1$
    addPanel(general, xmpOnly);

    BooleanSettingsPanel updates = new BooleanSettingsPanel(
        parent,
        Messages.getString("SettingsDialog.CheckForUpdates"), SETTING.CHECK_FOR_NEW_VERSION, true); //$NON-NLS-1$
    addPanel(general, updates);

    top.add(general);

    DefaultMutableTreeNode external = new DefaultMutableTreeNode(Messages
        .getString("SettingsDialog.ExternalPrograms")); //$NON-NLS-1$

    FileSettingsPanel browser = new FileSettingsPanel(parent, Messages
        .getString("SettingsDialog.Browser"), SETTING.BROWSER, ""); //$NON-NLS-1$//$NON-NLS-2$
    addPanel(external, browser);

    DefaultMutableTreeNode exiftool = new DefaultMutableTreeNode(Messages
        .getString("SettingsDialog.Exiftool")); //$NON-NLS-1$
    external.add(exiftool);

    FileSettingsPanel exiftoolPath = new FileSettingsPanel(
        parent,
        Messages.getString("SettingsDialog.ExiftoolPath"), SETTING.EXIFTOOL_PATH, "exiftool"); //$NON-NLS-1$ //$NON-NLS-2$
    addPanel(exiftool, exiftoolPath);

    StringSettingsPanel exiftoolArguments = new StringSettingsPanel(
        parent,
        Messages.getString("SettingsDialog.AdditionalExiftoolArguments"), SETTING.EXIFTOOL_ARGUMENTS, ""); //$NON-NLS-1$//$NON-NLS-2$
    addPanel(exiftool, exiftoolArguments);

    DefaultMutableTreeNode gpsbabel = new DefaultMutableTreeNode(Messages
        .getString("SettingsDialog.GPSBabel")); //$NON-NLS-1$
    external.add(gpsbabel);

    FileSettingsPanel gpsbabelPath = new FileSettingsPanel(
        parent,
        Messages.getString("SettingsDialog.GPSBabelPath"), SETTING.GPSBABEL_PATH, "gpsbabel"); //$NON-NLS-1$//$NON-NLS-2$
    addPanel(gpsbabel, gpsbabelPath);

    StringSettingsPanel gpsbabelProtocol = new StringSettingsPanel(
        parent,
        Messages.getString("SettingsDialog.GPSBabelProtocol"), SETTING.GPSBABEL_PROTOCOL, "garmin"); //$NON-NLS-1$//$NON-NLS-2$
    addPanel(gpsbabel, gpsbabelProtocol);

    StringSettingsPanel gpsbabelDevice = new StringSettingsPanel(
        parent,
        Messages.getString("SettingsDialog.GPSBabelDevice"), SETTING.GPSBABEL_DEVICE, GPSBabel.getDefaultDevice()); //$NON-NLS-1$
    addPanel(gpsbabel, gpsbabelDevice);

    FileSettingsPanel dcrawPath = new FileSettingsPanel(parent, Messages
        .getString("SettingsDialog.DcrawPath"), SETTING.DCRAW_PATH, "dcraw"); //$NON-NLS-1$ //$NON-NLS-2$
    addPanel(external, dcrawPath);

    top.add(external);

    DefaultMutableTreeNode export = new DefaultMutableTreeNode(Messages
        .getString("SettingsDialog.Export")); //$NON-NLS-1$
    DefaultMutableTreeNode googleearth = new DefaultMutableTreeNode(Messages
        .getString("SettingsDialog.GoogleEarth")); //$NON-NLS-1$
    BooleanSettingsPanel imagesInKmz = new BooleanSettingsPanel(
        parent,
        Messages.getString("SettingsDialog.ThumbsInKMZ"), SETTING.KMZ_STORE_THUMBNAILS, false); //$NON-NLS-1$
    addPanel(googleearth, imagesInKmz);

    StringSettingsPanel kmlImagePath = new StringSettingsPanel(parent, Messages
        .getString("SettingsDialog.KmlImagePath"), SETTING.KML_IMAGE_PATH, ""); //$NON-NLS-1$ //$NON-NLS-2$
    addPanel(googleearth, kmlImagePath);

    export.add(googleearth);

    top.add(export);

    DefaultMutableTreeNode location = new DefaultMutableTreeNode(Messages
        .getString("SettingsDialog.LookUp")); //$NON-NLS-1$
    DefaultMutableTreeNode geonames = new DefaultMutableTreeNode(Messages
        .getString("SettingsDialog.Geonames")); //$NON-NLS-1$
    IntegerSettingsPanel radius = new IntegerSettingsPanel(parent, Messages
        .getString("SettingsDialog.Radius"), SETTING.GEONAMES_RADIUS_KM, 0); //$NON-NLS-1$
    addPanel(geonames, radius);
    location.add(geonames);

    top.add(location);
  }

  /**
   * open the modal settings dialog
   */
  public void openDialog() {
    pack();
    setLocationRelativeTo(parent);
    setVisible(true);
  }

}
