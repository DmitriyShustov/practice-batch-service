package ru.axiomatika.batch_service.core.repository;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;
import ru.axiomatika.batch_service.core.entity.Batch;
import ru.axiomatika.batch_service.core.entity.BatchStatus;
import ru.axiomatika.batch_service.core.exception.DatabaseException;
import ru.axiomatika.batch_service.core.exception.InvalidHashException;

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

            throw new DatabaseException(e.getMessage());
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
            throw new DatabaseException("Failed to update batch status: " + e.getMessage());
        } finally {
            session.close();
        }
    }

    public void updateProcessingTimestamps(Batch batch) {
        if (batch == null) {
            throw new IllegalArgumentException("Batch cannot be null");
        }

        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            
            session.createQuery(
                            "UPDATE Batch b SET " +
                                    "b.previousAttempt = :prevAttempt, " +
                                    "b.nextAttempt = :nextAttempt " +
                                    "WHERE b.id = :id")
                    .setParameter("prevAttempt", batch.getPreviousAttempt())
                    .setParameter("nextAttempt", batch.getNextAttempt())
                    .setParameter("id", batch.getId())
                    .executeUpdate();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DatabaseException("Failed to update batch timestamps: " + e.getMessage());
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
            throw new DatabaseException("Failed to find batch by hash");
        } finally {
            session.close();
        }
    }

    private void checkValidHas(String hash) {
        if (hash == null || hash.isEmpty()) {
            throw new InvalidHashException();
        }
    }

    public Optional<BatchStatus> findStatusById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        Session session = sessionFactory.openSession();
        try {
            return session.createQuery(
                            "SELECT b.status FROM Batch b WHERE b.id = :id", BatchStatus.class)
                    .setParameter("id", id)
                    .setMaxResults(1)
                    .uniqueResultOptional();
        } catch (Exception e) {
            throw new DatabaseException("Failed to find batch status by ID: " + e.getMessage());
        } finally {
            session.close();
        }
    }

}