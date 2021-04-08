package discordInteraction;

import discordInteraction.card.AbstractCard;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.reflections.Reflections;

public class Deck {
    private HashMap<String, ArrayList<AbstractCard>> cardsByClass;

    public ArrayList<AbstractCard> getCardsByViewerClass(String viewerClass){
        return cardsByClass.get(viewerClass);
    }

    public ArrayList<String> getViewerClasses(){
        ArrayList<String> viewerClasses = new ArrayList<>();
        for (String viewerClass : cardsByClass.keySet())
            viewerClasses.add(viewerClass);
        return viewerClasses;
    }

    public Deck() {
        cardsByClass = new HashMap<String, ArrayList<AbstractCard>>();

        // So this part is... fun.
        // It will look through the entire project and add all defined subclasses of Card into the above Lists based on their ViewerClass.
        Reflections reflections = new Reflections(AbstractCard.class);

        for( Class<? extends AbstractCard> cardType : reflections.getSubTypesOf(AbstractCard.class)){
            if (Modifier.isAbstract(cardType.getModifiers()))
                continue;
            try {
                AbstractCard.setTextureForCard(cardType);
                AbstractCard card = cardType.newInstance();

                // For each class it says its part of, add it to that respective class's card list.
                for (String viewerClass : card.getViewerClasses()) {
                    // If its the first card type of its class, create a new entry for it.
                    if (!cardsByClass.containsKey(viewerClass))
                        cardsByClass.put(viewerClass, new ArrayList<AbstractCard>());
                    cardsByClass.get(viewerClass).add(card);
                }
            } catch (Exception e){
                Main.logger.debug(e);
            }
        }
    }
}
