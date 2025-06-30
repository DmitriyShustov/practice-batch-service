package ru.axiomatika.batch_service.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.axiomatika.batch_service.core.entity.BatchItem;
import ru.axiomatika.batch_service.core.repository.BatchItemRepository;
import ru.axiomatika.batch_service.web.mapper.BatchItemMapper;

@Service
@RequiredArgsConstructor
public class BatchItemService {

    private final BatchItemRepository batchItemRepository;

    public void save(BatchItem batchItem) {
        batchItemRepository.save(batchItem);

//        add to queue
    }

}
