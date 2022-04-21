package com.example.URL_shortener.repository;

import com.example.URL_shortener.entity.URLEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface URLRepository extends JpaRepository<URLEntity, Long> {
    Optional<URLEntity> findByAlias(String alias);

    Boolean existsByAlias(String alias);
}
