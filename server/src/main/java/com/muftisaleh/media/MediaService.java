package com.muftisaleh.media;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.muftisaleh.common.error.ApiException;
import com.muftisaleh.media.MediaDtos.CreateRequest;
import com.muftisaleh.media.MediaDtos.MediaResponse;
import com.muftisaleh.media.MediaDtos.UpdateRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaRepository repo;

    @Transactional(readOnly = true)
    public List<MediaResponse> list(MediaType type, Instant cursor, int size) {
        return repo.findPublished(type, cursor, Limit.of(size)).stream().map(this::toResponse).toList();
    }

    @Transactional
    public MediaResponse create(CreateRequest req) {
        MediaItem m = new MediaItem();
        m.setTitle(req.title());
        m.setDescription(req.description());
        m.setType(req.type() != null ? req.type() : MediaType.BAYAAN);
        m.setYoutubeId(req.youtubeId());
        m.setMediaUrl(req.mediaUrl());
        m.setDurationSeconds(req.durationSeconds());
        m.setPublishedAt(req.publishedAt());
        return toResponse(repo.save(m));
    }

    @Transactional
    public MediaResponse update(UUID id, UpdateRequest req) {
        MediaItem m = find(id);
        if (req.title() != null) m.setTitle(req.title());
        if (req.description() != null) m.setDescription(req.description());
        if (req.type() != null) m.setType(req.type());
        if (req.youtubeId() != null) m.setYoutubeId(req.youtubeId());
        if (req.mediaUrl() != null) m.setMediaUrl(req.mediaUrl());
        if (req.durationSeconds() != null) m.setDurationSeconds(req.durationSeconds());
        if (req.publishedAt() != null) m.setPublishedAt(req.publishedAt());
        return toResponse(repo.save(m));
    }

    @Transactional
    public void delete(UUID id) {
        repo.delete(find(id));
    }

    private MediaItem find(UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> ApiException.notFound("Media item not found.", "মিডিয়া পাওয়া যায়নি।"));
    }

    private MediaResponse toResponse(MediaItem m) {
        return new MediaResponse(m.getId(), m.getTitle(), m.getDescription(), m.getType(),
                m.getYoutubeId(), m.getMediaUrl(), m.getDurationSeconds(), m.getPublishedAt());
    }
}
