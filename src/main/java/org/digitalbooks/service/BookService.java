package org.digitalbooks.service;

//import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import org.digitalbooks.entity.Book;
import org.digitalbooks.entity.User;
import org.digitalbooks.exception.UserServiceException;
import org.digitalbooks.repository.UserRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
public class BookService {
    private static final String BOOK_REPO_URL = "http://localhost:9090/api/v1/digitalbooks/";

    private final UserRepository userRepository;

    public static Book getBookDataForBookId(Long bookId) {
        String retrieveBookUrl = BOOK_REPO_URL +"books/" + bookId;
        ResponseEntity<Book> bookResponse = new RestTemplate().getForEntity(retrieveBookUrl, Book.class);
        return Objects.requireNonNull(bookResponse.getBody());
    }

    public List<Book> callBookServiceToGetAllBooks() {
        ResponseEntity<Book[]> allBooks = new RestTemplate().getForEntity(BOOK_REPO_URL + "books", Book[].class);
        return Arrays.stream(Objects.requireNonNull(allBooks.getBody())).collect(Collectors.toList());
    }

    public Long addBook(Long authorId, Book book) {
        User user = userRepository.findById(authorId).orElseThrow(() -> new UserServiceException("Book Creation Error: AuthorID is invalid"));
        if (!user.isAuthorUser()) throw new UserServiceException("Book Creation Error: User is not an author");

        String addBookUrl = BOOK_REPO_URL + authorId + "/books";
        ResponseEntity<Long> addedBook = new RestTemplate().postForEntity(addBookUrl, book, Long.class);
        return addedBook.getBody();
    }

    public Long updateBook(Long authorId, Long bookId, Book book) {
        //Check if user is valid
        User user = userRepository
                .findById(authorId)
                .orElseThrow(() -> new UserServiceException("Book Update Error: AuthorID is invalid"));
        //Check if user is Author; Can also be done by user.getRole().equals(Role.AUTHOR)
        if (!user.isAuthorUser()) throw new UserServiceException("Book Update Error: User is not an author");
        //Check if bookId is valid - AUTOMATICALLY DONE BY BookMS
        //Check if user is AUTHOR OF THIS BOOK
        searchBooksByAuthorId(authorId).stream()
                .map(Book::getBookId)
                .filter(bookId::equals)
                .findFirst()
                .orElseThrow(() -> new UserServiceException("Book Update Error: Author is trying to delete a book that is not his? Read Fahrenheit 451"));
        //Finally - Update the book
        String updateBookUrl = BOOK_REPO_URL + authorId + "/books/" + bookId;
        ResponseEntity<Long> updatedBook = new RestTemplate().exchange(updateBookUrl, HttpMethod.PUT, new HttpEntity<>(book), Long.class);
        return updatedBook.getBody();
    }

    public List<Book> searchBooksByAuthorId(Long authorId) {
        User user = userRepository.findById(authorId).orElseThrow(() -> new UserServiceException("Author Not Found"));
        if (!user.isAuthorUser()) throw new UserServiceException("Book Search Error: User is not an author");

        String targetUrl = BOOK_REPO_URL + authorId + "/books";
        ResponseEntity<Book[]> booksByAuthor = new RestTemplate().getForEntity(targetUrl, Book[].class);
        return List.of(Objects.requireNonNull(booksByAuthor.getBody()));
    }

    public List<Book> searchUsingQuery(String category, String title, String author, String price, String publisher) {
        String targetUrl = BOOK_REPO_URL + "search?category={category}&title={title}&price={price}&publisher={publisher}";
        HashMap<String, String> bookSearchRequestParameters = new HashMap<>();
        bookSearchRequestParameters.put("category", category);
        bookSearchRequestParameters.put("title", title);
        bookSearchRequestParameters.put("price", price);
        bookSearchRequestParameters.put("publisher", publisher);
        ResponseEntity<Book[]> allBooksWithoutAuthorSearch = new RestTemplate().getForEntity(targetUrl, Book[].class, bookSearchRequestParameters);
        List<Book> booksOfSameAuthor = userRepository.findByNameContainsIgnoreCaseAllIgnoreCase(author)
                .stream()
                .filter(User::isAuthorUser)
                .map(User::getId)
                .map(this::searchBooksByAuthorId)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        return Stream.concat(Arrays.stream(allBooksWithoutAuthorSearch.getBody()), booksOfSameAuthor.stream()).distinct().collect(Collectors.toList());
    }

    public Long toggleBookBlock(Long authorId, Long bookId, boolean block) {
        //Check if author is valid
        User user = userRepository.findById(authorId).orElseThrow(() -> new UserServiceException("Block Error: Invalid Author ID!"));
        if (!user.isAuthorUser()) throw new UserServiceException("Block Error: User is not Author");
        //Check if bookId is valid - AUTOMATICALLY DONE BY BookMS
        //Check if user is AUTHOR OF THIS BOOK
        searchBooksByAuthorId(authorId).stream()
                .map(Book::getBookId)
                .filter(bookId::equals)
                .findFirst()
                .orElseThrow(() -> new UserServiceException("Book Update Error: Author is trying to delete a book that is not his? Read Fahrenheit 451"));

        //Finally Send Toggle Book Request
        String targetUrl = BOOK_REPO_URL + authorId + "/books/" + bookId + "?block={block}";
        HashMap<String, Boolean> blockRequestParameter = new HashMap<>();
        blockRequestParameter.put("block", block);
        ResponseEntity<Long> toggleBookBlockResponse = new RestTemplate().getForEntity(targetUrl, Long.class, blockRequestParameter);
        return toggleBookBlockResponse.getBody();
    }
}
