package com.jhcs.booklore.book;

import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BorrowedBookResponse {
    private Long id;
    private String title;
    private String authorName;
    private String isbn;
    private double rate;
    private boolean returned;
    private boolean returnApproved;
}
