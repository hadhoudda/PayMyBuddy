package com.paymybuddy.repository;

import com.paymybuddy.model.Connection;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ConnectionRepository extends CrudRepository<Connection, Integer> {
}
