package com.jhcs.booklore.domain.repository;

import com.jhcs.booklore.domain.entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FeedbackRepository extends JpaRepository<Feedback,Long> {
    @Query("""
            SELECT feedback
            FROM Feedback feedback
            WHERE feedback.book.id = :id
            """
    )
    Page<Feedback> findAllByBookId(
            Long id,
            Pageable pageable
                                  );
}
