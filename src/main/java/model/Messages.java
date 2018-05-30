package model;

import java.util.ArrayList;

public class Messages {

    static private ArrayList<Message> messages;

    static public void add(Message message) {
        messages.add(message);
    }

    static public Message getNext(int id) {
        for(Message m : messages) {
            if(m.getReciever() == id) {
                messages.remove(m);
                return m;
            }
        }
        return null;
    }
}
