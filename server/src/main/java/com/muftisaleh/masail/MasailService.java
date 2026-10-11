package com.muftisaleh.masail;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.muftisaleh.common.error.ApiException;
import com.muftisaleh.masail.MasailDtos.CreateRequest;
import com.muftisaleh.masail.MasailDtos.MasailResponse;
import com.muftisaleh.masail.MasailDtos.UpdateRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MasailService {

    private final MasailRepository repo;

    @Transactional(readOnly = true)
    public List<MasailResponse> listPublished(String category, int size) {
        return repo.findPublished(category, Limit.of(size)).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<MasailResponse> listAll(int size) {
        return repo.findAllDesc(Limit.of(size)).stream().map(this::toResponse).toList();
    }

    @Transactional
    public MasailResponse create(CreateRequest req) {
        Masail m = new Masail();
        m.setQuestion(req.question());
        m.setAnswer(req.answer());
        m.setCategory(req.category() != null ? req.category().toUpperCase() : "MISC");
        m.setPublished(req.published());
        return toResponse(repo.save(m));
    }

    @Transactional
    public MasailResponse update(UUID id, UpdateRequest req) {
        Masail m = find(id);
        if (req.question() != null) m.setQuestion(req.question());
        if (req.answer() != null) m.setAnswer(req.answer());
        if (req.category() != null) m.setCategory(req.category().toUpperCase());
        if (req.published() != null) m.setPublished(req.published());
        return toResponse(repo.save(m));
    }

    @Transactional
    public void delete(UUID id) {
        repo.delete(find(id));
    }

    private Masail find(UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> ApiException.notFound("Masail not found.", "মাসআলা পাওয়া যায়নি।"));
    }

    private MasailResponse toResponse(Masail m) {
        return new MasailResponse(m.getId(), m.getQuestion(), m.getAnswer(), m.getCategory(), m.getCreatedAt());
    }
}
