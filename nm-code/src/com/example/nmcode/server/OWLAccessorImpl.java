package com.example.nmcode.server;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

/**
 * This class includes implemented methods being used to retrieve meaning of and
 * relationships among terms in PATO using OWL API. Keywords, synonyms, and
 * parents of a term could be retrieved by giving the term.
 * 
 * TAO: http://berkeleybop.org/ontologies/tao.owl
 * PATO: http://purl.obolibrary.org/obo/pato.owl
 * 
 * @author Zilong Chang, Hong Cui, Erman Gurses
 * Modified by Erman Gurses
 */
public class OWLAccessorImpl implements OWLAccessor {
    
    private OWLOntologyManager manager;
    private OWLDataFactory df;
    private OWLOntology ont;
    private Set<OWLAnnotation> set;
    private HashMap<String, List<String>> hashMap = new HashMap<String, List<String>>();

    /**
    * Constructor that takes ontology URL
    */ 
   public OWLAccessorImpl(String ontoURL) {
       manager = OWLManager.createOWLOntologyManager();
       df = manager.getOWLDataFactory();
       IRI iri = IRI.create(ontoURL);
       try {
           ont = manager.loadOntologyFromOntologyDocument(iri);
       
       } catch (OWLOntologyCreationException e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
       }
   }

   /**
    * Constructor that takes file name
    */ 
    public OWLAccessorImpl(File file) {
        
        manager = OWLManager.createOWLOntologyManager();
        df = manager.getOWLDataFactory();

        try {
            ont = manager.loadOntologyFromOntologyDocument(file);
        } catch (OWLOntologyCreationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * Place each label (key) into hash map. 
     * Each label has a list (value) that contains the exact synonyms
     */  
    @SuppressWarnings("deprecation")
    public boolean mapLabelsToSynonyms() {
        
       Iterator<OWLAnnotation> iterKey; 
        String key, value;
     
        for (OWLClass cls : ont.getClassesInSignature()) {
            
            // Get the annotations on the class that use the label property
            set = getLabels(cls);
            iterKey = set.iterator();

            if (iterKey.hasNext()) {
                key  = getRefinedOutput(iterKey.next().toString());
                Set<OWLAnnotation> setExact  = getExactSynonyms(getClassByLabel(key));
                Set<OWLAnnotation> setRelated = getRelatedSynonyms(getClassByLabel(key));
                Set<OWLAnnotation> setBroad   = getBroadSynonyms(getClassByLabel(key));

                Set<OWLAnnotation> setAll = new TreeSet<OWLAnnotation>();
                setAll.addAll(setExact);
                setAll.addAll(setRelated);
                setAll.addAll(setBroad);
                
                Iterator<OWLAnnotation> iterValue = setAll.iterator();
                List<String> synyoyms = new ArrayList<String>();    

                while(iterValue.hasNext()) {
                    value = getRefinedOutput(iterValue.next().toString()); 
                     synyoyms.add(value);      
                }
                hashMap.put(key, synyoyms);
            } // if   
        }// for
        
        return !hashMap.isEmpty() ? true : false;
    }
 
    /**
     * Return exact synonyms given a term
     */  
    public List<String> getSynonymsfromMap(String token) {
        System.out.println(hashMap.get(token).toString());   
     return hashMap.get(token); 
    }
 
    /**
     * Return a set of labels given a OWLClass
     */  
    public Set<OWLAnnotation> getLabels(OWLClass cls) {
        return EntitySearcher.getAnnotations(cls,ont,df.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI())).collect(Collectors.toSet()); 
    }

    /**
     * Return label given a OWLClass
     */  
    @Override
    public String getLabel(OWLClass cls) {
        
        if (this.getLabels(cls).isEmpty()) {
            return "";
        }else{
            OWLAnnotation label = (OWLAnnotation) this.getLabels(cls).toArray()[0];        
            return this.getRefinedOutput(label.getValue().toString());
        }
    }

    /****************************************************************************
     * Remove the non-readable or non-meaningful characters in the retrieval
     * from OWL API, and return the refined output.
     */
    public String getRefinedOutput(String origin) {
       
        if (origin.startsWith("Annotation")) {
            origin = origin.replaceFirst("^Annotation.*>\\s+", "")
           .replaceFirst("^Annotation.*label", "")
           .replaceFirst("\\)\\s*$", "").trim();
        }

        /*
         * Remove the ^^xsd:string tail from the returned annotation value
         */
         return origin.replaceAll("\\^\\^xsd:string", "").replaceAll("\"", "")
        .replaceAll("\\.", "");
    }
     
    /**
     * Return the exact synonyms of a term represented by an OWLClass object.
     */
    public Set<OWLAnnotation> getExactSynonyms(OWLClass c) {
         return EntitySearcher.getAnnotations(c,ont,df.getOWLAnnotationProperty(IRI
         .create("http://www.geneontology.org/formats/oboInOwl#hasExactSynonym"))).collect(Collectors.toSet()); 
    }
    
    /**
     * Return the related synonyms of a term represented by an OWLClass object.
     */
    public Set<OWLAnnotation> getRelatedSynonyms(OWLClass c) {
         return EntitySearcher.getAnnotations(c,ont,df.getOWLAnnotationProperty(IRI
         .create("http://www.geneontology.org/formats/oboInOwl#hasRelatedSynonym"))).collect(Collectors.toSet()); 
    }
    
    /**
     * Return the broad synonyms of a term represented by an OWLClass object.
     */
    public Set<OWLAnnotation> getBroadSynonyms(OWLClass c) {
         return EntitySearcher.getAnnotations(c,ont,df.getOWLAnnotationProperty(IRI
         .create("http://www.geneontology.org/formats/oboInOwl#hasBroadSynonym"))).collect(Collectors.toSet()); 
    } 
    
    /**
     * Return all classes in the Ontology
     */
    @SuppressWarnings("deprecation")
    @Override
    public Set<OWLClass> getAllClasses() {
        // TODO Auto-generated method stub
        return ont.getClassesInSignature();
    }
    
    
    /**
    * Return OWLClass given a label
    */ 
    @Override
    public OWLClass getClassByLabel(String label) {
         
        for (OWLClass cls : this.getAllClasses()) {
            if (this.getLabel(cls).trim().toLowerCase()
                     .equals(label.trim().toLowerCase())) {
                 return cls;
            }
        }
        return null;
    }
    
    /**
     * 
     */
    public boolean isClassLabel(String label) {
        
        return hashMap.get(label) != null ? true : false;        
    }
    
    /**
     * 
     */
    public List<OWLClass> getAncestralPath(OWLClass cls){
        
        return null;
    }
    
    
    @Override
    public List<OWLClass> retrieveConcept(String con) {
        
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Set<String> getKeywords(OWLClass c) {
        
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public List<String> getSynonymLabels(OWLClass c) {
        
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public List<String> getParentsLabels(OWLClass c) {
        
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Set<String> getAllOffSprings(OWLClass c) {
     // TODO Auto-generated method stub
      return null;
     }
    
    @Override
    public String getID(OWLClass c) {
        // TODO Auto-generated method stub
        return null;
    }
}
