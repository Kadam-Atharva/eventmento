package com.atharva.eventmento.services;

import com.atharva.eventmento.domain.CreateEventRequest;
import com.atharva.eventmento.domain.UpdateEventRequest;
import com.atharva.eventmento.domain.dtos.EventParticipantsResponseDto;
import com.atharva.eventmento.domain.entities.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface EventService {
    Event createEvent(UUID organizerId, CreateEventRequest event);
    Page<Event> listEventsForOrganizer(UUID organizerId, Pageable pageable);
    Optional<Event> getEventForOrganizer(UUID organizerId,UUID id);
    Event updateEventForOrganizer(UUID organizerId, UUID id, UpdateEventRequest event);
    void deleteEventForOrganizer(UUID organizerId, UUID id);
    Page<Event> listPublishedEvents(Pageable pageable);
    Page<Event> searchPublishedEvents(String query, Pageable pageable);
    Optional<Event> getPublishedEvent(UUID id);
    void addStaffToEvent(UUID organizerId, UUID eventId, String staffEmail);
    EventParticipantsResponseDto getEventParticipants(UUID eventId, UUID requestingUserId);
}
