package ru.axiomatika.batch_service.core.repository;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import ru.axiomatika.batch_service.core.entity.BatchItem;
import ru.axiomatika.batch_service.core.exception.DatabaseException;

@Repository
@RequiredArgsConstructor
public class BatchItemRepository {

    private final SessionFactory sessionFactory;

    public void save(BatchItem batchItem) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            session.persist(batchItem);
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

}
