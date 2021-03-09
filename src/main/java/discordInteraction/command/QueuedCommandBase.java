package discordInteraction.command;

import net.dv8tion.jda.api.entities.User;

public class QueuedCommandBase{
    protected User viewer;

    public User getViewer(){
            return viewer;
        }

    public QueuedCommandBase(User viewer){
        this.viewer = viewer;
    }
}
