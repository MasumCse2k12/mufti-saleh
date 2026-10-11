package com.muftisaleh.question;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.muftisaleh.common.error.ApiException;
import com.muftisaleh.question.QuestionDtos.AnswerRequest;
import com.muftisaleh.question.QuestionDtos.QuestionAdminResponse;
import com.muftisaleh.question.QuestionDtos.QuestionPublicResponse;
import com.muftisaleh.question.QuestionDtos.SubmitRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository repo;

    @Transactional(readOnly = true)
    public List<QuestionPublicResponse> listAnswered(QuestionCategory category, Instant cursor, int size) {
        return repo.findAnswered(category, cursor, Limit.of(size))
                .stream().map(this::toPublic).toList();
    }

    @Transactional(readOnly = true)
    public List<QuestionAdminResponse> listPending(int size) {
        return repo.findPending(Limit.of(size)).stream().map(this::toAdmin).toList();
    }

    @Transactional(readOnly = true)
    public List<QuestionAdminResponse> listAll(int size) {
        return repo.findAllDesc(Limit.of(size)).stream().map(this::toAdmin).toList();
    }

    @Transactional
    public QuestionPublicResponse submit(SubmitRequest req) {
        Question q = new Question();
        q.setBody(req.body());
        q.setCategory(req.category() != null ? req.category() : QuestionCategory.GENERAL);
        q.setSubmitterName(req.submitterName());
        q.setSubmitterContact(req.submitterContact());
        q.setStatus(QuestionStatus.PENDING);
        return toPublic(repo.save(q));
    }

    @Transactional
    public QuestionAdminResponse answer(UUID id, AnswerRequest req) {
        Question q = find(id);
        if (q.getStatus() == QuestionStatus.ANSWERED) {
            throw ApiException.conflict("already_answered", "This question already has an answer.",
                    "এই প্রশ্নের ইতিমধ্যে উত্তর দেওয়া হয়েছে।");
        }
        q.setAnswer(req.answer());
        q.setAnsweredAt(Instant.now());
        q.setStatus(QuestionStatus.ANSWERED);
        return toAdmin(repo.save(q));
    }

    @Transactional
    public void reject(UUID id) {
        Question q = find(id);
        q.setStatus(QuestionStatus.REJECTED);
        repo.save(q);
    }

    @Transactional
    public void delete(UUID id) {
        repo.delete(find(id));
    }

    private Question find(UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> ApiException.notFound("Question not found.", "প্রশ্ন পাওয়া যায়নি।"));
    }

    private QuestionPublicResponse toPublic(Question q) {
        return new QuestionPublicResponse(q.getId(), q.getBody(), q.getCategory(),
                q.getAnswer(), q.getAnsweredAt());
    }

    private QuestionAdminResponse toAdmin(Question q) {
        return new QuestionAdminResponse(q.getId(), q.getBody(), q.getCategory(), q.getStatus(),
                q.getAnswer(), q.getAnsweredAt(), q.getSubmitterName(), q.getSubmitterContact(),
                q.getCreatedAt());
    }
}
