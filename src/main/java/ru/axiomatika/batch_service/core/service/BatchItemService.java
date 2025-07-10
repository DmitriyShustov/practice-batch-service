package ru.axiomatika.batch_service.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.axiomatika.batch_service.core.entity.BatchItem;
import ru.axiomatika.batch_service.core.repository.BatchItemRepository;

@Service
@RequiredArgsConstructor
public class BatchItemService {

    private final BatchItemRepository batchItemRepository;

    @Transactional
    public void save(BatchItem batchItem) {
        batchItemRepository.save(batchItem);
    }

}
