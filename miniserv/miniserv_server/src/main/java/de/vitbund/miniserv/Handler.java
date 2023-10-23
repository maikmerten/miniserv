package de.vitbund.miniserv;

import de.vitbund.miniserv.responders.Responder;

/**
 *
 * @author maik
 */
public class Handler {
    
    private final String method;
    private final Responder responder;
    private final AuthChecker authChecker;

    public Handler(String method, Responder responder, AuthChecker authChecker) {
        this.method = method;
        this.responder = responder;
        this.authChecker = authChecker;
    }

    /**
     * @return the method
     */
    public String getMethod() {
        return method;
    }

    /**
     * @return the responder
     */
    public Responder getResponder() {
        return responder;
    }

    /**
     * @return the authChecker
     */
    public AuthChecker getAuthChecker() {
        return authChecker;
    }
    
}
