package com.example.nmcode.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface ConnectionService extends RemoteService {
    String searchSynonyms(String name) throws IllegalArgumentException;
}
