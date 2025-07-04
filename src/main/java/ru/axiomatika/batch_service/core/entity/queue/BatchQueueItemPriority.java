package ru.axiomatika.batch_service.core.entity.queue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BatchQueueItemPriority {

    HIGH(0),
    NORMAL(1),
    LOW(2);

    private final int priority;

}
