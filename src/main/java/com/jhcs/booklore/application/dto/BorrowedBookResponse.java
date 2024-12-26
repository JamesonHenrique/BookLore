package com.jhcs.booklore.application.dto;

import lombok.*;


@Builder

public record BorrowedBookResponse(Long id, String title, String authorName, String isbn, double rate, boolean returned,
                                   boolean returnApproved) {}
