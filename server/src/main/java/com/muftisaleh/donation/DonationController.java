package com.muftisaleh.donation;

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

import com.muftisaleh.donation.DonationDtos.DonationResponse;
import com.muftisaleh.donation.DonationDtos.DonationSummary;
import com.muftisaleh.donation.DonationDtos.RecordRequest;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService service;

    @GetMapping("/donations/summary")
    @SecurityRequirements
    public DonationSummary summary() {
        return service.summary();
    }

    @GetMapping("/admin/donations")
    public List<DonationResponse> listAll(@RequestParam(defaultValue = "50") int size) {
        return service.listAll(size);
    }

    @PostMapping("/admin/donations")
    @ResponseStatus(HttpStatus.CREATED)
    public DonationResponse record(@Valid @RequestBody RecordRequest req) {
        return service.record(req);
    }

    @DeleteMapping("/admin/donations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
