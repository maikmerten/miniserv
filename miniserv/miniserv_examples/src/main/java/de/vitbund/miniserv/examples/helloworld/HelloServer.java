package de.vitbund.miniserv.examples.helloworld;

import de.vitbund.miniserv.Miniserv;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author merten
 */
public class HelloServer {
    
    public static void main(String[] args) {
        
        Miniserv server = new Miniserv();
        
        server.onGet("/api/helloworld", (HttpServletRequest request, HttpSession session) -> {
            return "Hello World";
        });
        
        server.start();
    }
    
}
