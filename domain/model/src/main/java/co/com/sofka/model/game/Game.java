package co.com.sofka.model.game;

import co.com.sofka.model.card.Card;
import co.com.sofka.model.player.Player;
import co.com.sofka.model.table.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Random;
import java.util.Set;

@Getter
@Setter
public class Game {
    private String id;
    private String gameId;
    private Boolean playing;
    private Player winner;
    private Set<Player> players;
    private Table table;
    private Integer maxCardsByPlayer;
    private Integer maxPlayers;
    private Integer minPlayers;
    private Integer currentPlayersNumber;

    public Game() {
        this.playing = false;
        this.winner = null;
        this.players = Set.of();
        this.maxCardsByPlayer = 5;
        this.maxPlayers = 6;
        this.minPlayers = 2;
        this.table = new Table();
    }

    public Game(String id, Player player, Integer currentPlayersNumber) {
        this.gameId = id;
        this.playing = false;
        this.winner = null;
        this.players = Set.of(player);
        this.maxCardsByPlayer = 5;
        this.maxPlayers = 6;
        this.minPlayers = 2;
        this.currentPlayersNumber = currentPlayersNumber;
        this.table = new Table();
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public void splitCards(List<Card> cards) {
        this.players.forEach(player -> {
            while (player.getDeck().size() < this.maxCardsByPlayer) {
                player.addCardToDeck(randomCard(cards));
            }
        });
    }

    private Card randomCard(List<Card> cards) {
        Random random = new Random();
        Card cardSelected = cards.get(random.nextInt(0, cards.size()));
        cards.remove(cardSelected);

        return cardSelected;
    }

    public Boolean isPlaying() {
        return this.playing;
    }
}
