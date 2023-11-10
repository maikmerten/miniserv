package de.maikmerten.miniserv.examples.time;

import java.util.Date;

/**
 *
 * @author merten
 */
public class Time {

    public final long milliseconds;
    public final String timestring;
    
    public Time() {
        Date now = new Date();
        milliseconds = now.getTime();
        timestring = now.toString();
    }
    
}
