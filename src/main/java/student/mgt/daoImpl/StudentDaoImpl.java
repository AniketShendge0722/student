package student.mgt.daoImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import student.mgt.dao.StudentDao;
import student.mgt.entity.Student;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class StudentDaoImpl implements StudentDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Student save(Student student) {
        entityManager.persist(student);
        entityManager.flush();
        return student;
    }

    @Override
    public Student update(Student student) {
        Student updated = entityManager.merge(student);
        entityManager.flush();
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Student> findById(Long id) {
        return Optional.ofNullable(
                entityManager.find(Student.class, id)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> findAll() {

        return entityManager.createQuery(
                "SELECT s FROM Student s " +
                        "JOIN FETCH s.parent " +
                        "LEFT JOIN FETCH s.bus " +
                        "ORDER BY s.id DESC",
                Student.class
        ).getResultList();
    }

    @Override
    public void deleteById(Long id) {

        Student student = entityManager.find(Student.class, id);

        if (student != null) {
            entityManager.remove(student);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByRollNo(String rollNo) {

        Long count = entityManager.createQuery(
                        "SELECT COUNT(s) FROM Student s " +
                                "WHERE LOWER(s.rollNo) = LOWER(:rollNo)",
                        Long.class
                ).setParameter("rollNo", rollNo)
                .getSingleResult();

        return count > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByRollNoAndIdNot(String rollNo, Long id) {

        Long count = entityManager.createQuery(
                        "SELECT COUNT(s) FROM Student s " +
                                "WHERE LOWER(s.rollNo) = LOWER(:rollNo) " +
                                "AND s.id <> :id",
                        Long.class
                ).setParameter("rollNo", rollNo)
                .setParameter("id", id)
                .getSingleResult();

        return count > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Student> findByRollNo(String rollNo) {

        List<Student> result = entityManager.createQuery(
                        "SELECT s FROM Student s " +
                                "WHERE LOWER(s.rollNo) = LOWER(:rollNo)",
                        Student.class
                ).setParameter("rollNo", rollNo)
                .getResultList();

        return result.stream().findFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> findByClassName(String className) {

        return entityManager.createQuery(
                        "SELECT s FROM Student s " +
                                "JOIN FETCH s.parent " +
                                "LEFT JOIN FETCH s.bus " +
                                "WHERE LOWER(s.className) = LOWER(:className) " +
                                "ORDER BY s.name",
                        Student.class
                ).setParameter("className", className)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> findByName(String name) {

        return entityManager.createQuery(
                        "SELECT s FROM Student s " +
                                "WHERE LOWER(s.name) LIKE LOWER(:name) " +
                                "ORDER BY s.name",
                        Student.class
                ).setParameter("name", "%" + name.trim() + "%")
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> findByBusId(Long busId) {

        return entityManager.createQuery(
                        "SELECT s FROM Student s " +
                                "JOIN FETCH s.parent " +
                                "LEFT JOIN FETCH s.bus " +
                                "WHERE s.bus.id = :busId " +
                                "ORDER BY s.name",
                        Student.class
                ).setParameter("busId", busId)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> searchStudents(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }

        return entityManager.createQuery(
                        "SELECT s FROM Student s " +
                                "JOIN FETCH s.parent " +
                                "LEFT JOIN FETCH s.bus " +
                                "WHERE LOWER(s.name) LIKE :keyword " +
                                "OR LOWER(s.rollNo) LIKE :keyword " +
                                "OR LOWER(s.className) LIKE :keyword " +
                                "OR LOWER(s.email) LIKE :keyword " +
                                "ORDER BY s.id DESC",
                        Student.class
                ).setParameter("keyword", "%" + keyword.trim().toLowerCase() + "%")
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {

        return entityManager.createQuery(
                "SELECT COUNT(s) FROM Student s",
                Long.class
        ).getSingleResult();
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveStudents() {

        return entityManager.createQuery(
                "SELECT COUNT(s) FROM Student s WHERE s.active = true",
                Long.class
        ).getSingleResult();
    }
}