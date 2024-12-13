package com.jhcs.booklore.application.dto;


import lombok.Builder;



@Builder
public record BookResponse(Long id, String title, String authorName, String isbn, String synopsis, String owner,
                           byte[] cover, double rate, boolean archived, boolean shareable) {}