package io.github.tpalucki.grammati.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "task")
@Data
public class Task {

    @Id
    @GeneratedValue
    private Long id;
    private String question;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

}