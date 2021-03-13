package discordInteraction;

import discordInteraction.card.Card;
import discordInteraction.card.Poke;
import discordInteraction.card.UnPoke;

import java.util.ArrayList;

public class Hand {
    private int capacity;
    private ArrayList<Card> cards;
    private ArrayList<FlavorType> flavorTypes;

    public ArrayList<Card> getCards(){
        return cards;
    }
    public ArrayList<FlavorType> getFlavorTypes(){
        return flavorTypes;
    }
    public void addFlavor(FlavorType flavor){
        if (!flavorTypes.contains(flavor))
            flavorTypes.add(flavor);
    }
    public void removeFlavor(FlavorType flavor){
        if (flavorTypes.contains(flavor))
            flavorTypes.remove(flavor);
    }

    public Hand(){
        capacity = 10;
        cards = new ArrayList<Card>();

        flavorTypes = new ArrayList<>();
        for (FlavorType flavor : FlavorType.values())
            if (flavor != FlavorType.basic)
                flavorTypes.add(flavor);

        drawNewHand(5, 2);
    }

    public void insertCard(Card card){
        if (cards.size() < capacity)
            cards.add(card);
    }

    public void removeCard(Card card){
        cards.remove(card);
    }

    public Card getFirstCardByName(String cardName) {
        for (Card card : cards)
            if (card.getName().replaceAll("\\s+","").equalsIgnoreCase(cardName.replaceAll("\\s+","")))
                return card;

        return null;
    }

    public void discardHand(){
        cards.clear();
    }

    public void draw(int pointsToDraw, int basicsToDraw){
        ArrayList<Card> cardPool = new ArrayList<Card>();

        for(FlavorType type : flavorTypes)
            for(Card card : Main.deck.getCardsByFlavorType(type))
                for(int x = 0; x < Main.deck.getHighestCost(); x++)
                    cardPool.add(card);

        while(pointsToDraw > 0 && cards.size() < capacity){
            int toDraw = Main.random.nextInt(cardPool.size() + 1) - 1;
            if (toDraw < 0)
                toDraw = 0;
            Card drawnCard = cardPool.get(toDraw);
            pointsToDraw -= drawnCard.getCost();
            cards.add(drawnCard);
        }

        drawBasics(basicsToDraw);
    }

    public void drawNewHand(int pointsToDraw, int basicsToDraw){
        discardHand();
        draw(pointsToDraw, basicsToDraw);
    }

    public void drawBasics(int pointsToDraw){
        ArrayList<Card> cardPool = new ArrayList<Card>();

        for(Card card : Main.deck.getCardsByFlavorType(FlavorType.basic))
            cardPool.add(card);

        while(pointsToDraw > 0 && cards.size() < capacity){
            int toDraw = Main.random.nextInt(cardPool.size() + 1) - 1;
            if (toDraw < 0)
                toDraw = 0;
            Card drawnCard = cardPool.get(toDraw);
            pointsToDraw -= drawnCard.getCost();
            cards.add(drawnCard);
        }
    }
}
