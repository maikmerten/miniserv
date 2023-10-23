package de.vitbund.miniserv.servlets;

import de.vitbund.miniserv.AuthChecker;
import de.vitbund.miniserv.Miniserv;
import de.vitbund.miniserv.responders.JsonParamResponder;
import de.vitbund.miniserv.responders.NoParamResponder;
import de.vitbund.miniserv.responders.Responder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 *
 * @author maik
 */
public class JsonServlet extends HttpServlet {

    private final Miniserv server;
    private final String method;
    private final Responder responder;
    private final AuthChecker authChecker;

    private class Wrapper {

        public final Object value;

        public Wrapper(Object value) {
            this.value = value;
        }
    }

    public JsonServlet(Miniserv server, String method, Responder responder, AuthChecker authChecker) {
        this.server = server;
        this.method = method;
        this.responder = responder;
        this.authChecker = authChecker;
    }
    
    private void checkMethod(String method) {
        server.debugOut("Servlet method " + this.method + "     request method: " + method);
        
        
        if(!this.method.equals(method)) {
            throw new RuntimeException("Wrong HTTP method. This endpoint expects " + this.method + " but was invoked with " + method);
        }
    }

    private void handle(String method, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        checkMethod(method);
        
        HttpSession session = request.getSession();
        boolean debugOut = server.isDebugOut();

        if (debugOut) {
            server.debugOut("  Request URI: " + request.getRequestURI() + " (" + request.getMethod() + ")");
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        if (this.authChecker == null || this.authChecker.isAuthenticated(session)) {

            String json = null;
            String contentType = request.getContentType();
            if (contentType != null && contentType.equals("application/json")) {
                json = new String(request.getInputStream().readAllBytes(), "utf-8");
            }

            if (debugOut) {
                server.debugOut(" Request JSON: " + json);
            }

            Object resObj = null;
            synchronized (server) {
                if (responder instanceof JsonParamResponder) {
                    JsonParamResponder resp = (JsonParamResponder) responder;
                    resObj = resp.respond(json, session);
                } else if (responder instanceof NoParamResponder) {
                    NoParamResponder resp = (NoParamResponder) responder;
                    resObj = resp.respond(session);
                }
            }

            if (resObj == null) {
                resObj = "null";
            }

            if (resObj instanceof String || resObj instanceof Number) {
                resObj = new Wrapper(resObj);
            }

            String resString = server.objectToJson(resObj);

            if (debugOut) {
                server.debugOut("Response JSON: " + resString + "\n");
            }

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println(resString);
        } else {
            if (debugOut) {
                server.debugOut("### Session not authenticated, forbidden! ###\n");
            }
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().println("{\"status\":\"Forbidden\"}");
        }

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handle("GET", request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handle("POST", request, response);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handle("PUT", request, response);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handle("DELETE", request, response);
    }

}
