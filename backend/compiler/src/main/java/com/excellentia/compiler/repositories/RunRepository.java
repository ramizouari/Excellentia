package com.excellentia.compiler.repositories;

import com.excellentia.compiler.dto.Run;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("[dbo].[Run]")
public interface RunRepository extends JpaRepository<Run, Long>
{

    <S extends Run> S save(S newRun);
    Optional<Run> findById(Long runId);
}