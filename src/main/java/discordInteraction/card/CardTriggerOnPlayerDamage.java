package discordInteraction.card;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import discordInteraction.command.Result;
import discordInteraction.command.ResultWithInt;
import net.dv8tion.jda.api.entities.User;

public abstract class CardTriggerOnPlayerDamage extends Card {
    @Override
    public ViewerCardType getViewerCardType() {
        return ViewerCardType.triggerOnPlayerDamage;
    }

    public abstract ResultWithInt handleOnPlayerDamageTrigger(int incomingDamage, DamageInfo damageInfo, AbstractPlayer player, User user);
}
