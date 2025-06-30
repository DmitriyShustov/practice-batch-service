package ru.axiomatika.batch_service.core.entity.queue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BatchQueueItemPriority {

    HIGH(1),
    NORMAL(2),
    LOW(3);

    private final int priority;

}
