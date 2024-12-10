package com.jhcs.booklore.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Long> {
    @Query("""
            SELECT history
            FROM BookTransactionHistory history
            WHERE history.user.id = :id
            
            """)
    Page<BookTransactionHistory> findAllBorrowedBooks(
            Long id,
            Pageable pageable
                                                     );

    @Query("""
            SELECT history
            FROM BookTransactionHistory history
            WHERE history.book..owner.id = :id
            
            """)
    Page<BookTransactionHistory> findAllReturnedBooks(
            Long id,
            Pageable pageable
                                                     );

    @Query("""
            SELECT
            (COUNT (*) > 0) AS isBorrowed
            FROM BookTransactionHistory bookTransactionHistory
            WHERE bookTransactionHistory.userId = :userId
            AND bookTransactionHistory.book.id = :bookId
            AND bookTransactionHistory.returnApproved = false
            """)
    boolean isAlreadyBorrowedByUser( Long bookId, Long userId);

    @Query("""
            SELECT
            (COUNT (*) > 0) AS isBorrowed
            FROM BookTransactionHistory bookTransactionHistory
            WHERE bookTransactionHistory.book.id = :bookId
            AND bookTransactionHistory.returnApproved = false
            """)
    boolean isAlreadyBorrowed(Long bookId);
}
