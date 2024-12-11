package com.jhcs.booklore.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Builder
public record BookResponse(Long id, String title, String authorName, String isbn, String synopsis, String owner,
                           byte[] cover, double rate, boolean archived, boolean shareable) {}