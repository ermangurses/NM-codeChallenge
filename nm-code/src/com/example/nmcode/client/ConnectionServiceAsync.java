package com.example.nmcode.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>ConnectionService</code>.
 */
public interface ConnectionServiceAsync {
    void searchSynonyms(String input, AsyncCallback<List<String>> callback) throws IllegalArgumentException;;
    void callMaplabelsToSynonyms(AsyncCallback<Boolean> callback) throws IllegalArgumentException;;


}
