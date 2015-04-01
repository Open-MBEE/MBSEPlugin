package org.eso.sdd.mbse.doc.algo;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.eso.sdd.mbse.doc.actions.MBSEShowEditPanelAction;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Comment;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
@SuppressWarnings("serial")
public class DocBookNode extends DefaultMutableTreeNode {
	private String type = null;

	private DocBookVector rep = null;

	private String imgName = null;

	private Element elem = null;

	private String count;

	private DefaultMutableTreeNode oldParent = null;
	private int oldIndex;

	public DocBookNode(String name) {
		super(name);

	}

	public void setType(String t) {
		type = t;
	}

	public String getType() {
		return type;
	}

	public void setRep(DocBookVector k) {
		rep = k;
	}

	public DocBookVector getRep() {
		return rep;
	}

	public void setImageName(String name) {
		imgName = name;
	}

	public String getImageName() {
		return imgName;
	}

	public void setElement(Element el) {
		elem = el;
		elem.addPropertyChangeListener((new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				//JOptionPane.showMessageDialog(null,evt.getPropertyName());
				// System.out.println(((Element) evt.getSource()).getHumanName()
				
					if (evt.getPropertyName().equals("BEFORE_DELETE")) {
						updateTree("delete");
						
					} else if (evt.getPropertyName().equals("INSTANCE_CREATED")) {
						updateTree("undo");
					} else {
						
						 if (checkEvent(evt)){
						    doUpdate();
						 }
					}
					
				

			}

		}));

		if (rep != null) {
			rep.setElement(elem);
		}

	}

	protected void updateTree(String mode) {

		if (mode.equals("delete")) {

			//JOptionPane.showMessageDialog(null,"fuc");
			if (MBSEShowEditPanelAction.treeView != null) {
				DefaultMutableTreeNode nodeToupdate = MBSEShowEditPanelAction
						.findNodeToUpdate(
								(DefaultMutableTreeNode) MBSEShowEditPanelAction.treeView
										.getModel().getRoot(), elem);
				//JOptionPane.showMessageDialog(null,nodeToupdate);

				DefaultTreeModel model = (DefaultTreeModel) MBSEShowEditPanelAction.treeView
						.getModel();
				if (model != null && nodeToupdate != null) {

					DefaultMutableTreeNode parent = (DefaultMutableTreeNode) nodeToupdate
							.getParent();
					oldParent = parent;
					oldIndex = parent.getIndex(nodeToupdate);

					// JOptionPane.showMessageDialog(null, parent.toString());

					MBSEShowEditPanelAction.treeView.collapsePath(new TreePath(parent
							.getPath()));
					model.removeNodeFromParent(nodeToupdate);
					MBSEShowEditPanelAction.treeView.expandPath(new TreePath(parent
							.getPath()));
				}
			}

		} else if (mode.equals("undo")) {
			if (MBSEShowEditPanelAction.treeView != null) {

				DefaultTreeModel model = (DefaultTreeModel) MBSEShowEditPanelAction.treeView
						.getModel();

				if (model != null && getRep() != null) {

					//DefaultMutableTreeNode parent = (DefaultMutableTreeNode) this
							//.getParent();

					// JOptionPane.showMessageDialog(null,
					// oldParent.toString());
					if (oldParent != null) {
						MBSEShowEditPanelAction.treeView.collapsePath(new TreePath(
								oldParent.getPath()));

						model.insertNodeInto(this, oldParent, oldIndex);

						MBSEShowEditPanelAction.treeView.expandPath(new TreePath(
								oldParent.getPath()));
					}
				}
			}

		}
	}

	protected boolean checkEvent(PropertyChangeEvent evt) {
		String event = evt.getPropertyName();

		if (evt instanceof com.nomagic.uml2.ext.jmi.IndexedPropertyChangeEvent) {

			System.out.println(evt.getClass() + "zxc");
			return false;
		}

		if (type.equals("section")) {
			if (event.equals("name")) {
				return true;
			}
			if (event.equals("Active Hyperlink")) {
				// movement
				return true;
			}
		} else if (type.equals("book")) {
			if (event.equals("name")) {
				// movement
				return true;
			}
			if (event.equals("Active Hyperlink")) {
				return true;
			}
		} else if (type.equals("chapter")) {
			if (event.equals("name")) {
				return true;
			}
			// System.out.println(
			// DerivedPropertyManager.isDerivedPropertyChangeEvent(evt));

			//if (evt instanceof com.nomagic.magicdraw.derivedproperty.b.a
				//	&& event.equals("Active Hyperlink")) {
				// movement
				//System.out.println("test");
				// doMovement();
				// return true;
			//}
		} else if (type.equals("preface")) {
			if (event.equals("name")) {
				return true;
			}
		} else if (type.equals("part")) {
			if (event.equals("name")) {
				return true;
			}
		} else if (type.equals("bibliography")) {
			if (event.equals("name")) {
				return true;
			}
		} else if (type.equals("paragraph")) {
			if (event.equals("body") /*|| event.equals("Active Hyperlink")*/) {

				//JOptionPane.showMessageDialog(null,"here");
				return true;
			}
		} else if (type.equals("biblioEntry")) {
			if (event.trim().equals("Active Hyperlink")) {
				return true;
			}
		} else if (type.equals("figureImage")) {
			if (event.trim().equals("..captiontext?")) {
				return true;
			}
		} else if (type.equals("figureDiagram")) {
			if (event.trim().equals("Active Hyperlink")) {
				return true;
			}
		}
		else if (type.equals("tableDiagram")) {
			if (event.trim().equals("Active Hyperlink")) {
				return true;
			}
		}else if (type.equals("query")) {
			if (event.trim().equals("body")) {
				return true;
			}
		}
		else if (type.equals("tableParagraph")) {
			if (event.trim().equals("body")) {
				return true;
			}
		}
		return false;
	}

	protected void doUpdate() {

		String newText = null;
		String panelText = null;

		if (isNamedElement()) {

			if (type.equals("biblioEntry")) {

				newText = TreeViewGenerator.getBiblioentryText(elem) + " <<"
						+ ((NamedElement) elem).getHumanType() + ">>";
				// JOptionPane.showMessageDialog(null,newText);
			} else {

				newText = Utilities
						.replaceBracketCharacters(((NamedElement) elem)
								.getName())
						+ " <<" + ((NamedElement) elem).getHumanType() + ">>";
			}
		} else {

			if (type.equals("paragraph")) {
				String content = TreeViewGenerator.getParaContent(elem);
				String body = content;

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
				newText = header.trim() + " <<"
						+ ((Comment) elem).getHumanType() + ">>";

			} else if (type.equals("programListing")) { 
				String content = TreeViewGenerator.getParaContent(elem);
				String body = content;

				String header;

				if (body.length() > 70) {
					header = body.replaceAll("\\<.*?>", "");
					header = header.substring(0, 70).trim();
				} else {
					header = body.replaceAll("\\<.*?>", "").trim();
				}
				newText = header.trim() + " <<"
						+ ((Comment) elem).getHumanType() + ">>";
			} else if (type.equals("figureDiagram")) {
				String[] captionText = TreeViewGenerator.getFigureDiagramText(
						elem, false); // index 0 is captiontext, index 1 is
										// fileName
				newText = captionText[0] + " <<"
						+ ((Comment) elem).getHumanType() + ">>";

			} else if (type.equals("figureImage")) {
				String[] captionText = TreeViewGenerator.getFigureImageText(
						elem, false);
				// index 0 is captiontext, index 1 is fileName
				newText = captionText[0] + " <<"
						+ ((Comment) elem).getHumanType() + ">>";
			}else if (type.equals("tableDiagram")) {
				String captionText = TreeViewGenerator.getDiagramTableText(
						elem); // index 0 is captiontext, index 1 is
										// fileName
				newText = captionText
						+ " <<"
						+ ((NamedElement) elem).getHumanType()
						+ ">>";

			}  else if (type.equals("query")) {
				newText = Utilities.replaceBracketCharacters(((Comment) elem)
						.getHumanName())
						+ " <<"
						+ ((Comment) elem).getHumanType() + ">>";
			}
			else if (type.equals("tableParagraph")) {
				newText = Utilities.replaceBracketCharacters(((Comment) elem)
						.getHumanName())
						+ " <<"
						+ ((Comment) elem).getHumanType() + ">>";
			}


		}

		panelText = getPanelText();

		this.setUserObject(newText);
		updatePanel(panelText);
	}

	private String getPanelText() {

		String txt = null;

		if (type.equals("book")) {

			String repText = rep.get(0).toString();
			String oldpart = repText.substring(0, repText.indexOf(":") + 1);
			// JOptionPane.showMessageDialog(null,oldpart);
			txt = oldpart + " " + ((NamedElement)elem).getName();
		} else if (type.equals("part") || type.equals("chapter")
				|| type.equals("section") || type.equals("preface")
				|| type.equals("bibliography")) {

			String repText = rep.get(0).toString();
			String oldpart = repText.substring(0, repText.indexOf(":") + 1);
			txt = oldpart
					+ " "
					+ Utilities.replaceBracketCharacters(((NamedElement) elem)
							.getName());
		} else if (type.equals("paragraph")) {
			txt = TreeViewGenerator.getParaContent(elem).trim() + "  ";
		} else if (type.equals("tableParagraph")) {
			txt = ((Comment) elem).getBody();
		} else if (type.equals("figureDiagram")) {
			String[] captionText = TreeViewGenerator.getFigureDiagramText(elem,
					false);
			JLabel label = (JLabel) rep.get(0);
			String repText = label.getText();
			String oldpart = repText.substring(0, repText.indexOf(":") + 1);
			txt = oldpart + " " + captionText[0];
		} else if (type.equals("figureImage")) {
			String[] captionText = TreeViewGenerator.getFigureImageText(elem,
					false);
			JLabel label = (JLabel) rep.get(0);
			String repText = label.getText();
			String oldpart = repText.substring(0, repText.indexOf(":") + 1);
			txt = oldpart + " " + captionText[0];
		} else if (type.equals("tableDiagram")) {
			String captionText = TreeViewGenerator.getDiagramTableText(elem);
			JLabel label = (JLabel) rep.get(0);
			String repText = label.getText();
			String oldpart = repText.substring(0, repText.indexOf(":") + 1);
			txt = oldpart + " " + captionText;
		}else if (type.equals("biblioEntry")) {
			String bbText = TreeViewGenerator.getBiblioentryText(elem);
			String repText = rep.get(0).toString();
			String oldpart = repText.substring(0, repText.indexOf(":") + 1);
			txt = oldpart + " " + bbText;
		} else if (type.equals("query")) {

			String repText = rep.get(0).toString();
			String oldpart = repText.substring(0, repText.indexOf(":") + 1);
			txt = oldpart
					+ " "
					+ Utilities.replaceBracketCharacters(((Comment) elem)
							.getBody());
		}

		return txt;
	}

	private void updatePanel(String newText) {

		if (rep == null) {

			//JOptionPane.showMessageDialog(null, "nill");
		}
		int row = MBSEShowEditPanelAction.contains(rep, MBSEShowEditPanelAction.rows,
				MBSEShowEditPanelAction.columns);
		// JOptionPane.showMessageDialog(null,row);

		if (type.equals("figureDiagram") || type.equals("figureImage") || type.equals("tableDiagram")) {
			JLabel picLabel = null;
			if(rep.getImage() == null){
				//JOptionPane.showMessageDialog(null,"null image icon");
			}
				picLabel = new JLabel(newText, new ImageIcon(rep.getImage()),
						JLabel.CENTER);
			picLabel.setVerticalTextPosition(JLabel.BOTTOM);
			picLabel.setHorizontalTextPosition(JLabel.CENTER);
			rep.set(0, picLabel);
			// data.addElement(tmp);

		} else {
			rep.set(0, newText);
		}

		if(row != -1){

		try {
			// remove row
			((DefaultTableModel) MBSEShowEditPanelAction.table.getModel())
					.removeRow(row);

			// add row with new content
			((DefaultTableModel) MBSEShowEditPanelAction.table.getModel()).insertRow(
					row, rep);

			// select changed row
			MBSEShowEditPanelAction.treeView.setSelectionPath(new TreePath(
					((DefaultMutableTreeNode) MBSEShowEditPanelAction.treeView
							.getModel().getRoot()).getPath()));
			MBSEShowEditPanelAction.treeView.setSelectionPath(new TreePath(this
					.getPath()));

		} catch (ArrayIndexOutOfBoundsException ex) {
			JOptionPane.showMessageDialog(null, row);
		}
		}

	}

	private boolean isNamedElement() {
		if (type.equals("book") || type.equals("chapter")
				|| type.equals("preface") || type.equals("part")
				|| type.equals("section") || type.equals("bibliography")
				|| type.equals("biblioEntry")) {
			return true;
		}
		return false;
	}

	public Element getElement() {
		return elem;
	}

	public String getCount() {
		// TODO Auto-generated method stub
		return count;
	}

	public void setCount(String ct) {
		count = ct;
	}

	/*public static ArrayList removeDuplicate(ArrayList<Object> arlList) {
		HashSet h = new HashSet(arlList);
		arlList.clear();
		arlList.addAll(h);
		return arlList;
	}*/

}