package ru.nsu.ccfit.khudyakov;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import ru.nsu.ccfit.khudyakov.core.MongoOperations;
import ru.nsu.ccfit.khudyakov.core.MongoOperationsImpl;
import ru.nsu.ccfit.khudyakov.test.FruitsRepository;
import ru.nsu.ccfit.khudyakov.test.Fruit;

import java.util.Optional;

public class Main {

    public static void main(String[] args) {
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase database = mongoClient.getDatabase("mydb");

            MongoOperations mongoOperations = new MongoOperationsImpl(database);
            FruitsRepository fruitsRepository = new FruitsRepository(mongoOperations, Fruit.class);

            Fruit fruit = new Fruit();
            fruit.setName("mango");
            fruit.setPrice(10d);

            fruit = fruitsRepository.save(fruit);

            //Optional<Fruit> fruit = fruitsRepository.findById("61ae5b17018fff71b455af50");
            System.out.println(fruit);
        }
    }

}
