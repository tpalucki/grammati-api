package io.github.tpalucki.grammati.controller;

import io.github.tpalucki.grammati.configuration.AppConfig;
import io.github.tpalucki.grammati.domain.Answer;
import io.github.tpalucki.grammati.domain.Question;
import io.github.tpalucki.grammati.domain.Quiz;
import io.github.tpalucki.grammati.exception.QuizNotFoundException;
import io.github.tpalucki.grammati.repository.ExerciseRepository;
import io.github.tpalucki.grammati.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * GET /quiz/quizId
 * Ma zwrócić pierwsze pytanie dla danego quizu
 * <p>
 * POST /quiz/quizId
 * Ma przetworzyć odpowiedź dla danego quizy oraz wyświetlić kolejne pytanie
 */

@Controller
@RequestMapping("/quiz")
@RequiredArgsConstructor
@Slf4j
public class QuizController {

    private final AppConfig appConfig;
    private final QuizRepository quizRepository;
    private final ExerciseRepository questionRepository;

    @GetMapping("/{quizReference}")
    public String showQuestion(@PathVariable("quizReference") String quizReference, Model model) throws QuizNotFoundException {
        log.info("GET /quiz/{}", quizReference);

        Quiz quiz = quizRepository.findQuizBySessionId(quizReference);

        if (quiz == null) {
            throw new QuizNotFoundException("Cannot find quiz with id: " + quizReference);
        }

        Long exerciseId = quiz.nextExerciseId();
        if (exerciseId == null) {
            model.addAttribute("message", "That is all for today, well done!");
            model.addAttribute("title", appConfig.getAppTitle());
            return "message";
        }

        quizRepository.save(quiz);
        Question question = questionRepository.findById(exerciseId).orElseThrow(() -> new IllegalArgumentException("Question not exists"));

        Answer answer = new Answer();

        model.addAttribute("question", question);
        model.addAttribute("quizReference", quizReference);
        model.addAttribute("questionReference", exerciseId);
        model.addAttribute("formAnswer", answer);
        model.addAttribute("title", appConfig.getAppTitle());

        return "quizView";
    }

    @PostMapping()
    public String handleAnswer(Answer answer, Model model) throws QuizNotFoundException {
        log.info("POST /quiz {}", answer);

        var quizReference = answer.getQuizReference();
        var quiz = quizRepository.findQuizBySessionId(quizReference);

        if (quiz == null) {
            throw new QuizNotFoundException("Cannot find quiz with id: " + quizReference);
        }

        var exerciseId = quiz.nextExerciseId();
        if (exerciseId == null) {
            model.addAttribute("message", "That is all for today, well done!");
            model.addAttribute("title", appConfig.getAppTitle());
            return "message";
        }

        this.quizRepository.save(quiz);

        var question = questionRepository.findById(exerciseId).orElseThrow(() -> new IllegalArgumentException("Question not exists"));

        model.addAttribute("question", question);
        model.addAttribute("quizReference", quizReference);
        model.addAttribute("questionReference", exerciseId);
        model.addAttribute("formAnswer", new Answer());
        model.addAttribute("title", appConfig.getAppTitle());

        return "quizView";
    }

    @ExceptionHandler(QuizNotFoundException.class)
    public String quizNotFound(QuizNotFoundException e, Model model) {
        log.info("Exception: " + e.getMessage());

        model.addAttribute("message", e.getMessage());
        model.addAttribute("title", appConfig.getAppTitle());

        return "message";
    }
}