package org.apache.clerezza.signal.file;

import org.apache.clerezza.BlankNode;
import org.apache.clerezza.Graph;
import org.apache.clerezza.implementation.TripleImpl;
import org.apache.clerezza.implementation.in_memory.SimpleGraph;
import org.apache.clerezza.implementation.literal.PlainLiteralImpl;
import org.apache.clerezza.signal.model.SIGNAL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.apache.clerezza.signal.file.FileOperations.retrieveResourceFromFile;

public class FileOperationsTest {

    public static String filename = System.getProperty( "user.dir" ) + "/signal.ttl";

    @Test
    public void shouldSaveGraphIntoFile() {

        createFileIfNotExist( filename );

        Graph testGraph = new SimpleGraph();
        testGraph.add( new TripleImpl( new BlankNode(), SIGNAL.username, new PlainLiteralImpl( "testUser" ) ) );
        FileOperations.saveGraph( testGraph, filename );
        Graph savedGraph = FileOperations.parseGraph( retrieveResourceFromFile( filename ) );

        Assertions.assertTrue( savedGraph.filter( null, null, new PlainLiteralImpl( "testUser" ) ).hasNext() );
    }

    /**
     * @param filename, with the given filename a new file will be created if file doesn't exist.
     *                  Will do nothing if file already exists.
     */
    public static void createFileIfNotExist( String filename ) {
        try {
            File file = new File( filename );
            if ( file.createNewFile() ) {
                System.out.println( "File created at: " + "\"" + filename + "\"" );
            } else {
                System.out.println( "Existing file at the directory: " + "\"" + filename + "\"" + " will be overwritten by the serializer" );
            }
        } catch ( Exception e ) {
            System.out.println( e.getLocalizedMessage() );
        }
    }

}
