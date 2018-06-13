package model.communication;

import model.Agent;

import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;

public class Messages {

    static private HashMap<Integer, HashMap<Message.performs, Queue<Message>>> messages = new HashMap<>();

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
    }

    /**
     * Add the message at the end of the queue of the target
     * @param message Message to add
     */
    static public synchronized void add(Message message) {
        System.out.println("## " + message.getPerform() + " " + message.getSender() + " => " + message.getReceiver() + " " + message.getAction());
        checkId(message.getReceiver());
        messages.get(message.getReceiver()).get(message.getPerform()).add(message);
        System.out.println(message.getReceiver() + " : " + messages.get(message.getReceiver()).get(message.getPerform()).size() + " waiting");
    }

    /**
     * Get the next message for the given agent
     * @param agent Agent for which is the message
     * @return Agent's next message or null if there isn't any
     */
    static public synchronized Message getNextRequest(Agent agent) {
        Message m = getNextMessage(agent, Message.performs.request);
        return m;
    }

    static public synchronized Message getNextResponse(Agent agent) {
        Message m = getNextMessage(agent, Message.performs.response);
        return m;
    }

    static private Message getNextMessage(Agent agent, Message.performs perform) {
        checkId(agent.getAgentId());
        return messages.get(agent.getAgentId()).get(perform).poll();
    }
}
