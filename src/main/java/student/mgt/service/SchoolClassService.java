package student.mgt.service;

import student.mgt.entity.SchoolClass;

import java.util.List;

public interface SchoolClassService {

    List<SchoolClass> findAllActive();
}