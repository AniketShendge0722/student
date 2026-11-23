package student.mgt.service;

import student.mgt.dto.StudentDTO;

import java.util.List;

public interface StudentService {

    StudentDTO save(StudentDTO studentDTO);

    StudentDTO update(Long id, StudentDTO studentDTO);

    StudentDTO findById(Long id);

    List<StudentDTO> findAll();

    void deleteById(Long id);

    StudentDTO findByRollNo(String rollNo);

    List<StudentDTO> findByClassName(String className);

    List<StudentDTO> findByName(String name);

    List<StudentDTO> findByBusId(Long busId);

    List<StudentDTO> searchStudents(String keyword);

    long count();

    long countActiveStudents();
}