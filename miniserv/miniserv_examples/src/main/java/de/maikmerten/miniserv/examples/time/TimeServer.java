package de.maikmerten.miniserv.examples.time;

import de.maikmerten.miniserv.Miniserv;

/**
 *
 * @author merten
 */
public class TimeServer {
    
    public static void main(String[] args) {
        Miniserv server = new Miniserv(8000, true);
        
        server.onGet("/api/time", (request) -> {
            return new Time();
        });
        
        server.start();
    }
    
}
