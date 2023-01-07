package org.digitalbooks.entity;

//import jakarta.persistence.*;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;
    private String title;
    private String category;
    private Double price;
    private Long authorId;
    private String publisher;
    private LocalDate publishedDate;
    private String content;
    private Boolean blocked;

}
