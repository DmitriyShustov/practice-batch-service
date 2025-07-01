package ru.axiomatika.batch_service.core.repository;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import ru.axiomatika.batch_service.core.entity.queue.BatchQueueItem;
import ru.axiomatika.batch_service.core.exception.BaseException;
import ru.axiomatika.batch_service.core.exception.BaseExceptionCode;

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
