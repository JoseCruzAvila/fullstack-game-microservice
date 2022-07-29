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

    public Mono<PlayerAddedToGame> addPlayer(Game game, Player player) {
        var gameToUpdate = Mono.just(game)
                .flatMap(this::verifyErrors)
                .onErrorResume(this.setErrorToReturn());

        return gameToUpdate.map(this.addPlayerToGame(player))
                .flatMap(repository::save)
                .map(currentGame -> new PlayerAddedToGame(currentGame.getGameId(), player));
    }

    private Mono<Game> verifyErrors(Game game) {
        var data = Mono.just(game);

        if (game.getCurrentPlayersNumber().equals(game.getPlayers().size()))
            data = Mono.error(new GameException("The given game already has the maximum numbers of players connected"));

        if (game.isPlaying().equals(true))
            data = Mono.error(new GameException("The given game already is being played"));

        if (this.verifyGameEnded().test(game))
            data = Mono.error(new GameException("The given game has been ended"));

        return data;
    }

    private Function<Throwable, Mono<Game>> setErrorToReturn() {
        return error -> Mono.error(error.getMessage().contains("The given game")
                ? error : new GameException("The given game doesn't exists"));
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
