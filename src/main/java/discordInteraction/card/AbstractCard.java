package discordInteraction.card;

import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.powers.AfterImagePower;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class AbstractCard implements Cloneable {
    private static HashMap<String, Texture> cardTextures = new HashMap<String, Texture>();

    public static void setTextureForCard(Class card){
        String cardName = card.getSimpleName().toLowerCase();
        Texture texture = new Texture("images/cards/" + cardName + ".png");
        cardTextures.put(cardName, texture);
    }
    public static Texture getTextureForCard(Class card){
        String cardName = card.getSimpleName().toLowerCase();
        return cardTextures.get(cardName);
    }

    public abstract String getName();
    public abstract int getCost();
    public abstract String getDescriptionForViewerDisplay();
    public String getDescriptionForGameDisplay(){ return getDescriptionForViewerDisplay(); }
    public abstract ArrayList<String> getViewerClasses();
    public abstract ViewerCardType getViewerCardType();
}
