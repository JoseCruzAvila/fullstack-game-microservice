package co.com.sofka.model.game.gateways;

import co.com.sofka.model.game.Game;
import reactor.core.publisher.Mono;

public interface GameRepository {
    Mono<Game> save(Game game);

    Mono<Game> findBy(String criteria, String toFind);
}
