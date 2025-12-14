package com.atharva.eventmento.services;

import com.atharva.eventmento.domain.entities.QrCode;
import com.atharva.eventmento.domain.entities.Ticket;

import java.util.UUID;

public interface QrCodeService {
    QrCode generateQrCode(Ticket ticket);

    byte[] getQrCodeImageForUserAndTicket(UUID userId, UUID ticketId);
}
