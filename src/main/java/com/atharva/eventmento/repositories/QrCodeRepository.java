package com.atharva.eventmento.repositories;

import com.atharva.eventmento.domain.entities.QrCode;
import com.atharva.eventmento.domain.entities.QrCodeStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QrCodeRepository extends JpaRepository<QrCode, UUID> {
    Optional<QrCode> findByTicketIdAndTicketPurchaserId(UUID uuid, UUID  ticketPurchaseId);
    Optional<QrCode> findByIdAndStatus(UUID id, QrCodeStatusEnum status);
}
