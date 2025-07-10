package ru.axiomatika.batch_service.core.repository;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.axiomatika.batch_service.core.entity.BatchItem;
import ru.axiomatika.batch_service.core.entity.BatchItemStatus;
import ru.axiomatika.batch_service.core.exception.DatabaseException;

@Repository
@RequiredArgsConstructor
public class BatchItemRepository {

    private final SessionFactory sessionFactory;

    public void save(BatchItem batchItem) {
        try (Session session = sessionFactory.openSession()) {
            session.save(batchItem);
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    public void updateStatusToPendingByBatchId(Long batchId) {
        try (Session session = sessionFactory.openSession()) {
            session.createQuery(
                            "UPDATE BatchItem bi " +
                                    "SET bi.status = :newStatus " +
                                    "WHERE bi.batch.id = :batchId")
                    .setParameter("newStatus", BatchItemStatus.PENDING)
                    .setParameter("batchId", batchId)
                    .executeUpdate();
        } catch (Exception e) {
            throw new DatabaseException("Failed to update BatchItem statuses to PENDING for batchId: " + batchId);
        }
    }

}
