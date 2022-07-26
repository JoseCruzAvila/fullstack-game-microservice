package co.com.sofka.api.game;

import co.com.sofka.event.EventPublisher;
import co.com.sofka.model.card.Card;
import co.com.sofka.generic.events.DomainEvent;
import co.com.sofka.model.events.GameStarted;
import co.com.sofka.model.game.Game;
import co.com.sofka.model.player.Player;
import co.com.sofka.usecase.addplayertogame.AddPlayerToGameUseCase;
import co.com.sofka.usecase.creategame.CreateGameUseCase;
import co.com.sofka.usecase.startgame.StartGameUseCase;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GameHandler {
    @Autowired
    private final EventPublisher<DomainEvent> publisher;
    private final CreateGameUseCase createGameUseCase;
    private final StartGameUseCase startGameUseCase;
    private final AddPlayerToGameUseCase addPlayerToGameUseCase;

    public Mono<ServerResponse> listenPOSTCreateGameUseCase(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(Game.class)
                .flatMap(createGameUseCase::createGame)
                .flatMap(game -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(game)))
                .onErrorResume(this::onErrorResume);
    }

    @ExceptionHandler
    public Mono<ServerResponse> listenPUTStartGameUseCase(ServerRequest serverRequest) {
        String gameId = serverRequest.pathVariable("gameId");
        return startGameUseCase.gameById(gameId)
                .flatMap(startGameUseCase::startGame)
                .doOnNext(publisher::publish)
                .flatMap(game -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(game)))
                .onErrorResume(this::onErrorResume);
    }

    public Mono<ServerResponse> listenPUTAddUserToGameUseCase(ServerRequest serverRequest) {
        String gameId = serverRequest.pathVariable("gameId");
        var game = startGameUseCase.gameById(gameId);

        return serverRequest.bodyToMono(Player.class)
                .zipWith(game)
                .flatMap(mono -> addPlayerToGameUseCase.addPlayer(mono.getT2(), mono.getT1()))
                .doOnNext(publisher::publish)
                .then(game)
                .flatMap(currentGame -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(currentGame)))
                .onErrorResume(this::onErrorResume);
    }

    private Mono<ServerResponse> onErrorResume(Throwable error) {
        return ServerResponse.status(error.getMessage().contains("doesn't exists") ? 404 : 409)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(error.getMessage()));
    }
}
