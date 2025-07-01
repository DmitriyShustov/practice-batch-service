package ru.axiomatika.batch_service.core.repository;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import ru.axiomatika.batch_service.core.entity.Batch;
import ru.axiomatika.batch_service.core.exception.BaseException;
import ru.axiomatika.batch_service.core.exception.BaseExceptionCode;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BatchRepository {

    private final SessionFactory sessionFactory;

    public void save(Batch batch) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            session.persist(batch);
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

    public void updateStatus(Batch batch) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            session.createQuery(
                            "UPDATE Batch b SET " +
                                    "b.status = :status " +
                                    "WHERE b.id = :id")
                    .setParameter("status", batch.getStatus())
                    .setParameter("id", batch.getId())
                    .executeUpdate();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new BaseException(
                    "Failed to update batch status: " + e.getMessage(),
                    BaseExceptionCode.DATABASE_EXCEPTION,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        } finally {
            session.close();
        }
    }

    public Optional<Batch> findByHash(String hash) {
        checkValidHas(hash);

        Session session = sessionFactory.openSession();
        try {
            return session.createQuery(
                            "SELECT b FROM Batch b WHERE b.hash = :hash", Batch.class)
                    .setParameter("hash", hash)
                    .setMaxResults(1)
                    .setCacheable(true)
                    .uniqueResultOptional();
        } catch (Exception e) {
            throw new BaseException(
                    "Failed to find batch by hash",
                    BaseExceptionCode.DATABASE_EXCEPTION,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            session.close();
        }
    }

    private void checkValidHas(String hash) {
        if (hash == null || hash.isEmpty()) {
            throw new BaseException(
                    "Invalid hash",
                    BaseExceptionCode.INVALID_MD5_HASH,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

}