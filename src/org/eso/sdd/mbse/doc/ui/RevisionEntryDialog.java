package org.eso.sdd.mbse.doc.ui;

import javax.swing.*;

import org.eso.sdd.mbse.doc.algo.Utilities;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;
import com.nomagic.magicdraw.ui.dialogs.SelectElementInfo;
import com.nomagic.magicdraw.ui.dialogs.SelectElementTypes;
import com.nomagic.magicdraw.ui.dialogs.selection.ElementSelectionDlg;
import com.nomagic.magicdraw.ui.dialogs.selection.ElementSelectionDlgFactory;

import com.nomagic.magicdraw.ui.dialogs.selection.TypeFilter;
import com.nomagic.magicdraw.ui.dialogs.selection.TypeFilterImpl;

import com.nomagic.magicdraw.uml.BaseElement;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.*;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class RevisionEntryDialog extends JFrame {

	int selection;
	
	public static RevisionEntryDialogPanel panel;
	
	
	public RevisionEntryDialog(String defRevDate, String defRevNum, String defRevRemarks, String defRevDescription ) {
		super();
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});

		final String[] labels = { "Enter Rev. Date","Enter Rev. Number","Enter Rev. Remarks", "Enter Rev. Description" };
		String[] defaults = { defRevDate,  defRevNum,  defRevRemarks, defRevDescription};
		
		panel = new RevisionEntryDialogPanel(labels, defaults, 40);
		selection = JOptionPane.showConfirmDialog(this, panel,
				"Enter Revision Info", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE);
	}

	public static String[] getResults() {
		return panel.getResults();
	}
	
	public int getSelection(){
		return selection;
	}
	
	public BaseElement getSelectedAuthor() {
		return panel.getSelectedAuthor();
	}
	
	
	public class RevisionEntryDialogPanel extends JPanel {
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		ArrayList<JTextField> options = new ArrayList<JTextField>();
		Utilities ut = new Utilities();
		
		BaseElement selectedAuthor = null;
		JButton authorSelector = null;
		JTextField authorName = null;
		
		public RevisionEntryDialogPanel(String[] labels,String[] defaults ,
				int fieldLength) {
			super();
			if (fieldLength == -1) {
				fieldLength = 20;
			}
			this.setLayout(gbl);
			
			JTextField text;
			authorSelector = new JButton("Select Author");
			authorName = new JTextField("",20);
			authorName.setEditable(false);
			addComponent(authorName,0,0,GridBagConstraints.WEST);
			
			addComponent(authorSelector,0,0,GridBagConstraints.EAST);
			authorSelector.addActionListener(new ActionListener() { 
				@Override
				public void actionPerformed(ActionEvent e) {
					// here we have to call the selector 
					selectAuthor();
				}
			});
			for (int i = 0; i < labels.length; i++) {
				final String labl = labels[i];
				addComponent(new JLabel(labels[i]), 0, i * 2 +2,
						GridBagConstraints.WEST);
				text = new JTextField(defaults[i], fieldLength);

				// add edit button
				final CustomButton button = new CustomButton("...");
				button.setID(i);
				button.setPreferredSize(new Dimension(20, 20));

				
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						int id = button.getID();
						showEditPanel(getResults()[id], id, labl);
					}

				});

				addComponent(text, 0, i * 2 + 3, GridBagConstraints.WEST);
				//addComponent(button, fieldLength + 2, i * 2 + 1,
				//		GridBagConstraints.WEST);
				options.add(text);
			}

			
		}

		protected void selectAuthor() {
			// TODO Auto-generated method stub
				ArrayList<Class<?>> select = new ArrayList<Class<?>>();

				ArrayList<Class<?>> display = new ArrayList<Class<?>>();
				ArrayList<Class<?>> create = new ArrayList<Class<?>>();
				ArrayList<Class<?>> restricted = new ArrayList<Class<?>>();
				
				select.add(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class.class);

				display.add(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package.class);
				display.add(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class.class);
				display.add(com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype.class);
				
				
				restricted.add(ut.getTheAuthorStereotype().getClass());
				
				//display.add(com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile.class);

				SelectElementTypes seTypes = new SelectElementTypes(display,
						select, create, restricted);

				// Martynas says: ElementSelectionDlg is preferred

				TypeFilter selectableFilter = new TypeFilterImpl(seTypes.select) {
				    @Override
					public boolean accept(BaseElement baseElement, boolean b)   {
				        return StereotypesHelper.hasStereotypeOrDerived((Element)baseElement, "author");
				    }
				};

				TypeFilter visibleFilter = new TypeFilterImpl(seTypes.display) {
				    @Override
					public boolean accept(BaseElement baseElement, boolean b)   {
				        return true;
				    }
				};

				SelectElementInfo sei = new SelectElementInfo(true, true,
						Application.getInstance().getProject().getModel(), true);

				ElementSelectionDlg dlg = ElementSelectionDlgFactory
						.create(MDDialogParentProvider.getProvider()
								.getDialogParent());
				
				//ElementSelectionDlgFactory.initSingle(dlg, seTypes, sei,
				//		Application.getInstance().getProject().getModel());

				//initSingle(ElementSelectionDlg dlg, SelectElementInfo info, TypeFilter visibleElementsFilter, TypeFilter selectableElementsFilter, java.util.Collection<?> creatableTypes, java.lang.Object selection) 
				
				ElementSelectionDlgFactory.initSingle(dlg,  sei,  visibleFilter, selectableFilter, null,
						Application.getInstance().getProject().getModel());

				dlg.show();

				if (dlg.getResult() == com.nomagic.ui.DialogConstants.OK
						&& dlg.getSelectedElement() != null) {
					BaseElement be = dlg.getSelectedElement();

 					selectedAuthor = be;

					authorName.setText(Utilities.getFirstElementString((Element)be, ut.getTheAuthorStereotype(), "firstname") + " " + 
						Utilities.getFirstElementString((Element)be, ut.getTheAuthorStereotype(), "surname"));

				}
				
		}

		public String[] getResults() {
			String[] results = new String[options.size()];
			for (int i = 0; i < options.size(); i++) {
				results[i] = ((javax.swing.text.JTextComponent) (options.get(i)))
						.getText();
			}
			return results;
		}
		
		public BaseElement getSelectedAuthor() {
			return selectedAuthor;
		}
		
		public void setEditText(String text, int ID){
			((javax.swing.text.JTextComponent) (options.get(ID))).setText(text);
		}

		public void addComponent(Component component, int xpos, int ypos) {
			gbc.gridx = xpos;
			gbc.gridy = ypos;
			gbc.insets = new Insets(2, 2, 2, 2);
			gbl.setConstraints(component, gbc);
			this.add(component);
		}

		public void addComponent(Component component, int xpos, int ypos, int anchor) {
			gbc.anchor = anchor;
			addComponent(component, xpos, ypos);
		}

		
		private void showEditPanel(String text, int id, String label){
		
			JTextArea area = new JTextArea(15, 35);
			area.setText(text);
			area.setLineWrap(true);
			JScrollPane pane = new JScrollPane(area);

			int result = JOptionPane.showOptionDialog(
			                 this,
			                 new Object[] {label+":", pane},
			                 "Edit Panel",
			                 JOptionPane.OK_CANCEL_OPTION,
			                 JOptionPane.QUESTION_MESSAGE,
			                 null, null, null);

			if (result == JOptionPane.OK_OPTION) {
			     setEditText(area.getText(),id);
			}
		}
		
		
		class CustomButton extends JButton{
			int id;
			
			private CustomButton(String text){
				this.setText(text);
			}
			
			private int getID(){
				return id;
			}
			
			private void setID(int no){
				id = no;
			}
		}
	}
}

