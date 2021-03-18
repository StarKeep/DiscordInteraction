package discordInteraction.card;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.powers.WeakPower;
import discordInteraction.FlavorType;
import discordInteraction.Main;
import discordInteraction.Utilities;
import discordInteraction.command.Result;
import net.dv8tion.jda.api.entities.User;

public class LayerItOn extends CardTargetless {
    @Override
    public String getName() {
        return "Layer It On";
    }

    @Override
    public int getCost() {
        return 2;
    }

    @Override
    public String getDescription() {
        return "Apply 12 Block and 2 Weak to the player.";
    }

    @Override
    public String getFlavorText() {
        return "As it turns out, armor is heavy.";
    }

    @Override
    public FlavorType[] getFlavorTypes() {
        return new FlavorType[]{
                FlavorType.support,
                FlavorType.chaos
        };
    }

    @Override
    public Result activate(User user, AbstractPlayer player) {
        Utilities.applyPower(player, new WeakPower(player, 2, false));
        player.addBlock(12);
        return new Result(true, "You applied 12 block and 2 weak to the streamer.");
    }
}
