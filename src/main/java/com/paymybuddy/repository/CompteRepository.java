package com.paymybuddy.repository;

import com.paymybuddy.model.Compte;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CompteRepository extends CrudRepository<Compte, Integer> {
}
