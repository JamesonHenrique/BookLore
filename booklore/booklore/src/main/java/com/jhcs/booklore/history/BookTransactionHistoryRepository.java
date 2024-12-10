package com.jhcs.booklore.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistoryRepository, Long> {
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
}
