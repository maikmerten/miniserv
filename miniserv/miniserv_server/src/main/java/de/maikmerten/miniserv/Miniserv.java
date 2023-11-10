package de.maikmerten.miniserv;

import com.google.gson.Gson;
import de.maikmerten.miniserv.responders.RequestResponder;
import de.maikmerten.miniserv.responders.Responder;
import de.maikmerten.miniserv.servlets.JsonServlet;
import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jetty.ee10.servlet.DefaultServlet;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.ee10.servlet.SessionHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.session.DefaultSessionCache;
import org.eclipse.jetty.session.FileSessionDataStore;
import org.eclipse.jetty.session.SessionCache;
import org.eclipse.jetty.util.resource.ResourceFactory;

public class Miniserv {

    private int port = 8000;
    private Server server;
    private String webDir;
    private ServletContextHandler context;
    private final Gson gson;
    private boolean debugOut;
    private final Map<String, JsonServlet> servlets;

    public Miniserv(int port, boolean debugOut) {
        this.webDir = System.getProperty("user.dir") + File.separator + "web";
        this.port = port;
        this.gson = new Gson();
        this.debugOut = debugOut;
        this.servlets = new HashMap<>();

        try {
            System.setProperty("org.eclipse.jetty.LEVEL", "INFO");

            this.server = new Server();
            ServerConnector connector = new ServerConnector(server);
            connector.setPort(this.port);
            server.addConnector(connector);

            // Setup the basic application "context" for this application at "/"
            // This is also known as the handler tree (in jetty speak)
            context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");
            server.setHandler(context);

            context.setSessionHandler(createSessionHandler());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Miniserv() {
        this(8000, true);
    }

    private SessionHandler createSessionHandler() {
        FileSessionDataStore fileSessionDataStore = new FileSessionDataStore();
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        File storeDir = new File(tmpDir, "miniserv-session-store");
        storeDir.mkdir();
        fileSessionDataStore.setStoreDir(storeDir);

        SessionHandler sessionHandler = new SessionHandler();
        SessionCache sessionCache = new DefaultSessionCache(sessionHandler);
        sessionCache.setSessionDataStore(fileSessionDataStore);
        sessionHandler.setSessionCache(sessionCache);
        sessionHandler.setHttpOnly(true);
        return sessionHandler;
    }

    public void start() {

        try (ResourceFactory.Closeable resourceFactory = ResourceFactory.closeable()) {
            context.setBaseResource(resourceFactory.newResource(webDir));
        }

        try {
            // last in chain: Default to serving static assets from current workdir
            ServletHolder holderStatic = new ServletHolder("default", DefaultServlet.class);
            holderStatic.setInitParameter("dirAllowed", "true");
            context.addServlet(holderStatic, "/");

            // gzip compression
            GzipHandler gzipHandler = new GzipHandler();
            gzipHandler.setIncludedMethods("PUT", "POST", "GET");
            gzipHandler.setHandler(server.getHandler());
            server.setHandler(gzipHandler);
            
            server.start();
            //server.dump(System.err);
            server.join();
        } catch (Exception t) {
            throw new RuntimeException(t);
        }
    }

    public void addResponder(String pathSpec, String method, Responder responder, AuthChecker authChecker) {
        JsonServlet servlet = servlets.get(pathSpec);
        if (servlet == null) {
            servlet = new JsonServlet(this);
            servlets.put(pathSpec, servlet);
            ServletHolder holder = new ServletHolder(servlet);
            context.addServlet(holder, pathSpec);
        }
        Handler handler = new Handler(method, responder, authChecker);
        servlet.addHandler(handler);
    }

    public void onGet(String pathSpec, RequestResponder responder, AuthChecker authChecker) {
        addResponder(pathSpec, "GET", responder, authChecker);
    }

    public void onGet(String pathSpec, RequestResponder responder) {
        onGet(pathSpec, responder, null);
    }

    public void onPost(String pathSpec, RequestResponder responder, AuthChecker authChecker) {
        addResponder(pathSpec, "POST", responder, authChecker);
    }

    public void onPost(String pathSpec, RequestResponder responder) {
        onPost(pathSpec, responder, null);
    }

    public void onPut(String pathSpec, RequestResponder responder, AuthChecker authChecker) {
        addResponder(pathSpec, "PUT", responder, authChecker);
    }

    public void onPut(String pathSpec, RequestResponder responder) {
        onPost(pathSpec, responder, null);
    }

    public void onDelete(String pathSpec, RequestResponder responder, AuthChecker authChecker) {
        addResponder(pathSpec, "DELETE", responder, authChecker);
    }

    public void onDelete(String pathSpec, RequestResponder responder) {
        onDelete(pathSpec, responder, null);
    }

    public String objectToJson(Object o) {
        synchronized (gson) {
            return gson.toJson(o);
        }
    }

    public <T> T jsonToObject(String json, Class<T> classOfT) {
        synchronized (gson) {
            return gson.fromJson(json, classOfT);
        }
    }

    public <T> T jsonToObject(HttpServletRequest request, Class<T> classOfT) {
        String json = null;
        String contentType = request.getContentType();
        if (contentType != null && contentType.equals("application/json")) {
            try {
                json = new String(request.getInputStream().readAllBytes(), "utf-8");
                if(debugOut) {
                    debugOut(" Request JSON: " + json);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return jsonToObject(json, classOfT);
    }

    public void setDebugOut(boolean b) {
        this.debugOut = b;
    }

    public boolean isDebugOut() {
        return this.debugOut;
    }

    public void debugOut(String s) {
        if (this.debugOut) {
            System.out.println(s);
        }
    }

    public void setWebDir(String webDir) {
        this.webDir = webDir;
    }

    public static void main(String[] args) throws Exception {
        int port = 8000;
        Miniserv server = new Miniserv(port, true);
        String webDir = System.getProperty("user.dir");
        server.setWebDir(webDir);
        server.debugOut("Serving " + webDir + " on port " + port);

        server.start();
    }

}
