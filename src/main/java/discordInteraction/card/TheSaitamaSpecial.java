package discordInteraction.card;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.powers.MetallicizePower;
import com.megacrit.cardcrawl.powers.RitualPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import discordInteraction.FlavorType;
import discordInteraction.Utilities;
import discordInteraction.command.Result;
import net.dv8tion.jda.api.entities.User;

public class TheSaitamaSpecial extends CardTargetless {
    @Override
    public String getName() {
        return "The Saitama Special";
    }

    @Override
    public int getCost() {
        return 5;
    }

    @Override
    public String getDescription() {
        return "Apply Ritual 3, Metallicize 12, and Strength - 12 to the player.";
    }

    @Override
    public String getFlavorText() {
        return "Slow down. Training takes time.";
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
        Utilities.applyPower(player, new RitualPower(player, 3, true));
        Utilities.applyPower(player, new MetallicizePower(player, 12));
        Utilities.applyPower(player, new StrengthPower(player, -12));
        player.addBlock(12);
        return new Result(true, "The player has begun their accent into hero hood.");
    }
}
