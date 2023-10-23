package de.vitbund.miniserv.examples.friends;

import de.vitbund.miniserv.AuthChecker;
import de.vitbund.miniserv.Miniserv;
import java.util.ArrayList;
import java.util.List;


public class FriendsServer {

    public static void main(String[] args) {
        Miniserv server = new Miniserv(8000, true);

        List<Person> friends = new ArrayList<>();
        
        AuthChecker auth = (session) -> {
            return true;
        };
        
        server.onPost("/api/friends", (request) -> {
            Person p = server.jsonToObject(request, Person.class);
            friends.add(p);
            return p;
        });
        
        server.onGet("/api/friends", (request) -> {
            server.debugOut("Session id:" + request.getSession().getId());
            return friends;
        }, auth);
        
        server.onDelete("/api/friends", (request) -> {
            Idx idx = server.jsonToObject(request, Idx.class);
            friends.remove(idx.getIdx());
            return idx;
        });
        
        server.start();
    }
}
