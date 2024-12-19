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
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;

    public Long save(BookRequest bookRequest, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Book book = bookMapper.toBook(bookRequest);
        book.setOwner(user);
        return bookRepository.save(book).getId();
    }

    public PageResponse<BookResponse> findAll(int size, int page, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());
        List<BookResponse> bookResponse = books.stream()
                .map(bookMapper::toBookResponse)
                .collect(Collectors.toList());
        return new PageResponse<>(bookResponse, books.getNumber(), books.getSize(), books.getTotalElements(), books.getTotalPages(), books.isFirst(), books.isLast());
    }

    public BookResponse findById(Long id) {
        return bookRepository.findById(id)
                .map(bookMapper::toBookResponse)
                .orElseThrow(() -> new EntityNotFoundException("Nenhum livro encontrado com o ID: " + id));
    }

    public PageResponse<BookResponse> findAllByOwner(int size, int page, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAll(BookSpecification.withOwnerId(user.getId()), pageable);
        List<BookResponse> bookResponse = books.stream()
                .map(bookMapper::toBookResponse)
                .collect(Collectors.toList());
        return new PageResponse<>(bookResponse, books.getNumber(), books.getSize(), books.getTotalElements(), books.getTotalPages(), books.isFirst(), books.isLast());
    }

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks( int size,  int page, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = transactionHistoryRepository.findAllBorrowedBooks(pageable, user.getId());
        List<BorrowedBookResponse> borrowedBookResponse = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .collect(Collectors.toList());
        return new PageResponse<>(borrowedBookResponse, allBorrowedBooks.getNumber(), allBorrowedBooks.getSize(), allBorrowedBooks.getTotalElements(), allBorrowedBooks.getTotalPages(), allBorrowedBooks.isFirst(), allBorrowedBooks.isLast());
    }

    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(@Positive @Max(100) int size, @Positive int page, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allReturnedBooks = transactionHistoryRepository.findAllReturnedBooks(pageable, user.getId());
        List<BorrowedBookResponse> borrowedBookResponse = allReturnedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .collect(Collectors.toList());
        return new PageResponse<>(borrowedBookResponse, allReturnedBooks.getNumber(), allReturnedBooks.getSize(), allReturnedBooks.getTotalElements(), allReturnedBooks.getTotalPages(), allReturnedBooks.isFirst(), allReturnedBooks.isLast());
    }

    public Long updateShareableStatus(Long id, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nenhum livro encontrado com o ID: " + id));
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("Você não pode atualizar o status de compartilhamento deste livro");
        }
        book.setShareable(!book.isShareable());
        bookRepository.save(book);
        return book.getId();
    }

    public Long updateArchivedStatus(Long id, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nenhum livro encontrado com o ID: " + id));
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("Você não pode atualizar o status de arquivamento deste livro");
        }
        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return book.getId();
    }

    public Long borrowBook(Long id, Authentication connectedUser) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nenhum livro encontrado com o ID: " + id));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("O livro solicitado não pode ser emprestado, pois está arquivado ou não é compartilhável");
        }
        User user = ((User) connectedUser.getPrincipal());
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("Você não pode emprestar seu próprio livro");
        }
        final boolean isAlreadyBorrowedByUser = transactionHistoryRepository.isAlreadyBorrowedByUser(id, user.getId());
        if (isAlreadyBorrowedByUser) {
            throw new OperationNotPermittedException("Você já emprestou este livro e ele ainda não foi devolvido ou a devolução não foi aprovada pelo proprietário");
        }

        final boolean isAlreadyBorrowedByOtherUser = transactionHistoryRepository.isAlreadyBorrowed(id);
        if (isAlreadyBorrowedByOtherUser) {
            throw new OperationNotPermittedException("O livro solicitado já está emprestado");
        }

        BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
                .user(user)
                .book(book)
                .returned(false)
                .returnApproved(false)
                .build();
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Long returnBorrowedBook(Long bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Nenhum livro encontrado com o ID: " + bookId));
        User user = ((User) connectedUser.getPrincipal());

        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("O livro solicitado não pode ser emprestado, pois está arquivado ou não é compartilhável");
        }
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("Você não pode emprestar ou devolver seu próprio livro");
        }
        BookTransactionHistory bookTransactionHistory = transactionHistoryRepository.findByBookIdAndUserId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("Você não emprestou este livro"));
        bookTransactionHistory.setReturned(true);
        transactionHistoryRepository.save(bookTransactionHistory);
        return bookTransactionHistory.getBook()
                .getId();
    }

    public Long approveReturnBorrowedBook(Long id, Authentication connectedUser) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nenhum livro encontrado com o ID: " + id));
        User user = ((User) connectedUser.getPrincipal());

        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("O livro solicitado não pode ser emprestado, pois está arquivado ou não é compartilhável");
        }
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("Você não pode emprestar ou devolver seu próprio livro");
        }
        BookTransactionHistory bookTransactionHistory = transactionHistoryRepository.findByBookIdAndOwnerId(id, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("O livro ainda não foi devolvido. Você não pode aprovar a devolução"));
        bookTransactionHistory.setReturnApproved(true);
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public void uploadBookCoverPicture(Long id, MultipartFile file, Authentication connectedUser) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nenhum livro encontrado com o ID: " + id));
        User user = ((User) connectedUser.getPrincipal());
        var bookCover = fileStorageService.saveFile(file, user.getId());
        book.setBookCover(bookCover);
        bookRepository.save(book);
    }
}