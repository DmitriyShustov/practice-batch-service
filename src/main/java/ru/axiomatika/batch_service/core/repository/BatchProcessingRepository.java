package ru.axiomatika.batch_service.core.repository;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.axiomatika.batch_service.core.entity.BatchProcessing;
import ru.axiomatika.batch_service.core.exception.DatabaseException;

@Repository
@RequiredArgsConstructor
public class BatchProcessingRepository {

    private final SessionFactory sessionFactory;

    public void saveOrUpdate(BatchProcessing processing) {
        try (Session session = sessionFactory.openSession()) {
            if (processing.getId() != null) {
                session.merge(processing);
            } else {
                session.persist(processing);
            }
        } catch (Exception e) {
            throw new DatabaseException("Failed to save or update batch processing");
        }
    }

    public BatchProcessing findByBatchId(Long batchId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "SELECT p FROM BatchProcessing p JOIN FETCH p.batch WHERE p.batch.id = :batchId",
                            BatchProcessing.class)
                    .setParameter("batchId", batchId)
                    .uniqueResult();
        } catch (Exception e) {
            throw new DatabaseException("Failed to find batch processing by batch id: " + batchId);
        }
    }

}