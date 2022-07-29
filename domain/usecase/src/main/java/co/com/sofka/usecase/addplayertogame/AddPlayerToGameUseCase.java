package co.com.sofka.usecase.addplayertogame;

import co.com.sofka.exceptions.GameException;
import co.com.sofka.model.events.PlayerAddedToGame;
import co.com.sofka.model.game.Game;
import co.com.sofka.model.game.gateways.GameRepository;
import co.com.sofka.model.player.Player;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.function.Function;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class AddPlayerToGameUseCase {
    private final GameRepository repository;

    public Mono<PlayerAddedToGame> addPlayer(String gameId, Player player) {
        var gameToUpdate = repository.findBy("gameId", gameId);

        return gameToUpdate.filter(Predicate.not(Game::isPlaying))
                .switchIfEmpty(Mono.error(new GameException("The game is already started")))
                .filter(this.verifyGameEnded())
                .switchIfEmpty(Mono.error(new GameException("The game has ended")))
                .map(this.addPlayerToGame(player))
                .flatMap(repository::save)
                .map(game -> new PlayerAddedToGame(gameId, player));
    }

    private Function<Game, Game> addPlayerToGame(Player player) {
        return game -> {
            game.addPlayer(player);
            return game;
        };
    }

    private Predicate<Game> verifyGameEnded () {
        return game -> game.getWinner() != null;
    }
}
