package de.vitbund.miniserv.examples.time;

import de.vitbund.miniserv.Miniserv;

/**
 *
 * @author merten
 */
public class TimeServer {
    
    public static void main(String[] args) {
        Miniserv server = new Miniserv(8000, true);
        
        server.onGet("/api/time", (request, session) -> {
            return new Time();
        });
        
        server.start();
    }
    
}
