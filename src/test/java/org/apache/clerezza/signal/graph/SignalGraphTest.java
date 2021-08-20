package org.apache.clerezza.signal.graph;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.apache.clerezza.signal.file.FileOperationsTest.createFileIfNotExist;
import static org.apache.clerezza.signal.file.FileOperationsTest.filename;

public class SignalGraphTest {

    @Test
    public void shouldBuildGraph() {
        createFileIfNotExist( filename );
        try {
            SignalGraph.buildGraph( new ObjectMapper().readValue( "{\"Person\":{\"username\":\"testUser\"},\"Message\":{\"text\":\"Test Message\",\"timestamp\":\"1629474568\"},\"Conversation\":{\"conversationName\":\"TestConversation\"}}", ObjectNode.class ), filename );
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        try ( BufferedReader br = new BufferedReader( new FileReader( filename ) ) ) {
            String line;
            while ( ( line = br.readLine() ) != null ) {
                sb.append( line ).append( "\n" );
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        String expected = sb.toString();
        Assertions.assertTrue( expected.contains( "testUser" ) );
        Assertions.assertTrue( expected.contains( "1629474568" ) );
        Assertions.assertTrue( expected.contains( "TestConversation" ) );

    }

}
