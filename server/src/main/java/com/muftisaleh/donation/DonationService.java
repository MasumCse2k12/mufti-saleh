package com.muftisaleh.donation;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.muftisaleh.common.error.ApiException;
import com.muftisaleh.donation.DonationDtos.DonationResponse;
import com.muftisaleh.donation.DonationDtos.DonationSummary;
import com.muftisaleh.donation.DonationDtos.RecordRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DonationService {

    private final DonationRepository repo;

    @Transactional(readOnly = true)
    @Cacheable("donation-summary")
    public DonationSummary summary() {
        List<Object[]> rows = repo.sumByPurpose();
        Map<String, BigDecimal> byPurpose = new LinkedHashMap<>();
        BigDecimal total = BigDecimal.ZERO;
        for (Object[] row : rows) {
            String purpose = ((DonationPurpose) row[0]).name();
            BigDecimal amount = (BigDecimal) row[1];
            byPurpose.put(purpose, amount);
            total = total.add(amount);
        }
        return new DonationSummary(byPurpose, total);
    }

    @Transactional(readOnly = true)
    public List<DonationResponse> listAll(int size) {
        return repo.findAllDesc(Limit.of(size)).stream().map(this::toResponse).toList();
    }

    @Transactional
    public DonationResponse record(RecordRequest req) {
        Donation d = new Donation();
        d.setDonorName(req.donorName());
        d.setAmount(req.amount());
        d.setPurpose(req.purpose());
        d.setNote(req.note());
        return toResponse(repo.save(d));
    }

    @Transactional
    public void delete(UUID id) {
        repo.delete(repo.findById(id)
                .orElseThrow(() -> ApiException.notFound("Donation record not found.", "ডোনেশন রেকর্ড পাওয়া যায়নি।")));
    }

    private DonationResponse toResponse(Donation d) {
        return new DonationResponse(d.getId(), d.getDonorName(), d.getAmount(),
                d.getPurpose(), d.getNote(), d.getCreatedAt());
    }
}
