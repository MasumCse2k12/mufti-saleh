package com.muftisaleh.media;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.muftisaleh.media.MediaDtos.CreateRequest;
import com.muftisaleh.media.MediaDtos.MediaResponse;
import com.muftisaleh.media.MediaDtos.UpdateRequest;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1")
@Tag(name = "Media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService service;

    @GetMapping("/media")
    @SecurityRequirements
    public List<MediaResponse> list(
            @RequestParam(required = false) MediaType type,
            @RequestParam(required = false) Instant cursor,
            @RequestParam(defaultValue = "20") int size) {
        return service.list(type, cursor, Math.min(size, 50));
    }

    @PostMapping("/admin/media")
    @ResponseStatus(HttpStatus.CREATED)
    public MediaResponse create(@Valid @RequestBody CreateRequest req) {
        return service.create(req);
    }

    @PatchMapping("/admin/media/{id}")
    public MediaResponse update(@PathVariable UUID id, @RequestBody UpdateRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/admin/media/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
