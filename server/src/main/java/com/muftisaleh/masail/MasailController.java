package com.muftisaleh.masail;

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

import com.muftisaleh.masail.MasailDtos.CreateRequest;
import com.muftisaleh.masail.MasailDtos.MasailResponse;
import com.muftisaleh.masail.MasailDtos.UpdateRequest;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Women's Masail")
@RequiredArgsConstructor
public class MasailController {

    private final MasailService service;

    @GetMapping("/masail")
    @SecurityRequirements
    public List<MasailResponse> list(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "50") int size) {
        return service.listPublished(category, Math.min(size, 100));
    }

    @GetMapping("/admin/masail")
    public List<MasailResponse> adminList(@RequestParam(defaultValue = "100") int size) {
        return service.listAll(size);
    }

    @PostMapping("/admin/masail")
    @ResponseStatus(HttpStatus.CREATED)
    public MasailResponse create(@Valid @RequestBody CreateRequest req) {
        return service.create(req);
    }

    @PatchMapping("/admin/masail/{id}")
    public MasailResponse update(@PathVariable UUID id, @RequestBody UpdateRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/admin/masail/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
