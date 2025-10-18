package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.event.EventDto;
import ru.yandex.practicum.filmorate.model.event.Event;

public class EventMapper {
    public static EventDto mapToDto(Event event) {
        return EventDto.builder()
                .userId(event.getUserId())
                .entityId(event.getEntityId())
                .operation(event.getOperation())
                .eventType(event.getEventType())
                .timestamp(event.getTimestamp().getTime()).build();
    }
}