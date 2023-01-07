package org.digitalbooks.service;

//import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import org.digitalbooks.entity.Book;
import org.digitalbooks.entity.BookSubscription;
import org.digitalbooks.entity.Subscription;
import org.digitalbooks.entity.User;
import org.digitalbooks.exception.UserServiceException;
import org.digitalbooks.repository.SubscriberRepository;
import org.digitalbooks.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.digitalbooks.service.BookService.getBookDataForBookId;

@Service
@Transactional
@RequiredArgsConstructor
public class SubscriptionService {
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final SubscriberRepository subscriberRepository;

    public Long subscribeToBook(Long userId, Long bookId) {
        User user = checkIfUserAndBookAreValid(userId, bookId);
        user.getSubscriptions().stream()
                .map(Subscription::getBookId)
                .filter(bookId::equals)
                .findFirst()
                .ifPresent(x -> {
                    throw new UserServiceException("Subscribe Error: Subscription already Present");
                });
        Subscription newSubscription = subscriberRepository.save(Subscription.builder().bookId(bookId).subscriptionDate(LocalDate.now()).build());
        user.getSubscriptions().add(newSubscription);
        return (long) user.getSubscriptions().size();
    }

    public Long unSubscribeToBook(Long userId, Long bookId) {
        User user = checkIfUserAndBookAreValid(userId, bookId);
        user.getSubscriptions().stream()
                .filter(subscription -> bookId.equals(subscription.getBookId()))
                .findFirst()
                .ifPresentOrElse(o -> {
                    LocalDate subscriptionDatePlus24 = o.getSubscriptionDate().plusDays(1);//+24hrs
                    if (!LocalDate.now().isAfter(subscriptionDatePlus24)) {
                        user.getSubscriptions().remove(o);
                        subscriberRepository.delete(o);
                    }else throw new UserServiceException("Subscription Error: Subscription cannot be cancelled after 24 hrs");
                }, () -> {
                    throw new UserServiceException("Subscription Error: Subscription is not present");
                });
        return (long) user.getSubscriptions().size();
    }

    public List<BookSubscription> retrieveSubscribedBooksForUser(Long userId) {
        User user = checkIfUserIsValid(userId);
        return user.getSubscriptions().stream()
                .map(subscription -> {
                    Long subscriptionId = subscription.getBookId();
                    Book book = getBookDataForBookId(subscriptionId);
                    return new BookSubscription(book, subscription);
                })
                .collect(Collectors.toList());
    }

    private User checkIfUserAndBookAreValid(Long userId, Long bookId) {
        getBookDataForBookId(bookId);// Just check if book exists
        return checkIfUserIsValid(userId);
    }

    private User checkIfUserIsValid(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new UserServiceException("Subscriber Error: User Not Available"));
    }
}
