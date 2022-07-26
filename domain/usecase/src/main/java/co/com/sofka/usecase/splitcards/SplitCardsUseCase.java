package co.com.sofka.usecase.splitcards;

import co.com.sofka.generic.events.DomainEvent;
import co.com.sofka.generic.usecase.UseCase;
import co.com.sofka.model.card.gateways.CardRepository;
import co.com.sofka.model.events.CardAddedToPlayer;
import co.com.sofka.model.game.Game;
import co.com.sofka.model.game.gateways.GameRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import java.util.Objects;

@RequiredArgsConstructor
public class SplitCardsUseCase extends UseCase {
    private final CardRepository cardRepository;
    private final GameRepository repository;

    @Override
    public Flux<DomainEvent> execute(Object game) {
        return this.addCardsToPlayers((Game) game);
    }

    public Flux<DomainEvent> addCardsToPlayers(Game game) {
        return Objects.requireNonNull(cardRepository.findAll()
                        .collectList()
                        .flatMap(cards -> {
                            game.splitCards(cards);
                            return repository.save(game)
                                    .map(currentGame -> Flux.fromIterable(game.getPlayers()));
                        })
                        .blockOptional())
                .orElseThrow()
                .map(player -> new CardAddedToPlayer(game.getGameId(), player));
    }
}
