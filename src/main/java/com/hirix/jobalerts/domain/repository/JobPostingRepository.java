package com.hirix.jobalerts.domain.repository;

import com.hirix.jobalerts.domain.entity.JobPosting;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobPostingRepository extends JpaRepository<JobPosting, UUID> {

    Optional<JobPosting> findByApplyUrl(String applyUrl);

    boolean existsByApplyUrl(String applyUrl);

    List<JobPosting> findAllByOrderByCreatedAtDesc();
}
