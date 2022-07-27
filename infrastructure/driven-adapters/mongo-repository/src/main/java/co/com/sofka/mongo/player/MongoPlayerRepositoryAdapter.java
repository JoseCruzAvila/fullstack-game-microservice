package co.com.sofka.mongo.player;

import co.com.sofka.model.player.Player;
import co.com.sofka.model.player.gateways.PlayerRepository;
import co.com.sofka.mongo.card.CardDocument;
import co.com.sofka.mongo.helper.AdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Repository
public class MongoPlayerRepositoryAdapter extends AdapterOperations<Player, PlayerDocument, String, MongoDBPlayerRepository> implements PlayerRepository {

    private final ReactiveMongoTemplate mongoTemplate;

    public MongoPlayerRepositoryAdapter(MongoDBPlayerRepository repository, ObjectMapper mapper, ReactiveMongoTemplate mongoTemplate) {
        super(repository, mapper, d -> mapper.map(d, Player.class));

        this.mongoTemplate = mongoTemplate;
    }

    public Mono<Player> findBy(String criteria, String toFind) {
        var condition = Query.query(Criteria.where(criteria).is(toFind));

        return mongoTemplate.find(condition, PlayerDocument.class)
                .map(this::toEntity)
                .single();
    }
}
