package ru.nsu.ccfit.khudyakov.repository;

import com.mongodb.client.result.DeleteResult;

public interface MongoOperations {

    <T> T save(T objectToSave);

    <T> T findById(Object id, Class<T> entityClass);

    void remove(Object object);

}
