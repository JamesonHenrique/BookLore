package com.jhcs.booklore.web.controller;

import com.jhcs.booklore.application.dto.FeedbackRequest;
import com.jhcs.booklore.application.dto.PageResponse;
import com.jhcs.booklore.application.service.FeedBackService;
import com.jhcs.booklore.application.dto.FeedbackResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("feedback")
@RequiredArgsConstructor
@Tag(name = "Feedback", description = "Endpoints for feedback")
public class FeedBackController {
    private final FeedBackService feedbackService;
    @PostMapping
    public ResponseEntity<Long> saveFeedback(
            @Valid
            @RequestBody FeedbackRequest request,
            Authentication connectedUser
                                      ) {
        return ResponseEntity.ok(feedbackService.save(request, connectedUser));
    }
    @GetMapping("/book/{id}")
    public ResponseEntity<PageResponse<FeedbackResponse>> findAllFeedbacksByBook(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0", name = "page", required = false) @Positive int page,
            @RequestParam(defaultValue = "10", name = "size", required = false) @Positive @Max(100) int size,
            Authentication connectedUser
                                                                               ) {
        return ResponseEntity.ok(feedbackService.findAllFeedbacksByBook(id, size, page, connectedUser));
    }
}
