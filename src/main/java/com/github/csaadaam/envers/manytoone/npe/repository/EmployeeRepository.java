package com.github.csaadaam.envers.manytoone.npe.repository;

import com.github.csaadaam.envers.manytoone.npe.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, RevisionRepository<Employee, Long, Integer> {
}
