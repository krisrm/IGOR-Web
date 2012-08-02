package com.igor.client.view.main.table;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.igor.client.view.main.MainView;
import com.igor.shared.VariableDTO;

public class VariableTable extends Composite {
	private static final int NAME = 30;
	private LayoutPanel main = new LayoutPanel();
	private FlexTable table = new FlexTable();
	private TextBox nameEdit;
	private ListBox typeEdit;
	private List<VariableDTO> items = new ArrayList<VariableDTO>();
	private Cell editing;
	private Button addBtn;
	private Button removeBtn;
	private boolean mouseOverRemove;
	private MainView view;

	class TableCell extends Cell {

		protected TableCell(HTMLTable htmlTable, int rowIndex, int cellIndex) {
			htmlTable.super(rowIndex, cellIndex);
		}

	}

	public VariableTable(final MainView mainView) {
		initWidget(main);
		this.view = mainView;
		FlexTable header = new FlexTable();
		header.setStyleName("features-table-header");
		header.setHTML(0, 0, "Name");
		header.setHTML(0, 1, "Type");
		header.getColumnFormatter().getElement(0).getStyle()
				.setWidth(50, Unit.PCT);
		main.add(header);
		main.setWidgetTopHeight(header, 55, Unit.PX, 45, Unit.PX);
		main.setWidgetLeftRight(header, 0, Unit.PX, 20, Unit.PX);

		nameEdit = new TextBox();
		typeEdit = new ListBox();
		nameEdit.setStyleName("input");
		typeEdit.setStyleName("input");

		typeEdit.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				if (editing == null)
					return;
				VariableDTO item = items.get(editing.getRowIndex());
				item.type = VariableDTO.VarType.values()[typeEdit.getSelectedIndex()];
				mainView.buildAttributes(item);
			}
		});
		
		for (String s : VariableDTO.typeStrings())
			typeEdit.addItem(s);

		table.setStylePrimaryName("features-table");
		final ScrollPanel tableScroll = new ScrollPanel(table);
		tableScroll.setStyleName("features-table-scroll");

		BlurHandler bh = new BlurHandler() {

			@Override
			public void onBlur(BlurEvent event) {
				if (!mouseOverRemove)
					clearRow();
			}
		};
//		nameEdit.addBlurHandler(bh);
//		typeEdit.addBlurHandler(bh);

		table.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Cell c = table.getCellForEvent(event);
				if (editing != null
						&& c.getCellIndex() == editing.getCellIndex()
						&& c.getRowIndex() == editing.getRowIndex()) {
					event.stopPropagation();
					return;
				}
				clearRow();
				editing = c;
				selectRow();
			}
		});

		main.add(tableScroll);
		main.setWidgetTopBottom(tableScroll, 100, Unit.PX, 0, Unit.PX);
		main.setWidgetLeftRight(tableScroll, 0, Unit.PX, 0, Unit.PX);

		addBtn = new Button("+");
		addBtn.setStyleName("action-btn");
		removeBtn = new Button("-");
		removeBtn.setStyleName("action-btn");
		removeBtn.setEnabled(false);
		removeBtn.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				mouseOverRemove = true;
			}
		});
		removeBtn.addMouseOutHandler(new MouseOutHandler() {

			@Override
			public void onMouseOut(MouseOutEvent event) {
				mouseOverRemove = false;
			}
		});
		addBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addRowData(new VariableDTO());
				tableScroll.scrollToBottom();
				clearRow();
				editing = new TableCell(table, items.size() - 1, 0);
				selectRow();
			}
		});
		removeBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (editing == null)
					return;
				int row = editing.getRowIndex();
				items.remove(row);
				table.removeRow(row);
				editing = null;
				for (int i = 0; i < items.size(); i++) {
					table.getRowFormatter().removeStyleName(i, "clicked");
					table.getRowFormatter().removeStyleName(i, "even");
					if (i % 2 == 0)
						table.getRowFormatter().addStyleName(i, "even");
				}
				removeBtn.setEnabled(false);
				view.buildAttributes(null);
			}
		});

		HorizontalPanel btnPanel = new HorizontalPanel();
		btnPanel.add(addBtn);
		btnPanel.add(removeBtn);

		main.add(btnPanel);
		main.setWidgetTopHeight(btnPanel, 10, Unit.PX, 45, Unit.PX);
		main.setWidgetRightWidth(btnPanel, 0, Unit.PX, 130, Unit.PX);

	}


	public List<VariableDTO> buildVars() {
		return items;
	}

	public void addRowData(VariableDTO item) {
		int row = items.size();
		items.add(item);
		table.setText(row, 0, shorten(item.getName(),NAME));
		table.setText(row, 1, item.getTypeString());
		if (row % 2 == 0)
			table.getRowFormatter().addStyleName(row, "even");
		table.getColumnFormatter().getElement(0).getStyle()
				.setWidth(50, Unit.PCT);
	}

	public void setRowData(List<VariableDTO> variables) {

		int i = 0;
		for (VariableDTO item : variables) {
			table.setText(i, 0, shorten(item.getName(),NAME));
			table.setText(i, 1, item.getTypeString());
			table.getRowFormatter().removeStyleName(i, "clicked");
			if (i % 2 == 0)
				table.getRowFormatter().addStyleName(i, "even");
			i++;
		}
		this.items = variables;
		table.getColumnFormatter().getElement(0).getStyle()
				.setWidth(50, Unit.PCT);
	}

	private void clearRow() {
		if (editing == null)
			return;
		int row = editing.getRowIndex();
		int col = editing.getCellIndex();
		table.getRowFormatter().removeStyleName(row, "clicked");
		VariableDTO item = items.get(row);
		if (col == 0) {
			item.setName(nameEdit.getText());
			table.setText(row, col, shorten(item.getName(),NAME));
		} else {
			item.type = VariableDTO.VarType.values()[typeEdit
					.getSelectedIndex()];
			table.setText(row, col, item.getTypeString());
		}

		removeBtn.setEnabled(false);
		editing = null;
		view.buildAttributes(null);
	}
	
	private void selectRow() {
		removeBtn.setEnabled(true);
		int row = editing.getRowIndex();
		int col = editing.getCellIndex();
		table.getRowFormatter().addStyleName(row, "clicked");
		VariableDTO item = items.get(row);
		if (col == 0) {
			table.setWidget(row, 0, nameEdit);
			nameEdit.setFocus(true);
			nameEdit.setText(item.getName());
		} else {
			table.setWidget(row, 1, typeEdit);
			typeEdit.setFocus(true);
			typeEdit.setSelectedIndex(VariableDTO.toIndex(item.type));
		}
		view.buildAttributes(item);
	}
	
	public void update(){
		System.out.println("updating");
		if (editing == null)
			return;
		int row = editing.getRowIndex();
		int col = editing.getCellIndex();
		VariableDTO item = items.get(row);
		if (col == 0) {
			nameEdit.setText(item.getName());
			table.setText(row, 1, item.getTypeString());
		} else {
			table.setText(row, 0, shorten(item.getName(),NAME));
			typeEdit.setSelectedIndex(VariableDTO.toIndex(item.type));
		}
	}

	private String shorten(String s, int i){
		if (s.length() > i){
			return s.substring(0,i-3) + "...";
		}
		return s;
	}

}
