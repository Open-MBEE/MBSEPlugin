/*
 * 
 *    (c) European Southern Observatory, 2011
 *    Copyright by ESO 
 *    All rights reserved
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA
 *    
 *    $Id: MBSETreeAction.java 2961 2011-11-04 18:40:44Z jesdabod $
 *
 */

package org.eso.sdd.mbse.doc.actions;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import org.eso.sdd.mbse.doc.algo.DocBookNode;
import org.eso.sdd.mbse.doc.algo.DocBookVector;
import org.eso.sdd.mbse.doc.algo.TreeViewGenerator;
import org.eso.sdd.mbse.doc.algo.Utilities;
import org.eso.sdd.mbse.doc.algo.genUtility;
import org.eso.sdd.mbse.doc.ui.GridLayout2;
import org.eso.sdd.mbse.doc.ui.TextAreaRenderer;
import org.eso.sdd.mbse.doc.ui.TreeTransferHandler;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.project.ProjectEventListenerAdapter;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.PropertyID;
import com.nomagic.magicdraw.properties.PropertyManager;
import com.nomagic.magicdraw.ui.ProjectWindow;
import com.nomagic.magicdraw.ui.ProjectWindowsManager;
import com.nomagic.magicdraw.ui.WindowComponentInfo;
import com.nomagic.magicdraw.ui.WindowsManager;
import com.nomagic.magicdraw.ui.browser.Node;
import com.nomagic.magicdraw.ui.browser.Tree;
import com.nomagic.magicdraw.ui.browser.WindowComponentContent;
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.magicdraw.ui.dialogs.specifications.SpecificationDialogManager;
import com.nomagic.ui.ProgressStatusRunner;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdmodels.Model;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Comment;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;

@SuppressWarnings("serial")
public class MBSEShowEditPanelAction extends DefaultBrowserAction {

	private PropertyManager properties = null;
	static ProjectWindow rendered = null;
	public static JTable table;

	static String dirPath = System.getProperty("user.home") + "/MBSE/tmp";

	// tree model data vector
	public static Vector<?> rows = null;
	public static Vector<String> columns = null;
	public static JTree treeView = null;
	private static JSplitPane cenPanel = null;
	private static JPanel searchBar = null;
	private static ProjectWindowsManager windowsManager = null;
	private static ArrayList<DefaultMutableTreeNode> openNodes = null;

	private static ArrayList<DefaultMutableTreeNode> searchResult = null;
	final static Color ERROR_COLOR = Color.PINK;
	final static Color OK_COLOR = Color.green;
	final static Color NORMAL_COLOR = Color.white;
	private static int resultIndex = 0;

	private static int tableDiagramCount = 1;

	private static Tree tree = null;

	public MBSEShowEditPanelAction() {
		super("", "SE2:Show Edit Panel", null, null);
		properties = new PropertyManager();
		properties.addProperty(new BooleanProperty(
				PropertyID.SHOW_DIAGRAM_INFO, true));


	}

	private static void displayWarning(String text) {
		JOptionPane.showMessageDialog(null, text);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//Tree tree = getTree();
		tree = getTree();
		Node node = null;
		Object userObject = null;
		if (tree.getSelectedNodes().length > 1) {
			displayWarning("MBSE Plugin: cannot do multiple selection");
			return;
		}

		try {

			Project theProject = Application.getInstance().getProject();

			Model theModel = theProject.getModel();

			if (theModel == null) {
				displayWarning("MBSE: YOUR MODEL IS EMPTY");
			}
		} catch (NullPointerException x) {
			displayWarning("NullPointerException");
		}

		node = tree.getSelectedNodes()[0];
		userObject = node.getUserObject();
		generateEditPanel(userObject);


	}

	public static void generateEditPanel(Object userObject) {

		Application.getInstance().addProjectEventListener(new ProjectEventListenerAdapter()
		{
			@Override
			public void projectActivated(Project project)
			{
			}

			@Override
			public void projectDeActivated(Project project)
			{		    	
				if(windowsManager != null){
					//displayWarning(project.getHumanName());
					windowsManager.removeWindow(rendered.getId());
					windowsManager = null;
				}
			}
		});

		// render main frame
		windowsManager = Application.getInstance()
				.getMainFrame().getProjectWindowsManager();
		WindowComponentInfo info = new WindowComponentInfo("MY_WINDOW",
				"Edit Panel", null, WindowsManager.SIDE_EAST,
				WindowsManager.STATE_DOCKED, true);

		rows = new Vector<Object>();
		columns = new Vector<String>();

		columns.addElement("Content");

		// Create the nodes.
		DocBookNode top = new DocBookNode(((NamedElement) userObject).getName() + " <<"
				+ genUtility.getReferredType((Element)userObject) + ">>");

		TreeViewGenerator k1 = new TreeViewGenerator();
		TreeViewGenerator.setStartElement((NamedElement) userObject);
		TreeViewGenerator.setMainNode(top);

		try {
			ProgressStatusRunner.runWithProgressStatus(k1,
					"Editor View Generation", true, 1000);
		} catch (Exception e1) {
			e1.printStackTrace();
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e1.printStackTrace(pw);

			displayWarning("An error occurred while generating the Editor Panel JTree\n" + sw.toString());

		}

		try {

			if (k1.getStatus() == 1) {
				displayWarning("Aborted by user ");
			} else {

				DefaultTreeModel treeModel = new DefaultTreeModel(top);
				treeView = new JTree(treeModel);
				treeView.setDragEnabled(true);
				treeView.setDropMode(DropMode.ON_OR_INSERT);
				treeView.setTransferHandler(new TreeTransferHandler());

				DefaultTreeCellRenderer renderers = (DefaultTreeCellRenderer) treeView
						.getCellRenderer();
				renderers.setTextSelectionColor(Color.black);
				renderers.setBackgroundSelectionColor(Color.LIGHT_GRAY);

				//create Table element
				createTable();

				// set startpanel view
				startPanelView();

				GridLayout2 experimentLayout = new GridLayout2(2, 2);

				final JPanel editPanel = new JPanel();
				editPanel.setLayout(experimentLayout);
				// construct panel
				cenPanel = new JSplitPane(
						JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(treeView),
						new JScrollPane(table));
				cenPanel.setOneTouchExpandable(true);
				cenPanel.setDividerLocation(300);

				//image bar
				//imgPanel =new ImagePanel("test/img.png");
				//imgPanel.setSize(400, 400);

				//mainPane = new JSplitPane(
				//JSplitPane.HORIZONTAL_SPLIT, cenPanel,
				//imgPanel);

				//mainPane.setOneTouchExpandable(true);
				//imgPanel.setVisible(false);

				editPanel.add(cenPanel);

				// search bar
				createSearchBar();
				editPanel.add(searchBar);


				// Add tree event listeners
				addTreeActions();

				WindowComponentContent content = new WindowComponentContent() {

					@Override
					public java.awt.Component getWindowComponent() {
						return editPanel;
					}

					@Override
					public java.awt.Component getDefaultFocusComponent() {
						return editPanel;
					}
				};

				rendered = new ProjectWindow(info, content);
				windowsManager.addWindow(rendered);
				windowsManager.updateWindow(rendered);


			}// not aborted

		} catch (Exception e1) {
			e1.printStackTrace();
    		StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e1.printStackTrace(pw);

			genUtility.displayWarning(sw.toString());

		}
	}

	private static void addTreeActions() {
		treeView.addTreeWillExpandListener(new TreeWillExpandListener() {

			@Override
			public void treeWillExpand(TreeExpansionEvent evt)
					throws ExpandVetoException {
			}

			@Override
			public void treeWillCollapse(TreeExpansionEvent evt)
					throws ExpandVetoException {
				// JTree tree = (JTree) evt.getSource();

				// Get the path that was collapsed
				TreePath path = evt.getPath();

				DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
						.getLastPathComponent();



				doCollapsed(node, path);
				System.out.println(node.toString() + " collapsing.");
			}

			private void doCollapsed(DefaultMutableTreeNode node,
					TreePath path) {

				Enumeration<DefaultMutableTreeNode> li = node.children();
				while (li.hasMoreElements()) {
					DocBookNode e = (DocBookNode) li.nextElement();
					TreePath newPath = null;
					if (e.children() != null) {

						Object[] p = path.getPath();
						List<Object> k = new ArrayList<Object>();
						for (Object c : p) {
							// JOptionPane.showMessageDialog(null,
							// c.toString());
							k.add(c);
						}

						k.add(e);
						newPath = new TreePath(k.toArray());

						// treeView.collapsePath(newPath);
						// JOptionPane.showMessageDialog(null, newPath);

						doCollapsed(e, newPath);
					}

					removeTableElement(e);
					treeView.collapsePath(newPath);

				}

			}

			public void removeTableElement(DocBookNode e) {

				int row = contains(e.getRep(), rows, columns);
				if (row != -1) {
					((DefaultTableModel) table.getModel())
					.removeRow(row);

				}

			}
		});

		treeView.addTreeExpansionListener(new TreeExpansionListener() {

			@Override
			public void treeExpanded(TreeExpansionEvent evt) {
				//JTree tree = (JTree) evt.getSource();
				// Get the path that was expanded
				TreePath path = evt.getPath();
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
						.getLastPathComponent();

				try{
					doExpanded(node);
				}catch(Exception e){
					System.out.println("exception found " + e.getMessage());
				}

				System.out.println(node.toString() + " expanded.");

			}

			@Override
			public void treeCollapsed(TreeExpansionEvent evt) {

			}

			private void doExpanded(DefaultMutableTreeNode node) {

				// get parent row
				int startIndex = contains(
						((DocBookNode) node).getRep(), rows, columns);

				Enumeration<DefaultMutableTreeNode> li = node.children();
				while (li.hasMoreElements()) {
					DocBookNode e = (DocBookNode) li.nextElement();
					
					if(Utilities.isFigureDiagram(e.getElement())){
						if(e.getRep().elementAt(0)==null){						
							BufferedImage img = TreeViewGenerator.getFigureDiagramBufferedImage(e.getElement());
							e.getRep().setImage(img);

							JLabel picLabel = new JLabel(e.getRep().getImageText(),
									new ImageIcon(img), JLabel.CENTER);
							picLabel.setVerticalTextPosition(JLabel.BOTTOM);
							picLabel.setHorizontalTextPosition(JLabel.CENTER);
							e.getRep().set(0, picLabel);
						}
					}
					if(Utilities.isFigureImage(e.getElement())){
						if(e.getRep().elementAt(0)==null){
							BufferedImage img = TreeViewGenerator.getFigureImageBufferedImage(e.getElement());
							e.getRep().setImage(img);
							
							JLabel picLabel = new JLabel(e.getRep().getImageText(),new ImageIcon(img),JLabel.CENTER);
							picLabel.setVerticalTextPosition(JLabel.BOTTOM);
							picLabel.setHorizontalTextPosition(JLabel.CENTER);
							e.getRep().set(0, picLabel);
						}
					}
					if(Utilities.isDiagramTable(e.getElement())){
						BufferedImage img = TreeViewGenerator.getDiagramTableBufferedImage(e.getElement());					
						e.getRep().setImage(img);
						
						JLabel picLabel = new JLabel(e.getRep().getImageText(),new ImageIcon(img),JLabel.CENTER);
						picLabel.setVerticalTextPosition(JLabel.BOTTOM);
						picLabel.setHorizontalTextPosition(JLabel.CENTER);
						e.getRep().set(0, picLabel);
					}
					
					addTableElement(e, startIndex);
					
					startIndex++;
				}

			}

			public void addTableElement(DocBookNode e, int startIndex) {

				((DefaultTableModel) table.getModel()).insertRow(
						startIndex + 1, e.getRep());

			}

		});

		treeView.addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent evt) {
				// Get all nodes whose selection status has changed
				TreePath[] paths = evt.getPaths();

				// Iterate through all affected nodes
				for (int i = 0; i < paths.length; i++) {
					if (evt.isAddedPath(i)) {
						// This node has been selected
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeView
								.getLastSelectedPathComponent();
						System.out.println(node.toString()
								+ " selected.");
						//displayWarning(node.toString());

						JScrollPane im = (JScrollPane) cenPanel
								.getComponent(1);
						JViewport ta = (JViewport) im.getComponent(0);
						scrollTo(((DocBookNode) node).getRep(), rows,
								columns, ta, table);

						// open node in containment tree when selected in edit panel tree
						DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeView
								.getModel().getRoot();
						openInContainmentTree(((DocBookNode) node).getRep(), root, false, true);

						windowsManager.updateWindow(rendered);

					} else {
						// This node has been deselected
					}
				}
			}
		});

		treeView.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if ((e.getKeyCode() == KeyEvent.VK_Z)
						&& ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
					// displayWarning("woot! no undo");
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
		});

		treeView.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {

					TreePath p = treeView.getPathForLocation(e.getX(),
							e.getY());
					treeView.setSelectionPath(p);

					DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeView
							.getLastSelectedPathComponent();

					// get row rep
					JTree target = (JTree) e.getSource();

					if (node != null) {
						JPopupMenu popup = createPopUp(((DocBookNode) node)
								.getRep());
						if (popup != null) {
							popup.show(target, e.getX(), e.getY());
						}
					}
				}
			}

			private JPopupMenu createPopUp(final DocBookVector com) {
				JPopupMenu popup = new JPopupMenu();

				Element ne = com.getElement();

				ActionListener menuListener = new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						System.out.println("Popup menu item ["
								+ event.getActionCommand()
								+ "] was pressed.");

						DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeView
								.getModel().getRoot();

						boolean lockprocess = false;

						if (event.getActionCommand().equals(
								"Open Specification Dialog")) {
							openInContainmentTree(com, root, true, false);
							lockprocess = true;

						} else if (event.getActionCommand().equals(
								"Select in containment tree")) {
							openInContainmentTree(com, root, false, true);
							lockprocess = true;
						}

						// check element lockness
						if (com.getElement().isEditable()) {

							performClickAction(event,com);
						}// end editable check
						else {
							if (!lockprocess) {
								displayWarning("This element is locked.");
							}
						}

					}
				};
				JMenuItem item;
				// GENERAL MENU
				popup.add(item = new JMenuItem(
						"Open Specification Dialog"));
				item.setHorizontalTextPosition(JMenuItem.RIGHT);
				item.addActionListener(menuListener);
				popup.add(item = new JMenuItem(
						"Select in containment tree"));
				item.setHorizontalTextPosition(JMenuItem.RIGHT);
				item.addActionListener(menuListener);
				popup.addSeparator();

				// MBSE MENU
				ArrayList<JMenuItem> menuList = genUtility
						.getMBSEMenu(ne);
				for (JMenuItem k : menuList) {
					popup.add(k);
					k.addActionListener(menuListener);
				}

				popup.setLabel("Justification");
				popup.setBorder(new BevelBorder(BevelBorder.RAISED));
				popup.addPopupMenuListener(new PopupPrintListener());

				return popup;
			}

			// An inner class to show when popup events occur
			class PopupPrintListener implements PopupMenuListener {
				@Override
				public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
					System.out.println("Popup menu will be visible!");
				}

				@Override
				public void popupMenuWillBecomeInvisible(
						PopupMenuEvent e) {
					System.out.println("Popup menu will be invisible!");
				}

				@Override
				public void popupMenuCanceled(PopupMenuEvent e) {
					System.out.println("Popup menu is hidden!");
				}
			}

		});

	}

	private static void createSearchBar() {

		searchBar = new JPanel();
		searchBar.setLayout(new FlowLayout());

		final JTextField searchBox = new JTextField();
		final JCheckBox caseBox = new JCheckBox("Case Sensitive");
		searchBox.setMinimumSize(new Dimension(300, 20));
		searchBox.setPreferredSize(new Dimension(300, 20));
		searchBox.setMaximumSize(new Dimension(300, 20));
		searchBox.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent keyEvent) {

			}

			@Override
			public void keyReleased(KeyEvent keyEvent) {


				if ((keyEvent.getKeyCode() == KeyEvent.VK_ENTER)) {
					//displayWarning("next");
					nextAction();
					searchBox.requestFocusInWindow();
				}
				else{

					DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeView
							.getModel().getRoot();
					String term = searchBox.getText().trim();

					if (!caseBox.isSelected()) {
						searchResult = null;
						resultIndex = 0;

						if (searchResult == null) {
							searchResult = new ArrayList<DefaultMutableTreeNode>();
						}

						//check root first
						String rootCont = ((DocBookNode) root).getRep()
								.toString();
						if (rootCont.matches("(?i:.*" + term + ".*)")) {
							searchResult.add(root);
						}

						search("no", term, root);
						if (searchResult.size() != 0) {
							searchBox.setBackground(OK_COLOR);
						} else {
							searchBox.setBackground(ERROR_COLOR);
							searchResult = null;
						}
					} else {
						searchResult = null;
						resultIndex = 0;

						if (searchResult == null) {
							searchResult = new ArrayList<DefaultMutableTreeNode>();
						}

						//check root first
						String rootCont = ((DocBookNode) root).getRep()
								.toString();
						if (rootCont.contains(term)) {
							searchResult.add(root);
						}

						search("yes", term, root);
						if (searchResult.size() != 0) {
							searchBox.setBackground(OK_COLOR);
						} else {
							searchBox.setBackground(ERROR_COLOR);
							searchResult = null;
						}
					}

					if (searchBox.getText().length() <= 0) {
						searchBox.setBackground(NORMAL_COLOR);
						searchResult = null;
					}
				}

			}

			@Override
			public void keyTyped(KeyEvent keyEvent) {
			}

		});

		JButton nextButton = new JButton("Next");
		nextButton.setSize(12, 2);
		nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				nextAction();

			}
		});

		JButton prevButton = new JButton("Previous");
		prevButton.setSize(12, 2);
		prevButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				previousAction();

			}
		});

		JButton refreshButton = new JButton("Refresh");
		refreshButton.setSize(10, 2);
		refreshButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				generateEditPanel(((DocBookNode)treeView.getModel().getRoot()).getElement());


			}
		});


		// add search bar to editPanel
		searchBar.add(searchBox);
		searchBar.add(nextButton);
		searchBar.add(prevButton);
		searchBar.add(caseBox);
		searchBar.add(refreshButton);
	}

	private static void createTable() {
		DefaultTableModel tabModel;

		// construct table model
		tabModel = new DefaultTableModel();
		tabModel.setDataVector(rows, columns);

		final Vector<Object> mall = tabModel.getDataVector();

		table = new JTable(tabModel) {

			@Override
			public boolean isCellEditable(int rowIndex, int colIndex) {

				/*
				 * DocBookVector m = (DocBookVector) mall.get(rowIndex);
				 * if(m.getElement().isEditable()){ return true; }
				 */

				return false;
			}

			@Override
			public Class<?> getColumnClass(int column) {
				return getValueAt(0, column).getClass();
			}

			@Override
			public Component prepareRenderer(
					TableCellRenderer renderer, int Index_row,
					int Index_col) {
				Component comp = super.prepareRenderer(renderer,
						Index_row, Index_col);
				// even index, selected or not selected
				if (!isCellSelected(Index_row, Index_col)) {
					comp.setBackground(Color.white);
				} else {
					comp.setBackground(Color.lightGray);
				}

				// We want renderer component to be transparent so background image is visible
				//if( comp instanceof JComponent )
				//((JComponent)comp).setOpaque(false);

				return comp;
			}

		};

		CellEditorListener ChangeNotification = new CellEditorListener() {
			@Override
			public void editingCanceled(ChangeEvent e) {
				// JOptionPane.showMessageDialog(null,"editting");
			}

			@Override
			public void editingStopped(ChangeEvent e) {
				// JOptionPane.showMessageDialog(null,"done editting");
			}
		};

		table.getDefaultEditor(String.class).addCellEditorListener(
				ChangeNotification);

		table.getColumnModel().getColumn(0)
		.setCellRenderer(new TextAreaRenderer());

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				if (e.getClickCount() == 1) {
					// get the coordinates of the mouse click
					Point p = e.getPoint();

					// get the row index that contains that coordinate
					int row = table.rowAtPoint(p);

					DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeView
							.getModel().getRoot();

					DocBookVector m = (DocBookVector) mall.get(row);
					
					// open node in containment tree when selected in edit panel
					openInContainmentTree(m, root, false, true);

				}

				if (e.getClickCount() == 2) {
					JTable target = (JTable) e.getSource();
					int row = target.getSelectedRow();

					DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeView
							.getModel().getRoot();

					DocBookVector m = (DocBookVector) mall.get(row);

					
					openInContainmentTree(m, root, false, false);

				}
				// Right mouse click
				else if (SwingUtilities.isRightMouseButton(e)) {
					// get the coordinates of the mouse click
					Point p = e.getPoint();

					// get the row index that contains that coordinate
					int rowNumber = table.rowAtPoint(p);


					// Get the ListSelectionModel of the JTable
					ListSelectionModel model = table
							.getSelectionModel();

					model.setSelectionInterval(rowNumber, rowNumber);

					// get row rep
					JTable target = (JTable) e.getSource();
					DocBookVector m = (DocBookVector) mall
							.get(rowNumber);
					// JOptionPane.showMessageDialog(null,m.getID());

					JPopupMenu popup = createPopUp(m);
					if (popup != null) {
						popup.show(target, e.getX(), e.getY());
					}
				}
			}

			private JPopupMenu createPopUp(final DocBookVector com) {
				JPopupMenu popup = new JPopupMenu();

				Element ne = com.getElement();

				ActionListener menuListener = new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						System.out.println("Popup menu item ["
								+ event.getActionCommand()
								+ "] was pressed.");

						DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeView
								.getModel().getRoot();

						boolean lockprocess = false;

						if (event.getActionCommand().equals("Open Specification Dialog")) {
							openInContainmentTree(com, root, true, false);
							lockprocess = true;
						} else if (event.getActionCommand().equals(
								"Select in containment tree")) {
							openInContainmentTree(com, root, false, true);
							lockprocess = true;
						}

						// check element lockness
						if (com.getElement().isEditable()) {
							performClickAction(event,com);
						}// end editable check
						else if (!lockprocess) {
							displayWarning("This element is locked.");
						}
					}
				};
				JMenuItem item;
				// GENERAL MENU
				popup.add(item = new JMenuItem(
						"Open Specification Dialog"));
				item.setHorizontalTextPosition(JMenuItem.RIGHT);
				item.addActionListener(menuListener);
				popup.add(item = new JMenuItem(
						"Select in containment tree"));
				item.setHorizontalTextPosition(JMenuItem.RIGHT);
				item.addActionListener(menuListener);
				popup.addSeparator();

				// MBSE MENU
				ArrayList<JMenuItem> menuList = genUtility.getMBSEMenu(ne);
				for (JMenuItem k : menuList) {
					popup.add(k);
					k.addActionListener(menuListener);
				}

				popup.setLabel("Justification");
				popup.setBorder(new BevelBorder(BevelBorder.RAISED));
				popup.addPopupMenuListener(new PopupPrintListener());

				return popup;
			}

			// An inner class to show when popup events occur
			class PopupPrintListener implements PopupMenuListener {
				@Override
				public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
					System.out.println("Popup menu will be visible!");
				}

				@Override
				public void popupMenuWillBecomeInvisible(
						PopupMenuEvent e) {
					System.out.println("Popup menu will be invisible!");
				}

				@Override
				public void popupMenuCanceled(PopupMenuEvent e) {
					System.out.println("Popup menu is hidden!");
				}
			}

		});
	}

	protected static void previousAction() {
		if (searchResult != null) {
			int check = resultIndex - 1;
			if (check >= 0) {
				resultIndex--;
			} else {
				resultIndex = searchResult.size()-1;
			}
			DefaultMutableTreeNode nodeTodisplay = searchResult
					.get(resultIndex);
			treeView.expandPath(new TreePath(nodeTodisplay
					.getPath()));
			treeView.setSelectionPath(new TreePath(nodeTodisplay
					.getPath()));
			treeView.scrollPathToVisible(new TreePath(nodeTodisplay
					.getPath()));
			JScrollPane im = (JScrollPane) cenPanel
					.getComponent(1);
			JViewport ta = (JViewport) im.getComponent(0);
			scrollTo(((DocBookNode) nodeTodisplay).getRep(), rows,
					columns, ta, table);
		}
	}

	protected static void nextAction() {
		if (searchResult != null) {
			int check = resultIndex + 1;
			if (check < searchResult.size()) {
				resultIndex++;
			} else {
				resultIndex = 0;
			}
			//displayWarning("here : " + searchResult.toString());
			DefaultMutableTreeNode nodeTodisplay = searchResult
					.get(resultIndex);
			treeView.expandPath(new TreePath(nodeTodisplay
					.getPath()));
			treeView.setSelectionPath(new TreePath(nodeTodisplay
					.getPath()));
			treeView.scrollPathToVisible(new TreePath(nodeTodisplay
					.getPath()));
			JScrollPane im = (JScrollPane) cenPanel
					.getComponent(1);
			JViewport ta = (JViewport) im.getComponent(0);
			scrollTo(((DocBookNode) nodeTodisplay).getRep(), rows,
					columns, ta, table);

		}
	}

	private static void startPanelView() {
		DefaultMutableTreeNode rt = (DefaultMutableTreeNode) treeView
				.getModel().getRoot();
		DocBookNode db = (DocBookNode) rt;
		((DefaultTableModel) table.getModel()).addRow(db.getRep());
		Enumeration<DefaultMutableTreeNode> li = rt.children();
		while (li.hasMoreElements()) {
			db = (DocBookNode) li.nextElement();
			((DefaultTableModel) table.getModel()).addRow(db.getRep());

		}
	}

	public static void scrollTo(DocBookVector rep, Vector rows, Vector<String> columns,
			JViewport port, JTable table) {

		int row = contains(rep, rows, columns);
		if (row != -1) {
			Rectangle cellRect = table.getCellRect(row, 0, true);
			cellRect.height = port.getSize().height;
			table.scrollRectToVisible(cellRect);
			table.setRowSelectionInterval(row, row);
		}

	}

	private static void performClickAction(ActionEvent event, DocBookVector com) {
		// TODO Auto-generated method stub
		if (event.getActionCommand().equals(
				"create Paragraph")) {
			NamedElement ne = (NamedElement) com.getElement();
			CreateParagraphAction tmp = new CreateParagraphAction();
			if (ne != null) {
				tmp.addParagraph(tree, ne, null);
			}
		} else if (event.getActionCommand().equals(
				"create Bibliography")) {
			NamedElement ne = (NamedElement) com.getElement();
			CreateBibliographyAction tmp = new CreateBibliographyAction();
			if (ne != null) {
				tmp.addBibliography(ne);
			}
		} else if (event.getActionCommand().equals(
				"create ProgramListing")) {
			NamedElement ne = (NamedElement) com.getElement();
			CreateProgramListingAction tmp = new CreateProgramListingAction();
			if (ne != null) {
				tmp.addProgramListing(tree, ne, null);
			}
		} else if (event.getActionCommand().equals(
				"create Query")) {
			NamedElement ne = (NamedElement) com.getElement();
			CreateQueryAction tmp = new CreateQueryAction();
			if (ne != null) {
				tmp.addQuery(ne, null);
			}
		} else if (event.getActionCommand().equals(
				"create FigureDiagram")) {
			NamedElement ne = (NamedElement) com.getElement();
			CreateFigureDiagramAction tmp = new CreateFigureDiagramAction();
			if (ne != null) {
				tmp.addFigure(ne);
			}
		} else if (event.getActionCommand().equals(
				"create FigureImage")) {
			NamedElement ne = (NamedElement) com.getElement();
			CreateFigureImageAction tmp = new CreateFigureImageAction();
			if (ne != null) {
				tmp.addFigure(ne);
			}
		} else if (event.getActionCommand().equals(
				"create Section")) {
			NamedElement ne = (NamedElement) com.getElement();
			CreateSectionAction tmp = new CreateSectionAction();
			if (ne != null) {
				tmp.addSection(ne);
			}
		} else if (event.getActionCommand().equals(
				"create Biblioentry")) {
			NamedElement ne = (NamedElement) com.getElement();
			CreateBiblioEntryAction tmp = new CreateBiblioEntryAction();
			if (ne != null) {
				tmp.addBiblioEntry(ne);
			}
		} else if (event.getActionCommand().equals(
				"insert Paragraph after")) {
			Element ne = com.getElement();
			CreateParagraphAction tmp = new CreateParagraphAction();
			if (ne != null) {
				tmp.addParagraph(tree,
						(NamedElement) ne
						.getOwner(), ne);
			}
		} else if (event.getActionCommand().equals(
				"insert Query after")) {
			Element ne = com.getElement();
			CreateQueryAction tmp = new CreateQueryAction();
			if (ne != null) {
				tmp.addQuery((NamedElement) ne.getOwner(), ne);
			}
		} else if (event.getActionCommand().equals(
				"create Chapter")) {
			NamedElement ne = (NamedElement) com.getElement();
			CreateChapterAction tmp = new CreateChapterAction();
			if (ne != null) {
				tmp.addChapter(ne);
			}
		} else if (event.getActionCommand().equals(
				"create Preface")) {
			NamedElement ne = (NamedElement) com.getElement();
			CreatePrefaceAction tmp = new CreatePrefaceAction();
			if (ne != null) {
				tmp.addPreface(ne);
			}
		} else if (event.getActionCommand().equals(
				"create Part")) {
			NamedElement ne = (NamedElement) com.getElement();
			CreatePartAction tmp = new CreatePartAction();
			if (ne != null) {
				tmp.addPart(ne);
			}
		} else if (event.getActionCommand().equals(
				"create tableDiagram")) {
			NamedElement ne = (NamedElement) com.getElement();
			CreateTableDiagramAction tmp = new CreateTableDiagramAction();
			if (ne != null) {
				tmp.addDiagramTable(tree, ne, null);
			}
		}
		else if (event.getActionCommand().equals(
				"create Revision entry")) {
			NamedElement ne = (NamedElement) com.getElement();
			CreateRevisionEntryAction tmp = new CreateRevisionEntryAction();
			if (ne != null) {
				tmp.addRevisionEntry(ne);
			}
		}
		else if (event.getActionCommand().equals(
				"create Revision History")) {
			NamedElement ne = (NamedElement) com.getElement();
			CreateRevisionHistoryAction tmp = new CreateRevisionHistoryAction();
			if (ne != null) {
				tmp.addRevisionHistory(ne);
			}
		}
		else if (event.getActionCommand().equals(
				"create tableParagraph")) {
			NamedElement ne = (NamedElement) com.getElement();
			CreateTableParagraphAction tmp = new CreateTableParagraphAction();
			if (ne != null) {
				tmp.addTableParagraph(tree, ne, null);
			}
		}
	}

	public static int contains(DocBookVector rep, Vector rows, Vector<String> fields) {
		boolean found = false;

		for (int i = 0; i < rows.size(); i++) {
			// String txt = rows.get(i).toString();
			DocBookVector com = (DocBookVector) rows.get(i);
			String comID = com.getID();
			if (comID.equals(rep.getID())) {
				//displayWarning("found");
				found = true;
				return i;
			}
		}

		return -1;
	}

	public static void openInContainmentTree(DocBookVector com, DefaultMutableTreeNode root,
			boolean openSpec, boolean conTree) {

		if (root != null) {

			if (openSpec) {
				SpecificationDialogManager.getManager().editSpecification(
						(com.getElement()));
			}
			if (conTree) {
				//Tree tree = getTree();
				//DefaultMutableTreeNode rooter = (DefaultMutableTreeNode) tree
				//.getRootNode();
				//Tree s = getTree();
				Tree s = tree;
				s.openNode(com.getElement(), true, true);
			}
			if (Utilities.isFigureDiagram(com.getElement())
					&& (!conTree && !openSpec)) {
				showDiagram(com.getElement(), "figureDiagram");
			}
			if (Utilities.isDiagramTable(com.getElement())
					&& (!conTree && !openSpec)) {
				showDiagram(com.getElement(), "tableDiagram");
			}
			if (Utilities.isChapter(com.getElement())
					|| Utilities.isSection(com.getElement())) {
				DefaultMutableTreeNode nodeToupdate = findNodeToUpdate(
						(DefaultMutableTreeNode) treeView.getModel().getRoot(),
						com.getElement());
				if (nodeToupdate != null) {
					if (treeView.isCollapsed(new TreePath(nodeToupdate
							.getPath())))
						treeView.expandPath(new TreePath(nodeToupdate.getPath()));
					else
						treeView.collapsePath(new TreePath(nodeToupdate
								.getPath()));
				}
			}

		}

	}

	private static void search(String cas, String term, DefaultMutableTreeNode root) {

		if(term.contains("<")){
			term = term.replaceAll("<", "&lt;");
			//displayWarning("<");
		}
		if(term.contains(">")){
			term = term.replaceAll(">", "&gt;");
		}

		Enumeration<DefaultMutableTreeNode> li = root.children();
		while (li.hasMoreElements()) {

			DefaultMutableTreeNode db = li.nextElement();
			DocBookVector comp = ((DocBookNode) db).getRep();
			String cont = comp.toString();
			cont = cont.replaceAll("\\<.*?>", "").trim();

			if (cas.equals("no")) {

				if (cont.toLowerCase().contains(term.toLowerCase())/*cont.trim().matches("(?iu:.*" + term + ".*)")*/) {
					searchResult.add(db);
				}
			} else if (cas.equals("yes")) {

				if (cont.contains(term)) {
					//displayWarning("here");
					searchResult.add(db);
				}
			}

			if (db.children() != null) {
				search(cas, term, db);
			}

		}
	}

	private static void showDiagram(Element element, String type) {

		Utilities ut = new Utilities();

		Project project = Application.getInstance().getProject();
		if (!SessionManager.getInstance().isSessionCreated()) {
			// create new session
			SessionManager.getInstance()
			.createSession("Create and add diagram");

			if (type.equals("figureDiagram")) {

				// class diagram is created and added to parent model element
				Object diagramObject = StereotypesHelper
						.getStereotypePropertyFirst(element,
								ut.getTheFigureDiagramStereotype(), "diagram");
				Diagram diag = (Diagram) diagramObject;
				// open diagram
				project.getDiagram(diag).open();
			} else if (type.equals("tableDiagram")) {
				Object diagramObject = StereotypesHelper
						.getStereotypePropertyFirst(element,
								ut.getTheTableDiagramStereotype(),
								"diagramTable");

				Diagram diag = (Diagram) diagramObject;
				// open diagram
				project.getDiagram(diag).open();
			}

			SessionManager.getInstance().closeSession();
		}

	}

	public void showImage(String imgName) {
		JFrame frame = new JFrame("Display image");
		JLabel label = new JLabel();
		label.setIcon(new ImageIcon(dirPath + "/" + imgName));
		frame.getContentPane().add(label);
		frame.pack();
		frame.setVisible(true);
	}

	public static void updateEditorView(NamedElement father, Element child,
			String type) {

		if (treeView != null) {

			int imgCount = 1;// TreeViewGenerator.imgCount();
			int diagramCount = 1;// TreeViewGenerator.diagCount();

			DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeView
					.getModel().getRoot();

			DefaultMutableTreeNode nodeToupdate = null;

			if(((DocBookNode)root).getElement() == father){
				nodeToupdate = root;
			}
			else{
				nodeToupdate = findNodeToUpdate(root,
						father);
			}


			int chapCount = getChildCount(nodeToupdate, "chapter");

			DefaultTreeModel tModel = (DefaultTreeModel) treeView.getModel();

			if (nodeToupdate != null) {
				// collapse tree first
				treeView.collapsePath(new TreePath(nodeToupdate.getPath()));

				if (type.equals("paragraph")) {

					String content = "";
					if (((Comment) child).getBody() != null) {
						content = TreeViewGenerator.getParaContent(child);
					}

					DocBookNode nd = new DocBookNode(content + " <<"
							+ genUtility.getReferredType(child) + ">>");
					DocBookVector j = new DocBookVector();
					j.add(content.trim() + " ");
					j.setID(child.getID());
					nd.setRep(j);
					nd.setElement(child);
					nd.setType(genUtility.getReferredType(child));

					tModel.insertNodeInto(nd, nodeToupdate, nodeToupdate.getChildCount());

				} else if (type.equals("programListing")) {

						String content = "";
						if (((Comment) child).getBody() != null) {
							content = TreeViewGenerator.getParaContent(child);
						}

						DocBookNode nd = new DocBookNode(content + " <<"
								+ genUtility.getReferredType(child) + ">>");
						DocBookVector j = new DocBookVector();
						j.add(content.trim() + " ");
						j.setID(child.getID());
						nd.setRep(j);
						nd.setElement(child);
						nd.setType(genUtility.getReferredType(child));

						tModel.insertNodeInto(nd, nodeToupdate, nodeToupdate.getChildCount());

				} else if (type.equals("query")) {

					String body = ((Comment) child).getBody();

					if (body.contains("</html>")) {

						String pattern = "<head\\b[^>]*>[^<]*(?:(?!</?object\\b)<[^<]*)*</head\\s*>";

						Matcher ma = null;
						Pattern pa = Pattern.compile(pattern, Pattern.DOTALL);
						ma = pa.matcher(body);

						if (ma.find()) {
							body = ma.replaceFirst("");
						}
					}

					String header;

					if (body.length() > 70) {
						header = body.replaceAll("\\<.*?>", "");
						header = header.substring(0, 70).trim();
					} else {
						header = body.replaceAll("\\<.*?>", "").trim();
					}

					DocBookNode nd = new DocBookNode(header + " <<"
							+ genUtility.getReferredType(child) + ">>");
					nd.setType(genUtility.getReferredType(child));

					DocBookVector tmp = new DocBookVector();

					tmp.addElement("Query : " + body/* .replaceAll("\\<;.*?&gt;", "") */);
					tmp.setID(child.getID());
					// data.addElement(tmp);

					nd.setRep(tmp);
					nd.setElement(child);

					tModel.insertNodeInto(nd, nodeToupdate, 0);

				} else if (type.equals("figureDiagram")
						|| type.equals("figureImage")) {

					String[] txt = null;

					if (type.equals("figureDiagram")) {
						txt = TreeViewGenerator.getFigureDiagramText(child,
								false);
					} else {
						txt = TreeViewGenerator
								.getFigureImageText(child, false);
					}

					DocBookNode nd = new DocBookNode(txt[0] + " <<"
							+ genUtility.getReferredType(child)+ ">>");
					nd.setType(genUtility.getReferredType(child));
					nd.setImageName(txt[1]);

					DocBookVector tmp = new DocBookVector();
					if (type.equals("figureDiagram")) {
						BufferedImage img = TreeViewGenerator.getFigureDiagramBufferedImage(child);
						if (img != null) {
							JLabel picLabel = new JLabel("FigureDiagram " + diagramCount + ":" + txt[0],new ImageIcon(img),JLabel.CENTER);
							picLabel.setVerticalTextPosition(JLabel.BOTTOM);
							picLabel.setHorizontalTextPosition(JLabel.CENTER);

							// JTextArea picText = new JTextArea ("my test text");
							
							// tmp.addElement(picText);
							tmp.addElement(picLabel);
											diagramCount++;

							tmp.setImage(img);
						} else {
							System.out.println("Could not add representation of figureDiagram since image is null");
						}

					} else {
						BufferedImage img = TreeViewGenerator.getFigureImageBufferedImage(child);
						JLabel picLabel = new JLabel("FigureImage " + imgCount + ":" + txt[0],new ImageIcon(img),JLabel.CENTER);
						picLabel.setVerticalTextPosition(JLabel.BOTTOM);
						picLabel.setHorizontalTextPosition(JLabel.CENTER);

						tmp.addElement(picLabel);
						if (img != null) {
							tmp.setImage(img);
						}
						imgCount++;
					}
					tmp.setID(child.getID());
					nd.setRep(tmp);
					nd.setElement(child);

					tModel.insertNodeInto(nd, nodeToupdate, nodeToupdate.getChildCount());

				} else if (type.equals("biblioEntry")) {

					String biblioEntryText = TreeViewGenerator
							.getBiblioentryText(child);

					DocBookNode nd = new DocBookNode(biblioEntryText + " <<"
							+ genUtility.getReferredType(child) + ">>");

					nd.setType(genUtility.getReferredType(child));

					DocBookVector tmp = new DocBookVector();
					tmp.addElement("Biblioentry : " + biblioEntryText);
					tmp.setID(child.getID());
					nd.setRep(tmp);
					nd.setElement(child);
					tModel.insertNodeInto(nd, nodeToupdate,
							nodeToupdate.getChildCount());

				} else if (type.equals("chapter")) {

					if (Utilities.isPart(father)) {
						DocBookNode nd = new DocBookNode(
								Utilities
								.replaceBracketCharacters(((NamedElement) child)
										.getName())
										+ " <<"
										+ genUtility.getReferredType(child)
										+ ">>");
						nd.setType(genUtility.getReferredType(child));

						int partCount = getChildCount(nodeToupdate, "chapter");

						//displayWarning("tt"+partCount);

						DocBookVector tmp = new DocBookVector();
						tmp.addElement("Chapter "
								+ partCount
								+ " : "
								+ Utilities
								.replaceBracketCharacters(((NamedElement) child)
										.getName()));
						tmp.setID(child.getID());
						nd.setRep(tmp);
						nd.setElement(child);
						nd.setCount(Integer.toString(partCount));
						tModel.insertNodeInto(nd, nodeToupdate,
								nodeToupdate.getChildCount());
					} else {
						DocBookNode nd = new DocBookNode(
								Utilities
								.replaceBracketCharacters(((NamedElement) child)
										.getName())
										+ " <<"
										+ genUtility.getReferredType(child)
										+ ">>");
						nd.setType(genUtility.getReferredType(child));

						//chapCount++;
						DocBookVector tmp = new DocBookVector();
						tmp.addElement("Chapter "
								+ chapCount
								+ " : "
								+ Utilities
								.replaceBracketCharacters(((NamedElement) child)
										.getName()));
						tmp.setID(child.getID());
						nd.setRep(tmp);
						nd.setElement(child);
						nd.setCount(Integer.toString(chapCount));
						tModel.insertNodeInto(nd, nodeToupdate,
								nodeToupdate.getChildCount());
					}

				} else if (type.equals("section") || type.equals("subsection")) {

					// JOptionPane.showMessageDialog(null, "bilbiography");

					int childCount = getChildCount(nodeToupdate, "section");
					String sct = ((DocBookNode) nodeToupdate).getCount() + "."
							+ childCount;

					DocBookNode nd = new DocBookNode(
							Utilities
							.replaceBracketCharacters(((NamedElement) child)
									.getName())
									+ " <<"
									+ genUtility.getReferredType(child)
									+ ">>");
					nd.setType(genUtility.getReferredType(child));

					DocBookVector tmp = new DocBookVector();

					tmp.addElement("Section "
							+ sct
							+ " : "
							+ Utilities
							.replaceBracketCharacters(((NamedElement) child)
									.getName()));
					tmp.setID(child.getID());
					nd.setRep(tmp);
					nd.setElement(child);
					nd.setCount(sct);

					tModel.insertNodeInto(nd, nodeToupdate,
							nodeToupdate.getChildCount());

				} else if (type.equals("prefaceSection")) {

					JOptionPane.showMessageDialog(null, "bilbiography");

					int childCount = getChildCount(nodeToupdate, "section");

					DocBookNode nd = new DocBookNode(
							Utilities
							.replaceBracketCharacters(((NamedElement) child)
									.getName())
									+ " <<"
									+ genUtility.getReferredType(child)
									+ ">>");
					nd.setType(genUtility.getReferredType(child));

					DocBookVector tmp = new DocBookVector();

					tmp.addElement("Section "
							+ childCount
							+ " : "
							+ Utilities
							.replaceBracketCharacters(((NamedElement) child)
									.getName()));
					tmp.setID(child.getID());
					nd.setRep(tmp);
					nd.setElement(child);
					nd.setCount(Integer.toString(childCount));

					tModel.insertNodeInto(nd, nodeToupdate,
							nodeToupdate.getChildCount());

				}

				else if (type.equals("bibliography")) {

					// JOptionPane.showMessageDialog(null, "bilbiography");
					DocBookNode nd = new DocBookNode(
							Utilities
							.replaceBracketCharacters(((NamedElement) child)
									.getName())
									+ " <<"
									+ genUtility.getReferredType(child)
									+ ">>");
					nd.setType(genUtility.getReferredType(child));

					DocBookVector tmp = new DocBookVector();
					tmp.addElement("Bibliography : "
							+ Utilities
							.replaceBracketCharacters(((NamedElement) child)
									.getName()));
					tmp.setID(child.getID());
					nd.setRep(tmp);
					nd.setElement(child);

					tModel.insertNodeInto(nd, nodeToupdate,
							nodeToupdate.getChildCount());

				} else if (type.equals("preface")) {

					DocBookNode nd = new DocBookNode(
							Utilities
							.replaceBracketCharacters(((NamedElement) child)
									.getName())
									+ " <<"
									+ genUtility.getReferredType(child)
									+ ">>");
					nd.setType(genUtility.getReferredType(child));

					DocBookVector tmp = new DocBookVector();
					tmp.addElement("Preface : "
							+ Utilities
							.replaceBracketCharacters(((NamedElement) child)
									.getName()));
					tmp.setID(child.getID());

					nd.setRep(tmp);
					nd.setElement(child);

					tModel.insertNodeInto(nd, nodeToupdate,
							nodeToupdate.getChildCount());

				}

				else if (type.equals("part")) {

					DocBookNode nd = new DocBookNode(
							Utilities
							.replaceBracketCharacters(((NamedElement) child)
									.getName())
									+ " <<"
									+ genUtility.getReferredType(child)
									+ ">>");
					nd.setType(genUtility.getReferredType(child));

					DocBookVector tmp = new DocBookVector();
					tmp.addElement("Part "
							+ getChildCount(nodeToupdate, "part")
							+ " : "
							+ Utilities
							.replaceBracketCharacters(((NamedElement) child)
									.getName()));
					tmp.setID(child.getID());

					nd.setRep(tmp);
					nd.setElement(child);

					tModel.insertNodeInto(nd, nodeToupdate,
							nodeToupdate.getChildCount());

				} else if (type.equals("tableDiagram")) {

					String captionText = TreeViewGenerator
							.getDiagramTableText(child);

					tableDiagramCount = 0;

					getDiagramTableCount((DefaultMutableTreeNode) treeView
							.getModel().getRoot());

					DocBookNode nd = new DocBookNode(captionText
							+ " <<" +
							genUtility.getReferredType(child)+ ">>");
					nd.setType(genUtility.getReferredType(child));

					DocBookVector tmp = new DocBookVector();
					BufferedImage img = TreeViewGenerator.getDiagramTableBufferedImage(child);
					JLabel picLabel = new JLabel("DiagramTable " + tableDiagramCount + " : "
							+ captionText,new ImageIcon(img),JLabel.CENTER);
					picLabel.setVerticalTextPosition(JLabel.BOTTOM);
					picLabel.setHorizontalTextPosition(JLabel.CENTER);

					tmp.addElement(picLabel);
					if (img != null) {
						tmp.setImage(img);
					}

					tmp.setImageText("DiagramTable " + tableDiagramCount + " : "
							+ captionText);
					tmp.setID(child.getID());
					// data.addElement(tmp);

					nd.setRep(tmp);
					nd.setElement(child);

					tModel.insertNodeInto(nd, nodeToupdate,
							nodeToupdate.getChildCount());
				}
				else if (type.equals("tableParagraph")) {
					//displayWarning("foundd");
					DocBookNode nd = new DocBookNode("tableParagraph" + " <<"
							+ genUtility.getReferredType(child) + ">>");
					nd.setType(genUtility.getReferredType(child));

					nd.setType(((Comment) child).getHumanType().trim());

					DocBookVector tmp = new DocBookVector();
					tmp.addElement(((Comment) child).getBody());
					tmp.setID(child.getID());

					nd.setRep(tmp);
					nd.setElement(child);

					tModel.insertNodeInto(nd, nodeToupdate,
							nodeToupdate.getChildCount());
				}
				else if (type.equals("revhistory")) {

					// JOptionPane.showMessageDialog(null, "bilbiography");
					DocBookNode nd = new DocBookNode(
							Utilities
							.replaceBracketCharacters(((NamedElement) child)
									.getName())
									+ " <<"
									+ genUtility.getReferredType(child)
									+ ">>");
					nd.setType(genUtility.getReferredType(child));

					DocBookVector tmp = new DocBookVector();

					tmp.addElement("Revision History : "
							+ Utilities
							.replaceBracketCharacters(((NamedElement) child)
									.getName()));
					tmp.setID(child.getID());
					nd.setRep(tmp);
					nd.setElement(child);

					tModel.insertNodeInto(nd, nodeToupdate,
							nodeToupdate.getChildCount());

				}
				else if (type.equals("revisionEntry")) {

					Utilities theUtilities = new Utilities();

					DocBookNode nd = new DocBookNode(((NamedElement) child).getName() + " <<"
							+ genUtility.getReferredType(child) + ">>");

					nd.setType(genUtility.getReferredType(child));

					Stereotype revEntryStereo = theUtilities.getTheRevisionEntryStereotype();
					Stereotype authorStereo = theUtilities.getTheAuthorStereotype();
					NamedElement ne = (NamedElement) child;

					//find author
					Element theAuthor = null;
					String authorTxt = "";
					List<?> bcVect = StereotypesHelper.getStereotypePropertyValue(ne,revEntryStereo, "author");
					if (!bcVect.isEmpty()) {
						theAuthor = (Element)(bcVect.get(0));

						authorTxt = "Author : " + Utilities.getFirstElementString(theAuthor,authorStereo, "firstname") + " "+
								Utilities.getFirstElementString(theAuthor,authorStereo, "surname")  + ", ";
					} 


					DocBookVector tmp = new DocBookVector();
					tmp.addElement("Revision Entry : " + ne.getName() + ", " +
							"Number : " + Utilities.getFirstElementString(ne,revEntryStereo, "revnumber") + ", " +
							"Date : " + Utilities.getFirstElementString(ne,revEntryStereo, "date") + " "+
							"Description : " + Utilities.getFirstElementString(ne,revEntryStereo, "revdescription")  + ", " +
							authorTxt +
							"Remarks : " + Utilities.getFirstElementString(ne,revEntryStereo, "revremark")
							);
					tmp.setID(child.getID());
					nd.setRep(tmp);
					nd.setElement(child);

					tModel.insertNodeInto(nd, nodeToupdate,
							nodeToupdate.getChildCount());
				}

				// update views
				treeView.expandPath(new TreePath(nodeToupdate.getPath()));
			} else {
				JOptionPane.showMessageDialog(null, "nodeToUpdate is null");
			}

		}
	}


	private static void getDiagramTableCount(DefaultMutableTreeNode root) {

		Enumeration<DefaultMutableTreeNode> li = root.children();
		while (li.hasMoreElements()) {
			DocBookNode db = (DocBookNode) li.nextElement();
			if (Utilities.isDiagramTable(db.getElement())) {
				// JOptionPane.showMessageDialog(null, "nodeToUpdate is null");
				tableDiagramCount++;
			}
			if (db.children() != null) {
				getDiagramTableCount(db);
			}
		}
	}

	public static int getChildCount(DefaultMutableTreeNode nodeToupdate,
			String type) {
		Enumeration<DefaultMutableTreeNode> li = null;
		int count = 0;
		if(nodeToupdate == null ) { 
			return count;
		}
		li = nodeToupdate.children();
		while (li.hasMoreElements()) {
			DocBookNode db = (DocBookNode) li.nextElement();
			if (db.getType().equals(type)) {
				count++;
			}
		}

		return count + 1;
	}

	public static DefaultMutableTreeNode findNodeToUpdate(
			DefaultMutableTreeNode root, Element father) {

		if (((DocBookNode) root).getElement().equals(father)) {

			return root;
		}

		Enumeration<DefaultMutableTreeNode> li = root.children();
		DefaultMutableTreeNode retNode = null;
		while (li.hasMoreElements()) {
			DocBookNode db = (DocBookNode) li.nextElement();

			if (db.getElement().getID().equals(father.getID())) {

				return db;
			}

			if (db.children() != null) {
				retNode = findNodeToUpdate(db, father);
				if (retNode != null) {
					return retNode;
				}
			}

		}

		return retNode;
	}

}
