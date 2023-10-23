package de.vitbund.miniserv.responders;

import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * @author maik
 * @param <T> Type of response objectType of response object
 */
public interface RequestResponder<T> extends Responder {
    public T respond(HttpServletRequest request);
}
