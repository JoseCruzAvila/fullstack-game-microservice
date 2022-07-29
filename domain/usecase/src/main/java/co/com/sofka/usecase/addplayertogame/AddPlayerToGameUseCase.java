package co.com.sofka.usecase.addplayertogame;

import co.com.sofka.model.events.PlayerAddedToGame;
import co.com.sofka.model.game.Game;
import co.com.sofka.model.game.gateways.GameRepository;
import co.com.sofka.model.player.Player;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@RequiredArgsConstructor
public class AddPlayerToGameUseCase {
    private final GameRepository repository;

    public Mono<PlayerAddedToGame> addPlayer(String gameId, Player player) {
        var gameToUpdate = repository.findBy("gameId", gameId);
        return gameToUpdate.map(this.addPlayerToGame(player))
                .flatMap(repository::save)
                .map(game -> new PlayerAddedToGame(gameId, player));
    }

    private Function<Game, Game> addPlayerToGame(Player player) {
        return game -> {
            game.addPlayer(player);
            return game;
        };
    }
}
