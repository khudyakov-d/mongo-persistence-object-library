package ru.nsu.ccfit.khudyakov;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import ru.nsu.ccfit.khudyakov.core.MongoOperations;
import ru.nsu.ccfit.khudyakov.core.MongoOperationsImpl;
import ru.nsu.ccfit.khudyakov.core.mapping.query.Criteria;
import ru.nsu.ccfit.khudyakov.test.Fruit;
import ru.nsu.ccfit.khudyakov.test.FruitsRepository;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        try (MongoClient mongoClient = MongoClients.create()) {

            MongoDatabase database = mongoClient.getDatabase("mydb");
            MongoOperations mongoOperations = new MongoOperationsImpl(database);

            FruitsRepository fruitsRepository = new FruitsRepository(mongoOperations, Fruit.class);
            fruitsRepository.save(createFruit("apple", 10d));
            fruitsRepository.save(createFruit("banana", 20d));
            fruitsRepository.save(createFruit("mango", 30d));

            Criteria criteria = new Criteria().orOperator(
                    Criteria.where("name").is("mango"),
                    Criteria.where("name").is("banana")
            );

            List<Fruit> fruits = fruitsRepository.find(criteria);

            System.out.println();
        }
    }

    private static Fruit createFruit(String name, Double price) {
        Fruit fruit = new Fruit();
        fruit.setName(name);
        fruit.setPrice(price);
        return fruit;
    }

    /*
    public static void main(String[] args) {
        try (MongoClient mongoClient = MongoClients.create()) {

            MongoDatabase database = mongoClient.getDatabase("mydb");
            MongoOperations mongoOperations = new MongoOperationsImpl(database);

            ShopRepository shopRepository = new ShopRepository(mongoOperations, Variety.class);
            Variety variety = new Variety();
            variety.setName("red");
            variety = shopRepository.save(variety);

            FruitsRepository fruitsRepository = new FruitsRepository(mongoOperations, Fruit.class);
            Fruit fruit = new Fruit();
            fruit.setName("apple");
            fruit.setPrice(10d);
            fruit = fruitsRepository.save(fruit);

            variety.setFruit(fruit);
            variety = shopRepository.save(variety);

            fruit.setVarieties(List.of(variety));
            fruit = fruitsRepository.save(fruit);

            Optional<Fruit> searchResult = fruitsRepository.findById(fruit.getId());
            fruit = searchResult.orElseThrow();

            System.out.println();

            fruitsRepository.delete(fruit);
            searchResult = fruitsRepository.findById(fruit.getId());
            System.out.println(searchResult.isEmpty());;*//*

        }
    }*/


}
