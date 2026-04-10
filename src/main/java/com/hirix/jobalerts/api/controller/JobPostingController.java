package com.hirix.jobalerts.api.controller;

import com.hirix.jobalerts.domain.entity.JobPosting;
import com.hirix.jobalerts.domain.repository.JobPostingRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobPostingController {

    private final JobPostingRepository jobPostingRepository;

    @GetMapping
    public List<JobPosting> getAllJobs() {
        return jobPostingRepository.findAll();
    }

    @GetMapping("/latest")
    public List<JobPosting> getLatestJobs() {
        return jobPostingRepository.findAllByOrderByCreatedAtDesc();
    }
}
