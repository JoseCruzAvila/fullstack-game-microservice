package co.com.sofka.model.table;

import co.com.sofka.model.card.Card;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class Table {
    private String id;
    private Set<Card> cardsInGame;
    private Integer pointsByRound;
    private Integer totalRounds;

    public Table() {
        this.cardsInGame = Set.of();
        this.pointsByRound = 10;
        this.totalRounds = 0;
    }

    private void addCard(Card card) {
        if (!this.cardsInGame.contains(card))
            this.cardsInGame.add(card);
    }

    private void removeCard(Card card) {
        if (this.cardsInGame.contains(card))
            this.cardsInGame.remove(card);
    }
}
