package com.jhcs.booklore.book;

import com.jhcs.booklore.commun.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("books")
@RestController
@RequiredArgsConstructor
@Tag( name = "Books", description = "Endpoints for managing books")
public class BookController {
    private final BookService bookService;
    @PostMapping
    public ResponseEntity<Long> saveBook(@Valid @RequestBody BookRequest request, Authentication connectedUser) {
        return ResponseEntity.ok(bookService.save(request, connectedUser));
    }
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> findBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.findById(id));
    }
    @GetMapping()
    public ResponseEntity<PageResponse<BookResponse>> findAllBooks(
            @RequestParam(defaultValue = "0",name = "page", required = false) @Positive int page,
            @RequestParam(defaultValue = "10",name = "size", required = false)
            @Positive @Max(100) int size, Authentication connectedUser
                                                                  ) {

        return ResponseEntity.ok(bookService.findAll(size, page, connectedUser));
    }

}
