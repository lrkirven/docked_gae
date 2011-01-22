package com.zarcode.client;

import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class LLRegistrar implements EntryPoint {
	
	private static String REGISTER_URI = "register";
	
	private static String REGISTER_RETURN_URI = "registerReturn";
	
	public static final String simplePassword = "lflsfsfnsflsfj";
	
	private static Logger logger = Logger.getLogger(LLRegistrar.class.getName());
	
	private List<String> loginUrls = null;
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final RegisterServiceAsync registerService = GWT.create(RegisterService.class);
	
	private TextBox emailAddrField = null;
	
	private Button manualRegisterBtn = null;
	
	//
	// Create the popup dialog box
	//
	final DialogBox dialogBox = new DialogBox();
	
	private Button closeButton = null;
	
	final HTML serverResponseLabel = new HTML();

	private FlexTable buildProviderTable(List<String> links) {
		int i = 0;
		FlexTable providerTable = new FlexTable();
		providerTable.setWidth("300px");
		providerTable.setStyleName("providerTable");
	    int numRows = providerTable.getRowCount();
	   
	    // Google
	    Image image0 = new Image();
	    Anchor target0 = new Anchor();
	    String html = "<img src=\"images/googleB.png\" />";
	    target0.setHTML(html);
	    target0.setHref(links.get(i++));
	    providerTable.setWidget(numRows, 0, target0);
	    numRows = providerTable.getRowCount();
	   
	    // Yahoo
	    Anchor target1 = new Anchor();
	    html = "<img src=\"images/yahooB.png\" />";
	    target1.setHTML(html);
	    target1.setHref(links.get(i++));
	    providerTable.setWidget(numRows, 0, target1);
	    numRows = providerTable.getRowCount();
	   
	    // AOL
	    Anchor target2 = new Anchor();
	    html = "<img src=\"images/aolB.png\" />";
	    target2.setHTML(html);
	    target2.setHref(links.get(i++));
	    providerTable.setWidget(numRows, 0, target2);
	    numRows = providerTable.getRowCount();
	    
	    return providerTable;
	}
	
	private void buildUI(List<String> loginUrls) {
		
		final VerticalPanel vPanel = new VerticalPanel();
	    vPanel.addStyleName("widePanel");
	    vPanel.setHorizontalAlignment(VerticalPanel.ALIGN_LEFT);
	    /*
	    vPanel.setHeight(Window.getClientHeight() + "px");
	    Window.addResizeHandler(new ResizeHandler() {
	    	public void onResize(ResizeEvent event) {
	    		int height = event.getHeight();
	    		vPanel.setHeight(height + "px");
	    	}
	    });
	    */
	    
	    final Image logo = new Image();
	    logo.setUrl("images/user.png");
	    logo.setStyleName("logo");
	    vPanel.add(logo);
	    
		final Label regPrompt = new Label();
		regPrompt.setText("To register, please select a provider below that matches your email address:");
		regPrompt.setStyleName("prompt");
		vPanel.add(regPrompt);
	    
		final FlexTable providerTable = buildProviderTable(loginUrls);
		vPanel.add(providerTable);
		
		final Label emailPrompt = new Label();
		emailPrompt.setText("Or enter your email address to register manually:");
		emailPrompt.setStyleName("prompt");
		vPanel.add(emailPrompt);
		
		emailAddrField = new TextBox();
		emailAddrField.setWidth("80%");
		emailAddrField.setText("");
		emailAddrField.setFocus(true);
		emailAddrField.selectAll();
		vPanel.add(emailAddrField);
		
		manualRegisterBtn = new Button("Manual Register");
		manualRegisterBtn.addStyleName("manualRegisterButton");
		//
		// Create a handler for the registration button
		//
		class ManualRegisterHandler implements ClickHandler, KeyUpHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick(ClickEvent event) {
				registerEmailAddr();
			}

			/**
			 * Fired when the user types in the nameField.
			 */
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					registerEmailAddr();
				}
			}
			
			private void completeRegistration(String emailAddr) {
				String registerReturnUri = GWT.getHostPageBaseURL() + REGISTER_RETURN_URI;
				Window.open(registerReturnUri, "_self", ""); 
			}

			/**
			 * Send the name from the nameField to the server and wait for a response.
			 */
			private void registerEmailAddr() {
				Window.setTitle(Common.APPNAME);
				manualRegisterBtn.setEnabled(false);
				final String emailAddrProvided = emailAddrField.getText();
				serverResponseLabel.setText("");
				registerService.manualRegister(emailAddrProvided,
					new AsyncCallback<String>() {
						public void onFailure(Throwable caught) {
							// Show the RPC error message to the user
							dialogBox.setText("Email Address Registration - Failure");
							dialogBox.center();
							closeButton.setFocus(true);
							dialogBox.show();
						}

						public void onSuccess(String result) {
							if ("Verified".equalsIgnoreCase(result)) {
								String registerReturnUri = GWT.getHostPageBaseURL() + REGISTER_RETURN_URI;
								Window.open(registerReturnUri, "_self", ""); 
							}
							else {
								emailAddrField.setText("");
								/*
								Window.alert(result);
								*/
								dialogBox.center();
								dialogBox.setAnimationEnabled(true);
								dialogBox.setWidth("320px");
								dialogBox.setText(result);
								closeButton.setFocus(true);
								/*
								manualRegisterBtn.setEnabled(true);
								*/
							}
						}
					}
				);
			}
		} // ManualRegisterHandler
		manualRegisterBtn.addClickHandler(new ManualRegisterHandler());
		vPanel.add(manualRegisterBtn);
		RootPanel.get("browser").add(vPanel);
		
	} // buildUI
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		Window.setTitle(Common.APPNAME);
		closeButton = new Button("Close", new ClickListener() {
	        public void onClick(Widget sender) {
	        	dialogBox.hide();
	        	manualRegisterBtn.setEnabled(true);
	        }
	    });
		closeButton.setWidth("100%");
		dialogBox.setWidget(closeButton);
		//
		// setup registration process
		//
		String requestUri = GWT.getHostPageBaseURL() + REGISTER_URI;
		registerService.setup(requestUri, new AsyncCallback<List<String>>() {
			public void onFailure(Throwable caught) {
				// Show the RPC error message to the user
				logger.severe("Error message from server trying to get providers ...");
			}

			public void onSuccess(List<String> result) {
				logger.info("Got urls ... # of item(s): " + result.size());
				if (result != null && result.size() > 0) {
					buildUI(result);
				}
			}
		});
	}
}
