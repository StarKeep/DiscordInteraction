package discordInteraction.card.targetless;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import discordInteraction.FlavorType;
import discordInteraction.Main;
import discordInteraction.card.targetless.AbstractCardTargetless;
import discordInteraction.command.Result;
import kobting.friendlyminions.monsters.AbstractFriendlyMonster;
import net.dv8tion.jda.api.entities.User;

public class JoyfulNursing extends AbstractCardTargetless {
    @Override
    public String getName() {
        return "Joyful Nursing";
    }

    @Override
    public int getCost() {
        return 3;
    }

    @Override
    public String getDescriptionForViewerDisplay() {
        return "Heal 5 + X health to the player and all living viewers, where X is equal to the number of alive viewers in the battle.";
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
        for (AbstractFriendlyMonster viewer : Main.battle.getViewerMonsters().values())
            if (!viewer.isDeadOrEscaped())
                viewer.heal(toHeal);
        return new Result(true, "You healed the player and all viewers for " + toHeal);
    }
}