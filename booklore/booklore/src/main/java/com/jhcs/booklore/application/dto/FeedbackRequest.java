package com.jhcs.booklore.application.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.NonNull;

@Builder
public record FeedbackRequest(
        @Positive(message = "200") @Min(message = "201", value = 0) @Max(message = "202", value = 5) Double note,
        @NotEmpty(message = "203") @NotNull(message = "203") @NotBlank(message = "203") String comment,
        @NotNull(message = "204") Long bookId) {}