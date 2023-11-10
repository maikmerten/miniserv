package de.maikmerten.miniserv.examples.helloworld;

import de.maikmerten.miniserv.Miniserv;

/**
 *
 * @author merten
 */
public class HelloServer {
    
    public static void main(String[] args) {
        
        Miniserv server = new Miniserv();
        
        server.onGet("/api/helloworld", (request) -> {
            return "Hello World";
        });
        
        server.start();
    }
    
}
