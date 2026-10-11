package com.muftisaleh.schedule;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.muftisaleh.common.error.ApiException;
import com.muftisaleh.schedule.ScheduleDtos.CreateRequest;
import com.muftisaleh.schedule.ScheduleDtos.ScheduleEventResponse;
import com.muftisaleh.schedule.ScheduleDtos.UpdateRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository repo;

    @Transactional(readOnly = true)
    public List<ScheduleEventResponse> listUpcoming() {
        return repo.findUpcoming(Instant.now()).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ScheduleEventResponse> listAll() {
        return repo.findAllDesc().stream().map(this::toResponse).toList();
    }

    @Transactional
    public ScheduleEventResponse create(CreateRequest req) {
        ScheduleEvent e = new ScheduleEvent();
        e.setTitle(req.title());
        e.setDescription(req.description());
        e.setEventAt(req.eventAt());
        e.setLocation(req.location());
        e.setType(req.type() != null ? req.type() : EventType.PROGRAM);
        return toResponse(repo.save(e));
    }

    @Transactional
    public ScheduleEventResponse update(UUID id, UpdateRequest req) {
        ScheduleEvent e = find(id);
        if (req.title() != null) e.setTitle(req.title());
        if (req.description() != null) e.setDescription(req.description());
        if (req.eventAt() != null) e.setEventAt(req.eventAt());
        if (req.location() != null) e.setLocation(req.location());
        if (req.type() != null) e.setType(req.type());
        return toResponse(repo.save(e));
    }

    @Transactional
    public void delete(UUID id) {
        repo.delete(find(id));
    }

    private ScheduleEvent find(UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> ApiException.notFound("Schedule event not found.", "সময়সূচি পাওয়া যায়নি।"));
    }

    private ScheduleEventResponse toResponse(ScheduleEvent e) {
        return new ScheduleEventResponse(e.getId(), e.getTitle(), e.getDescription(),
                e.getEventAt(), e.getLocation(), e.getType());
    }
}
