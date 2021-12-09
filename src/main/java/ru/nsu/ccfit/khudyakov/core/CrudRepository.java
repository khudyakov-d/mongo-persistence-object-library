package ru.nsu.ccfit.khudyakov.core;

import java.util.Optional;

public interface CrudRepository<T, I> {

    <S extends T> S save(S entity);

    Optional<T> findById(I id);

    <S extends T> void delete(S entity);

}
