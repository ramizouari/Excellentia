package com.excellentia.compiler.repositories;
import com.excellentia.compiler.dto.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;


@Repository("[dbo].[Problem]")
public interface ProblemRepository extends JpaRepository<Problem, Long>
{
    Optional<Problem> findById(Long problemId);

    Optional<Problem> findByName(String problemName);
}