
package student.mgt.dao;

import student.mgt.entity.AppUser;

import java.util.List;
import java.util.Optional;

public interface AppUserDao {

    AppUser save(AppUser user);

    AppUser update(AppUser user);

    Optional<AppUser> findById(Long id);

    Optional<AppUser> findByUsername(String username);

    List<AppUser> findAll();

    boolean existsByUsername(String username);

    boolean existsByUsernameAndIdNot(String username, Long id);
}
