
package student.mgt.daoImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import student.mgt.dao.ParentDao;
import student.mgt.entity.Parent;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class ParentDaoImpl implements ParentDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Parent save(Parent parent) {

        entityManager.persist(parent);
        entityManager.flush();

        return parent;
    }

    @Override
    public Parent update(Parent parent) {

        Parent updated = entityManager.merge(parent);
        entityManager.flush();

        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Parent> findById(Long id) {

        return Optional.ofNullable(
                entityManager.find(Parent.class, id)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Parent> findAll() {

        return entityManager.createQuery(
                "SELECT DISTINCT p FROM Parent p " +
                        "LEFT JOIN FETCH p.students " +
                        "ORDER BY p.id DESC",
                Parent.class
        ).getResultList();
    }

    @Override
    public void deleteById(Long id) {

        Parent parent = entityManager.find(Parent.class, id);

        if (parent != null) {
            entityManager.remove(parent);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Parent> findByEmail(String email) {

        List<Parent> result = entityManager.createQuery(
                        "SELECT p FROM Parent p " +
                                "WHERE LOWER(p.email) = LOWER(:email)",
                        Parent.class
                )
                .setParameter("email", email)
                .getResultList();

        return result.stream().findFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {

        Long count = entityManager.createQuery(
                        "SELECT COUNT(p) FROM Parent p " +
                                "WHERE LOWER(p.email) = LOWER(:email)",
                        Long.class
                )
                .setParameter("email", email)
                .getSingleResult();

        return count > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByPhone(String phone) {

        Long count = entityManager.createQuery(
                        "SELECT COUNT(p) FROM Parent p " +
                                "WHERE p.phone = :phone",
                        Long.class
                )
                .setParameter("phone", phone)
                .getSingleResult();

        return count > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByPhoneAndIdNot(String phone, Long id) {

        Long count = entityManager.createQuery(
                        "SELECT COUNT(p) FROM Parent p " +
                                "WHERE p.phone = :phone " +
                                "AND p.id <> :id",
                        Long.class
                )
                .setParameter("phone", phone)
                .setParameter("id", id)
                .getSingleResult();

        return count > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Parent> searchParents(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }

        return entityManager.createQuery(
                "SELECT DISTINCT p FROM Parent p " +
                        "LEFT JOIN FETCH p.students " +
                        "WHERE LOWER(p.fatherName) LIKE :keyword " +
                        "OR LOWER(p.motherName) LIKE :keyword " +
                        "OR LOWER(p.email) LIKE :keyword " +
                        "OR p.phone LIKE :keyword " +
                        "ORDER BY p.id DESC",
                Parent.class
        ).setParameter(
                "keyword",
                "%" + keyword.trim().toLowerCase() + "%"
        ).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {

        return entityManager.createQuery(
                "SELECT COUNT(p) FROM Parent p",
                Long.class
        ).getSingleResult();
    }
}
