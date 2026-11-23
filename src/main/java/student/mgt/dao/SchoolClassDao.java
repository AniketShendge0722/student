package student.mgt.dao;

import student.mgt.entity.SchoolClass;

import java.util.List;

public interface SchoolClassDao {

    List<SchoolClass> findAllActive();
}