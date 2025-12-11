package com.atharva.eventmento.mappers;

import com.atharva.eventmento.domain.dtos.GetTicketResponseDto;
import com.atharva.eventmento.domain.dtos.ListEventResponseDto;
import com.atharva.eventmento.domain.dtos.ListTicketResponseDto;
import com.atharva.eventmento.domain.dtos.ListTicketTicketTypeDtoResponse;
import com.atharva.eventmento.domain.entities.Ticket;
import com.atharva.eventmento.domain.entities.TicketType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TicketMapper {

    ListTicketTicketTypeDtoResponse toListTicketTicketTypeResponseDto(TicketType ticketType);

    ListTicketResponseDto toListTicketResponseDto(Ticket ticket);

    @Mapping(target = "price", source = "ticket.ticketType.price")
    @Mapping(target = "description", source = "ticket.ticketType.description")
    @Mapping(target = "eventName", source = "ticket.ticketType.event.name")
    @Mapping(target = "eventVenue", source = "ticket.ticketType.event.venue")
    @Mapping(target = "eventStart", source = "ticket.ticketType.event.start")
    @Mapping(target = "eventEnd", source = "ticket.ticketType.event.end")
    GetTicketResponseDto toGetTicketResponseDto(Ticket ticket);

}
