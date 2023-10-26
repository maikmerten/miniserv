package de.vitbund.miniserv;

/**
 *
 * @author merten
 */
public class Response {

    private int code;
    private Object result;

    public Response(int code, Object result) {
        this.code = code;
        this.result = result;
    }
    
    public Response(Object result) {
        this(200, result);
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

}
