package de.vitbund.miniserv.examples.time;

import de.vitbund.miniserv.Miniserv;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author merten
 */
public class TimeServer {
    
    public static void main(String[] args) {
        Miniserv server = new Miniserv(8000, true);
        
        server.onGet("/api/time", (HttpSession session) -> {
            return new Time();
        });
        
        server.start();
    }
    
}
