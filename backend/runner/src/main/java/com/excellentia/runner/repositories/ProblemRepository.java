package com.excellentia.runner.repositories;

import com.excellentia.runner.dto.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository("[dbo].[Problem]")
public interface ProblemRepository extends JpaRepository<Problem, Long>
{
    Optional<Problem> findById(Long problemId);

    Optional<Problem> findByName(String problemName);
}