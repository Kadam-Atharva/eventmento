package com.atharva.eventmento.domain.dtos;

import com.atharva.eventmento.domain.entities.EventStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetPublishedEventDetailsResponseDto {

    private UUID id;
    private String name;
    private LocalDateTime start;
    private LocalDateTime end;
    private String venue;
    private String coverImage;
    private String description;
    private List<GetPublishedEventDetailsTicketTypeResponseDto> ticketTypes = new ArrayList<>();
}
