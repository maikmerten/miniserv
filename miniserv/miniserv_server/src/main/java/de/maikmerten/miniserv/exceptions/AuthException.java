package de.maikmerten.miniserv.exceptions;

/**
 *
 * @author merten
 */
public class AuthException extends HttpException {
    
    public AuthException(String message) {
        super(403, message);
    }
    
    public AuthException() {
        this("Forbidden");
    }
    
}
