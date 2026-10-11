package com.muftisaleh.announcement;

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

import com.muftisaleh.announcement.AnnouncementDtos.AnnouncementResponse;
import com.muftisaleh.announcement.AnnouncementDtos.CreateRequest;
import com.muftisaleh.announcement.AnnouncementDtos.UpdateRequest;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService service;

    // --- Public ---

    @GetMapping("/announcements")
    @SecurityRequirements
    public List<AnnouncementResponse> list(
            @RequestParam(required = false) Instant cursor,
            @RequestParam(defaultValue = "20") int size) {
        return service.listActive(cursor, Math.min(size, 50));
    }

    @GetMapping("/announcements/{id}")
    @SecurityRequirements
    public AnnouncementResponse get(@PathVariable UUID id) {
        return service.get(id);
    }

    // --- Admin ---

    @GetMapping("/admin/announcements")
    public List<AnnouncementResponse> adminList(@RequestParam(defaultValue = "50") int size) {
        return service.listAll(size);
    }

    @PostMapping("/admin/announcements")
    @ResponseStatus(HttpStatus.CREATED)
    public AnnouncementResponse create(@Valid @RequestBody CreateRequest req) {
        return service.create(req);
    }

    @PatchMapping("/admin/announcements/{id}")
    public AnnouncementResponse update(@PathVariable UUID id, @RequestBody UpdateRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/admin/announcements/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
