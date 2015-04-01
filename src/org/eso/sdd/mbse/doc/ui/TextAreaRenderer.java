package org.eso.sdd.mbse.doc.ui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.Map.Entry;

@SuppressWarnings("serial")
public class TextAreaRenderer extends JEditorPane implements TableCellRenderer {
	private final DefaultTableCellRenderer adaptee = new DefaultTableCellRenderer();
	/** map from table to map of rows to map of column heights */
	private final Map<JTable, Map<Integer, Map<Integer, Integer>>> cellSizes = 
			new HashMap<JTable, Map<Integer, Map<Integer, Integer>>>();

	public TextAreaRenderer() {
		setContentType("text/html");

	}

	@Override
	public Component getTableCellRendererComponent(
			//
			JTable table, Object obj, boolean isSelected, boolean hasFocus,
			int row, int column) {

		if (obj instanceof JLabel) { // Boolean
			
			JLabel imgLabel = (JLabel)obj;
			imgLabel.setSize(400, 400);
			int height = imgLabel.getHeight();
			table.setRowHeight(row, height+100);
			return imgLabel;
		}

		adaptee.getTableCellRendererComponent(table, obj, isSelected, hasFocus,
				row, column);
		setForeground(adaptee.getForeground());
		setBackground(adaptee.getBackground());
		setBorder(adaptee.getBorder());
		setFont(adaptee.getFont());

		setText(adaptee.getText());


		// This line was very important to get it working with JDK1.4
		TableColumnModel columnModel = table.getColumnModel();
		setSize(columnModel.getColumn(column).getWidth(), 100);
		int height_wanted = (int) getPreferredSize().getHeight();
		addSize(table, row, column, height_wanted);
		height_wanted = findTotalMaximumRowSize(table, row);
		if (height_wanted != table.getRowHeight(row)) {
			table.setRowHeight(row, height_wanted);
		}

		return this;
	}

	private void addSize(JTable table, int row, int column, int height) {
		Map<Integer, Map<Integer, Integer>> rows = cellSizes.get(table);
		if (rows == null) {
			rows = new HashMap<Integer, Map<Integer, Integer>>();
			cellSizes.put(table, rows);
		}
		Map<Integer, Integer> rowheights = rows.get(new Integer(row));
		if (rowheights == null) {
			rowheights = new HashMap<Integer, Integer>();
			rows.put(new Integer(row), rowheights);
		}
		rowheights.put(new Integer(column), new Integer(height));
	}

	/**
	 * Look through all columns and get the renderer. If it is also a
	 * TextAreaRenderer, we look at the maximum height in its hash table for
	 * this row.
	 */
	private int findTotalMaximumRowSize(JTable table, int row) {
		int maximum_height = 0;
		Enumeration<TableColumn> columns = table.getColumnModel().getColumns();
		while (columns.hasMoreElements()) {
			TableColumn tc = columns.nextElement();
			TableCellRenderer cellRenderer = tc.getCellRenderer();
			if (cellRenderer instanceof TextAreaRenderer) {
				TextAreaRenderer tar = (TextAreaRenderer) cellRenderer;
				maximum_height = Math.max(maximum_height,
						tar.findMaximumRowSize(table, row));
			}
		}
		return maximum_height;
	}

	private int findMaximumRowSize(JTable table, int row) {
		Map<Integer, Map<Integer, Integer>> rows = cellSizes.get(table);
		if (rows == null)
			return 0;
		Map<Integer, Integer> rowheights = rows.get(new Integer(row));
		if (rowheights == null)
			return 0;
		int maximum_height = 0;
		for (Entry<Integer, Integer> entry : rowheights.entrySet()) {
			int cellHeight = entry.getValue().intValue();
			maximum_height = Math.max(maximum_height, cellHeight);
		}
		return maximum_height;
	}

}
