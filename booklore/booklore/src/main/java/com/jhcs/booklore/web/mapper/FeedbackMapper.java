package com.jhcs.booklore.web.mapper;
import com.jhcs.booklore.application.dto.FeedbackRequest;

import com.jhcs.booklore.domain.entity.Book;
import com.jhcs.booklore.domain.entity.Feedback;
import org.springframework.stereotype.Service;

@Service
public class FeedbackMapper {
    public static Feedback toFeedback( FeedbackRequest request) {
        return Feedback.builder()
                .note(request.note())
                .comment(request.comment())
                .book(Book.builder()
                        .id(request.bookId())
                        .build())
                .build();
    }
}
