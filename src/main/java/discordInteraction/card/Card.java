package discordInteraction.card;

public interface Card extends Cloneable {
    public abstract String getName();
    public abstract int getCost();
    public abstract String getDescription();
    public abstract String getFlavorText();
    public abstract FlavorType[] getFlavorTypes();
}
