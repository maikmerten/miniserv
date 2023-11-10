package de.maikmerten.miniserv.examples.friends;

import de.maikmerten.miniserv.AuthChecker;
import de.maikmerten.miniserv.Miniserv;
import de.maikmerten.miniserv.Response;
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
            return new Response(201, "added friend");
        });
        
        server.onGet("/api/friends", (request) -> {
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
