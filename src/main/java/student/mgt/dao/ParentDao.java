
package student.mgt.dao;

import student.mgt.entity.Parent;

import java.util.List;
import java.util.Optional;

public interface ParentDao {

    Parent save(Parent parent);

    Parent update(Parent parent);

    Optional<Parent> findById(Long id);

    List<Parent> findAll();

    void deleteById(Long id);

    Optional<Parent> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByPhoneAndIdNot(String phone, Long id);

    List<Parent> searchParents(String keyword);

    long count();
}
