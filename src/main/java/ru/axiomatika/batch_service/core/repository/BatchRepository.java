package ru.axiomatika.batch_service.core.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import ru.axiomatika.batch_service.core.entity.Batch;
import ru.axiomatika.batch_service.core.exception.BaseException;
import ru.axiomatika.batch_service.core.exception.BaseExceptionCode;

@Repository
public class BatchRepository {

    private final SessionFactory sessionFactory;

    public BatchRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Batch save(Batch batch) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            session.persist(batch);
            transaction.commit();

            return batch;
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