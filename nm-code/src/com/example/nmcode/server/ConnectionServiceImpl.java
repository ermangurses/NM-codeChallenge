package com.example.nmcode.server;

import java.util.Arrays;
import java.util.List;

import com.example.nmcode.client.ConnectionService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;


/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ConnectionServiceImpl extends RemoteServiceServlet implements ConnectionService {

    OWLAccessorImpl accessor = new OWLAccessorImpl("https://raw.githubusercontent.com/pato-ontology/pato/master/pato.owl");

    
    /**
     * The server-side implementation to search synonyms given input
     * 
     * It gets data from pre-loaded hash-map
     * 
     */    
    @Override
    public List<String> searchSynonyms(String input) throws IllegalArgumentException {
        if(accessor.isClassLabel(input)) {
            return accessor.getSynonymsfromMap(input);             
        } else {
            List<String> list =  Arrays.asList("There is no synonym"); 
            return list;       
        }   
    }
    /**
     * The server-side implementation to map labels to synonyms using hash map
     * 
     */
    public boolean callMaplabelsToSynonyms() {
        if(accessor.mapLabelsToSynonyms()){
            System.out.println("Loading the map is successful!");
            return true;
        } else {
            System.out.println("Loading the map is failed!"); 
            return false;
        }      
    }
}
