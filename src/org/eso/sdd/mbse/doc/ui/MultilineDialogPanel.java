package org.eso.sdd.mbse.doc.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class MultilineDialogPanel extends JPanel {
	GridBagLayout gbl = new GridBagLayout();
	GridBagConstraints gbc = new GridBagConstraints();
	ArrayList<JTextField> options = new ArrayList<JTextField>();

	public MultilineDialogPanel(String[] labels, String[] defaults,
			int fieldLength) {
		super();
		if (fieldLength == -1) {
			fieldLength = 20;
		}
		;
		this.setLayout(gbl);
		
		JTextField text;
		
		for (int i = 0; i < labels.length; i++) {
			final String labl = labels[i];
			addComponent(new JLabel(labels[i]), 0, i * 2,
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

			addComponent(text, 0, i * 2 + 1, GridBagConstraints.WEST);
			addComponent(button, fieldLength + 2, i * 2 + 1,
					GridBagConstraints.WEST);
			options.add(text);
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
