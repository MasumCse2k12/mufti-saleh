package com.muftisaleh.bayah;

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

import com.muftisaleh.bayah.BayahDtos.BayahResponse;
import com.muftisaleh.bayah.BayahDtos.SubmitRequest;
import com.muftisaleh.bayah.BayahDtos.UpdateStatusRequest;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1")
@Tag(name = "Bay'ah Requests")
@RequiredArgsConstructor
public class BayahController {

    private final BayahService service;

    @PostMapping("/bayah")
    @SecurityRequirements
    @ResponseStatus(HttpStatus.CREATED)
    public BayahResponse submit(@Valid @RequestBody SubmitRequest req) {
        return service.submit(req);
    }

    @GetMapping("/admin/bayah")
    public List<BayahResponse> list(
            @RequestParam(required = false) BayahStatus status,
            @RequestParam(defaultValue = "50") int size) {
        return service.list(status, size);
    }

    @PatchMapping("/admin/bayah/{id}/status")
    public BayahResponse updateStatus(@PathVariable UUID id, @RequestBody UpdateStatusRequest req) {
        return service.updateStatus(id, req);
    }

    @DeleteMapping("/admin/bayah/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
