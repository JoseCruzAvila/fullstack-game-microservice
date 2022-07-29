package co.com.sofka.event;

import co.com.sofka.bus.RabbitMQEventBus;
import co.com.sofka.generic.events.DomainEvent;
import co.com.sofka.generic.usecase.UseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class EventListener implements ApplicationListener<AuditEvent> {
    @Autowired
    private RabbitMQEventBus bus;
    @Autowired
    private Flux<UseCase.UseCaseWrap> useCases;

    @Override
    public void onApplicationEvent(AuditEvent event) {
        var entity = (DomainEvent) event.getEntity();
        bus.publish(entity);
    }
}
