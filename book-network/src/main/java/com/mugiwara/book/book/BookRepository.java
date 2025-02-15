package com.mugiwara.book.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer>, JpaSpecificationExecutor<Book> {
    @Query("""
            SELECT book
            FROM Book book
            WHERE book.archived = false
            AND book.sharable = true
            """)
    Page<Book> findAllDisplayableBook(Pageable pageable, Integer userId);
//                AND book.owner.id != :userId it should be in line no 16



    @Query("""
        SELECT DISTINCT b.title FROM Book b
        WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
        ORDER BY b.title
    """)
    List<String> findByName(@Param("keyword") String keyword);

    @Query("""
            SELECT book
            FROM Book book
            WHERE book.archived = false
            AND book.sharable = true
            """)
    Page<Book> find(Pageable pageable);
}
