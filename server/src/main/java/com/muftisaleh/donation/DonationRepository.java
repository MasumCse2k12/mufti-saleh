package com.muftisaleh.donation;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DonationRepository extends JpaRepository<Donation, UUID> {

    @Query("select d from Donation d order by d.createdAt desc")
    List<Donation> findAllDesc(Limit limit);

    @Query("select d.purpose, sum(d.amount) from Donation d group by d.purpose")
    List<Object[]> sumByPurpose();
}
