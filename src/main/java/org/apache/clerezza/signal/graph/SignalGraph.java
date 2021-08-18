package org.apache.clerezza.signal.graph;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.clerezza.*;
import org.apache.clerezza.implementation.TripleImpl;
import org.apache.clerezza.implementation.in_memory.SimpleGraph;
import org.apache.clerezza.implementation.literal.PlainLiteralImpl;
import org.apache.clerezza.ontologies.FOAF;
import org.apache.clerezza.ontologies.RDF;
import org.apache.clerezza.signal.file.FileOperations;
import org.apache.clerezza.signal.model.SIGNAL;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import static org.apache.clerezza.signal.file.FileOperations.retrieveResourceFromFile;

public class SignalGraph {

    /**
     * @param json,     an {@link ObjectNode} with the Person, Conversation and Message information.
     * @param filename, requested file
     * @throws IOException, if there's no file named with filename.
     *                      <p>
     *                      Check if there’s data ready to read. If not, save incoming graph.
     *                      If there’s data to read, that means there are existing messages.
     *                      Iterate over the nodes to check whether conversation is existing or not. If the conversation exists, then overwrite the graph.
     */
    public static void buildGraph( ObjectNode json, String filename ) throws IOException {
        InputStream inputStream = retrieveResourceFromFile( filename );
        Graph mergedGraph = new SimpleGraph();
        BlankNodeOrIRI pNode = null;
        BlankNodeOrIRI cNode = null;

        PlainLiteralImpl username = new PlainLiteralImpl( json.get( "Person" ).get( "username" ).textValue() );
        PlainLiteralImpl conversationName = new PlainLiteralImpl( json.get( "Conversation" ).get( "conversationName" ).textValue() );

        if ( inputStream.available() > 1 ) {
            //for existingGraph
            ImmutableGraph existingGraph = FileOperations.parseGraph( inputStream ); //file contains existing messages.

            if ( existingGraph != null ) {
                mergedGraph.addAll( existingGraph );
            }
            Iterator<Triple> iterator = mergedGraph.filter( null, RDF.type, FOAF.Person );
            Triple triple;
            while ( iterator.hasNext() ) {
                triple = iterator.next();
                Iterator<Triple> pIterator = mergedGraph.filter( triple.getSubject(), SIGNAL.username, username );
                if ( pIterator.hasNext() ) {
                    pNode = triple.getSubject();
                    break;
                }
            }
            iterator = mergedGraph.filter( null, RDF.type, SIGNAL.Conversation );
            while ( iterator.hasNext() ) {
                triple = iterator.next();
                Iterator<Triple> cIterator = mergedGraph.filter( triple.getSubject(), SIGNAL.conversationName, conversationName );
                if ( cIterator.hasNext() ) {
                    cNode = triple.getSubject();
                    break;
                }
            }
        }

        if ( pNode == null ) {
            pNode = new BlankNode();
            mergedGraph.add( new TripleImpl( pNode, RDF.type, FOAF.Person ) );
            mergedGraph.add( new TripleImpl( pNode, SIGNAL.username, username ) );
        }
        if ( cNode == null ) {
            cNode = new BlankNode();
            mergedGraph.add( new TripleImpl( cNode, RDF.type, SIGNAL.Conversation ) );
            mergedGraph.add( new TripleImpl( cNode, SIGNAL.conversationName, conversationName ) );

        }

        //each time a new messageNode must be created because each time this request is called, there will be new message.
        BlankNode messageNode = new BlankNode();
        mergedGraph.add( new TripleImpl( messageNode, RDF.type, SIGNAL.Message ) );
        mergedGraph.add( new TripleImpl( messageNode, SIGNAL.text, new PlainLiteralImpl( json.get( "Message" ).get( "text" ).textValue() ) ) );
        mergedGraph.add( new TripleImpl( messageNode, SIGNAL.timeStamp, new PlainLiteralImpl( json.get( "Message" ).get( "timestamp" ).textValue() ) ) );
        mergedGraph.add( new TripleImpl( pNode, SIGNAL.post, messageNode ) );
        mergedGraph.add( new TripleImpl( cNode, SIGNAL.consistOf, messageNode ) );

        FileOperations.saveGraph( mergedGraph, filename );
    }

}
