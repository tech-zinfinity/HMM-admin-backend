package app.repositories;


import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import app.entities.Transaction;

public interface TransactionRepository extends ReactiveMongoRepository<Transaction, String>{

}
