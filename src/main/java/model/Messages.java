package model;

import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class Messages {

    static private HashMap<Integer, HashMap<Message.performs, Queue<Message>>> messages = new HashMap<>();
    static private HashMap<Integer, Boolean> blocked = new HashMap<>();

    static public boolean blocked(int id) {
        checkId(id);
        return blocked.get(id);
    }

    static private void checkId(int id) {
        if(!messages.containsKey(id)) {
            messages.put(id, new HashMap<>());
            messages.get(id).put(Message.performs.request, new PriorityQueue<>(
                    20,
                    Comparator.comparingInt(Message::getPriority)));
            messages.get(id).put(Message.performs.response, new PriorityQueue<>(
                    20,
                    Comparator.comparingInt(Message::getPriority)));
        }
        if(!blocked.containsKey(id)) {
            blocked.put(id, false);
        }
    }

    /**
     * Add the message at the end of the queue of the target
     * @param message Message to add
     */
    static public synchronized void add(Message message) {
        System.out.println("## " + message.getPerform() + " " + message.getSender() + " => " + message.getReceiver());
        if(message.getPerform() == Message.performs.request) {
            blocked.put(message.getSender(), true);
        } else {
            blocked.put(message.getSender(), false);
        }
        checkId(message.getReceiver());
        messages.get(message.getReceiver()).get(message.getPerform()).add(message);
    }

    /**
     * Get the next message for the given agent
     * @param agent Agent for which is the message
     * @return Agent's next message or null if there isn't any
     */
    static public Message getNextRequest(Agent agent) {
        Message m = getNextMessage(agent, Message.performs.request);
        blocked.put(agent.getAgentId(), m != null);
        return m;
    }

    static public Message getNextResponse(Agent agent) {
        Message m = getNextMessage(agent, Message.performs.response);
        blocked.put(agent.getAgentId(), m == null);
        return m;
    }

    static private synchronized  Message getNextMessage(Agent agent, Message.performs perform) {
        checkId(agent.getAgentId());
        return messages.get(agent.getAgentId()).get(perform).poll();
    }
}
