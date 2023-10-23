package de.vitbund.miniserv.responders;

import jakarta.servlet.http.HttpSession;

/**
 *
 * @author maik
 * @param <T> Type of response object
 */
public interface NoParamResponder<T> extends Responder {
    public T respond(HttpSession session);
}
