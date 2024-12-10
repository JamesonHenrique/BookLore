package com.jhcs.booklore.book;

import com.jhcs.booklore.commun.PageResponse;
import com.jhcs.booklore.entity.User;
import com.jhcs.booklore.exception.OperationNotPermittedException;
import com.jhcs.booklore.history.BookTransactionHistory;
import com.jhcs.booklore.history.BookTransactionHistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepository transactionHistoryRepository;

    public Long save(BookRequest bookRequest, Authentication connectedUser) {

        User user = (User) connectedUser.getPrincipal();
        Book book = bookMapper.toBook(bookRequest);
        book.setOwner(user);

        return bookRepository.save( book ).getId();
    }



    public PageResponse<BookResponse> findAll(int size, int page, Authentication connectedUser) {

        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable,user.getId());
        List<BookResponse> bookResponse = books.stream()
                .map(bookMapper::toBookResponse)
                .collect(Collectors.toList());
        return new PageResponse<>(
                bookResponse,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast());

    }

    public BookResponse findById(Long id) {
        return bookRepository.findById(id)
                .map(bookMapper::toBookResponse)
                .orElseThrow(() -> new EntityNotFoundException("Nob book found with id " + id));

    }

    public PageResponse<BookResponse> findAllByOwner(
            int size,
           int page,
            Authentication connectedUser
                                                    ) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAll(BookSpecification.withOwnerId(user.getId()), pageable);
        List<BookResponse> bookResponse = books.stream()
                .map(bookMapper::toBookResponse)
                .collect(Collectors.toList());
        return new PageResponse<>(
                bookResponse,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast());
    }

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(
            @Positive @Max(100) int size,
            @Positive int page,
            Authentication connectedUser
                                                          ) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrewedBooks = transactionHistoryRepository.findAllBorrowedBooks(user.getId(), pageable);
        List<BorrowedBookResponse> borrowedBookResponse = allBorrewedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .collect(Collectors.toList());
        return new PageResponse<>(
                borrowedBookResponse,
                allBorrewedBooks.getNumber(),
                allBorrewedBooks.getSize(),
                allBorrewedBooks.getTotalElements(),
                allBorrewedBooks.getTotalPages(),
                allBorrewedBooks.isFirst(),
                allBorrewedBooks.isLast());
    }

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(
            @Positive @Max(100) int size,
            @Positive int page,
            Authentication connectedUser
                                                                  ) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrewedBooks = transactionHistoryRepository.findAllReturnedBooks(user.getId(), pageable);
        List<BorrowedBookResponse> borrowedBookResponse = allBorrewedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .collect(Collectors.toList());
        return new PageResponse<>(
                borrowedBookResponse,
                allBorrewedBooks.getNumber(),
                allBorrewedBooks.getSize(),
                allBorrewedBooks.getTotalElements(),
                allBorrewedBooks.getTotalPages(),
                allBorrewedBooks.isFirst(),
                allBorrewedBooks.isLast());
    }

    public Long updateShareableStatus(
            Long id,
            Authentication connectedUser
                                        ) {
        User user = (User) connectedUser.getPrincipal();
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No book found with id " + id));
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot update the shareable status of this book");
        }
        book.setShareable(!book.isShareable());
        bookRepository.save(book);
        return book.getId();

    }
    public Long updateArchivedStatus(
            Long id,
            Authentication connectedUser
                                     ) {
        User user = (User) connectedUser.getPrincipal();
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No book found with id " + id));
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot update the archived status of this book");
        }
        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return book.getId();

    }


    public Long borrowBook(Long id, Authentication connectedUser) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + id));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("The requested book cannot be borrowed since it is archived or not shareable");
        }
        User user = ((User) connectedUser.getPrincipal());
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow your own book");
        }
        final boolean isAlreadyBorrowedByUser = transactionHistoryRepository.isAlreadyBorrowedByUser(id, user.getId());
        if (isAlreadyBorrowedByUser) {
            throw new OperationNotPermittedException("You already borrowed this book and it is still not returned or the return is not approved by the owner");
        }

        final boolean isAlreadyBorrowedByOtherUser = transactionHistoryRepository.isAlreadyBorrowed(id);
        if (isAlreadyBorrowedByOtherUser) {
            throw new OperationNotPermittedException("The requested book is already borrowed");
        }

        BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
                .user(user)
                .book(book)
                .returned(false)
                .returnApproved(false)
                .build();
        return transactionHistoryRepository.save(bookTransactionHistory).getId();

    }

}

