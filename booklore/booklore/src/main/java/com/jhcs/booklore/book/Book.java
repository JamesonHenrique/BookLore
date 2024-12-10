package com.jhcs.booklore.book;

import com.jhcs.booklore.commun.BaseEntity;
import com.jhcs.booklore.entity.Feedback;
import com.jhcs.booklore.entity.User;
import com.jhcs.booklore.history.BookTransactionHistory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


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
    private List<BookTransactionHistory> histories;
    @Transient
    public double getRate() {
        if(feedbacks == null || feedbacks.isEmpty()) {
            return 0.0;
        }
        var rate = feedbacks.stream().mapToDouble(Feedback::getNote).average().orElse(0.0);
        var roundedRate = Math.round(rate * 10) / 10.0;
        return roundedRate;
    }
}
