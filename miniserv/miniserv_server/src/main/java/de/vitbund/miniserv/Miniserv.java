package de.vitbund.miniserv;

import com.google.gson.Gson;
import de.vitbund.miniserv.servlets.JsonServlet;
import java.io.File;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.session.DefaultSessionCache;
import org.eclipse.jetty.server.session.FileSessionDataStore;
import org.eclipse.jetty.server.session.SessionCache;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;

public class Miniserv {

    private int port = 8000;
    private Server server;
    private String webDir;
    private ServletContextHandler context;
    private final Gson gson;
    private boolean debugOut;

    public Miniserv(int port, boolean debugOut) {
        this.webDir = System.getProperty("user.dir") + File.separator + "web";
        this.port = port;
        this.gson = new Gson();
        this.debugOut = debugOut;

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

    public void addJsonResponder(String pathSpec, JsonResponder responder, AuthChecker authChecker) {
        JsonServlet jsonServlet = new JsonServlet(this, responder, authChecker);
        ServletHolder holder = new ServletHolder(jsonServlet);
        context.addServlet(holder, pathSpec);
    }
    
    public void addJsonResponder(String pathSpec, JsonResponder responder) {
        addJsonResponder(pathSpec, responder, null);
    }

    public void start() {
        try {
            context.setBaseResource(Resource.newResource(webDir));

            // last in chain: Default to serving static assets from current workdir
            ServletHolder holderStatic = new ServletHolder("default", DefaultServlet.class);
            holderStatic.setInitParameter("dirAllowed", "true");
            context.addServlet(holderStatic, "/");

            server.start();
            //server.dump(System.err);
            server.join();
        } catch (Exception t) {
            throw new RuntimeException(t);
        }
    }

    public String objectToJson(Object o) {
        synchronized(gson) {
            return gson.toJson(o);
        }
    }

    public <T> T jsonToObject(String json, Class<T> classOfT) {
        synchronized(gson) {
            return gson.fromJson(json, classOfT);
        }
    }
    
    public void setDebugOut(boolean b) {
        this.debugOut = b;
    }
    
    public boolean isDebugOut() {
        return this.debugOut;
    }
    
    public void debugOut(String s) {
        if(this.debugOut) {
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