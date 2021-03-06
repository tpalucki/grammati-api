package io.github.tpalucki.grammati.repository;

import io.github.tpalucki.grammati.domain.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    Quiz findBySessionId(String sessionId);

}
