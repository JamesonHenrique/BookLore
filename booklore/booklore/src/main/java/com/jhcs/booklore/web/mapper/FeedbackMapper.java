package com.jhcs.booklore.web.mapper;
import com.jhcs.booklore.application.dto.FeedbackRequest;

import com.jhcs.booklore.application.dto.FeedbackResponse;
import com.jhcs.booklore.domain.entity.Book;
import com.jhcs.booklore.domain.entity.Feedback;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FeedbackMapper {
    public Feedback toFeedback( FeedbackRequest request) {
        return Feedback.builder()
                .note(request.note())
                .comment(request.comment())
                .book(Book.builder()
                        .id(request.bookId())
                        .build())
                .build();
    }
 public FeedbackResponse toFeedbackResponse(Feedback feedback, Long id) {
        return FeedbackResponse.builder()
                .note(feedback.getNote())
                .comment(feedback.getComment())
                .ownFeedback(Objects.equals(feedback.getCreatedBy(), id))
                .build();
    }
}
