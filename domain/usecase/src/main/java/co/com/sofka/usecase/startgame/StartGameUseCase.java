package co.com.sofka.usecase.startgame;

import co.com.sofka.exceptions.GameException;
import co.com.sofka.model.events.GameStarted;
import co.com.sofka.model.game.Game;
import co.com.sofka.model.game.gateways.GameRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.function.Predicate;

@RequiredArgsConstructor
public class StartGameUseCase {
    private final GameRepository repository;

    public Mono<Game> gameById(String gameId) {
        return repository.findBy("gameId", gameId)
                .onErrorResume(error -> Mono.error(new GameException("The given game doesn't exists")));
    }

    public Mono<GameStarted> startGame(Game game) {
        var validation = verifyErrors(game);

        return validation.map(this::startPlayingGame)
                .flatMap(repository::save)
                .map(newGame -> new GameStarted(game.getId(), newGame));
    }

    private Mono<Game> verifyErrors(Game game) {
        var data = Mono.just(game);

        if (game.isPlaying().equals(true))
            data = Mono.error(new GameException("The given game already is being played"));

        if (this.verifyGameEnded().test(game))
            data = Mono.error(new GameException("The given game has been ended"));

        return data;
    }

    private Predicate<Game> verifyGameEnded () {
        return game -> game.getWinner() != null;
    }

    private Game startPlayingGame(Game game) {
        game.setPlaying(true);
        return game;
    }
}
