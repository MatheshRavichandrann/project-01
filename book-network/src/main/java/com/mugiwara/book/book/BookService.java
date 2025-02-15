package com.mugiwara.book.book;

import com.mugiwara.book.common.PageResponse;
import com.mugiwara.book.exception.OperationNotPermittedException;
import com.mugiwara.book.file.FileStorageService;
import com.mugiwara.book.history.BookTransactionHistory;
import com.mugiwara.book.history.BookTransactionHistoryRepository;
import com.mugiwara.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.util.*;

import static com.mugiwara.book.book.BookSpecification.withOwnerId;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookTransactionHistoryRepository transactionHistoryRepository;
    private final FileStorageService fileStorageService;

    public Integer save(BookRequest request, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Book book = bookMapper.toBook(request);
        book.setOwner(user);
        return bookRepository.save(book).getId();
    }

    public BookResponse findById(Integer bookId) {
        return bookRepository.findById(bookId)
                .map(bookMapper::toBookResponse)
                .orElseThrow(() -> new EntityNotFoundException("No book found with the ID: " + bookId));
    }

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBook(pageable, user.getId());
        List<BookResponse> bookResponses = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAll(withOwnerId(user.getId()), pageable);
        List<BookResponse> bookResponse = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponse,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = transactionHistoryRepository.findAllBorrowedBooks(pageable, user.getId());
        List<BorrowedBookResponse> bookResponse = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponse,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );
    }


    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = transactionHistoryRepository.findAllReturnedBooks(pageable, user.getId());
        List<BorrowedBookResponse> bookResponse = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponse,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );
    }

    public Integer updateShareableStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with the ID: " + bookId));
        User user = (User) connectedUser.getPrincipal();
        if (!Objects.equals(book.getOwner().getId(), user.getId())) { // book.getOwner().getBooks()
            throw new OperationNotPermittedException("You cannot update others books sharable status");
        }

        book.setSharable(!book.isSharable());
        bookRepository.save(book);
        return bookId;
    }

    public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with the ID: " + bookId));
        User user = (User) connectedUser.getPrincipal();
        if (!Objects.equals(book.getOwner().getId(), user.getId())) { // book.getOwner().getBooks()
            throw new OperationNotPermittedException("You cannot update others books archived status");
        }

        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return bookId;

    }

    public Integer borrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No Book found with id: " + bookId));
        if (book.isArchived() || !book.isSharable()) {
            throw new OperationNotPermittedException("The Requested book cannot be borrowed since, it is archived or not sharable");
        }
        User user = (User) connectedUser.getPrincipal();
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow yours own book");
        }
        final boolean isAlreadyBorrowed = transactionHistoryRepository.isAlreadyBorrowedByUser(bookId, user.getId());
        if (isAlreadyBorrowed) {
            throw new OperationNotPermittedException("The Requested book is already borrowed");
        }
        BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
                .user(user)
                .book(book)
                .returned(false)
                .returnApproved(false)
                .build();
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }


    public Integer returnBorrowBook(Integer bookId, Authentication connectedUser) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with the ID: " + bookId));
        if (book.isArchived() || !book.isSharable()) {
            throw new OperationNotPermittedException("The Requested book cannot be borrowed since, it is archived or not sharable");
        }
        User user = (User) connectedUser.getPrincipal();
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow or return yours own book");
        }
        BookTransactionHistory bookTransactionHistory = transactionHistoryRepository.findByBookIdAndUserId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("You did not borrow this book"));
        bookTransactionHistory.setReturned(true);
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Integer approveReturnBorrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with the ID: " + bookId));
        if (book.isArchived() || !book.isSharable()) {
            throw new OperationNotPermittedException("The Requested book cannot be borrowed since, it is archived or not sharable");
        }
        User user = (User) connectedUser.getPrincipal();
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot return a book that you do not own");
        }
        BookTransactionHistory bookTransactionHistory = transactionHistoryRepository.findByBookIdAndOwnerId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("The book is not returned yet, You cannot approve its return"));
        bookTransactionHistory.setReturnApproved(true);
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Integer bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with the ID: " + bookId));
        User user = (User) connectedUser.getPrincipal();
        var bookCover = fileStorageService.saveFile(file, user.getId());
        book.setBookCover(bookCover);
        bookRepository.save(book);
    }

    public List<String> getListOfSuggestions(String keyword) {
        List<String> result = bookRepository.findByName(keyword);
        System.out.println("Suggestions: " + result);
        return !result.isEmpty() ? result : List.of("No suggestions found");
    }

    /*public List<Integer> addAllBooks(Authentication connectedUser) {
//        User user = new User();
        User user = ((User) connectedUser.getPrincipal());
        Random random = new SecureRandom
        List<Integer> list = null;


        for (int i = 0; i < 15; i++) {
            Book book = Book.builder()
                    .archived(false)
                    .sharable(true)
                    .isbn("1")
                    .title("A")
                    .synopsis("B")
                    .authorName("A")
                    .build();
            book.setOwner(user);
            list = Collections.singletonList(bookRepository.save(book).getId());
//            list = Collections.singletonList(bookRepository.save(book));
        }
        return list;
    }*/

    public List<Integer> addAllBooks(Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Random random = new SecureRandom();
        List<Integer> list = new ArrayList<>();

        String[] titles = {"Java Basics", "Spring Boot Guide", "Microservices Architecture", "Effective Java", "Cloud Computing",
                "Clean Code", "Kubernetes Essentials", "Docker Mastery", "AI for Developers", "Design Patterns in Java",
                "WebSocket Programming", "Modern Software Engineering", "The DevOps Handbook", "Full-Stack Development", "Cybersecurity Fundamentals"};

        String[] authors = {"Joshua Bloch", "Martin Fowler", "Robert C. Martin", "Sam Newman", "Eric Evans",
                "Kent Beck", "Chris Richardson", "Gene Kim", "Andrew Ng", "Sandi Metz",
                "Adam Bien", "Neal Ford", "Mark Richards", "Vaughn Vernon", "Bruce Schneier"};

        String[] synopsisList = {"A deep dive into Java programming", "Step-by-step Spring Boot guide", "Mastering microservices",
                "Best practices in Java development", "Understanding cloud technologies",
                "How to write clean and maintainable code", "A practical guide to Kubernetes",
                "Master Docker containers", "AI concepts for developers", "Learn design patterns",
                "WebSocket communication essentials", "Modern software development practices",
                "The ultimate DevOps guide", "Become a full-stack expert", "Essential cybersecurity principles"};

        for (int i = 0; i < 15; i++) {
            Book book = Book.builder()
                    .archived(false) // Always false
                    .sharable(true) // Always true
                    .isbn(UUID.randomUUID().toString().substring(0, 13)) // Generate random ISBN
                    .title(titles[i]) // Use unique titles from array
                    .synopsis(synopsisList[i]) // Use unique synopsis from array
                    .authorName(authors[i]) // Use unique author from array
                    .build();
            book.setOwner(user);
            list.add(bookRepository.save(book).getId()); // Store each generated book ID
        }

        return list;
    }

    public PageResponse<BookResponse> findAllBook(Authentication connectedUser ,int page, int size) {
       /* Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.find(pageable);
        List<BookResponse> bookResponses = books.stream()
                .map(bookMapper::toBookResponsePrivate)
                .toList();
        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );*/

        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBook(pageable, user.getId());
        List<BookResponse> bookResponses = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }
}