package discordInteraction.card;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import discordInteraction.command.Result;

public class UnPoke extends CardTargetless {
    @Override
    public String getName() {
        return "UnPoke";
    }

    @Override
    public int getCost() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "Restore 3 health to the player character.";
    }

    @Override
    public String getFlavorText() {
        return "Take back one of the many bad things you've said about the streamer, restoring a bit of their will to live.";
    }

    @Override
    public FlavorType[] getFlavorTypes() {
        return new FlavorType[]{
                FlavorType.basic
        };
    }

    @Override
    public Result activate(AbstractPlayer player) {
        player.heal(3, true);
        return new Result(true, "You healed " + player.name + " for 3 health.");
    }
}
