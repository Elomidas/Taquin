package model;

import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class Messages {

    static private HashMap<Integer, Queue<Message>> messages = new HashMap<>();

    static private void checkId(int id) {
        if(!messages.containsKey(id)) {
            messages.put(id, new PriorityQueue<>(
                    20,
                    Comparator.comparingInt(Message::getPriority)));
        }
    }

    /**
     * Add the message at the end of the queue of the target
     * @param message Message to add
     */
    static public void add(Message message) {
        checkId(message.getReceiver());
        messages.get(message.getReceiver()).add(message);
    }

    /**
     * Get the next message for the given agent
     * @param agent Agent for which is the message
     * @return Agent's next message or null if there isn't any
     */
    static public Message getNext(Agent agent) {
        checkId(agent.getAgentId());
        return messages.get(agent.getAgentId()).poll();
    }
}
