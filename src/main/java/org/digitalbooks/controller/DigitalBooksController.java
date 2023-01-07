package org.digitalbooks.controller;

import lombok.RequiredArgsConstructor;
import org.digitalbooks.entity.Book;
import org.digitalbooks.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/digitalbooks/books/")
@RequiredArgsConstructor
@CrossOrigin("*")
public class DigitalBooksController {
    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<Book>> retrieveAllBooks() {
        return ResponseEntity.ok(bookService.callBookServiceToGetAllBooks());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchQuery(@RequestParam String category, @RequestParam String title, @RequestParam String author, @RequestParam String price, @RequestParam String publisher) {
        return ResponseEntity.ok(bookService.searchUsingQuery(category, title, author, price, publisher));
    }

    @GetMapping("/{authorId}")
    public ResponseEntity<List<Book>> searchBooksByAuthorId(@PathVariable Long authorId) {
        return ResponseEntity.ok(bookService.searchBooksByAuthorId(authorId));
    }
}
