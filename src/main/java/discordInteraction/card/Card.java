package discordInteraction.card;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import discordInteraction.FlavorType;

import java.util.HashMap;

public abstract class Card implements Cloneable {
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
    public abstract String getDescription();
    public abstract String getFlavorText();
    public abstract FlavorType[] getFlavorTypes();
}
