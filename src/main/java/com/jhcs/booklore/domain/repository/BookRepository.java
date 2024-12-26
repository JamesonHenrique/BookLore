package com.jhcs.booklore.domain.repository;

import com.jhcs.booklore.domain.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, Long>,
                                        JpaSpecificationExecutor<Book> {

    @Query("""
            SELECT book
            FROM Book book
            WHERE book.archived = false
            AND book.shareable = true
            AND book.createdBy != :id
            """)
    Page<Book> findAllDisplayableBooks(Pageable pageable, Long id);


}
