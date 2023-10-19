package de.vitbund.miniserv;

import jakarta.servlet.http.HttpSession;

/**
 *
 * @author maik
 * @param <T> Type of response object
 */
public interface JsonResponder<T> {
    
    public T respond(String json, HttpSession session);
    
}
