package co.com.sofka.mongo.game;

import co.com.sofka.model.card.Card;
import co.com.sofka.model.game.Game;
import co.com.sofka.model.game.gateways.GameRepository;
import co.com.sofka.model.player.Player;
import co.com.sofka.mongo.card.CardDocument;
import co.com.sofka.mongo.helper.AdapterOperations;
import co.com.sofka.mongo.player.PlayerDocument;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Repository
public class MongoGameRepositoryAdapter extends AdapterOperations<Game, GameDocument, String, MongoDBGameRepository> implements GameRepository {
    private final ReactiveMongoTemplate mongoTemplate;

    public MongoGameRepositoryAdapter(MongoDBGameRepository repository, ObjectMapper mapper, ReactiveMongoTemplate mongoTemplate) {
        super(repository, mapper, d -> mapper.map(d, Game.class));

        this.mongoTemplate = mongoTemplate;
    }

    public Mono<Game> findBy(String criteria, String toFind) {
        var condition = Query.query(Criteria.where(criteria).is(toFind));
        return mongoTemplate.find(condition, GameDocument.class)
                .map(this::toEntity)
                .single();
    }
}
