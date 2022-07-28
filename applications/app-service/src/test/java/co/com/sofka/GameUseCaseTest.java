package co.com.sofka;

import co.com.sofka.api.game.GameRouterRest;
import co.com.sofka.event.EventPublisher;
import co.com.sofka.generic.events.DomainEvent;
import co.com.sofka.model.events.GameStarted;
import co.com.sofka.model.game.Game;
import co.com.sofka.model.game.gateways.GameRepository;
import co.com.sofka.model.player.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {GameRouterRest.class, MockProvider.class})
@WebFluxTest
class GameUseCaseTest {
    @Autowired
    private ApplicationContext context;
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private GameRepository repository;
    @Autowired
    private EventPublisher<DomainEvent> publisher;

    @BeforeEach
    public void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(context).build();
    }

    @Test
    @DisplayName("POST /game success")
    void testCreateGameSuccess() {
        Game game = new Game("prueba", new Player(), 2);
        doReturn(Mono.just(game)).when(repository).save(any());

        webTestClient.post()
                .uri("/game")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(game), Game.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Game.class)
                .value(response -> {
                    Assertions.assertEquals("prueba", response.getGameId());
                    Assertions.assertEquals(5, response.getMaxCardsByPlayer());
                    Assertions.assertFalse(response.getPlaying());
                });
    }

    @Test
    @DisplayName("POST /game already exist")
    void testCreateGameAlreadyExist() {
        Game game = new Game("prueba", new Player(), 2);
        doReturn(Mono.error(new DuplicateKeyException("gameId dup key"))).when(repository).save(any());

        webTestClient.post()
                .uri("/game")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(game), Game.class)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(String.class)
                .value(response -> {
                    Assertions.assertEquals("The given gameId already exists", response);
                });
    }

    @Test
    @DisplayName("POST /game wrong players number")
    void testCreateGameWrongPlayerNumber() {
        Game game = new Game("prueba", new Player(), 7);

        webTestClient.post()
                .uri("/game")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(game), Game.class)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(String.class)
                .value(response -> {
                    Assertions.assertEquals("The game must have at least 2 players and max 6", response);
                });
    }

    @Test
    @DisplayName("POST /game/start/prueba success")
    void testStartGameSuccess() {
        doReturn(Mono.just(new Game("prueba", new Player(), 2))).when(repository).findBy(any(), any());
        doReturn(Mono.just(new Game("prueba", new Player(), 2))).when(repository).save(any());

        webTestClient.put()
                .uri("/game/start/prueba")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(new Game("prueba", new Player(), 2)), Game.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(GameStarted.class)
                .value(response -> {;
                    Assertions.assertEquals("game.GameStarted", response.getType());
                    Assertions.assertEquals("prueba", response.getSource().getGameId());
                    Assertions.assertEquals(5, response.getSource().getMaxCardsByPlayer());
                    Assertions.assertTrue(response.getSource().getPlaying());
                });
    }

    @Test
    @DisplayName("POST /game/start/prueba not found")
    void testStartGameNotFound() {
        Game game = new Game("prueba", new Player(), 2);

        doReturn(Mono.error(new Exception())).when(repository).findBy(any(), any());

        webTestClient.put()
                .uri("/game/start/prueba")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(game), Game.class)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .value(response -> {
                    Assertions.assertEquals("The given game doesn't exists", response);
                });
    }

    @Test
    @DisplayName("POST /game/start/prueba already started")
    void testStartGameAlreadyStarted() {
        Game game = new Game("prueba", new Player(), 2);
        game.setPlaying(true);
        doReturn(Mono.just(game)).when(repository).findBy(any(), any());

        webTestClient.put()
                .uri("/game/start/prueba")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(game), Game.class)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(String.class)
                .value(response -> {
                    Assertions.assertEquals("The given game already is being played", response);
                });
    }
}
