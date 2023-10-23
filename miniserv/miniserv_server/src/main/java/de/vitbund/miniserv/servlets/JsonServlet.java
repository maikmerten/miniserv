package de.vitbund.miniserv.servlets;

import de.vitbund.miniserv.AuthChecker;
import de.vitbund.miniserv.Handler;
import de.vitbund.miniserv.Miniserv;
import de.vitbund.miniserv.responders.RequestResponder;
import de.vitbund.miniserv.responders.Responder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author maik
 */
public class JsonServlet extends HttpServlet {

    private final Miniserv server;
    private final Map<String, Handler> handlers;

    private class Wrapper {

        public final Object value;

        public Wrapper(Object value) {
            this.value = value;
        }
    }
    

    public JsonServlet(Miniserv server) {
        this.server = server;
        this.handlers = new HashMap<>();
    }
    
    public void addHandler(Handler handler) {
        this.handlers.put(handler.getMethod(), handler);
    }

    
    private void handle(String method, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI().trim();
        Handler handler = handlers.get(method);
        if(handler == null) {
            throw new RuntimeException("found no handler for " + uri + " with method " + method);
        }
        
        Responder responder = handler.getResponder();
        AuthChecker authChecker = handler.getAuthChecker();
       
        
        HttpSession session = request.getSession();
        boolean debugOut = server.isDebugOut();

        if (debugOut) {
            server.debugOut("  Request URI: " + request.getRequestURI() + " (" + request.getMethod() + ")");
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        if (authChecker == null || authChecker.isAuthenticated(session)) {

            Object resObj = null;
            synchronized (server) {
                if (responder instanceof RequestResponder) {
                    RequestResponder resp = (RequestResponder) responder;
                    resObj = resp.respond(request, session);
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
