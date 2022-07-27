package co.com.sofka.usecase.startgame;

import co.com.sofka.exceptions.GameException;
import co.com.sofka.model.events.GameStarted;
import co.com.sofka.model.game.Game;
import co.com.sofka.model.game.gateways.GameRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class StartGameUseCase {
    private final GameRepository repository;

    public Mono<Game> gameById(String gameId) {
        return repository.findBy("gameId", gameId)
                .onErrorResume(error -> Mono.error(new GameException("The given game doesn't exists")));
    }

    public Mono<GameStarted> startGame(Game game) {
        if (game.isPlaying().equals(true))
            return Mono.error(new GameException("The given game already is being played"));

        game.setPlaying(true);
        return repository.save(game)
                .map(newGame -> new GameStarted(game.getId(), game));
    }
}
