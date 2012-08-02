package com.igor.client.view.main.attr;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.igor.client.view.main.attr.AttributeEditor.Editor;
import com.igor.client.view.main.attr.TextEdit.TextTable;
import com.igor.client.view.main.table.EditableTable;

public class TextEdit extends Editor {

	class TextTable extends EditableTable<String>{

		public TextTable() {
//			table.getElement().getStyle().setWidth(99, Unit.PCT);
			
			EditableCol<TextBox> textCol = new EditableCol<TextBox>(){

				@Override
				public TextBox buildWidget() {
					TextBox w = new TextBox();
					w.setStyleName("input");
					return w;
				}

				@Override
				public String displayItem(String item) {
					return item;
				}

				@Override
				public void setItem(String item) {
					widget.setText(item);
				}

				@Override
				public String getItem(int row, String item) {
					String text = widget.getText();
					if (editor.current.urls.size() <= row){
						editor.current.urls.add(text);
					} else {
						editor.current.urls.set(row, text);
					}
					return text;
				}
				
			};
			addColumn(textCol);
		}
		
		@Override
		protected String getTableStyle() {
			return "text-table";
		}

		@Override
		protected String getTableScrollStyle() {
			return "text-table-scroll";
		}

		@Override
		protected String emptyItem() {
			
			return "";
		}

		@Override
		protected FlexTable buildHeader() {
			FlexTable header = new FlexTable();
			header.setStyleName("text-table-header");
			header.setHTML(0, 0, "Text");
			return header;
		}

		@Override
		protected void selectionChanged(String selection) {
			
		}
		
	}

	private AttributeEditor editor;
	private TextTable table;
	
	public TextEdit(AttributeEditor attributeEditor, LayoutPanel main) {
		attributeEditor.super(main);
		this.editor = attributeEditor;
		table = new TextTable();
		
		table.setRowData(editor.current.keys);
		
		main.add(table);
		main.setWidgetTopBottom(table, 20, Unit.PX, 0, Unit.PX);
		main.setWidgetLeftRight(table, 0, Unit.PX, 0, Unit.PX);
		
	}

	public void buildText() {
		editor.current.keys = table.getRowData();
	}

}
