package student.mgt.dao;

import student.mgt.entity.Student;
import java.util.List;
import java.util.Optional;

public interface StudentDao {

    Student save(Student student);

    Student update(Student student);

    Optional<Student> findById(Long id);

    List<Student> findAll();

    void deleteById(Long id);

    boolean existsByRollNo(String rollNo);

    boolean existsByRollNoAndIdNot(String rollNo, Long id);

    Optional<Student> findByRollNo(String rollNo);

    List<Student> findByClassName(String className);

    List<Student> findByName(String name);

    List<Student> findByBusId(Long busId);

    List<Student> searchStudents(String keyword);

    long count();

    long countActiveStudents();
}