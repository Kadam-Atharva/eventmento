package com.atharva.eventmento.services.impl;

import com.atharva.eventmento.domain.CreateEventRequest;
import com.atharva.eventmento.domain.UpdateEventRequest;
import com.atharva.eventmento.domain.UpdateTicketTypeRequest;
import com.atharva.eventmento.domain.dtos.EventParticipantsResponseDto;
import com.atharva.eventmento.domain.dtos.UserSummaryDto;
import com.atharva.eventmento.domain.entities.Event;
import com.atharva.eventmento.domain.entities.EventStatusEnum;
import com.atharva.eventmento.domain.entities.TicketType;
import com.atharva.eventmento.domain.entities.User;
import com.atharva.eventmento.exceptions.EventNotFoundException;
import com.atharva.eventmento.exceptions.EventUpdateException;
import com.atharva.eventmento.exceptions.TicketTypeNotFoundException;
import com.atharva.eventmento.exceptions.UserNotFoundException;
import com.atharva.eventmento.repositories.EventRepository;
import com.atharva.eventmento.repositories.UserRepository;
import com.atharva.eventmento.services.EventService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public Event createEvent(UUID organizerId, CreateEventRequest event) {
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("User with Id '%s' not found",organizerId))
                );


        Event eventToCreate = new Event();

        List<TicketType> ticketTypesToCreate = event.getTicketTypes().stream()
                .map(ticketType -> {
                    TicketType ticketTypeToCreate = new TicketType();
                    ticketTypeToCreate.setName(ticketType.getName());
                    ticketTypeToCreate.setPrice(ticketType.getPrice());
                    ticketTypeToCreate.setDescription(ticketType.getDescription());
                    ticketTypeToCreate.setTotalAvailable(ticketType.getTotalAvailable());
                    ticketTypeToCreate.setEvent(eventToCreate);
                    return ticketTypeToCreate;
                }).toList();

        eventToCreate.setName(event.getName());
        eventToCreate.setStart(event.getStart());
        eventToCreate.setEnd(event.getEnd());
        eventToCreate.setVenue(event.getVenue());
        eventToCreate.setSalesStart(event.getSalesStart());
        eventToCreate.setSalesEnd(event.getSalesEnd());
        eventToCreate.setStatus(event.getStatus());
        eventToCreate.setOrganizer(organizer);
        eventToCreate.setCoverImage(event.getCoverImage());
        eventToCreate.setDescription(event.getDescription());
        eventToCreate.setTicketTypes(ticketTypesToCreate);

        return eventRepository.save(eventToCreate);
    }

    @Override
    public Page<Event> listEventsForOrganizer(UUID organizerId, Pageable pageable) {
        return eventRepository.findByOrganizerId(organizerId,pageable);
    }

    @Override
    public Optional<Event> getEventForOrganizer(UUID organizerId, UUID id) {
        return eventRepository.findByIdAndOrganizerId(id, organizerId);
    }

    @Override
    @Transactional
    public Event updateEventForOrganizer(UUID organizerId, UUID id, UpdateEventRequest event) {
        if(null == event.getId()){
            throw new EventUpdateException("Event Id cannot be null");
        }

        if(!id.equals(event.getId())) {
            throw new EventUpdateException("Cannot update the Id of an event");
        }

        Event existingEvent = eventRepository.findByIdAndOrganizerId(id, organizerId)
                .orElseThrow(() -> new EventNotFoundException(
                        String.format("Event with Id '%s' does not exist", id)
                ));

        existingEvent.setName(event.getName());
        existingEvent.setStart(event.getStart());
        existingEvent.setEnd(event.getEnd());
        existingEvent.setVenue(event.getVenue());
        existingEvent.setSalesStart(event.getSalesStart());
        existingEvent.setSalesEnd(event.getSalesEnd());
        existingEvent.setStatus(event.getStatus());
        existingEvent.setCoverImage(event.getCoverImage());
        existingEvent.setDescription(event.getDescription());

        Set<UUID> requestTicketTypeIds = event.getTicketTypes()
                .stream()
                .map(UpdateTicketTypeRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        existingEvent.getTicketTypes().removeIf(existingTicketType ->
                !requestTicketTypeIds.contains(existingTicketType.getId()));

        Map<UUID, TicketType> existingTicketTypesIndex = existingEvent.getTicketTypes().stream()
                .collect(Collectors.toMap(TicketType::getId, Function.identity()));

        for(UpdateTicketTypeRequest ticketType : event.getTicketTypes()) {
            if(null == ticketType.getId()) {
                TicketType ticketTypeToCreate = new TicketType();
                ticketTypeToCreate.setName(ticketType.getName());
                ticketTypeToCreate.setPrice(ticketType.getPrice());
                ticketTypeToCreate.setDescription(ticketType.getDescription());
                ticketTypeToCreate.setTotalAvailable(ticketType.getTotalAvailable());
                ticketTypeToCreate.setEvent(existingEvent);
                existingEvent.getTicketTypes().add(ticketTypeToCreate);
            } else if(existingTicketTypesIndex.containsKey(ticketType.getId())){
                TicketType existingTicketType = existingTicketTypesIndex.get(ticketType.getId());
                existingTicketType.setName(ticketType.getName());
                existingTicketType.setPrice(ticketType.getPrice());
                existingTicketType.setDescription(ticketType.getDescription());
                existingTicketType.setTotalAvailable(ticketType.getTotalAvailable());
            } else {
                throw new TicketTypeNotFoundException(
                        String.format("Ticket type with Id '%s' does not exist", ticketType.getId()));
            }
        }

        return eventRepository.save(existingEvent);
    }

    @Override
    @Transactional
    public void deleteEventForOrganizer(UUID organizerId, UUID id) {
        getEventForOrganizer(organizerId, id).ifPresent(eventRepository::delete);
    }

    @Override
    public Page<Event> listPublishedEvents(Pageable pageable) {
        return eventRepository.findByStatus(EventStatusEnum.PUBLISHED, pageable);
    }

    @Override
    public Page<Event> searchPublishedEvents(String query, Pageable pageable) {
        return eventRepository.searchEvents(query, pageable);
    }

    @Override
    public Optional<Event> getPublishedEvent(UUID id) {
        return eventRepository.findByIdAndStatus(id, EventStatusEnum.PUBLISHED);
    }

    @Override
    @Transactional
    public void addStaffToEvent(UUID organizerId, UUID eventId, String staffEmail) {
        Event event = eventRepository.findByIdAndOrganizerId(eventId, organizerId)
                .orElseThrow(() -> new EventNotFoundException(
                        String.format("Event with ID %s not found or you are not the organizer", eventId)
                ));
        User staffMember = userRepository.findByEmail(staffEmail)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("User with email %s not found.", staffEmail)
                ));
        staffMember.getStaffingEvents().add(event);
        event.getStaff().add(staffMember);
        userRepository.save(staffMember);
    }

    @Override
    @Transactional
    public EventParticipantsResponseDto getEventParticipants(UUID eventId, UUID requestingUserId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(
                        String.format("Event with ID %s not found", eventId)
                ));
        // (Optional logic: Check if requestingUserId allows them to see this data)

        UserSummaryDto organizerDto = UserSummaryDto.builder()
                .name(event.getOrganizer().getName())
                .email(event.getOrganizer().getEmail())
                .build();

        List<UserSummaryDto> staffDtos = event.getStaff().stream()
                .map(user -> UserSummaryDto.builder()
                        .name(user.getName())
                        .email(user.getEmail())
                        .build())
                .toList();

        List<UserSummaryDto> attendeeDtos = event.getAttendees().stream()
                .map(user -> UserSummaryDto.builder()
                        .name(user.getName())
                        .email(user.getEmail())
                        .build())
                .toList();

        return EventParticipantsResponseDto.builder()
                .organizer(organizerDto)
                .staff(staffDtos)
                .attendees(attendeeDtos)
                .build();
    }
}
