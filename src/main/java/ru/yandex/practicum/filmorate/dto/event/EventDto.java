package ru.yandex.practicum.filmorate.dto.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventDto {
    private int eventId;
    private int userId;
    private int entityId;
    private long timestamp;
    private String eventType;
    private String operation;
}