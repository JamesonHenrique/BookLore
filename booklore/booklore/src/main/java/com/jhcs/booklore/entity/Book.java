package com.jhcs.booklore.entity;

import com.jhcs.booklore.commun.BaseEntity;
import com.jhcs.booklore.history.BookTransactionHistory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


import java.time.LocalDateTime;
import java.util.List;

@Entity
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Book extends BaseEntity {

    private String title;
    private String authorName;
    private String synopsis;
    private String bookCover;
    private String isbn;
    private boolean archived;
    private boolean shareable;
    @ManyToOne()
    @JoinColumn(name = "owner_id")
    private User owner;
    @OneToMany(mappedBy = "book")
    private List<Feedback> feedbacks;
    @OneToMany(mappedBy = "book")
    private List<BookTransactionHistory
            > histories;
}
