package com.jobflow.repository;

import com.jobflow.entity.HistoryItem;
import com.jobflow.entity.enums.HistoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HistoryRepository extends JpaRepository<HistoryItem, UUID> {

  @Query("SELECT h FROM HistoryItem h WHERE h.user.id = :userId AND h.deletedAt IS NULL ORDER BY h.createdAt DESC")
  Page<HistoryItem> findAllByUserId(UUID userId, Pageable pageable);

  @Query("SELECT h FROM HistoryItem h WHERE h.user.id = :userId AND h.type = :type AND h.deletedAt IS NULL ORDER BY h.createdAt DESC")
  Page<HistoryItem> findAllByUserIdAndType(UUID userId, HistoryType type, Pageable pageable);

  @Query("SELECT h FROM HistoryItem h WHERE h.id = :id AND h.user.id = :userId AND h.deletedAt IS NULL")
  Optional<HistoryItem> findByIdAndUserId(UUID id, UUID userId);
}
