package discordInteraction;

import discordInteraction.card.Card;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.reflections.Reflections;

public class Deck {
    private HashMap<FlavorType, ArrayList<Card>> cards;

    public ArrayList<Card> getCardsByFlavorType(FlavorType type){
        return cards.get(type);
    }

    public Deck() {
        cards = new HashMap<FlavorType, ArrayList<Card>>();

        for (FlavorType type : FlavorType.values())
            cards.put(type, new ArrayList<Card>());

        // So this part is... fun.
        // It will look through the entire project and add all defined subclasses of Card into the above Lists based on their Flavor.
        Reflections reflections = new Reflections(Card.class);

        for( Class<? extends Card> cardType : reflections.getSubTypesOf(Card.class)){
            if (Modifier.isAbstract(cardType.getModifiers()))
                continue;
            try {
                Card.setTextureForCard(cardType);

                Card card = cardType.newInstance();
                if (Arrays.stream(card.getFlavorTypes()).anyMatch(FlavorType.basic::equals))
                    cards.get(FlavorType.basic).add(card);
                if (Arrays.stream(card.getFlavorTypes()).anyMatch(FlavorType.support::equals))
                    cards.get(FlavorType.support).add(card);
                if (Arrays.stream(card.getFlavorTypes()).anyMatch(FlavorType.oppose::equals))
                    cards.get(FlavorType.oppose).add(card);
                if (Arrays.stream(card.getFlavorTypes()).anyMatch(FlavorType.chaos::equals))
                    cards.get(FlavorType.chaos).add(card);
            } catch (Exception e){
                Main.logger.debug(e);
            }

        }
    }
}
