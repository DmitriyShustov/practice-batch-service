package ru.axiomatika.batch_service.core.repository;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import ru.axiomatika.batch_service.core.entity.BatchItem;
import ru.axiomatika.batch_service.core.exception.BaseException;
import ru.axiomatika.batch_service.core.exception.BaseExceptionCode;

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

            throw new BaseException(
                    e.getMessage(),
                    BaseExceptionCode.DATABASE_EXCEPTION,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        } finally {
            session.close();
        }
    }

}
