package co.com.sofka.generic.usecase;


import co.com.sofka.generic.events.DomainEvent;
import reactor.core.publisher.Flux;

public abstract class UseCase {

    public Flux<DomainEvent> execute(Object source) {
        return null;
    }

    public static class UseCaseWrap {
        private final UseCase useCase;
        private final String eventType;

        public UseCaseWrap(UseCase useCase, String eventType) {
            this.useCase = useCase;
            this.eventType = eventType;
        }

        public UseCase useCase() {
            return useCase;
        }

        public String eventType() {
            return eventType;
        }
    }
}
