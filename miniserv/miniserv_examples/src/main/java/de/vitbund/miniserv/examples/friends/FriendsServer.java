package de.vitbund.miniserv.examples.friends;

import de.vitbund.miniserv.AuthChecker;
import de.vitbund.miniserv.Miniserv;
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
        
        server.addJsonResponder("/api/addfriend", (String json, HttpSession session) -> {
            Person p = server.jsonToObject(json, Person.class);
            friends.add(p);
            return p;
        });
        
        server.addJsonResponder("/api/listfriends", (String json, HttpSession session) -> {
            return friends;
        }, auth);
        
        server.addJsonResponder("/api/removefriend", (String json, HttpSession session) -> {
            Idx idx = server.jsonToObject(json, Idx.class);
            friends.remove(idx.getIdx());
            return idx;
        });
        
        server.start();
    }
}
