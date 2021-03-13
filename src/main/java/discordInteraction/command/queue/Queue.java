package discordInteraction.command.queue;

import discordInteraction.command.QueuedCommandBase;

import java.util.ArrayList;
import java.util.Stack;

// This class is designed to be safe from multiple queries.
public class Queue<T extends QueuedCommandBase> {
    private final Object lock = new Object();
    private Stack<T> commands;

    public Queue(){
        commands = new Stack<T>();
    }

    public ArrayList<T> getCommands(){
        synchronized (lock){
            ArrayList<T> list = new ArrayList<T>();
            for(T command : commands)
                list.add(command);
            return list;
        }
    }

    public void add(T command){
        synchronized (lock){
            commands.add(command);
        }
    }

    public boolean hasAnotherCommand(){
        synchronized (lock){
            return commands != null && !commands.isEmpty();
        }
    }
    public T getNextCommand(){
        synchronized (lock){
            return commands.pop();
        }
    }
}
