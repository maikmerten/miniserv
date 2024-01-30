package de.maikmerten.miniserv.exceptions;

/**
 *
 * @author merten
 */
public class HttpException extends RuntimeException {
    
    private int code;
    private String message;
    
    public HttpException(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public HttpException(String message) {
        this(500, message);
    }
    
    public HttpException() {
        this("Internal Server Error");
    }
    
    public int getCode() {
        return code;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
    
}
