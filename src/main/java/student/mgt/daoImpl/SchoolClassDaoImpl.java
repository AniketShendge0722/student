package student.mgt.daoImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import student.mgt.dao.SchoolClassDao;
import student.mgt.entity.SchoolClass;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public class SchoolClassDaoImpl implements SchoolClassDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<SchoolClass> findAllActive() {

        TypedQuery<SchoolClass> query = entityManager.createQuery(
                "SELECT sc FROM SchoolClass sc " +
                        "WHERE sc.active = true " +
                        "ORDER BY sc.id ASC",
                SchoolClass.class
        );

        return query.getResultList();
    }
}