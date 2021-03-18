package discordInteraction.card;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import discordInteraction.FlavorType;
import discordInteraction.Main;
import discordInteraction.ViewerMinion;
import discordInteraction.command.Result;
import kobting.friendlyminions.monsters.AbstractFriendlyMonster;
import net.dv8tion.jda.api.entities.User;

public class JoyfulNursing extends CardTargetless {
    @Override
    public String getName() {
        return "Joyful Nursing";
    }

    @Override
    public int getCost() {
        return 3;
    }

    @Override
    public String getDescription() {
        return "Heal 5 + X health to the player, where X is equal to the number of alive viewers in the battle.";
    }

    @Override
    public String getFlavorText() {
        return "Can you really be sure they're different people?";
    }

    @Override
    public FlavorType[] getFlavorTypes() {
        return new FlavorType[]{
                FlavorType.support
        };
    }

    @Override
    public Result activate(User user, AbstractPlayer player) {
        int viewerCount = 0;
        for (AbstractFriendlyMonster viewer : Main.battle.getViewerMonsters().values())
            if (!viewer.isDeadOrEscaped())
                viewerCount++;
        int toHeal = 5 + viewerCount;
        player.heal(toHeal);
        return new Result(true, "You healed the player for " + toHeal);
    }
}
