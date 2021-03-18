package discordInteraction.card;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import discordInteraction.FlavorType;
import discordInteraction.command.Result;
import net.dv8tion.jda.api.entities.User;

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
    public String getDescriptionForViewerDisplay() {
        return "Restore 3 health to the player.";
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
    public Result activate(User user, AbstractPlayer player) {
        player.heal(3, true);
        return new Result(true, "You healed " + player.name + " for 3 health.");
    }
}
