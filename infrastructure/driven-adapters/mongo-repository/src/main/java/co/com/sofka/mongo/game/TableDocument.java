package co.com.sofka.mongo.game;

import co.com.sofka.mongo.card.CardDocument;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document
public class TableDocument {
    @Id
    private String id;
    private Set<CardDocument> cardsInGame;
    private Integer pointsByRound;
    private Integer totalRounds;
}
