package com.atharva.eventmento.services.impl;

import com.atharva.eventmento.domain.entities.Ticket;
import com.atharva.eventmento.domain.entities.TicketStatusEnum;
import com.atharva.eventmento.domain.entities.TicketType;
import com.atharva.eventmento.domain.entities.User;
import com.atharva.eventmento.exceptions.TicketTypeNotFoundException;
import com.atharva.eventmento.exceptions.TicketsSoldOutException;
import com.atharva.eventmento.exceptions.UserNotFoundException;
import com.atharva.eventmento.repositories.TicketRepository;
import com.atharva.eventmento.repositories.TicketTypeRepository;
import com.atharva.eventmento.repositories.UserRepository;
import com.atharva.eventmento.services.QrCodeService;
import com.atharva.eventmento.services.TicketTypeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketTypeServiceImpl implements TicketTypeService {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final QrCodeService qrCodeService;

    @Override
    @Transactional
    public Ticket purchaseTicket(UUID userId, UUID ticketTypeId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(
                String.format("User with Id %s was not found", userId)
        ));

        TicketType ticketType = ticketTypeRepository.findByIdWithLock(ticketTypeId).orElseThrow(() -> new TicketTypeNotFoundException(
                String.format("Ticket Type with Id %s was not found", ticketTypeId)
        ));

        int purchasedTickets = ticketRepository.countByTicketTypeId(ticketType.getId());
        Integer totalAvailable = ticketType.getTotalAvailable();

        if(purchasedTickets + 1 > totalAvailable) {
            throw new TicketsSoldOutException();
        }

        Ticket ticket = new Ticket();
        ticket.setStatus(TicketStatusEnum.PURCHASED);
        ticket.setTicketType(ticketType);
        ticket.setPurchaser(user);

        Ticket savedTicket = ticketRepository.save(ticket);
        qrCodeService.generateQrCode(savedTicket);

        return ticketRepository.save(savedTicket);
    }
}
