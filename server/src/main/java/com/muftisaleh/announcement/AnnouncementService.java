package com.muftisaleh.announcement;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.muftisaleh.announcement.AnnouncementDtos.AnnouncementResponse;
import com.muftisaleh.announcement.AnnouncementDtos.CreateRequest;
import com.muftisaleh.announcement.AnnouncementDtos.UpdateRequest;
import com.muftisaleh.common.error.ApiException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository repo;

    @Transactional(readOnly = true)
    public List<AnnouncementResponse> listActive(Instant cursor, int size) {
        return repo.findActive(Instant.now(), cursor, Limit.of(size))
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AnnouncementResponse> listAll(int size) {
        return repo.findAllForAdmin(Limit.of(size))
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public AnnouncementResponse get(UUID id) {
        return toResponse(find(id));
    }

    @Transactional
    public AnnouncementResponse create(CreateRequest req) {
        Announcement a = new Announcement();
        a.setTitle(req.title());
        a.setBody(req.body());
        a.setPinned(req.pinned());
        a.setPublishedAt(req.publishedAt());
        a.setExpiresAt(req.expiresAt());
        return toResponse(repo.save(a));
    }

    @Transactional
    public AnnouncementResponse update(UUID id, UpdateRequest req) {
        Announcement a = find(id);
        if (req.title() != null) a.setTitle(req.title());
        if (req.body() != null) a.setBody(req.body());
        if (req.pinned() != null) a.setPinned(req.pinned());
        if (req.publishedAt() != null) a.setPublishedAt(req.publishedAt());
        if (req.expiresAt() != null) a.setExpiresAt(req.expiresAt());
        return toResponse(repo.save(a));
    }

    @Transactional
    public void delete(UUID id) {
        repo.delete(find(id));
    }

    private Announcement find(UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> ApiException.notFound("Announcement not found.", "ঘোষণা পাওয়া যায়নি।"));
    }

    private AnnouncementResponse toResponse(Announcement a) {
        return new AnnouncementResponse(a.getId(), a.getTitle(), a.getBody(),
                a.isPinned(), a.getPublishedAt(), a.getExpiresAt());
    }
}
