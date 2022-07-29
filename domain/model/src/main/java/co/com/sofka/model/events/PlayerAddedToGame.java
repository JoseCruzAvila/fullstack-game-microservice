package co.com.sofka.model.events;

import co.com.sofka.generic.events.DomainEvent;
import co.com.sofka.model.player.Player;

public class PlayerAddedToGame extends DomainEvent<Player> {

    public PlayerAddedToGame(String aggregateId, Player player) {
        super("game.PlayerAdded", aggregateId, player);
    }
}
