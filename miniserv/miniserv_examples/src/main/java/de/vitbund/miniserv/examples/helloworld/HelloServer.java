package de.vitbund.miniserv.examples.helloworld;

import de.vitbund.miniserv.Miniserv;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author merten
 */
public class HelloServer {
    
    public static void main(String[] args) {
        
        Miniserv server = new Miniserv();
        
        server.addJsonResponder("/api/helloworld", (String json, HttpSession session) -> {
            return "Hello World";
        });
        
        server.start();
    }
    
}
