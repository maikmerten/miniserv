package de.vitbund.miniserv;

import jakarta.servlet.http.HttpSession;

/**
 *
 * @author maik
 */
public interface AuthChecker {
    
    public boolean isAuthenticated(HttpSession session);
    
}
