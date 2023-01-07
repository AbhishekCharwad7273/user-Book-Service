package org.digitalbooks.entity;

import lombok.Data;

@Data
public class BookSubscription {
    private final Book book;
    private final Subscription subscription;
}
