package org.eso.sdd.mbse.doc.ui;

import javax.swing.*;
import java.awt.event.*;

@SuppressWarnings("serial")
public class CustomDialog extends JFrame {

	int selection;
	
	public static MultilineDialogPanel panel;
	public static MultilineDialogPanel2 panel2;

	public CustomDialog(String title, String type) {
		super(title);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});

		if (type.equals("book")) {
			String[] labels = { "Enter Book Name", "Enter Document Number ",
					"Enter Issue Date", "Enter Issue Number" };
			String[] defaults = { "", "", "", "" };
			panel = new MultilineDialogPanel(labels, defaults, 40);
			selection = JOptionPane.showConfirmDialog(this, panel,
					"Multiline Dialog", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			System.out.println(selection);
			if(selection == 2){
				System.out.println("canceled");
			}
			
		} else if (type.equals("bibliography")) {
			String[] labels = { "Enter Biblioentry Name",
					"Enter Biblioentry Title", "Enter Issue Number",
					"Enter Product Number", "Enter Publication Date" };
			String[] defaults = { "", "", "","","" };
			panel = new MultilineDialogPanel(labels, defaults, 40);
			selection = JOptionPane.showConfirmDialog(this, panel,
					"Multiline Dialog", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			
		} else if (type.equals("revhistory")) {
			String[] labels = { "Enter Author Name",
					"Enter Author Initials", "Enter Rev. Date",
					"Enter Rev. Description","Enter Rev. Number",  
					"Enter Rev. Remarks" };
			String[] defaults = { "", "", "","","","" };
			panel = new MultilineDialogPanel(labels, defaults, 40);
			selection = JOptionPane.showConfirmDialog(this, panel,
					"Multiline Dialog", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);
		}
		
		else if (type.equals("apply")) {
			String[] labels = { "apply recursively",
					"set role names",
					"set subsetted properties",
					"redefine value properties",
					"create value properties"};
			String[] defaults = { "", "" };
			panel2 = new MultilineDialogPanel2(labels, defaults, 40);
			selection = JOptionPane.showConfirmDialog(this, panel2,
					"System Reasoner Pattern Wizard", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (selection == 2)
	            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
		
		else if (type.equals("unapply")) {
			String[] labels = { "unapply recursively",
					"remove role names",
					"remove subsetted properties",
					"remove redefined value properties",
					"remove value properties"};
			String[] defaults = { "", "" };
			panel2 = new MultilineDialogPanel2(labels, defaults, 40);
			selection = JOptionPane.showConfirmDialog(this, panel2,
					"Remove Pattern Wizard", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (selection == 2)
	            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
		
	}

	public static void main(String[] args) {
		CustomDialog dlg = new CustomDialog("System Reasoner Pattern Wizard", "unapply");
		System.out.println(panel2.getResults()[0]);
		System.out.println(panel2.getResults()[1]);
		System.out.println(panel2.getResults()[2]);
		System.out.println(panel2.getResults()[3]);
		//System.out.println(selection+"");
		//dlg.setVisible(true);
		//System.exit(0);
	}

	public static MultilineDialogPanel getResults() {
		return panel;
	}
	
	public static MultilineDialogPanel2 getResults2() {
		return panel2;
	}
	
	public int getSelection(){
		return selection;
	}
}
