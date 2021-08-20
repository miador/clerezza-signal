package org.apache.clerezza.signal.file;

import org.apache.clerezza.Graph;
import org.apache.clerezza.ImmutableGraph;
import org.apache.clerezza.representation.Parser;
import org.apache.clerezza.representation.Serializer;
import org.apache.clerezza.representation.SupportedFormat;
import org.apache.clerezza.representation.UnsupportedFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class FileOperations {

    private static final Logger logger = LoggerFactory.getLogger( FileOperations.class );

    /**
     * @param graph, takes mergedGraph and serialize it into file.
     */
    public static void saveGraph( Graph graph, String filename ) {
        Serializer serializer = Serializer.getInstance();
        try {
            FileOutputStream outputStream = new FileOutputStream( filename );
            serializer.serialize( outputStream, graph, SupportedFormat.TURTLE );
        } catch ( FileNotFoundException ex ) {
            logger.warn( ex.getMessage() );
        } catch ( UnsupportedFormatException ex ) {
            logger.warn( String.format( "%s is not supported by the used serializer", SupportedFormat.TURTLE ) );
        }
    }

    /**
     * @param inputStream from existing graph
     * @return null or {@link ImmutableGraph}
     */
    public static ImmutableGraph parseGraph( InputStream inputStream ) {
        // parse the graph using Jena parser into simple graph
        ImmutableGraph graph = null;
        Parser parser = Parser.getInstance();
        try {
            graph = parser.parse( inputStream, SupportedFormat.TURTLE );
        } catch ( UnsupportedFormatException ex ) {
            logger.warn( String.format( "%s is not supported by the used parser", SupportedFormat.TURTLE ) );
            logger.error( ex.getMessage() );
        }
        return graph;
    }

    /**
     *
     * @param filename, with the given filename, will retrieve data from it.
     * @return {@link InputStream} parsed from the file with the given name.
     */
    public static InputStream retrieveResourceFromFile( String filename ) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream( filename );
        } catch ( FileNotFoundException e ) {
            e.printStackTrace();
        }
        return inputStream;
    }

}
