package com.jhcs.booklore.web.mapper;

import com.jhcs.booklore.application.dto.BookRequest;
import com.jhcs.booklore.application.dto.BookResponse;
import com.jhcs.booklore.application.dto.BorrowedBookResponse;
import com.jhcs.booklore.domain.entity.Book;
import com.jhcs.booklore.infrastructure.file.FileUtils;
import com.jhcs.booklore.domain.entity.BookTransactionHistory;
import org.springframework.stereotype.Service;

@Service
public class BookMapper {
    public Book toBook(BookRequest request) {
        return Book.builder()
                .id(request.id())
                .title(request.title())
                .isbn(request.isbn())
                .authorName(request.authorName())
                .synopsis(request.synopsis())
                .archived(false)
                .shareable(request.shareable())

                .build();
    }

    public BookResponse toBookResponse(Book book) {

        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authorName(book.getAuthorName())
                .isbn(book.getIsbn())
                .synopsis(book.getSynopsis())
                .rate(book.getRate())
                .archived(book.isArchived())
                .shareable(book.isShareable())
                .owner(book.getOwner()
                        .fullName())
                .cover(FileUtils.readFileFromLocation(book.getBookCover()))
                .build();
    }


    public BorrowedBookResponse toBorrowedBookResponse(BookTransactionHistory bookTransactionHistory) {
        return BorrowedBookResponse.builder()
                .id(bookTransactionHistory.getId())
                .title(bookTransactionHistory.getBook()
                        .getTitle())
                .authorName(bookTransactionHistory.getBook()
                        .getAuthorName())
                .isbn(bookTransactionHistory.getBook()
                        .getIsbn())
                .rate(bookTransactionHistory.getBook()
                        .getRate())
                .returned(bookTransactionHistory.isReturned())
                .returnApproved(bookTransactionHistory.isReturnApproved())
                .build();
    }
}