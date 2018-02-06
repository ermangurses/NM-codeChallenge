package com.example.nmcode.client;

import java.util.List;
import java.util.ListIterator;

import com.example.nmcode.shared.FieldVerifier;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Nm_code implements EntryPoint {
    
    VerticalPanel verticalPanel;
    HorizontalPanel horizontalPanel;
    RichTextArea areaLeft;
    RichTextArea areaRight;
    RichTextArea.Formatter colorFormatter;
    private boolean initFlag = true;  

    
    /**
     * The message displayed to the user when the server cannot be reached or
     * returns an error.
     */
    private static final String SERVER_ERROR = "An error occurred while "
            + "attempting to contact the server. Please check your network " + "connection and try again.";

    /**
     * Create a remote service proxy to talk to the server-side Greeting service.
     */
    private final ConnectionServiceAsync connectionService = GWT.create(ConnectionService.class);

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        this.wordEditorInterface();
        if(initFlag) {
            this.initServerSide();
            initFlag = false;
        }
        
        final Button sendButton = new Button("Send");
        final TextBox nameField = new TextBox();
        nameField.setText("elliptic");
        final Label errorLabel = new Label();

        // We can add style names to widgets
        sendButton.addStyleName("sendButton");

        // Add the nameField and sendButton to the RootPanel
        // Use RootPanel.get() to get the entire body element
        RootPanel.get("nameFieldContainer").add(nameField);
        RootPanel.get("sendButtonContainer").add(sendButton);
        RootPanel.get("errorLabelContainer").add(errorLabel);

        // Focus the cursor on the name field when the app loads
        nameField.setFocus(true);
        nameField.selectAll();

        
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
        dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
        dialogVPanel.add(textToServerLabel);
        dialogVPanel.add(serverResponseLabel);
        dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
        dialogVPanel.add(closeButton);
        dialogBox.setWidget(dialogVPanel);

        // Add a handler to close the DialogBox
        closeButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                dialogBox.hide();
                sendButton.setEnabled(true);
                sendButton.setFocus(true);
            }
        });

        // Create a handler for the sendButton and nameField
        class MyHandler implements ClickHandler, KeyUpHandler {
            /**
             * Fired when the user clicks on the sendButton.
             */
            public void onClick(ClickEvent event) {
                sendWordToServer();
            }

            /**
             * Fired when the user types in the nameField.
             */
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    sendWordToServer();
                }
            }

            /**
             * Send the name from the nameField to the server and wait for a response.
             */
            private void sendWordToServer() {
                // First, we validate the input.
                errorLabel.setText("");
                String textToServer = nameField.getText();
                if (!FieldVerifier.isValidName(textToServer)) {
                    errorLabel.setText("Please enter at least four characters");
                    return;
                }

                // Then, we send the input to the server.
                sendButton.setEnabled(false);
                textToServerLabel.setText(textToServer);
                serverResponseLabel.setText("");
                connectionService.searchSynonyms(textToServer, new AsyncCallback<List<String>>() {
                  
                    public void onFailure(Throwable caught) {
                        // Show the RPC error message to the user
                        dialogBox.setText("Remote Procedure Call - Failure");
                        serverResponseLabel.addStyleName("serverResponseLabelError");
                        serverResponseLabel.setHTML(SERVER_ERROR);
                        dialogBox.center();
                        closeButton.setFocus(true);
                    }

                    public void onSuccess(List<String> result) {
                        //dialogBox.setText("Remote Procedure Call");
                        ListIterator<String> itrList = null;
                        itrList = result.listIterator(); 
                        areaLeft.setVisible(true);
                
                        RichTextArea.Formatter formatter2 = areaLeft.getFormatter();
       
                        areaLeft.setHTML("");
                        formatter2.insertHTML("Synonyms: <br />");
                        while(itrList.hasNext()) {
                            formatter2.insertHTML("<br>&emsp;&emsp;--->"+"   "+itrList.next()+"<br />\n");
                        } 
                        dialogBox.center();
                        closeButton.setFocus(false);  
                    }
                });
            }
        }

        // Add a handler to send the name to the server
        MyHandler handler = new MyHandler();
        sendButton.addClickHandler(handler);
        nameField.addKeyUpHandler(handler);
    }
    
    /**
     * The skeleton interface for the word processor
     */ 
    public Widget wordEditorInterface() {
        
         verticalPanel = new VerticalPanel();
         verticalPanel.setWidth("100%");
         verticalPanel.setHeight("300%");
         
         areaLeft = new RichTextArea();
         areaRight = new RichTextArea();
         areaLeft.setVisible(false);
         areaRight.setVisible(false);
            
         RootPanel.get().add(areaLeft); 
         
       return verticalPanel;
    }
    /**
     * The RPC call that calls loadMap method
     * loadMap calls mapLabelsToExactSynonyms method
     * 
     * callMappingLabelsToExactSynonyms:
     * Place each label(key) into hash map. 
     * Each label has a list (value) that contains the exact synonyms
     */ 
    private void initServerSide() {
        connectionService.callMaplabelsToSynonyms(new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable caught) {
                // TODO Auto-generated method stub 
            }
            public void onSuccess(Boolean result) {
                areaLeft.setVisible(true);
                RichTextArea.Formatter formatter2 = areaLeft.getFormatter();
                
                areaLeft.setHTML("");
                formatter2.insertHTML("Success");
             }
         });
     }
    
}
