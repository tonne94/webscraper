package com.test.webscraper.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepository extends CrudRepository<HashRecord, Long> {

    List<HashRecord> findAll();

    HashRecord findByHash(String hash);

    long countByHash(String hash);

}