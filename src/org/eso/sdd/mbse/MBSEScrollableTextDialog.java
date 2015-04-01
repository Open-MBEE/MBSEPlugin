package org.eso.sdd.mbse;

import java.awt.BorderLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;

@SuppressWarnings("serial")
public class MBSEScrollableTextDialog extends JDialog implements ActionListener {

	private static int result;
	
	private final String ACTIONCMD_OK = "OK";
	private final String ACTIONCMD_CANCEL = "Cancel";
	
	private MBSEScrollableTextDialog(String title, String text) {
		super(MDDialogParentProvider.getProvider().getDialogParent(), title);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		result = 0;
		
		
		String msg = "<html>";
		//String msg = "";
		msg += text; 
		msg += "</html>";
		
		JLabel contentlbl = new JLabel(msg);
		add(new JScrollPane(contentlbl), BorderLayout.CENTER);
		
		JButton okbtn = new JButton(ACTIONCMD_OK);
		okbtn.setActionCommand(ACTIONCMD_OK);
		okbtn.addActionListener(this);
		
		JButton cancelbtn = new JButton(ACTIONCMD_CANCEL);
		cancelbtn.setActionCommand(ACTIONCMD_CANCEL);
		cancelbtn.addActionListener(this);
		
		Panel btnpanel = new Panel(); // default is FlowLayout
		btnpanel.add(okbtn);
		btnpanel.add(cancelbtn);
		add(btnpanel, BorderLayout.SOUTH);
		
		pack();
		setModal(true);
		setLocationRelativeTo(MDDialogParentProvider.getProvider().getDialogParent());
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(ACTIONCMD_OK)) {
			result = JOptionPane.OK_OPTION;
			dispose();
		} else if(e.getActionCommand().equals(ACTIONCMD_CANCEL)) {
			result = JOptionPane.CANCEL_OPTION;
			dispose();
		}
	}
	
	public static int showDialog(String title, String text) {
		MBSEScrollableTextDialog dlg = new MBSEScrollableTextDialog(title, text);
		dlg.setVisible(true);
		
		return result;
	}

}
