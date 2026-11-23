
package student.mgt.daoImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import student.mgt.dao.AppUserDao;
import student.mgt.entity.AppUser;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class AppUserDaoImpl implements AppUserDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public AppUser save(AppUser user) {
        entityManager.persist(user);
        return user;
    }

    @Override
    public AppUser update(AppUser user) {
        return entityManager.merge(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AppUser> findById(Long id) {
        return Optional.ofNullable(
                entityManager.find(AppUser.class, id)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AppUser> findByUsername(String username) {

        if (username == null || username.isBlank()) {
            return Optional.empty();
        }

        List<AppUser> results = entityManager.createQuery(
                        "SELECT u FROM AppUser u " +
                                "WHERE LOWER(u.username) = LOWER(:username)",
                        AppUser.class
                )
                .setParameter("username", username.trim())
                .setMaxResults(1)
                .getResultList();

        return results.stream().findFirst();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppUser> findAll() {
        return entityManager.createQuery(
                        "SELECT u FROM AppUser u ORDER BY u.createdAt DESC",
                        AppUser.class
                )
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {

        Long count = entityManager.createQuery(
                        "SELECT COUNT(u) FROM AppUser u " +
                                "WHERE LOWER(u.username) = LOWER(:username)",
                        Long.class
                )
                .setParameter("username", username.trim())
                .getSingleResult();

        return count > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsernameAndIdNot(
            String username,
            Long id) {

        Long count = entityManager.createQuery(
                        "SELECT COUNT(u) FROM AppUser u " +
                                "WHERE LOWER(u.username) = LOWER(:username) " +
                                "AND u.id <> :id",
                        Long.class
                )
                .setParameter("username", username.trim())
                .setParameter("id", id)
                .getSingleResult();

        return count > 0;
    }
}
