package co.com.sofka.bus;

import co.com.sofka.event.EventListenerSubscriber;
import co.com.sofka.generic.events.DomainEvent;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class RabbitMQEventBus {
    private static final Logger log = LoggerFactory.getLogger(RabbitMQEventBus.class);
    private final RabbitTemplate rabbitTemplate;
    private final Exchange exchange;
    private final EventListenerSubscriber subscriber;
    private final Flux<DomainEvent> events;

    public RabbitMQEventBus(RabbitTemplate rabbitTemplate, Exchange exchange, EventListenerSubscriber subscriber, Flux<DomainEvent> events) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.subscriber = subscriber;
        this.events = events;
    }

    public void publish(DomainEvent event) {
        log.info("Event received: {}", event.getType());
        Mono.just(event)
                .doOnNext(currentEvent -> rabbitTemplate.convertAndSend(exchange.getName(), currentEvent.getType(), new Gson().toJson(currentEvent)))
                .thenMany(events)
                .filter(currentEvent -> event.getType().equals(currentEvent.getType()))
                .map(currentEvent -> event)
                .subscribe(subscriber::onNext);
    }
}
