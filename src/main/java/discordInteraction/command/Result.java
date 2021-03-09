package discordInteraction.command;

public class Result {
    private Boolean resolved;
    private String whatHappened;

    public Boolean hadResolved(){
        return resolved;
    }

    public String getWhatHappened(){
        return whatHappened;
    }

    public Result(Boolean resolved, String whatHappened){
        this.resolved = resolved;
        this.whatHappened = whatHappened;
    }
}
