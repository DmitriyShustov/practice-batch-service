package ru.axiomatika.batch_service.core.repository;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import ru.axiomatika.batch_service.core.entity.BatchProcessing;
import ru.axiomatika.batch_service.core.exception.DatabaseException;

@Repository
@RequiredArgsConstructor
public class BatchProcessingRepository {

    private final SessionFactory sessionFactory;

    public void saveOrUpdate(BatchProcessing processing) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            if (processing.getId() != null) {
                session.merge(processing);
            } else {
                session.persist(processing);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DatabaseException("Failed to save or update batch processing");
        } finally {
            session.close();
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