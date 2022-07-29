package co.com.sofka.usecase.creategame;

import co.com.sofka.exceptions.GameException;
import co.com.sofka.generic.usecase.UseCase;
import co.com.sofka.model.events.GameCreated;
import co.com.sofka.model.game.Game;
import co.com.sofka.model.game.gateways.GameRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@RequiredArgsConstructor
public class CreateGameUseCase extends UseCase<GameCreated, Game> {
    private final GameRepository repository;

    public Mono<Game> createGame(Game game) {
        return Mono.just(game)
                .filter(this::validateGameIdNotNull)
                .switchIfEmpty(Mono.error(new GameException("The gameId couldn't be empty")))
                .filter(this::validateGamePlayers)
                .flatMap(repository::save)
                .switchIfEmpty(Mono.error(new GameException("The game must have at least 2 players and max 6")))
                .onErrorResume(this.getError());
    }

    private boolean validateGameIdNotNull(Game game) {
        return game.getGameId() != null && !game.getGameId().equals("");
    }

    private boolean validateGamePlayers(Game game) {
        return game.getCurrentPlayersNumber() >= game.getMinPlayers() &&
                game.getCurrentPlayersNumber() <= game.getMaxPlayers();
    }

    private Function<Throwable, Mono<Game>> getError() {
        return error -> Mono.error(error.getMessage()
                .contains("gameId dup key") ? new GameException("The given gameId already exists")
                : error);
    }
}
