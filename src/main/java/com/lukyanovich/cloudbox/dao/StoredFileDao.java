package com.lukyanovich.cloudbox.dao;

import com.lukyanovich.cloudbox.model.StoredFile;
import java.util.List;
import java.util.Optional;

public interface StoredFileDao extends Dao<Long, StoredFile> {

    List<StoredFile> findAllByUserId(Long userId);

    Optional<StoredFile> findByIdAndUserId(Long fileId, Long userId);

    boolean deleteByIdAndUserId(Long fileId, Long userId);

}
