package org.digitalbooks.entity;

//import jakarta.persistence.*;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private Long bookId;

    private LocalDate subscriptionDate;
}
