package co.com.sofka;

import co.com.sofka.api.game.GameHandler;
import co.com.sofka.event.EventPublisher;
import co.com.sofka.model.game.gateways.GameRepository;
import co.com.sofka.usecase.addplayertogame.AddPlayerToGameUseCase;
import co.com.sofka.usecase.creategame.CreateGameUseCase;
import co.com.sofka.usecase.startgame.StartGameUseCase;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MockProvider {
    @Bean
    public GameRepository gameRepository() {
        return Mockito.mock(GameRepository.class);
    }

    @Bean
    public GameHandler gameHandler() {
        return new GameHandler(eventPublisher(), createGameUseCase(), startGameUseCase(), addPlayerToGameUseCase());
    }

    @Bean
    public CreateGameUseCase createGameUseCase() {
        return new CreateGameUseCase(gameRepository());
    }

    @Bean
    public StartGameUseCase startGameUseCase() {
        return new StartGameUseCase(gameRepository());
    }

    @Bean
    public AddPlayerToGameUseCase addPlayerToGameUseCase() {
        return new AddPlayerToGameUseCase(gameRepository());
    }

    @Bean
    public EventPublisher eventPublisher() {
        return Mockito.mock(EventPublisher.class);
    }
}
