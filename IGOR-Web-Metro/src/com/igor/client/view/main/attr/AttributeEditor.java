package com.igor.client.view.main.attr;


import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.igor.client.view.main.MainView;
import com.igor.shared.VariableDTO;

public class AttributeEditor extends Composite {

	protected VariableDTO current;
	protected LayoutPanel main = new LayoutPanel();
	MainView view;
	private Editor editor;

	public AttributeEditor(MainView mainView) {
		initWidget(main);
		this.view = mainView;
	}

	public void setCurrentVariable(VariableDTO current) {
		this.current = current;
		main.clear();
		switch (current.type) {
		case CONSTRAINT:
			editor = new ConstraintEdit(main);
			break;
		case IMAGE:
			editor = new ImageEdit(this, main);
			break;
		case NUMERIC:
			editor = new NumberEdit(main);
			break;
		case TEXT:
			editor = new TextEdit(this, main);
			break;
		}
	}

	abstract class Editor {

		public Editor(LayoutPanel main) {
			
		}

	}

	class NumberEdit extends Editor {

		public NumberEdit(LayoutPanel main) {
			super(main);
			FlowPanel m = new FlowPanel();
			HorizontalPanel h = new HorizontalPanel();
			Label rangeL = new Label("Range:");
			Label toL = new Label("to");
			Label stepL = new Label("Step:");
			final TextBox min = new TextBox();
			final TextBox max = new TextBox();
			final TextBox step = new TextBox();
			rangeL.setStyleName("number-label");
			toL.setStyleName("number-label");
			stepL.setStyleName("number-label");
			min.setStyleName("number-input");
			max.setStyleName("number-input");
			step.setStyleName("number-input");

			min.setText(current.getMin());
			max.setText(current.getMax());
			step.setText(current.getStep());

			ChangeHandler ch = new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					current.setMin(min.getText());
					current.setMax(max.getText());
					current.setStep(step.getText());
				}
			};
			min.addChangeHandler(ch);
			max.addChangeHandler(ch);
			step.addChangeHandler(ch);

			m.add(rangeL);
			h.add(min);
			h.add(toL);
			h.add(max);
			m.add(h);
			m.add(stepL);
			m.add(step);
			main.add(m);
			main.setWidgetLeftRight(m, 20, Unit.PX, 20, Unit.PX);
			main.setWidgetTopBottom(m, 50, Unit.PX, 20, Unit.PX);
		}

	}

	class ConstraintEdit extends Editor {

		public ConstraintEdit(LayoutPanel main) {
			super(main);
			final TextArea constraint = new TextArea();
			constraint.setStyleName("constraint-edit");
			main.add(constraint);
			main.setWidgetLeftRight(constraint, 20, Unit.PX, 20, Unit.PX);
			main.setWidgetTopBottom(constraint, 50, Unit.PX, 20, Unit.PX);
			constraint.setText(current.constraint);
			constraint.addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					current.constraint = constraint.getText();
					view.updateFeatures();
				}
			});
		}

	}

	public void clear() {
		if (editor instanceof TextEdit){
			((TextEdit) editor).buildText();
		}
		main.clear();
	}

	public void setImagesSelected(boolean selectionExists) {
		if (editor instanceof ImageEdit){
			((ImageEdit) editor).imagesSelected(selectionExists);
		}
	}

}
