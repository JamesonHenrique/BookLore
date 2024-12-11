package com.jhcs.booklore.web.controller;

import com.jhcs.booklore.application.dto.FeedbackRequest;
import com.jhcs.booklore.application.service.FeedBackService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
