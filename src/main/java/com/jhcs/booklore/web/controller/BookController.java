package com.jhcs.booklore.web.controller;

import com.jhcs.booklore.application.dto.*;
import com.jhcs.booklore.application.service.BookService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("books")
@RestController
@RequiredArgsConstructor
@Tag(name = "Books", description = "Endpoints for managing books")
public class BookController {
    private final BookService bookService;

    @PostMapping
    public ResponseEntity<Long> saveBook(
            @Valid @RequestBody BookRequest request,
            Authentication connectedUser
                                        ) {
        return ResponseEntity.ok(bookService.save(request, connectedUser));
    }


    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> findBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.findById(id));
    }

    @GetMapping()
    public ResponseEntity<PageResponse<BookResponse>> findAllBooks(
            @RequestParam(defaultValue = "0", name = "page", required = false) @PositiveOrZero int page,
            @RequestParam(defaultValue = "10", name = "size", required = false) @Positive @Max(100) int size,
            Authentication connectedUser
                                                                  ) {

        return ResponseEntity.ok(bookService.findAll(size, page, connectedUser));
    }

    @GetMapping("/owner")
    public ResponseEntity<PageResponse<BookResponse>> findAllBooksByOwner(
            @RequestParam(defaultValue = "0", name = "page", required = false) @PositiveOrZero int page,
            @RequestParam(defaultValue = "10", name = "size", required = false) @Positive @Max(100) int size,
            Authentication connectedUser
                                                                         ) {

        return ResponseEntity.ok(bookService.findAllByOwner(size, page, connectedUser));
    }

    @GetMapping("/borrowed")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllBorrowedBooks(
            @RequestParam(defaultValue = "0", name = "page", required = false) @PositiveOrZero int page,
            @RequestParam(defaultValue = "10", name = "size", required = false) @Positive @Max(100) int size,
            Authentication connectedUser
                                                                                  ) {

        return ResponseEntity.ok(bookService.findAllBorrowedBooks(size, page, connectedUser));
    }

    @GetMapping("/returned")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllReturnedBooks(
            @RequestParam(defaultValue = "0", name = "page", required = false) @PositiveOrZero int page,
            @RequestParam(defaultValue = "10", name = "size", required = false) @Positive @Max(100) int size,
            Authentication connectedUser
                                                                                  ) {

        return ResponseEntity.ok(bookService.findAllReturnedBooks(size, page, connectedUser));
    }
    @PatchMapping("/shareable/{id}")
    public ResponseEntity<Long> updateShareableStatus(
            @PathVariable Long id,

            Authentication connectedUser
                                              ) {
        return ResponseEntity.ok(bookService.updateShareableStatus(id, connectedUser));
    }
    @PatchMapping("/archived/{id}")
    public ResponseEntity<Long> updateArchivedStatus(
            @PathVariable Long id,

            Authentication connectedUser
                                                     ) {
        return ResponseEntity.ok(bookService.updateArchivedStatus(id, connectedUser));
    }
    @PostMapping("/borrow/{id}")
    public ResponseEntity<Long> borrowBook(
            @PathVariable Long id,
            Authentication connectedUser
                                          ) {
        return ResponseEntity.ok(bookService.borrowBook(id, connectedUser));
    }




    @PatchMapping("/borrow/return/{id}")
    public ResponseEntity<Long> returnBorrowBook(
            @PathVariable Long id,
            Authentication connectedUser
                                                ) {
        return ResponseEntity.ok(bookService.returnBorrowedBook(id, connectedUser));
    }

    @PatchMapping("/borrow/return/approve/{id}")
    public ResponseEntity<Long> approveReturnBorrowBook(
            @PathVariable Long id,
            Authentication connectedUser
                                                       ) {
        return ResponseEntity.ok(bookService.approveReturnBorrowedBook(id, connectedUser));
    }

    @PostMapping(value = "/cover/{id}", consumes = "multipart/form-data")
    public ResponseEntity<Long> uploadBookCover(
            @PathVariable Long id,
            @Parameter(description = "Book cover image") @RequestPart("file") MultipartFile file,

            Authentication connectedUser
                                               ) {
        bookService.uploadBookCoverPicture(id, file, connectedUser);
        return ResponseEntity.accepted()
                .build();
    }
}
