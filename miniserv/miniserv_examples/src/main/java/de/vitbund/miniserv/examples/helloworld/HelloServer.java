package de.vitbund.miniserv.examples.helloworld;

import de.vitbund.miniserv.Miniserv;

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
