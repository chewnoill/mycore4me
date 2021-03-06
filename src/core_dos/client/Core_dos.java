package core_dos.client;

import core_dos.shared.FieldVerifier;
import core_dos.shared.JsEvent;
import core_dos.shared.JsEventList;
import core_dos.shared.secret;


import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Core_dos implements EntryPoint,ValueChangeHandler {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";
	private String fragment = "";
	Label lbl = new Label();
	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final register_userAsync reg_service = GWT
			.create(register_user.class);
	
	private static final String CLIENT_ID = core_dos.shared.secret.CLIENT_ID;
	private static final String API_KEY = core_dos.shared.secret.API_KEY;
	private static final String APPLICATION_NAME = "Core Dos";
	
	final Button sendCoreButton = new Button("Submit");
	final Button sendGoogleButton = new Button("Send to Google");
	
	final TextBox username = new TextBox();
	final PasswordTextBox password = new PasswordTextBox();
	final CheckBox googleCheckbox = new CheckBox();
	
	private String access_token;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		//CALENDAR.		        
		//new GoogleApiRequestTransport(APPLICATION_NAME, API_KEY));

		
		
		
		googleCheckbox.setText("google");
		googleCheckbox.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				if(googleCheckbox.getValue()){
					Core_dos.getAuth();
				}
				
			}});
		
		
		VerticalPanel panel = new VerticalPanel();
	    panel.add(lbl);
	    RootPanel.get().add(panel);
	    
	    
		History.addValueChangeHandler(this);
		History.fireCurrentHistoryState();
		//String gdata = google_post.post();
		
		username.setText("username");
		password.setText("password");
		
		final Label errorLabel = new Label();

		// We can add style names to widgets
		sendCoreButton.addStyleName("sendButton");

		// Add the nameField and sendButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
		//RootPanel.get("nameFieldContainer").add(username);
		
		RootPanel.get("usernameContainer").add(username);
		RootPanel.get("passwordContainer").add(password);
		
		
		RootPanel.get("sendButtonContainer").add(sendCoreButton);
		RootPanel.get("errorLabelContainer").add(errorLabel);
		
		RootPanel.get("googleCheckContainer").add(googleCheckbox);
		

		// Focus the cursor on the name field when the app loads
		
		username.setFocus(true);
		username.selectAll();
		
		//password.selectAll();
		// Create the popup dialog box
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Remote Procedure Call");
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		final Label textToServerLabel = new Label();
		final HTML serverResponseLabel = new HTML();
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		
		dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);

		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				sendCoreButton.setEnabled(true);
				sendCoreButton.setFocus(true);
			}
		});

		// Create a handler for the sendButton and nameField
		class MyHandler implements ClickHandler, KeyUpHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick(ClickEvent event) {
				sendNameToServer();
			}

			/**
			 * Fired when the user types in the nameField.
			 */
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					sendNameToServer();
				}
			}

			/**
			 * Send the name from the nameField to the server and wait for a response.
			 */
			private void sendNameToServer() {
				// First, we validate the input.
				errorLabel.setText("");
				String user = username.getText();
				String pass = password.getText();
				
				String textToServer = user+":"+pass;
				// Then, we send the input to the server.
				sendCoreButton.setEnabled(false);
				textToServerLabel.setText(textToServer);
				serverResponseLabel.setText("");
				reg_service.register_coreauth(user,
						pass,
						new AsyncCallback<String>() {
							public void onFailure(Throwable caught) {
								// Show the RPC error message to the user
								dialogBox
										.setText("Remote Procedure Call - Failure");
								serverResponseLabel
										.addStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(SERVER_ERROR);
								dialogBox.center();
								closeButton.setFocus(true);
								sendCoreButton.setEnabled(true);
							}

							public void onSuccess(String result) {
								sendCoreButton.setEnabled(true);
								dialogBox.setText("Remote Procedure Call");
								serverResponseLabel
										.removeStyleName("serverResponseLabelError");
								if(isError(result)){
									serverResponseLabel.setHTML(getError_message(result));
									dialogBox.center();
									closeButton.setFocus(true);	
								} else {
									if(Core_dos.this.googleCheckbox.getValue()){
										RootPanel.get("googlePostContainer").add(sendGoogleButton);
									}
									Core_dos.this.renderCalUI(result);
								}
								
							}
						});
			}
		}
		sendGoogleButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				sendGoogleButton.setEnabled(false);
				reg_service.post_to_google(new AsyncCallback<String>() {
				
				@Override
				public void onFailure(Throwable caught) {
					serverResponseLabel.setHTML("Error!! "+caught.getMessage());
					dialogBox.center();
					closeButton.setFocus(true);	
					sendGoogleButton.setEnabled(true);
					
					
				}

				@Override
				public void onSuccess(String result) {
					serverResponseLabel.setHTML("Sent!");
					dialogBox.center();
					closeButton.setFocus(true);	
					sendGoogleButton.setEnabled(true);
					
				}});
				
			}});
		// Add a handler to send the name to the server
		MyHandler handler = new MyHandler();
		sendCoreButton.addClickHandler(handler);
		username.addKeyUpHandler(handler);
	}

	@Override
	public void onValueChange(ValueChangeEvent event) {
		this.fragment = (String) event.getValue();
		int stoken = this.fragment.indexOf("access_token=");
		
		String cal = "{\"events\":["+
			"{\"summary\": \"BAR MONTH END \",\"location\": \"Remote Locations\","+
				"\"start\": { \"date\": \"2012-09-01T09:00:00-0400\"},"+
				"\"end\": { \"date\": \"2012-09-02T01:00:00-0400\"}}"+
				"]}";
		//renderCalUI(cal);
		if(stoken==-1){
			//load google auth
			

		}else {
			
			stoken+="access_token=".length();
			int etoken = this.fragment.indexOf("&",stoken);
			this.access_token = this.fragment.substring(stoken, etoken);
			reg_service.register_googleauth(access_token,new AsyncCallback<String>() {
				
				@Override
				public void onFailure(Throwable caught) {
					Core_dos.this.googleCheckbox.setValue(false);
					
				}
				
				@Override
				public void onSuccess(String result) {
					System.out.println("result: "+result+"---");
					if(result.contains("400 OK")){
						Core_dos.this.googleCheckbox.setValue(false);
						RootPanel.get("googlePostContainer").remove(0);
					}
					else{
						Core_dos.this.googleCheckbox.setValue(true);
						
					}
					
				}});
		}
		
	}
	
	private void renderCalUI(String events){
		System.out.println("renderCalUi: "+events+"\n"+JsEventList.getLayout());
		render(JsEventList.getLayout(),events);
		
	}
	
	private static void getAuth(){
		String site = "https://accounts.google.com/o/oauth2/auth?";
		String type = "response_type=token&";
		String auth = "client_id="+secret.CLIENT_ID+"&";
		String scope = "scope=https://www.googleapis.com/auth/calendar&";
		String redirect = "redirect_uri="+secret.REDIRECT;
		System.out.println("load auth");
		loadPage(site+type+auth+scope+redirect);
	}
	private static native String loadPage(String url)/*-{
		parent.location.replace(url);
	}-*/;
	private static native boolean isError(String json)/*-{
		var obj = JSON.parse(json);
		if(obj.error_id&&obj.error_id!=""){
			return true;
		} else {
			return false;
		}
	}-*/;
	private static native String getError_message(String json)/*-{
		var obj = JSON.parse(json);
		return obj.error_message;
	}-*/;
	
	private native void render(String layout,String json)/*-{
		var content = JSON.parse(json);
		var table = parent.document.getElementById("main_table");
		var newevent = document.createElement("div");
		var output = parent.Mustache.render(layout,content);
		//return output
		newevent.innerHTML=output;
		table.appendChild(newevent);
	}-*/;
	
	
}
