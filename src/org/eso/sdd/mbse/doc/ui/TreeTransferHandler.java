package org.eso.sdd.mbse.doc.ui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.eso.sdd.mbse.doc.actions.MBSEShowEditPanelAction;
import org.eso.sdd.mbse.doc.algo.DocBookNode;
import org.eso.sdd.mbse.doc.algo.Utilities;
import org.eso.sdd.mbse.doc.algo.genUtility;

import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;

@SuppressWarnings("serial")
public class TreeTransferHandler extends TransferHandler {
	DataFlavor nodesFlavor;
	DataFlavor[] flavors = new DataFlavor[1];
	DefaultMutableTreeNode[] nodesToRemove;

	public TreeTransferHandler() {
		try {
			String mimeType = DataFlavor.javaJVMLocalObjectMimeType
					+ ";class=\""
					+ javax.swing.tree.DefaultMutableTreeNode[].class.getName()
					+ "\"";
			nodesFlavor = new DataFlavor(mimeType);
			flavors[0] = nodesFlavor;
		} catch (ClassNotFoundException e) {
			System.out.println("ClassNotFound: " + e.getMessage());
		}
	}

	@Override
	public boolean canImport(TransferHandler.TransferSupport support) {
		if (!support.isDrop()) {
			return false;
		}
		support.setShowDropLocation(true);
		if (!support.isDataFlavorSupported(nodesFlavor)) {
			return false;
		}
		// Do not allow a drop on the drag source selections.
		JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
		JTree tree = (JTree) support.getComponent();
		int dropRow = tree.getRowForPath(dl.getPath());
		int[] selRows = tree.getSelectionRows();
		for (int i = 0; i < selRows.length; i++) {
			if (selRows[i] == dropRow) {
				return false;
			}
		}
		// Do not allow MOVE-action drops if a non-leaf node is
		// selected unless all of its children are also selected.
		int action = support.getDropAction();
		if (action == MOVE) {
			return haveCompleteNode(tree);
		}
		// Do not allow a non-leaf node to be copied to a level
		// which is less than its source level.
		TreePath dest = dl.getPath();
		DefaultMutableTreeNode target = (DefaultMutableTreeNode) dest
				.getLastPathComponent();
		TreePath path = tree.getPathForRow(selRows[0]);
		DefaultMutableTreeNode firstNode = (DefaultMutableTreeNode) path
				.getLastPathComponent();
		if (firstNode.getChildCount() > 0
				&& target.getLevel() < firstNode.getLevel()) {
			return false;
		}
		/*
		 * JOptionPane.showMessageDialog(null,firstNode.getParent().toString());
		 * if(firstNode.getParent().equals(target)) { return false; }
		 */
		return true;
	}

	private boolean haveCompleteNode(JTree tree) {
		int[] selRows = tree.getSelectionRows();
		TreePath path = tree.getPathForRow(selRows[0]);
		DefaultMutableTreeNode first = (DefaultMutableTreeNode) path
				.getLastPathComponent();
		int childCount = first.getChildCount();
		// first has children and no children are selected.
		/*
		 * if (childCount > 0 && selRows.length == 1) return false; // first may
		 * have children. for (int i = 1; i < selRows.length; i++) { path =
		 * tree.getPathForRow(selRows[i]); DefaultMutableTreeNode next =
		 * (DefaultMutableTreeNode) path .getLastPathComponent(); if
		 * (first.isNodeChild(next)) { // Found a child of first. if (childCount
		 * > selRows.length - 1) { // Not all children of first are selected.
		 * return false; } } }
		 */
		return true;
	}

	@Override
	protected Transferable createTransferable(JComponent c) {
		JTree tree = (JTree) c;
		TreePath[] paths = tree.getSelectionPaths();
		if (paths != null) {
			// Make up a node array of copies for transfer and
			// another for/of the nodes that will be removed in
			// exportDone after a successful drop.
			List<DefaultMutableTreeNode> copies = new ArrayList<DefaultMutableTreeNode>();
			List<DefaultMutableTreeNode> toRemove = new ArrayList<DefaultMutableTreeNode>();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[0]
					.getLastPathComponent();
			DefaultMutableTreeNode copy = copy(node);
			copies.add(copy);
			toRemove.add(node);
			addNodes(node, copy, copies, toRemove);
			/*
			 * for (int i = 1; i < paths.length; i++) { DefaultMutableTreeNode
			 * next = (DefaultMutableTreeNode) paths[i] .getLastPathComponent();
			 * // Do not allow higher level nodes to be added to list. if
			 * (next.getLevel() < node.getLevel()) { break; } else if
			 * (next.getLevel() > node.getLevel()) { // child node
			 * copy.add(copy(next)); // node already contains child } else { //
			 * sibling copies.add(copy(next)); toRemove.add(next); } }
			 */
			DefaultMutableTreeNode[] nodes = copies
					.toArray(new DefaultMutableTreeNode[copies.size()]);
			nodesToRemove = toRemove
					.toArray(new DefaultMutableTreeNode[toRemove.size()]);
			return new NodesTransferable(nodes);
		}
		return null;
	}

	private void addNodes(DefaultMutableTreeNode node,
			DefaultMutableTreeNode copy, List<DefaultMutableTreeNode> copies,
			List<DefaultMutableTreeNode> toRemove) {

		Enumeration<?> li = node.children();
		while (li.hasMoreElements()) {
			DefaultMutableTreeNode next = (DefaultMutableTreeNode) li
					.nextElement();
			DefaultMutableTreeNode cpnext = copy(next);
			if (next.getLevel() < node.getLevel()) {
				break;
			} else if (next.getLevel() > node.getLevel()) { // child node
				if (!next.isLeaf()) {
					addNodes(next, cpnext, copies, toRemove);
				}
				copy.add(cpnext);
				// node already contains child
			} else { // sibling
				if (!next.isLeaf()) {
					addNodes(next, cpnext, copies, toRemove);
				}
				copies.add(cpnext);
				toRemove.add(next);

			}

		}

	}

	/** Defensive copy used in createTransferable. */
	private DefaultMutableTreeNode copy(DefaultMutableTreeNode node) {

		DocBookNode k = new DocBookNode(node.toString());
		k.setType(((DocBookNode) node).getType());
		k.setRep(((DocBookNode) node).getRep());
		k.setElement(((DocBookNode) node).getElement());

		return k;
	}

	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {
		if ((action & MOVE) == MOVE) {
			JTree tree = (JTree) source;
			DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
			/*
			 * // Remove nodes saved in nodesToRemove in createTransferable. for
			 * (int i = 0; i < nodesToRemove.length; i++) {
			 * model.removeNodeFromParent(nodesToRemove[i]); }
			 */
		}
	}

	@Override
	public int getSourceActions(JComponent c) {
		return COPY_OR_MOVE;
	}

	@Override
	public boolean importData(TransferHandler.TransferSupport support) {
		if (!canImport(support)) {
			return false;
		}
		// Extract transfer data.
		DefaultMutableTreeNode[] nodes = null;
		try {
			Transferable t = support.getTransferable();
			nodes = (DefaultMutableTreeNode[]) t.getTransferData(nodesFlavor);
		} catch (UnsupportedFlavorException ufe) {
			System.out.println("UnsupportedFlavor: " + ufe.getMessage());
		} catch (java.io.IOException ioe) {
			System.out.println("I/O error: " + ioe.getMessage());
		}
		// Get drop location info.
		JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
		int childIndex = dl.getChildIndex();
		TreePath dest = dl.getPath();
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) dest
				.getLastPathComponent();
		JTree tree = (JTree) support.getComponent();

		// JOptionPane.showMessageDialog(null, parent.getPath());

		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		// Configure for drop mode.
		int index = childIndex; // DropMode.INSERT
		if (childIndex == -1) { // DropMode.ON
			if (parent.getChildCount() == 0) {
				index = 0;
			} else {
				index = parent.getChildCount() - 1;
			}
		} else if (index == parent.getChildCount()) {
			index--;
		}
		// Add data to model.
		try {

			for (int i = 0; i < nodes.length; i++) {

				if (!genUtility.checkMatch((DocBookNode) parent,
						(DocBookNode) nodes[i])) {
					JOptionPane.showMessageDialog(null,
							((DocBookNode) nodes[i]).getType()
									+ " cannot be moved under "
									+ ((DocBookNode) parent).getType());
					return false;
				}
				
				if(!((DocBookNode) nodes[i]).getElement().isEditable()){
					JOptionPane.showMessageDialog(null,"This element is locked.");
					return false;
				}
			}

			// Remove nodes saved in nodesToRemove in createTransferable.
			for (int i = 0; i < nodesToRemove.length; i++) {
				model.removeNodeFromParent(nodesToRemove[i]);
				removeFromTable(nodesToRemove[i]);
			}

			

			if (!SessionManager.getInstance().isSessionCreated()) {
				SessionManager.getInstance().createSession("MBSE-doc");

				Utilities ut = new Utilities();

				for (int i = 0; i < nodes.length; i++) {

					tree.expandPath(dest);
					// insert treenode
					model.insertNodeInto(nodes[i], parent,
							index++);

					boolean upd = updateMove((DocBookNode) nodes[i], ut,
							parent, tree);

					if (upd) { // same parent no update in view
						tree.expandPath(dest);
						tree.collapsePath(dest);
						tree.expandPath(dest);

					} else {
						tree.collapsePath(dest);
						tree.expandPath(dest);
					}

				}

				SessionManager.getInstance().closeSession();
			}

		} catch (Exception e) {
			e.printStackTrace();
    		StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);

			genUtility.displayWarning(sw.toString());
		}
		return true;
	}

	private void removeFromTable(DefaultMutableTreeNode node) {

		DocBookNode td = (DocBookNode) node;
		DefaultTableModel k = (DefaultTableModel) MBSEShowEditPanelAction.table
				.getModel();
		k.removeRow(MBSEShowEditPanelAction.contains(td.getRep(), MBSEShowEditPanelAction.rows,
				MBSEShowEditPanelAction.columns));

	}

	private boolean updateMove(DocBookNode docBookNode, Utilities ut,
			DefaultMutableTreeNode parent, JTree tree) {

		boolean update = true;

		Stereotype ptyper = getStereotype(((DocBookNode) parent).getType(), ut);
		Stereotype oldtyper = getStereotype(((NamedElement) docBookNode
				.getElement().getOwner()).getHumanType().trim(), ut);

		Element father = ((DocBookNode) parent).getElement();
		Element olddad = docBookNode.getElement().getOwner();

		if (father == olddad) {
			update = false;

			String sonsType = getSonsType(docBookNode,
					((DocBookNode) parent).getType());


				//JOptionPane.showMessageDialog(null, sonsType);

				//JOptionPane.showMessageDialog(null, oldtyper);
				// get all oldsons first
				List<?> oldSons = StereotypesHelper.getStereotypePropertyValue(
						olddad, oldtyper, sonsType, false);

				// clear olddad
				StereotypesHelper.clearStereotypeProperty(olddad, oldtyper,
						sonsType, false);

				Enumeration li = ((DocBookNode) docBookNode.getParent()).children();
				while (li.hasMoreElements()) {
					DocBookNode e = (DocBookNode) li.nextElement();

					if (checkAddable(e, sonsType)) {
						
						StereotypesHelper.setStereotypePropertyValue(father,
								ptyper, sonsType, e.getElement(), true);
					}
				}
		

		}
		// different parent movement
		else {

			// JOptionPane.showMessageDialog(null,"different parent");

			String newSonsType = getSonsType(docBookNode, ((DocBookNode) parent).getType());
			String oldSonsType = getSonsType(docBookNode, olddad.getHumanType().trim());

			// JOptionPane.showMessageDialog(null,"new sons" + newSonsType);
			// JOptionPane.showMessageDialog(null,"old sons" + oldSonsType);

			// get all oldsons first
			List<?> oldSons = StereotypesHelper.getStereotypePropertyValue(olddad,
					oldtyper, oldSonsType, false);

			// clear olddad
			StereotypesHelper.clearStereotypeProperty(olddad, oldtyper,
					oldSonsType, false);

			for (int i2 = 0; i2 < oldSons.size(); i2++) {
				// JOptionPane.showMessageDialog(null,oldSons.size());
				if (((Element) oldSons.get(i2))
						.equals(docBookNode.getElement())) {
					// JOptionPane.showMessageDialog(null,"found");
					oldSons.remove(i2);
				}
				// JOptionPane.showMessageDialog(null,"not found");
			}
			// add back oldssons to olddad
			for (int i2 = 0; i2 < oldSons.size(); i2++) {

				StereotypesHelper.setStereotypePropertyValue(olddad, oldtyper,
						oldSonsType, oldSons.get(i2), true);
			}
			// add to new parent
			try {
				ModelElementsManager.getInstance().moveElement(
						docBookNode.getElement(), father);

			} catch (Exception e) {
				e.printStackTrace();
	    		StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);

				genUtility.displayWarning(sw.toString());

			}

			StereotypesHelper.setStereotypePropertyValue(father, ptyper,
					newSonsType, docBookNode.getElement(), true);

			// update look
			String sonsType = getSonsType(docBookNode,
					((DocBookNode) parent).getType());
			StereotypesHelper.clearStereotypeProperty(father, ptyper, sonsType,
					false);

			Enumeration li = ((DocBookNode) docBookNode.getParent()).children();
			while (li.hasMoreElements()) {
				DocBookNode e = (DocBookNode) li.nextElement();

				if (getSonsType(e, ((DocBookNode) parent).getType()).equals(
						sonsType)) {
					StereotypesHelper.setStereotypePropertyValue(father,
							ptyper, sonsType, e.getElement(), true);
				}
			}

		}

		// update chapter and sections count
		updateCount(parent, docBookNode);

		return update;
	}

	private boolean checkAddable(DocBookNode e, String sonsType) {

		if (sonsType.equals("divisions")) {
			if (e.getType().equals("part")) {
				return true;
			}
		} else if (sonsType.equals("prefacePara")) {
			if (e.getType().equals("paragraph")) {
				return true;
			}
		} else if (sonsType.equals("prefaceSection")) {
			if (e.getType().equals("section")) {
				return true;
			}
		} else if (sonsType.equals("blockelements")) {
			if (e.getType().equals("paragraph") || e.getType().equals("query") || e.getType().equals("programListing")
					|| e.getType().equals("figureDiagram") || e.getType().equals("figureImage")) {
				return true;
			}
		}
		else if (sonsType.equals("sections")) {
			if (e.getType().equals("section")) {
				return true;
			}
		}
		else if (sonsType.equals("subsection")) {
			if (e.getType().equals("section")) {
				return true;
			}
		}
		else if (sonsType.equals("bookComponent")) {
			if (e.getType().equals("chapter") || e.getType().equals("preface")) {
				return true;
			}
		}
		
		else if (sonsType.equals("biblioEntry")) {
			if (e.getType().equals("biblioEntry")) {
				return true;
			}
		}

		return false;
	}

	public static void updateCount(DefaultMutableTreeNode parent,
			DocBookNode docBookNode) {
		// TODO Auto-generated method stub

		int startIndex = 0;

		/*
		 * if(docBookNode != null){ startIndex = parent.getIndex(docBookNode); }
		 */
		int childCt = parent.getChildCount();

		// JOptionPane.showMessageDialog(null, parent.toString() +
		// "...child index..."+ startIndex);
		int i = 0;
		int chapCount = 0;
		int partCount = 0;
		int prefaceSecCount = 0;
		while (i < childCt) {
			DocBookNode nd = (DocBookNode) parent.getChildAt(i);
			/*
			 * JOptionPane.showMessageDialog(null, "Chapter "+ ct +" : "+
			 * Utilities.removeBracketCharacters(((NamedElement)
			 * nd.getElement()) .getName()));
			 */
			if (nd.getType().equals("chapter")) {
				int ct = chapCount + 1;
				nd.getRep()
						.set(0,
								"Chapter "
										+ ct
										+ " : "
										+ Utilities
												.replaceBracketCharacters(((NamedElement) nd
														.getElement())
														.getName()));
				nd.setCount(Integer.toString(ct));
				chapCount++;
			} else if (nd.getType().equals("section")
					&& ((DocBookNode) parent).getType().equals("preface")) {
				int ct = prefaceSecCount + 1;
				nd.getRep()
						.set(0,
								"Section "
										+ ct
										+ " : "
										+ Utilities
												.replaceBracketCharacters(((NamedElement) nd
														.getElement())
														.getName()));
				nd.setCount(Integer.toString(ct));
				prefaceSecCount++;
			} else if (nd.getType().equals("section")) {
				int ct = chapCount + 1;
				String sct = ((DocBookNode) parent).getCount() + "." + ct;
				nd.getRep()
						.set(0,
								"Section "
										+ sct
										+ " : "
										+ Utilities
												.replaceBracketCharacters(((NamedElement) nd
														.getElement())
														.getName()));
				nd.setCount(sct);
				chapCount++;
			} else if (nd.getType().equals("part")) {
				int ct = partCount + 1;
				nd.getRep()
						.set(0,
								"Part "
										+ ct
										+ " : "
										+ Utilities
												.replaceBracketCharacters(((NamedElement) nd
														.getElement())
														.getName()));
				nd.setCount(Integer.toString(ct));
				partCount++;
			}
			i++;

			if (nd.children() != null) {
				updateCount(nd, null);
			}
		}

	}

	public static String getSonsType(DocBookNode docBookNode, String parentType) {
		if (Utilities.isParagraph(docBookNode.getElement())
				|| Utilities.isQuery(docBookNode.getElement())
				|| Utilities.isFigureImage(docBookNode.getElement())
				|| Utilities.isFigureDiagram(docBookNode.getElement())) {

			if (parentType.equals("preface")) {

				return "prefacePara";
			}

			return "blockelements";
		} else if (Utilities.isSection(docBookNode.getElement())) {
			if (parentType.equals("section")) {
				return "subsection";
			} else if (parentType.equals("chapter")) { // chapter parent
				return "sections";
			} else if (parentType.equals("preface")) { // chapter parent
				return "prefaceSection";
			}
		}

		else if (Utilities.isChapter(docBookNode.getElement())
				|| Utilities.isPreface(docBookNode.getElement())) {

			return "bookComponent";
		} else if (Utilities.isPart(docBookNode.getElement())) {

			return "divisons";
		}
		else if (Utilities.isBiblioEntry(docBookNode.getElement())) {
			if (parentType.equals("bibliography")) {
				return "biblioEntry";
			}
		}
		return null;
	}

	private Stereotype getStereotype(String type, Utilities ut) {
		if (type.equals("chapter")) {
			return ut.getTheChapterStereotype();

		} else if (type.equals("section")) {
			return ut.getTheSectionStereotype();
		} else if (type.equals("bibliography")) {
			return ut.getTheBibliographyStereotype();
		} else if (type.equals("part")) {
			return ut.getThePartStereotype();
		} else if (type.equals("book")) {
			return ut.getTheBookStereotype();
		} else if (type.equals("preface")) {
			return ut.getThePrefaceStereotype();
		}
		return null;
	}

	@Override
	public String toString() {
		return getClass().getName();
	}

	public class NodesTransferable implements Transferable {
		DefaultMutableTreeNode[] nodes;

		public NodesTransferable(DefaultMutableTreeNode[] nodes) {
			this.nodes = nodes;
		}

		@Override
		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException {
			if (!isDataFlavorSupported(flavor))
				throw new UnsupportedFlavorException(flavor);
			return nodes;
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return flavors;
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return nodesFlavor.equals(flavor);
		}
	}
}
