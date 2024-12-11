package com.jhcs.booklore.domain.repository;

import com.jhcs.booklore.domain.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback,Long> {

}
