package org.digitalbooks.repository;

import org.digitalbooks.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriberRepository extends JpaRepository<Subscription,Long> {

}
