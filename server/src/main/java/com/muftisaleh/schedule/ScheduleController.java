package com.muftisaleh.schedule;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.muftisaleh.schedule.ScheduleDtos.CreateRequest;
import com.muftisaleh.schedule.ScheduleDtos.ScheduleEventResponse;
import com.muftisaleh.schedule.ScheduleDtos.UpdateRequest;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1")
@Tag(name = "Schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService service;

    @GetMapping("/schedule")
    @SecurityRequirements
    public List<ScheduleEventResponse> upcoming() {
        return service.listUpcoming();
    }

    @GetMapping("/admin/schedule")
    public List<ScheduleEventResponse> all() {
        return service.listAll();
    }

    @PostMapping("/admin/schedule")
    @ResponseStatus(HttpStatus.CREATED)
    public ScheduleEventResponse create(@Valid @RequestBody CreateRequest req) {
        return service.create(req);
    }

    @PatchMapping("/admin/schedule/{id}")
    public ScheduleEventResponse update(@PathVariable UUID id, @RequestBody UpdateRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/admin/schedule/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
