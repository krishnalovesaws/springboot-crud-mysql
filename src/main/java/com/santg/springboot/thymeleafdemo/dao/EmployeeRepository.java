package com.santg.springboot.thymeleafdemo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.santg.springboot.thymeleafdemo.entity.Employee;

//@RepositoryRestResource(path="members")
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

	// that's it
	
	// add method to sort by last name
	public List<Employee> findAllByOrderByLastNameAsc();
}
