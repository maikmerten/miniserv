package de.vitbund.miniserv;

import de.vitbund.miniserv.responders.Responder;
import java.util.Objects;

/**
 *
 * @author maik
 */
public class Handler {
    
    private final String uri;
    private final String method;
    private final Responder responder;
    private final AuthChecker authChecker;

    public Handler(String uri, String method, Responder responder, AuthChecker authChecker) {
        this.uri = uri;
        this.method = method;
        this.responder = responder;
        this.authChecker = authChecker;
    }
   

    /**
     * @return the uri
     */
    public String getUri() {
        return uri;
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
