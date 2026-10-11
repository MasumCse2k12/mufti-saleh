package com.muftisaleh.bayah;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.muftisaleh.bayah.BayahDtos.BayahResponse;
import com.muftisaleh.bayah.BayahDtos.SubmitRequest;
import com.muftisaleh.bayah.BayahDtos.UpdateStatusRequest;
import com.muftisaleh.common.error.ApiException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BayahService {

    private final BayahRepository repo;

    @Transactional(readOnly = true)
    public List<BayahResponse> list(BayahStatus status, int size) {
        return repo.findByStatus(status, Limit.of(size)).stream().map(this::toResponse).toList();
    }

    @Transactional
    public BayahResponse submit(SubmitRequest req) {
        BayahRequest b = new BayahRequest();
        b.setName(req.name());
        b.setPhone(req.phone());
        b.setDistrict(req.district());
        b.setNote(req.note());
        return toResponse(repo.save(b));
    }

    @Transactional
    public BayahResponse updateStatus(UUID id, UpdateStatusRequest req) {
        BayahRequest b = find(id);
        if (req.status() != null) b.setStatus(req.status());
        if (req.adminNote() != null) b.setAdminNote(req.adminNote());
        return toResponse(repo.save(b));
    }

    @Transactional
    public void delete(UUID id) {
        repo.delete(find(id));
    }

    private BayahRequest find(UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> ApiException.notFound("Bay'ah request not found.", "বাইয়াতের আবেদন পাওয়া যায়নি।"));
    }

    private BayahResponse toResponse(BayahRequest b) {
        return new BayahResponse(b.getId(), b.getName(), b.getPhone(), b.getDistrict(),
                b.getNote(), b.getStatus(), b.getAdminNote(), b.getCreatedAt());
    }
}
