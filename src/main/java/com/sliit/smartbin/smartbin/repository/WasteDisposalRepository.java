package com.sliit.smartbin.smartbin.repository;

import com.sliit.smartbin.smartbin.model.User;
import com.sliit.smartbin.smartbin.model.WasteDisposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WasteDisposalRepository extends JpaRepository<WasteDisposal, Long> {
    List<WasteDisposal> findByUserOrderByCreatedAtDesc(User user);
}

