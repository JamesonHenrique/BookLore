package com.jhcs.booklore.application.service;

import com.jhcs.booklore.application.dto.FeedbackRequest;
import com.jhcs.booklore.application.dto.FeedbackResponse;
import com.jhcs.booklore.application.dto.PageResponse;
import com.jhcs.booklore.domain.entity.Book;
import com.jhcs.booklore.domain.entity.Feedback;
import com.jhcs.booklore.domain.entity.User;
import com.jhcs.booklore.domain.repository.BookRepository;
import com.jhcs.booklore.domain.repository.FeedbackRepository;
import com.jhcs.booklore.infrastructure.handler.exception.OperationNotPermittedException;
import com.jhcs.booklore.web.controller.FeedBackController;
import com.jhcs.booklore.web.mapper.FeedbackMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedBackService {
    private final BookRepository bookRepository;
    private final FeedbackMapper feedbackMapper;
    private final FeedbackRepository feedbackRepository;

    public Long save(
            FeedbackRequest request,
            Authentication connectedUser
                    ) {
        User user = (User) connectedUser.getPrincipal(); Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + request.bookId()));


        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("You cannot give a feedback on an archived or unshareable book");
        } ; if (Objects.equals(book.getOwner()
                .getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot give a feedback on your own book");
        } ; Feedback feedback = feedbackMapper.toFeedback(request); return feedbackRepository.save(feedback)
                .getId();

    }

    public PageResponse<FeedbackResponse> findAllFeedbacksByBook(
            Long id,
            int size,
           int page, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal(); Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + id));
        Pageable pageable = PageRequest.of(page, size);
        Page<Feedback> feedbacks = feedbackRepository.findAllByBookId(id, pageable);
        List<FeedbackResponse> feedbackResponses = feedbacks.stream()
                .map(f -> feedbackMapper.toFeedbackResponse(f, user.getId()))
                .toList();
        return new PageResponse<>(
                feedbackResponses,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast()
        );

    }
}

