package student.mgt.daoImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import student.mgt.dao.AttendanceDao;
import student.mgt.entity.Attendance;
import student.mgt.entity.AttendanceStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class AttendanceDaoImpl implements AttendanceDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Attendance save(Attendance attendance) {

        entityManager.persist(attendance);
        entityManager.flush();

        return attendance;
    }

    @Override
    public Attendance update(Attendance attendance) {

        Attendance updated = entityManager.merge(attendance);
        entityManager.flush();

        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Attendance> findById(Long id) {

        return Optional.ofNullable(
                entityManager.find(Attendance.class, id)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attendance> findAll() {

        return entityManager.createQuery(
                "SELECT a FROM Attendance a " +
                        "JOIN FETCH a.student " +
                        "ORDER BY a.date DESC, a.id DESC",
                Attendance.class
        ).getResultList();
    }

    @Override
    public void deleteById(Long id) {

        Attendance attendance =
                entityManager.find(Attendance.class, id);

        if (attendance != null) {
            entityManager.remove(attendance);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attendance> findByDate(LocalDate date) {

        return entityManager.createQuery(
                        "SELECT a FROM Attendance a " +
                                "JOIN FETCH a.student " +
                                "WHERE a.date = :date " +
                                "ORDER BY a.student.name",
                        Attendance.class
                ).setParameter("date", date)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attendance> findByStudentId(Long studentId) {

        return entityManager.createQuery(
                        "SELECT a FROM Attendance a " +
                                "JOIN FETCH a.student " +
                                "WHERE a.student.id = :studentId " +
                                "ORDER BY a.date DESC",
                        Attendance.class
                ).setParameter("studentId", studentId)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attendance> findByStudentIdAndDate(
            Long studentId, LocalDate date) {

        return entityManager.createQuery(
                        "SELECT a FROM Attendance a " +
                                "JOIN FETCH a.student " +
                                "WHERE a.student.id = :studentId " +
                                "AND a.date = :date",
                        Attendance.class
                ).setParameter("studentId", studentId)
                .setParameter("date", date)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByStudentIdAndDate(
            Long studentId, LocalDate date) {

        Long count = entityManager.createQuery(
                        "SELECT COUNT(a) FROM Attendance a " +
                                "WHERE a.student.id = :studentId " +
                                "AND a.date = :date",
                        Long.class
                ).setParameter("studentId", studentId)
                .setParameter("date", date)
                .getSingleResult();

        return count > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public long countByDate(LocalDate date) {

        return entityManager.createQuery(
                        "SELECT COUNT(a) FROM Attendance a " +
                                "WHERE a.date = :date",
                        Long.class
                ).setParameter("date", date)
                .getSingleResult();
    }

    @Override
    @Transactional(readOnly = true)
    public long countByDateAndStatus(
            LocalDate date, AttendanceStatus status) {

        return entityManager.createQuery(
                        "SELECT COUNT(a) FROM Attendance a " +
                                "WHERE a.date = :date " +
                                "AND a.status = :status",
                        Long.class
                ).setParameter("date", date)
                .setParameter("status", status)
                .getSingleResult();
    }
}