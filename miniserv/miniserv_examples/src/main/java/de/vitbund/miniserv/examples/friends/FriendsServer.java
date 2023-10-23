package de.vitbund.miniserv.examples.friends;

import de.vitbund.miniserv.AuthChecker;
import de.vitbund.miniserv.Miniserv;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;


public class FriendsServer {

    public static void main(String[] args) {
        Miniserv server = new Miniserv(8000, true);

        List<Person> friends = new ArrayList<>();
        
        AuthChecker auth = (HttpSession) -> {
            return true;
        };
        
        server.onPost("/api/friends", (HttpServletRequest request, HttpSession session) -> {
            Person p = server.jsonToObject(request, Person.class);
            friends.add(p);
            return p;
        });
        
        server.onGet("/api/friends", (HttpServletRequest request, HttpSession session) -> {
            return friends;
        }, auth);
        
        server.onDelete("/api/friends", (HttpServletRequest request, HttpSession session) -> {
            Idx idx = server.jsonToObject(request, Idx.class);
            friends.remove(idx.getIdx());
            return idx;
        });
        
        server.start();
    }
}
