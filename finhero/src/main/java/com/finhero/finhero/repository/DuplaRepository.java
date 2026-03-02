package com.finhero.finhero.repository;

import com.finhero.finhero.model.Dupla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DuplaRepository extends JpaRepository<Dupla, Long> {
    
    @Query("SELECT d FROM Dupla d WHERE d.userAId = :userId OR d.userBId = :userId")
    Optional<Dupla> findByUserId(Long userId);
    
    @Query("SELECT COUNT(d) > 0 FROM Dupla d WHERE d.userAId = :userId OR d.userBId = :userId")
    boolean existsByUserId(Long userId);
}

