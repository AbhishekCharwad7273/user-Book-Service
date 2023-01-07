package org.digitalbooks.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.digitalbooks.entity.BookSubscription;
import org.digitalbooks.entity.User;
import org.digitalbooks.exception.UserServiceException;
import org.digitalbooks.service.SubscriptionService;
import org.digitalbooks.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/digitalbooks/user/")
@RequiredArgsConstructor
@SecurityRequirement(name = "jwt_token_security")
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;
    private final SubscriptionService subscriptionService;

    @GetMapping("/subscribe/{bookId}/{userId}")
    public ResponseEntity<Long> subscribeUserToBook(@PathVariable Long bookId, @PathVariable Long userId) {
        Long sizeOfSubscription = subscriptionService.subscribeToBook(userId, bookId);
        return ResponseEntity.ok(sizeOfSubscription);
    }

    @GetMapping("/unsubscribe/{bookId}/{userId}")
    public ResponseEntity<Long> unSubscribeUserToBook(@PathVariable Long bookId, @PathVariable Long userId) {
        Long sizeOfSubscription = subscriptionService.unSubscribeToBook(userId, bookId);
        return ResponseEntity.ok(sizeOfSubscription);
    }

    @GetMapping("/subscribe/{userId}")
    public ResponseEntity<List<BookSubscription>> retrieveSubscribedBookList(@PathVariable Long userId) {
        List<BookSubscription> sizeOfSubscription = subscriptionService.retrieveSubscribedBooksForUser(userId);
        return ResponseEntity.ok(sizeOfSubscription);
    }

    @CrossOrigin
    @GetMapping("/readers/{emailId}/books/{subscriptionId}")
    public ResponseEntity<BookSubscription> fetchSubscribedBookData(@PathVariable String emailId,@PathVariable Long subscriptionId){
        User user = userService.retrieveUserByEmail(emailId).orElseThrow(() -> new UserServiceException("Read Data Error: Email is Invalid"));
        BookSubscription subscription = subscriptionService.retrieveSubscribedBooksForUser(user.getId()).stream()
                .filter(bookSubscription -> Objects.equals(bookSubscription.getSubscription().getId(), subscriptionId))
                .findFirst()
                .orElseThrow(() -> new UserServiceException("Read Data Error: Subscription Id is invalid"));
        return ResponseEntity.ok(subscription);
    }

    @CrossOrigin
    @GetMapping("/readers/{emailId}/books/{subscriptionId}/read")
    public ResponseEntity<String> fetchSubscribedBookContent(@PathVariable String emailId,@PathVariable Long subscriptionId){
        User user = userService.retrieveUserByEmail(emailId).orElseThrow(() -> new UserServiceException("Read Data Error: Email is Invalid"));
        BookSubscription subscription = subscriptionService.retrieveSubscribedBooksForUser(user.getId()).stream()
                .filter(bookSubscription -> Objects.equals(bookSubscription.getSubscription().getId(), subscriptionId))
                .findFirst()
                .orElseThrow(() -> new UserServiceException("Read Data Error: Subscription Id is invalid"));
        return ResponseEntity.ok(subscription.getBook().getContent());
    }
}
