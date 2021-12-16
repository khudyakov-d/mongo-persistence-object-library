import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import data.Fruit;
import data.FruitsRepository;
import data.Tree;
import data.TreeRepository;
import data.Variety;
import data.VarietyDto;
import data.VarietyRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.nsu.ccfit.khudyakov.core.MongoOperations;
import ru.nsu.ccfit.khudyakov.core.MongoOperationsImpl;
import ru.nsu.ccfit.khudyakov.core.mapping.query.Criteria;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
class RepositoryTest {

    private final Logger logger = LogManager.getLogger(RepositoryTest.class);

    public static final String DATABASE_NAME = "mydb";

    @Container
    private MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));

    @Test
    void testAssociations() {
        try (MongoClient mongoClient = MongoClients.create(mongoDBContainer.getReplicaSetUrl())) {
            MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
            MongoOperations mongoOperations = new MongoOperationsImpl(database);

            FruitsRepository fruitsRepository = new FruitsRepository(mongoOperations, Fruit.class);
            TreeRepository treeRepository = new TreeRepository(mongoOperations, Tree.class);
            VarietyRepository varietyRepository = new VarietyRepository(mongoOperations, Variety.class);

            String fruitName = "apple";
            Fruit fruit = createFruit(fruitName);
            fruit = fruitsRepository.save(fruit);

            String treeName = "apple-tree";
            Tree tree = createTree(treeName, fruit);
            tree = treeRepository.save(tree);

            String varietyName = "red";
            Variety variety = createVariety(varietyName, 100d, 4, fruit);
            variety = varietyRepository.save(variety);

            fruit.setTree(tree);
            fruit.setVarieties(List.of(variety));
            fruitsRepository.save(fruit);

            logger.debug("Find fruit with id {}", fruit.getId());
            Fruit result = fruitsRepository.findById(fruit.getId()).orElseThrow();

            assertEquals(fruitName, result.getName());

            logger.debug("Get fruit tree");
            Tree resultTree = result.getTree();
            assertEquals(treeName, resultTree.getName());
            assertEquals(fruitName, resultTree.getFruit().getName());

            logger.debug("Get fruit varieties");
            Variety resultVariety = result.getVarieties().get(0);
            assertEquals(varietyName, resultVariety.getName());
            assertEquals(fruitName, resultVariety.getFruit().getName());
        }
    }

    @Test
    void testCriteriaApi_andOperator() {
        try (MongoClient mongoClient = MongoClients.create(mongoDBContainer.getReplicaSetUrl())) {
            MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
            MongoOperations mongoOperations = new MongoOperationsImpl(database);

            FruitsRepository fruitsRepository = new FruitsRepository(mongoOperations, Fruit.class);
            VarietyRepository varietyRepository = new VarietyRepository(mongoOperations, Variety.class);

            String fruitName = "apple";
            Fruit fruit = createFruit(fruitName);
            fruit = fruitsRepository.save(fruit);

            varietyRepository.save(createVariety("red", 100d, 4, fruit));
            varietyRepository.save(createVariety("gala", 150d, 5, fruit));
            varietyRepository.save(createVariety("black prince", 200d, 3, fruit));
            varietyRepository.save(createVariety("golden", 160d, 4, fruit));

            Criteria criteria = new Criteria().andOperator(
                    Criteria.where("price").lt(150),
                    Criteria.where("tasteLevel").gt(3)
            );

            List<Variety> varieties = varietyRepository.findAll(criteria);
            assertEquals(1, varieties.size());
            assertEquals("red", varieties.get(0).getName());
            assertEquals(100d, varieties.get(0).getPrice());
            assertEquals(4, varieties.get(0).getTasteLevel());
        }
    }

    @Test
    void testCriteriaApi_withProjection() {
        try (MongoClient mongoClient = MongoClients.create(mongoDBContainer.getReplicaSetUrl())) {
            MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
            MongoOperations mongoOperations = new MongoOperationsImpl(database);

            FruitsRepository fruitsRepository = new FruitsRepository(mongoOperations, Fruit.class);
            VarietyRepository varietyRepository = new VarietyRepository(mongoOperations, Variety.class);

            String fruitName = "apple";
            Fruit fruit = createFruit(fruitName);
            fruit = fruitsRepository.save(fruit);

            varietyRepository.save(createVariety("red", 100d, 4, fruit));
            varietyRepository.save(createVariety("gala", 150d, 5, fruit));
            varietyRepository.save(createVariety("black prince", 200d, 3, fruit));
            varietyRepository.save(createVariety("golden", 160d, 4, fruit));

            Criteria criteria = Criteria.where("name").is("gala");

            List<VarietyDto> varieties = varietyRepository.findAll(criteria, VarietyDto.class);
            assertEquals(1, varieties.size());
            assertEquals("gala", varieties.get(0).getName());
            assertEquals(150d, varieties.get(0).getPrice());
        }
    }

    @Test
    void testRemove() {
        try (MongoClient mongoClient = MongoClients.create(mongoDBContainer.getReplicaSetUrl())) {
            MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
            MongoOperations mongoOperations = new MongoOperationsImpl(database);

            FruitsRepository fruitsRepository = new FruitsRepository(mongoOperations, Fruit.class);

            String fruitName = "apple";
            Fruit fruit = createFruit(fruitName);
            fruit = fruitsRepository.save(fruit);

            fruitsRepository.delete(fruit);
            Optional<Fruit> result = fruitsRepository.findById(fruit.getId());
            Assertions.assertTrue(result.isEmpty());
        }
    }

    private Fruit createFruit(String name) {
        Fruit fruit = new Fruit();
        fruit.setName(name);
        return fruit;
    }

    private Tree createTree(String name, Fruit fruit) {
        Tree tree = new Tree();
        tree.setName(name);
        tree.setFruit(fruit);
        return tree;
    }

    private Variety createVariety(String name, Double price, Integer tasteLevel, Fruit fruit) {
        Variety variety = new Variety();
        variety.setName(name);
        variety.setPrice(price);
        variety.setTasteLevel(tasteLevel);
        variety.setFruit(fruit);
        return variety;
    }
}
