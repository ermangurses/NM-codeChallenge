package com.example.nmcode.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("connect")
public interface ConnectionService extends RemoteService {
    List<String>  searchSynonyms(String input) throws IllegalArgumentException;
    boolean callMaplabelsToSynonyms();

}
