package com.lukyanovich.cloudbox.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<K, V> {

    V save (V entity);

    List<V> findAll ();

    Optional<V> findById (K id);

    void update (V entity);

    boolean delete (K id);
}
