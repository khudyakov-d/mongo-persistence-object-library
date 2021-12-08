package ru.nsu.ccfit.khudyakov;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;
import ru.nsu.ccfit.khudyakov.core.MongoOperations;
import ru.nsu.ccfit.khudyakov.core.MongoOperationsImpl;
import ru.nsu.ccfit.khudyakov.test.FruitsRepository;
import ru.nsu.ccfit.khudyakov.test.Fruit;
import ru.nsu.ccfit.khudyakov.test.Shop;
import ru.nsu.ccfit.khudyakov.test.ShopRepository;

import java.util.List;
import java.util.Optional;

public class Main {

    public static void main(String[] args) {
        try (MongoClient mongoClient = MongoClients.create()) {
            MongoDatabase database = mongoClient.getDatabase("mydb");
            MongoOperations mongoOperations = new MongoOperationsImpl(database);

            ShopRepository shopRepository = new ShopRepository(mongoOperations, Shop.class);
            FruitsRepository fruitsRepository = new FruitsRepository(mongoOperations, Fruit.class);

            Shop shop = new Shop();
            shop.setName("metro");
            shop = shopRepository.save(shop);

            Fruit fruit = new Fruit();
            fruit.setName("mango");
            fruit.setPrice(10d);
            fruit.setShops(List.of(shop));

            fruit = fruitsRepository.save(fruit);

            Optional<Fruit> result = fruitsRepository.findById(fruit.getId());
            System.out.println(result);
        }
    }

}
