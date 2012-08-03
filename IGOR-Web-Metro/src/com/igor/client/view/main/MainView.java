package com.igor.client.view.main;

import gwtupload.client.SingleUploader;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.igor.client.IGOR_Web_Metro;
import com.igor.client.LoggedCallback;
import com.igor.client.view.FadeAnimation;
import com.igor.client.view.UploadWidget;
import com.igor.client.view.main.attr.AttributeEditor;
import com.igor.client.view.main.image.IGORImage;
import com.igor.client.view.main.image.ImagePanel;
import com.igor.client.view.main.table.VariableDTOTable;
import com.igor.shared.TemplateDTO;
import com.igor.shared.VariableDTO;

public class MainView extends Composite {
	interface MainUIBinder extends UiBinder<LayoutPanel, MainView> {
	}

	private static MainUIBinder uiBinder = GWT.create(MainUIBinder.class);

	@UiField
	HorizontalPanel topPanel;

	@UiField
	FocusPanel logo;
	FocusPanel upload;
	FocusPanel download;
	FocusPanel generate;

	@UiField
	SimpleLayoutPanel questionbox;
	@UiField
	SimpleLayoutPanel imagesbox;
	@UiField
	SimpleLayoutPanel featuresbox;
	@UiField
	SimpleLayoutPanel attributesbox;

	// @UiField
	// HorizontalPanel images;
	// @UiField
	// ScrollPanel imagesScroll;

	@UiField
	TextArea question;

	private SingleUploader uploader;
	private boolean canUpload = true;
	private TemplateDTO template = new TemplateDTO();
	private VariableDTOTable featuresTable;
	private ImagePanel imagePanel;
	private AttributeEditor attributesEditor;

	public MainView(final IGOR_Web_Metro sp) {
		initWidget(uiBinder.createAndBindUi(this));

		upload = new FocusPanel();
		upload.setStyleName("top-button upload");
		// uploader = new SingleUploader(FileInputType.CUSTOM.with(upload,
		// false));
		// uploader.setValidExtensions(".xml");
		// uploader.setServletPath("xml.xmlupload");
		// uploader.setStatusWidget(null);
		// uploader.setAutoSubmit(true);
		// uploader.addOnFinishUploadHandler(new
		// IUploader.OnFinishUploaderHandler() {
		// public void onFinish(IUploader uploader) {
		// if (!canUpload) {
		// return;
		// }
		// canUpload = false;
		// if (uploader.getStatus() == Status.SUCCESS) {
		// IGOR_Web_Metro.model.upload(
		// new AsyncCallback<TemplateDTO>() {
		//
		// @Override
		// public void onFailure(Throwable caught) {
		// MainView.this
		// .displayError("Errors while processing file.");
		// }
		//
		// @Override
		// public void onSuccess(TemplateDTO result) {
		// MainView.this.setTemplate(result);
		// }
		// });
		// }
		// new Timer() {
		//
		// @Override
		// public void run() {
		// canUpload = true;
		// }
		//
		// }.schedule(1000);
		// }
		// });
		// uploader.addStyleName("top-button");
		// topPanel.add(uploader);
		topPanel.add(upload);

		UploadWidget xmlUpl = new UploadWidget(upload, "upload.xmlupload");
		upload.setWidget(xmlUpl);
		xmlUpl.addOnFinishedHandler(new SubmitCompleteHandler() {

			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				IGOR_Web_Metro.model.upload(new LoggedCallback<TemplateDTO>() {

					@Override
					public void handleError(Throwable caught) {
						MainView.this
								.displayError("Errors while processing file.");
					}

					@Override
					public void onSuccess(TemplateDTO result) {
						MainView.this.setTemplate(result);
					}
				});
			}
		});

		logo.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				new FadeAnimation(MainView.this, false) {

					@Override
					public void finished() {
						sp.logout();
					}

				}.run(200);
			}
		});

		download = new FocusPanel();
		download.setStyleName("top-button download");
		download.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (sp.user.name.equals("guest")) {
					Window.alert("Can't do this as a guest user!");
					return;
				}
				rebuildTemplate();
				IGOR_Web_Metro.model.download(template,
						new LoggedCallback<String>() {

							@Override
							public void handleError(Throwable caught) {
//								StackTraceElement[] e = caught.getStackTrace();
//								String st = "";
//								for (int i = 0; i < e.length; i++) {
//								}
//								displayError(st);
								displayError("Could not download current template as xml.");
							}

							@Override
							public void onSuccess(String result) {
								if (result == null)
									handleError(null);
								else
									startDownload(result);
							}
						});
			}
		});
		topPanel.add(download);

		generate = new FocusPanel();
		generate.setStyleName("top-button generate");
		generate.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (sp.user.name.equals("guest")) {
					Window.alert("Can't do this as a guest user!");
					return;
				}
				rebuildTemplate();
				IGOR_Web_Metro.model.generate(template,
						new LoggedCallback<String>() {

							@Override
							public void onSuccess(String result) {
								if (result == null)
									handleError(null);
								else
									MainView.this.displayGenerated(result);
							}

							@Override
							public void handleError(Throwable caught) {
								displayError("Could not generate HTML for current model.");
							}
						});
			}
		});
		topPanel.add(generate);

		featuresTable = new VariableDTOTable(this);
		featuresbox.add(featuresTable);

		imagePanel = new ImagePanel(this);
		imagesbox.add(imagePanel);

		attributesEditor = new AttributeEditor(this);
		attributesbox.add(attributesEditor);

		if (Cookies.getCookie("animate-main") == null) {
			animate();
		}
		Cookies.removeCookie("animate-main");

	}

	public void displayError(String error) {
		Window.alert(error);
	}

	protected void rebuildTemplate() {
		template.stem = question.getText();
		template.variables = featuresTable.getRowData();
	}

	public void setTemplate(TemplateDTO result) {
		if (result == null) {
			return;
		}
		this.template = result;
		question.setText(result.stem);
		featuresTable.setRowData(template.variables);
		imagePanel.clear();
		for (VariableDTO vdt : template.variables) {
			if (vdt.type == VariableDTO.VarType.IMAGE)
				for (String url : vdt.urls)
					imagePanel.addImage(url);
		}
	}

	protected void displayGenerated(String url) {
		// Window.alert(GWT.getHostPageBaseURL() + url);
		Cookies.setCookie("animate-main", "false");
		Window.Location.assign(GWT.getHostPageBaseURL() + url);
	}

	protected void startDownload(String url) {
		// Window.alert(GWT.getHostPageBaseURL() + url);
		Cookies.setCookie("animate-main", "false");
		Window.Location.assign(GWT.getHostPageBaseURL() + url);
	}

	private void animate() {
		logo.getElement().getStyle().setOpacity(0);
		upload.getElement().getStyle().setOpacity(0);
		download.getElement().getStyle().setOpacity(0);
		generate.getElement().getStyle().setOpacity(0);
		questionbox.getElement().getStyle().setOpacity(0);
		imagesbox.getElement().getStyle().setOpacity(0);
		featuresbox.getElement().getStyle().setOpacity(0);
		attributesbox.getElement().getStyle().setOpacity(0);

		final Animation appFade = new Animation() {
			@Override
			protected void onUpdate(double progress) {
				questionbox.getElement().getStyle().setOpacity(progress);
				imagesbox.getElement().getStyle().setOpacity(progress);
				featuresbox.getElement().getStyle().setOpacity(progress);
				attributesbox.getElement().getStyle().setOpacity(progress);
			}

			@Override
			public void onComplete() {
				questionbox.getElement().getStyle().clearOpacity();
				imagesbox.getElement().getStyle().clearOpacity();
				featuresbox.getElement().getStyle().clearOpacity();
				attributesbox.getElement().getStyle().clearOpacity();
			}
		};
		final ChainedAnimation headerFade = new ChainedAnimation(logo, upload,
				download, generate) {

			@Override
			public void update(Widget w, double progress) {
				w.getElement().getStyle().setOpacity(progress);
				w.getElement().getStyle()
						.setMarginLeft(-25 + progress * 25, Unit.PX);
			}

			@Override
			public void completed(Widget w) {
				w.getElement().getStyle().clearOpacity();
				w.getElement().getStyle().clearMarginLeft();
			}

			@Override
			public void finished() {
				appFade.run(500);
			}

		};
		new Timer() {
			public void run() {
				headerFade.start(300, 0.5);
			}
		}.schedule(1000);
	}

	public void updateFeatures() {
		featuresTable.update();
	}

	public void buildAttributes(VariableDTO current) {
		if (current == null) {
			attributesEditor.clear();
			return;
		}
		attributesEditor.setCurrentVariable(current);
	}

	public List<IGORImage> getImages() {
		return imagePanel.getImages();
	}

	public List<IGORImage> getSelectedImages() {
		return imagePanel.getSelected();
	}

	public void deselectImages() {
		imagePanel.deselect();
	}

	public void setHasImageSelection(boolean selectionExists) {
		attributesEditor.setImagesSelected(selectionExists);
	}

}
