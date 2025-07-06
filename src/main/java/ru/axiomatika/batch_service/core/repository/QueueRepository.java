package ru.axiomatika.batch_service.core.repository;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import ru.axiomatika.batch_service.core.entity.BatchItem;
import ru.axiomatika.batch_service.core.entity.BatchItemStatus;
import ru.axiomatika.batch_service.core.entity.queue.BatchQueueItem;
import ru.axiomatika.batch_service.core.exception.DatabaseException;
import ru.axiomatika.batch_service.web.dto.QueueAndBatchItemDto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class QueueRepository {

    private final SessionFactory sessionFactory;

    public void save(BatchQueueItem queueItem) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            session.persist(queueItem);
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            throw new DatabaseException(e.getMessage());
        } finally {
            session.close();
        }
    }

    public List<QueueAndBatchItemDto> findQueueItemsWithBatchItems(int limit, int maxRetryCount) {
        Session session = sessionFactory.openSession();
        try {
            return session.createQuery(
                            "SELECT NEW ru.axiomatika.batch_service.web.dto.QueueAndBatchItemDto(q, b) " +
                                    "FROM BatchQueueItem q " +
                                    "JOIN q.batchItem b " +
                                    "WHERE q.nextProcessingTime <= :currentTime " +
                                    "AND b.status NOT IN (:excludedStatuses) " +
                                    "AND q.retryCount < :maxRetryCount " +
                                    "ORDER BY q.priority ASC, q.nextProcessingTime ASC",
                            QueueAndBatchItemDto.class)
                    .setParameter("currentTime", LocalDateTime.now())
                    .setParameter("excludedStatuses",
                            Arrays.asList(BatchItemStatus.SUCCESS, BatchItemStatus.VALIDATION_ERROR))
                    .setParameter("maxRetryCount", maxRetryCount)
                    .setMaxResults(limit)
                    .getResultList();
        }
        catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        } finally {
            session.close();
        }
    }

    public void updateQueueItemsWithBatchItems(List<QueueAndBatchItemDto> items) {
        if (items == null || items.isEmpty()) {
            return;
        }

        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            for (QueueAndBatchItemDto dto : items) {
                BatchQueueItem queueItem = dto.getQueueItem();
                session.update(queueItem);

                BatchItem batchItem = dto.getBatchItem();
                session.update(batchItem);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DatabaseException("Failed to update queue and batch items");
        } finally {
            session.close();
        }
    }

}
