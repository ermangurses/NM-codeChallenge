package com.example.nmcode.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ConnectionServiceAsync {
    void searchSynonyms(String input, AsyncCallback<String> callback) throws IllegalArgumentException;
}
