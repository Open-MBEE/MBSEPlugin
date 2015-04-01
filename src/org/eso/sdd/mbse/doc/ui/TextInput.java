/**
 * TODO This file seems to be unused and copied from a tutorial. Check whether it should be removed!
 * I commented it's contents to clearly state that is not needed.
 */
package org.eso.sdd.mbse.doc.ui;


//import java.awt.*;
//
//import java.awt.event.*;
//import javax.swing.*;
//import javax.swing.text.*;
//
//import org.eso.sdd.mbse.doc.algo.SpringUtilities;

/**
 * TextInputDemo.java uses these additional files:
 *   SpringUtilities.java
 *   ...
 */
public class TextInput { //extends JPanel implements ActionListener, FocusListener {
//	JTextArea paragraphField;
//	JFormattedTextField zipField;
//	JSpinner stateSpinner;
//	boolean addressSet = false;
//	Font regularFont, italicFont;
//	JLabel addressDisplay;
//	final static int GAP = 10;
//
//    public TextInput() {
//        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
//
//        JPanel leftHalf = new JPanel() {
//            //Don't allow us to stretch vertically.
//            @Override
//			public Dimension getMaximumSize() {
//                Dimension pref = getPreferredSize();
//                return new Dimension(Integer.MAX_VALUE,
//                                     pref.height);
//            }
//        };
//        leftHalf.setLayout(new BoxLayout(leftHalf,
//                                         BoxLayout.PAGE_AXIS));
//        leftHalf.add(createEntryFields());
//        leftHalf.add(createButtons());
//
//        add(leftHalf);
//    }
//
//    protected JComponent createButtons() {
//        JPanel panel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
//
//        JButton button = new JButton("OK");
//        button.addActionListener(this);
//        panel.add(button);
//
//        button = new JButton("Cancel");
//        button.addActionListener(this);
//        button.setActionCommand("Cancel");
//        panel.add(button);
//
//        //Match the SpringLayout's gap, subtracting 5 to make
//        //up for the default gap FlowLayout provides.
//        panel.setBorder(BorderFactory.createEmptyBorder(0, 0,
//                                                GAP-5, GAP-5));
//        return panel;
//    }
//
//    /**
//     * Called when the user clicks the button or presses
//     * Enter in a text field.
//     */
//    @Override
//	public void actionPerformed(ActionEvent e) {
//        if ("Cancel".equals(e.getActionCommand())) {
//            addressSet = false;
//            paragraphField.setText("");
//        } else {
//            addressSet = true;
//            setVisible(false);
//
//        }
//    }
//    
//    //A convenience method for creating a MaskFormatter.
//    protected MaskFormatter createFormatter(String s) {
//        MaskFormatter formatter = null;
//        try {
//            formatter = new MaskFormatter(s);
//        } catch (java.text.ParseException exc) {
//            System.err.println("formatter is bad: " + exc.getMessage());
//            System.exit(-1);
//        }
//        return formatter;
//    }
//
//    /**
//     * Called when one of the fields gets the focus so that
//     * we can select the focused field.
//     */
//    @Override
//	public void focusGained(FocusEvent e) {
//        Component c = e.getComponent();
//        if (c instanceof JFormattedTextField) {
//            selectItLater(c);
//        } else if (c instanceof JTextField) {
//            ((JTextField)c).selectAll();
//        }
//    }
//
//    //Workaround for formatted text field focus side effects.
//    protected void selectItLater(Component c) {
//        if (c instanceof JFormattedTextField) {
//            final JFormattedTextField ftf = (JFormattedTextField)c;
//            SwingUtilities.invokeLater(new Runnable() {
//                @Override
//				public void run() {
//                    ftf.selectAll();
//                }
//            });
//        }
//    }
//
//    //Needed for FocusListener interface.
//    @Override
//	public void focusLost(FocusEvent e) { } //ignore
//
//    protected JComponent createEntryFields() {
//        JPanel panel = new JPanel(new SpringLayout());
//
//        String[] labelStrings = {
//            "Enter Paragraph ",
//        };
//
//        JLabel[] labels = new JLabel[labelStrings.length];
//        JComponent[] fields = new JComponent[labelStrings.length];
//        int fieldNum = 0;
//
//        //Create the text field and set it up.
//        paragraphField  = new JTextArea();
//        paragraphField.setColumns(40);
//        paragraphField.setBounds(new Rectangle(40,40));
//        fields[fieldNum++] = paragraphField;
//
//        //Associate label/field pairs, add everything,
//        //and lay it out.
//        for (int i = 0; i < labelStrings.length; i++) {
//            labels[i] = new JLabel(labelStrings[i],
//                                   JLabel.TRAILING);
//            labels[i].setLabelFor(fields[i]);
//            panel.add(labels[i]);
//            panel.add(fields[i]);
//
//            //Add listeners to each field.
//            JTextArea tf = null;
//            if (fields[i] instanceof JSpinner) {
//                //tf = getTextField((JSpinner)fields[i]);
//            } else {
//                tf = (JTextArea)fields[i];
//            }
//            //tf.addActionListener(this);
//            //tf.addFocusListener(this);
//        }
//        SpringUtilities.makeCompactGrid(panel,
//                                        labelStrings.length, 2,
//                                        GAP, GAP, //init x,y
//                                        GAP, GAP/2);//xpad, ypad
//        return panel;
//    }
//
// 
//    public JFormattedTextField getTextField(JSpinner spinner) {
//        JComponent editor = spinner.getEditor();
//        if (editor instanceof JSpinner.DefaultEditor) {
//            return ((JSpinner.DefaultEditor)editor).getTextField();
//        } else {
//            System.err.println("Unexpected editor type: "
//                               + spinner.getEditor().getClass()
//                               + " isn't a descendant of DefaultEditor");
//            return null;
//        }
//    }
//
//    /**
//     * Create the GUI and show it.  For thread safety,
//     * this method should be invoked from the
//     * event dispatch thread.
//     */
//    private static void createAndShowGUI() {
//        //Create and set up the window.
//        JFrame frame = new JFrame("Enter Paragraph Text");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//        TextInput myTextInput = new TextInput();
//        //Add contents to the window.
//        frame.add(myTextInput);
//
//        //Display the window.
//        frame.pack();
//        frame.setVisible(true);
//        System.out.println("am I finished yet?");        
//    }
//
//    public static void main(String[] args) {
//        //Schedule a job for the event dispatch thread:
//        //creating and showing this application's GUI.
//    	try { 
//    		javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
//    			@Override
//				public void run() {
//    				//Turn off metal's use of bold fonts
//    				UIManager.put("swing.boldMetal", Boolean.FALSE);
//    				createAndShowGUI();
//    			}
//    		});
//    	} catch(Exception e) { 
//    		e.printStackTrace();
//    		
//    	}
//        System.out.println("I was here");
//
//    
//    }
}
