package de.maikmerten.miniserv.servlets;

import de.maikmerten.miniserv.AuthChecker;
import de.maikmerten.miniserv.Handler;
import de.maikmerten.miniserv.Miniserv;
import de.maikmerten.miniserv.Response;
import de.maikmerten.miniserv.exceptions.AuthException;
import de.maikmerten.miniserv.exceptions.HttpException;
import de.maikmerten.miniserv.responders.RequestResponder;
import de.maikmerten.miniserv.responders.Responder;
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
        if (handler == null) {
            throw new RuntimeException("found no handler for " + uri + " with method " + method);
        }

        Responder responder = handler.getResponder();
        AuthChecker authChecker = handler.getAuthChecker();

        HttpSession session = request.getSession();
        boolean debugOut = server.isDebugOut();

        if (debugOut) {
            server.debugOut("  Request URI: " + request.getRequestURI() + " (" + request.getMethod() + ")");
        }

        String encoding = "utf-8";
        String contentType = "application/json";
        String fileName = null;
        Object resObj = "";
        int httpCode = HttpServletResponse.SC_OK;

        try {
            synchronized (server) {
                if ((authChecker != null) && !authChecker.isAuthenticated(session)) {
                    server.debugOut("### Session not authenticated, forbidden! ###\n");
                    throw new AuthException();
                }

                if (responder instanceof RequestResponder) {
                    try {
                        RequestResponder resp = (RequestResponder) responder;
                        resObj = resp.respond(request);
                    } catch (HttpException e) {
                        server.debugOut("### HttpException message: " + e.getMessage());
                        throw e;
                    } catch (Exception e) {
                        server.debugOut("### Exception message: " + e.getMessage());
                        throw new HttpException();
                    }
                }
            }

        } catch (HttpException e) {
            httpCode = e.getCode();
            Map<String, Object> exceptionMap = new HashMap<>();
            exceptionMap.put("code", e.getCode());
            exceptionMap.put("message", e.getMessage());
            resObj = exceptionMap;
        }

        if (resObj == null) {
            resObj = "null";
        }

        if (resObj instanceof Response) {
            Response r = (Response) resObj;
            resObj = r.getResult();
            httpCode = r.getCode();
            contentType = r.getContentType();
            fileName = r.getFileName();
        }

        if (resObj instanceof String || resObj instanceof Number) {
            resObj = new Wrapper(resObj);
        }

        response.setStatus(httpCode);
        if (contentType.equals("application/json")) {
            String resString = server.objectToJson(resObj);
            if (debugOut) {
                server.debugOut("Response JSON: " + resString + "\n");
            }
            response.setContentType(contentType);
            response.setCharacterEncoding(encoding);
            response.getWriter().println(resString);
        } else {
            byte[] resData = (byte[]) resObj;
            response.setContentType(contentType);
            if(fileName != null) {
                fileName = fileName.replaceAll("\n", "");
                response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
            }
            response.getOutputStream().write(resData);
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
