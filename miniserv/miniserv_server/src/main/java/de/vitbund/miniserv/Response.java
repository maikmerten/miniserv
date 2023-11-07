package de.vitbund.miniserv;

/**
 *
 * @author merten
 */
public class Response {

    private int code;
    private Object result;
    private String contentType;

    public Response(int code, Object result, String contentType) {
        this.code = code;
        this.result = result;
        this.contentType = contentType;
        
        if(!(contentType.equals("application/json"))) {
            if(!(result instanceof byte[])) {
                throw new IllegalArgumentException("result for content types other than application/json must be a byte array");
            }
        }
    }
    
    public Response(int code, Object result) {
        this(code, result, "application/json");
    }
    
    public Response(Object result) {
        this(200, result, "application/json");
    }

    /**
     * @return the HTTP code
     */
    public int getCode() {
        return code;
    }

    /**
     * @param code the HTTP code to set
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * @return the result object
     */
    public Object getResult() {
        return result;
    }

    /**
     * @param result the result object to set
     */
    public void setResult(Object result) {
        this.result = result;
    }

    /**
     * @return the content type
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @param contentType the content type to set
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

}
