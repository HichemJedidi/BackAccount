package com.sgcib.bankacountapplication.query.repositories;

import com.sgcib.bankacountapplication.query.entities.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationRepository extends JpaRepository<Operation,Long> {
}
