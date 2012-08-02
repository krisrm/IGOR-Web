package com.igor.client.view.main.table;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

public abstract class EditableTable<T> extends Composite {

	private LayoutPanel main = new LayoutPanel();
	protected FlexTable table = new FlexTable();
	private List<T> items = new ArrayList<T>();
	private Cell editing;
	private Button addBtn;
	private Button removeBtn;
	private List<EditableCol<?>> columns = new ArrayList<EditableCol<?>>();

	// private TextBox nameEdit;
	// private ListBox typeEdit;
	// private MainView view;

	class TableCell extends Cell {

		protected TableCell(HTMLTable htmlTable, int rowIndex, int cellIndex) {
			htmlTable.super(rowIndex, cellIndex);
		}

	}

	public abstract class EditableCol<I extends FocusWidget> {
		protected I widget;

		public EditableCol() {
			widget = buildWidget();
		}

		public abstract I buildWidget();

		public int col() {
			return columns.indexOf(this);
		}

		public abstract String displayItem(T item);

		public abstract void setItem( T item);

		public abstract T getItem(int row, T item);

		abstract class ItemChangedHandler {
			public abstract void itemChanged(T item);

		}

		public ChangeHandler getItemChangeHandler(final ItemChangedHandler c) {
			ChangeHandler ch = new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					if (editing == null)
						return;
					T item = items.get(editing.getRowIndex());
					c.itemChanged(item);
				}
			};

			return ch;
		}

		public I getWidget() {
			return widget;
		}

	}

	public EditableTable() {
		initWidget(main);

		FlexTable header = buildHeader();
		main.add(header);
		main.setWidgetTopHeight(header, 55, Unit.PX, 45, Unit.PX);
		main.setWidgetLeftRight(header, 0, Unit.PX, 0, Unit.PX);

		table.setStylePrimaryName(getTableStyle());
		final ScrollPanel tableScroll = new ScrollPanel(table);
		tableScroll.setStyleName(getTableScrollStyle());

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

		addBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addRowData(EditableTable.this.emptyItem());
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
				EditableTable.this.selectionChanged(null);
			}
		});

		HorizontalPanel btnPanel = new HorizontalPanel();
		btnPanel.add(addBtn);
		btnPanel.add(removeBtn);

		main.add(btnPanel);
		main.setWidgetTopHeight(btnPanel, 10, Unit.PX, 45, Unit.PX);
		main.setWidgetRightWidth(btnPanel, 0, Unit.PX, 130, Unit.PX);

	}

	protected abstract String getTableStyle();

	protected abstract String getTableScrollStyle();

	protected abstract T emptyItem();

	protected abstract FlexTable buildHeader();

	protected abstract void selectionChanged(T selection);

	public void addColumn(EditableCol<?> col) {
		this.columns.add(col);
		resetWidth();

	}

	public List<T> getRowData() {
	
		if (editing != null) {
			int row = editing.getRowIndex();
			for (EditableCol<?> col : columns) {
			
				items.set(row, col.getItem(row,items.get(row)));
			}
		}

		return items;
	}

	public void addRowData(T item) {
		int row = items.size();
		items.add(item);
		if (row % 2 == 0)
			table.getRowFormatter().addStyleName(row, "even");

		for (EditableCol<?> col : columns) {
			table.setText(row, col.col(), col.displayItem(item));
		}
	}

	public void setRowData(List<T> variables) {

		int i = 0;
		for (T item : variables) {
			for (EditableCol<?> col : columns) {
				table.setText(i, col.col(), col.displayItem(item));
			}

			table.getRowFormatter().removeStyleName(i, "clicked");
			if (i % 2 == 0)
				table.getRowFormatter().addStyleName(i, "even");
			i++;
		}
		this.items = variables;
		resetWidth();
	}

	private void resetWidth() {
		for (int i = 0; i < columns.size(); i++) {
			table.getColumnFormatter().getElement(i).getStyle()
					.setWidth(100 / columns.size(), Unit.PCT);
		}
	}

	private void clearRow() {
		if (editing == null)
			return;
		int row = editing.getRowIndex();
		int col = editing.getCellIndex();
		table.getRowFormatter().removeStyleName(row, "clicked");
		T item = items.get(row);
		removeBtn.setEnabled(false);
		editing = null;

		EditableCol<?> column = columns.get(col);
		item = column.getItem(row,item);
		items.set(row, item);
		table.setText(row, col, column.displayItem(item));
		selectionChanged(null);

	}

	private void selectRow() {
		removeBtn.setEnabled(true);
		int row = editing.getRowIndex();
		int col = editing.getCellIndex();
		table.getRowFormatter().addStyleName(row, "clicked");
		T item = items.get(row);

		EditableCol<?> column = columns.get(col);
		FocusWidget w = column.getWidget();
		table.setWidget(row, column.col(), w);
		w.setFocus(true);
		column.setItem(item);
		selectionChanged(item);

	}

	public void update() {
		if (editing == null)
			return;
		int row = editing.getRowIndex();
		int col = editing.getCellIndex();
		T item = items.get(row);
		EditableCol<?> column = columns.get(col);
		column.setItem(item);
		table.setText(row, column.col(), column.displayItem(item));

	}

}
