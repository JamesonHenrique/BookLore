package com.jhcs.booklore.application.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BookRequest(Long id, @NotEmpty(message = "100") String title,
                          @NotNull(message = "101") @NotEmpty(message = "101") String authorName,
                          @NotNull(message = "102") @NotEmpty(message = "102") String isbn,
                          @NotNull(message = "103") @NotEmpty(message = "103") String synopsis, boolean shareable) {}