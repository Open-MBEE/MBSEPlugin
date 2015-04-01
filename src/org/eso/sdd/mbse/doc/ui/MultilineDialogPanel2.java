package org.eso.sdd.mbse.doc.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;
import com.nomagic.magicdraw.ui.dialogs.SelectElementInfo;
import com.nomagic.magicdraw.ui.dialogs.SelectElementTypes;
import com.nomagic.magicdraw.ui.dialogs.selection.ElementSelectionDlg;
import com.nomagic.magicdraw.ui.dialogs.selection.ElementSelectionDlgFactory;
import com.nomagic.magicdraw.uml.BaseElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;

@SuppressWarnings("serial")
public class MultilineDialogPanel2 extends JPanel {
	GridBagLayout gbl = new GridBagLayout();
	GridBagConstraints gbc = new GridBagConstraints();
	ArrayList<JCheckBox> options = new ArrayList<JCheckBox>();
	ElementSelectionDlg dlg = null;
	List<BaseElement> be = null;
	static JLabel number = null;
	String selectedPattern = "";

	public MultilineDialogPanel2(String[] labels, String[] defaults,
			int fieldLength) {
		super();
		if (fieldLength == -1) {
			fieldLength = 20;
		}
		;
		this.setLayout(gbl);

		JCheckBox boxer;

		number = new JLabel("Select pattern block(s):");
		addComponent(number, 0, 0 * 2,
				GridBagConstraints.WEST);
		
		// add edit button
		JButton button = new JButton("...");
		button.setPreferredSize(new Dimension(40, 20));
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				createDlg();
				dlg.show(); // this method is deprecated but I could not find any alternative yet.
				
				if (dlg.isOkClicked()
						&& dlg.getSelectedElements() != null) {
					be = dlg.getSelectedElements();
					
					String newTxt = "";
					for(BaseElement kk: be){
						newTxt += ((NamedElement)kk).getName() +"\n";
					}
					selectedPattern = newTxt;
				}
			}

		});
		
		addComponent(button, 5, 0 * 3,
				GridBagConstraints.WEST);
		
		for (int i = 0; i < labels.length; i++) {
			final String labl = labels[i];
			//text = new JTextField(defaults[i], fieldLength);
			
			boxer = new JCheckBox(labl);
		

			addComponent(boxer, 0, i * 2 + 1, GridBagConstraints.WEST);
			options.add(boxer);
		}

	}

	private void createDlg() {
		ArrayList<Class<?>> select = new ArrayList<Class<?>>();
		select.add(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class.class);

		ArrayList<Class<?>> display = new ArrayList<Class<?>>();
		display.add(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package.class);
		display.add(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class.class);
		display.add(com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdmodels.Model.class);

		ArrayList<Class<?>> create = new ArrayList<Class<?>>();

		ArrayList<Class<?>> restricted = new ArrayList<Class<?>>();
		// restricted.add(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram.class);

		SelectElementTypes seTypes = new SelectElementTypes(display,
				select, create, restricted);

		SelectElementInfo sei = new SelectElementInfo(true, false,
				Application.getInstance().getProject().getModel(), true);

		dlg = ElementSelectionDlgFactory
				.create(MDDialogParentProvider.getProvider()
						.getDialogParent());
		

		if(be != null){
			ElementSelectionDlgFactory.initMultiple(dlg, seTypes, sei,be);
		}
		else{
		ElementSelectionDlgFactory.initMultiple(dlg, seTypes, sei,
				Application.getInstance().getProject().getModel().getMdExtensions());
		}
	}

	public Boolean[] getResults() {
		Boolean[] results = new Boolean[options.size()];
		for (int i = 0; i < options.size(); i++) {
			results[i] = options.get(i).isSelected();
		}
		return results;
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
	
	public List<BaseElement> getSelectedElements(){
		return be;
	}
	
	private void displayWarning(String text) {
		JOptionPane.showMessageDialog(MDDialogParentProvider.getProvider()
				.getDialogParent(), text);

	}
	
}
