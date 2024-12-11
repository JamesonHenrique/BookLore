package com.jhcs.booklore.application.service;

import com.jhcs.booklore.application.dto.BookRequest;
import com.jhcs.booklore.application.dto.BookResponse;
import com.jhcs.booklore.application.dto.BorrowedBookResponse;
import com.jhcs.booklore.application.dto.PageResponse;
import com.jhcs.booklore.domain.entity.Book;
import com.jhcs.booklore.domain.repository.BookRepository;
import com.jhcs.booklore.domain.entity.User;
import com.jhcs.booklore.domain.service.BookSpecification;
import com.jhcs.booklore.infrastructure.handler.exception.OperationNotPermittedException;
import com.jhcs.booklore.infrastructure.file.FileStorageService;
import com.jhcs.booklore.domain.entity.BookTransactionHistory;
import com.jhcs.booklore.domain.repository.BookTransactionHistoryRepository;
import com.jhcs.booklore.web.mapper.BookMapper;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepository transactionHistoryRepository;
    private final FileStorageService fileStorageService;

    public Long save(
            BookRequest bookRequest,
            Authentication connectedUser
                    ) {

        User user = (User) connectedUser.getPrincipal();
        Book book = bookMapper.toBook(bookRequest);
        book.setOwner(user);

        return bookRepository.save(book)
                .getId();
    }


    public PageResponse<BookResponse> findAll(
            int size,
            int page,
            Authentication connectedUser
                                             ) {

        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate")
                .descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());
        List<BookResponse> bookResponse = books.stream()
                .map(bookMapper::toBookResponse)
                .collect(Collectors.toList());
        return new PageResponse<>(bookResponse, books.getNumber(), books.getSize(), books.getTotalElements(), books.getTotalPages(), books.isFirst(), books.isLast());

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
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate")
                .descending());
        Page<Book> books = bookRepository.findAll(BookSpecification.withOwnerId(user.getId()), pageable);
        List<BookResponse> bookResponse = books.stream()
                .map(bookMapper::toBookResponse)
                .collect(Collectors.toList());
        return new PageResponse<>(bookResponse, books.getNumber(), books.getSize(), books.getTotalElements(), books.getTotalPages(), books.isFirst(), books.isLast());
    }

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(
            @Positive @Max(100) int size,
            @Positive int page,
            Authentication connectedUser
                                                                  ) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate")
                .descending());
        Page<BookTransactionHistory> allBorrewedBooks = transactionHistoryRepository.findAllBorrowedBooks(pageable, user.getId());
        List<BorrowedBookResponse> borrowedBookResponse = allBorrewedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .collect(Collectors.toList());
        return new PageResponse<>(borrowedBookResponse, allBorrewedBooks.getNumber(), allBorrewedBooks.getSize(), allBorrewedBooks.getTotalElements(), allBorrewedBooks.getTotalPages(), allBorrewedBooks.isFirst(), allBorrewedBooks.isLast());
    }

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(
            @Positive @Max(100) int size,
            @Positive int page,
            Authentication connectedUser
                                                                  ) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate")
                .descending());
        Page<BookTransactionHistory> allBorrewedBooks = transactionHistoryRepository.findAllReturnedBooks(pageable, user.getId());
        List<BorrowedBookResponse> borrowedBookResponse = allBorrewedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .collect(Collectors.toList());
        return new PageResponse<>(borrowedBookResponse, allBorrewedBooks.getNumber(), allBorrewedBooks.getSize(), allBorrewedBooks.getTotalElements(), allBorrewedBooks.getTotalPages(), allBorrewedBooks.isFirst(), allBorrewedBooks.isLast());
    }

    public Long updateShareableStatus(
            Long id,
            Authentication connectedUser
                                     ) {
        User user = (User) connectedUser.getPrincipal();
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No book found with id " + id));
        if (!Objects.equals(book.getOwner()
                .getId(), user.getId())) {
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
        if (!Objects.equals(book.getOwner()
                .getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot update the archived status of this book");
        }
        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return book.getId();

    }


    public Long borrowBook(
            Long id,
            Authentication connectedUser
                          ) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + id));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("The requested book cannot be borrowed since it is archived or " + "not shareable");
        }
        User user = ((User) connectedUser.getPrincipal());
        if (Objects.equals(book.getOwner()
                .getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow your own book");
        }
        final boolean isAlreadyBorrowedByUser = transactionHistoryRepository.isAlreadyBorrowedByUser(id, user.getId());
        if (isAlreadyBorrowedByUser) {
            throw new OperationNotPermittedException("You already borrowed this book and it is still not returned or " + "the return is not approved by the owner");
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
        return transactionHistoryRepository.save(bookTransactionHistory)
                .getId();

    }

    public Long returnBorrowedBook(
            Long bookId,
            Authentication connectedUser
                                  ) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
        User user = ((User) connectedUser.getPrincipal());

        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("The requested book cannot be borrowed since it is archived or " + "not shareable");
        }
        ;
        if (Objects.equals(book.getOwner()
                .getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow or return your own book");
        }
        BookTransactionHistory bookTransactionHistory = transactionHistoryRepository.findByBookIdAndUserId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("You did not borrow this book"));
        bookTransactionHistory.setReturned(true);
        transactionHistoryRepository.save(bookTransactionHistory);
        return bookTransactionHistory.getId();

    }


    public Long approveReturnBorrowedBook(
            Long id,
            Authentication connectedUser
                                         ) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + id));
        User user = ((User) connectedUser.getPrincipal());

        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("The requested book cannot be borrowed since it is archived or " + "not shareable");
        }
        ;
        if (Objects.equals(book.getOwner()
                .getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow or return your own book");
        }
        BookTransactionHistory bookTransactionHistory = transactionHistoryRepository.findByBookIdAndOwnerId(id, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("The book is not returned yet. You cannot approve its return"));
        bookTransactionHistory.setReturnApproved(true);
        return transactionHistoryRepository.save(bookTransactionHistory).getId();

    }

    public void uploadBookCoverPicture(
            Long id,
            MultipartFile file,
            Authentication connectedUser
                                         ) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + id));
        User user = ((User) connectedUser.getPrincipal());
        var bookCover = fileStorageService.saveFile(file,user.getId());
        book.setBookCover(bookCover);
        bookRepository.save(book);

    }
}

