package co.com.sofka.config;

import co.com.sofka.generic.events.DomainEvent;
import co.com.sofka.generic.usecase.UseCase;
import co.com.sofka.model.events.CardAddedToPlayer;
import co.com.sofka.model.events.GameStarted;
import co.com.sofka.model.game.Game;
import co.com.sofka.usecase.splitcards.SplitCardsUseCase;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.reactive.HiddenHttpMethodFilter;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
public class ApplicationConfig {

    @Bean
    public Exchange exchange() {
        return new FanoutExchange("fullstack.game");
    }

    @Bean
    public Flux<UseCase.UseCaseWrap> useCasesForListener(
            SplitCardsUseCase splitCardsUseCase

    ) {
        return Flux.just(new UseCase.UseCaseWrap(splitCardsUseCase, "game.GameStarted"));
    }

    @Bean
    public Flux<DomainEvent> domainEvents() {
        return Flux.just(new GameStarted("", new Game()));
    }

    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
                return chain.filter(exchange);
            }
        };
    }
}
