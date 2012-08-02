package com.igor.client.view.main.table;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.igor.client.view.main.MainView;
import com.igor.shared.VariableDTO;

public class VariableDTOTable extends EditableTable<VariableDTO> {
	private static final int NAME_LENGTH = 30;
	private EditableCol<TextBox> nameCol;
	private MainView view;
	private EditableCol<ListBox> typeCol;

	public VariableDTOTable(MainView mainView) {
		super();
		this.view = mainView;
		nameCol = new EditableCol<TextBox>() {

			@Override
			public TextBox buildWidget() {
				TextBox t = new TextBox();
				t.setStyleName("input");
				return t;
			}

			@Override
			public String displayItem(VariableDTO item) {
				return shorten(item.getName(), NAME_LENGTH);
			}

			@Override
			public void setItem(VariableDTO item) {
				widget.setText(item.getName());
			}

			@Override
			public VariableDTO getItem(int row, VariableDTO item) {
				item.setName(widget.getText());
				return item;
			}
		};
		typeCol = new EditableCol<ListBox>() {

			@Override
			public ListBox buildWidget() {
				ListBox w = new ListBox();
				ChangeHandler ch = getItemChangeHandler(new ItemChangedHandler() {

					@Override
					public void itemChanged(VariableDTO item) {
						item.type = VariableDTO.VarType.values()[widget
								.getSelectedIndex()];
						selectionChanged(item);
					}
				});
				w.addChangeHandler(ch);
				w.setStyleName("input");
				for (String s : VariableDTO.typeStrings())
					w.addItem(s);
				return w;
			}

			@Override
			public String displayItem(VariableDTO item) {
				return item.getTypeString();
			}

			@Override
			public void setItem(VariableDTO item) {
				widget.setSelectedIndex(VariableDTO.toIndex(item.type));
			}


			@Override
			public VariableDTO getItem(int row, VariableDTO item) {
				item.type = VariableDTO.VarType.values()[widget
				                                         .getSelectedIndex()];
				return item;
			}
		};
		addColumn(nameCol);
		addColumn(typeCol);

	}

	private String shorten(String s, int i) {
		if (s.length() > i) {
			return s.substring(0, i - 3) + "...";
		}
		return s;
	}

	@Override
	protected FlexTable buildHeader() {

		FlexTable header = new FlexTable();
		header.setStyleName("features-table-header");
		header.setHTML(0, 0, "Name");
		header.setHTML(0, 1, "Type");
		header.getColumnFormatter().getElement(0).getStyle()
				.setWidth(50, Unit.PCT);
		return header;

	}

	@Override
	protected void selectionChanged(VariableDTO selection) {
		view.buildAttributes(selection);

	}

	@Override
	protected VariableDTO emptyItem() {
		return new VariableDTO();
	}

	@Override
	protected String getTableStyle() {
		return "features-table";
	}

	@Override
	protected String getTableScrollStyle() {
		return "features-table-scroll";
	}

}
