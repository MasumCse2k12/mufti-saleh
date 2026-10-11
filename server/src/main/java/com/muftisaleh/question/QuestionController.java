package com.muftisaleh.question;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.muftisaleh.question.QuestionDtos.AnswerRequest;
import com.muftisaleh.question.QuestionDtos.QuestionAdminResponse;
import com.muftisaleh.question.QuestionDtos.QuestionPublicResponse;
import com.muftisaleh.question.QuestionDtos.SubmitRequest;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1")
@Tag(name = "Questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService service;

    // --- Public ---

    @GetMapping("/questions")
    @SecurityRequirements
    public List<QuestionPublicResponse> list(
            @RequestParam(required = false) QuestionCategory category,
            @RequestParam(required = false) Instant cursor,
            @RequestParam(defaultValue = "20") int size) {
        return service.listAnswered(category, cursor, Math.min(size, 50));
    }

    @PostMapping("/questions")
    @SecurityRequirements
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionPublicResponse submit(@Valid @RequestBody SubmitRequest req) {
        return service.submit(req);
    }

    // --- Admin ---

    @GetMapping("/admin/questions")
    public List<QuestionAdminResponse> adminList(
            @RequestParam(defaultValue = "false") boolean pending,
            @RequestParam(defaultValue = "50") int size) {
        return pending ? service.listPending(size) : service.listAll(size);
    }

    @PostMapping("/admin/questions/{id}/answer")
    public QuestionAdminResponse answer(@PathVariable UUID id, @Valid @RequestBody AnswerRequest req) {
        return service.answer(id, req);
    }

    @PostMapping("/admin/questions/{id}/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reject(@PathVariable UUID id) {
        service.reject(id);
    }

    @DeleteMapping("/admin/questions/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
