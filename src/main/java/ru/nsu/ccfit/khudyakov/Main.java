package ru.nsu.ccfit.khudyakov;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.internal.MongoClientImpl;
import org.bson.Document;
import ru.nsu.ccfit.khudyakov.mapping.MongoConverterImpl;
import ru.nsu.ccfit.khudyakov.mapping.context.MongoMappingContext;
import ru.nsu.ccfit.khudyakov.repository.MongoOperations;
import ru.nsu.ccfit.khudyakov.repository.MongoOperationsImpl;
import ru.nsu.ccfit.khudyakov.test.FruitsRepository;
import ru.nsu.ccfit.khudyakov.test.fruits;

import java.util.Optional;

public class Main {

    public static void main(String[] args) {
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase database = mongoClient.getDatabase("mydb");

            MongoMappingContext context = new MongoMappingContext();
            MongoOperations mongoOperations = new MongoOperationsImpl(context, new MongoConverterImpl(context), database);
            FruitsRepository fruitsRepository = new FruitsRepository(mongoOperations, fruits.class);

            Optional<fruits> fruit = fruitsRepository.findById("61ae5b17018fff71b455af50");
            System.out.println(fruit);
        }
    }

}
