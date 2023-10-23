package de.vitbund.miniserv.responders;

import jakarta.servlet.http.HttpSession;

/**
 *
 * @author maik
 * @param <T> Type of response object
 */
public interface JsonParamResponder<T> extends Responder {
    
    public T respond(String json, HttpSession session);
    
}
