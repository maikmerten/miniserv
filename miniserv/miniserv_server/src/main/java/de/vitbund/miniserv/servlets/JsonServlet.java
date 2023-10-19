package de.vitbund.miniserv.servlets;

import de.vitbund.miniserv.AuthChecker;
import de.vitbund.miniserv.JsonResponder;
import de.vitbund.miniserv.Miniserv;
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
    private final JsonResponder responder;
    private final AuthChecker authChecker;

    public JsonServlet(Miniserv server, JsonResponder responder, AuthChecker authChecker) {
        this.server = server;
        this.responder = responder;
        this.authChecker = authChecker;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

            Object resObj;
            synchronized (server) {
                resObj = responder.respond(json, session);
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

}
