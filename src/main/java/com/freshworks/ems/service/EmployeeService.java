package com.freshworks.ems.service;

import com.freshworks.ems.exceptions.DepartmentNotFound;
import com.freshworks.ems.exceptions.EmployeeNotFound;
import com.freshworks.ems.model.Employee;
import com.freshworks.ems.util.AgeSorter;
import com.freshworks.ems.util.NameSorter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class EmployeeService {
    private final Map<Integer, Employee> hashMap;
    private final Logger logger = LoggerFactory.getLogger(EmployeeService.class);
    private DepartmentService departmentService;
    private AtomicInteger counter = new AtomicInteger(0);

    public EmployeeService(){
        hashMap = new HashMap<>();
    }

    public EmployeeService(DepartmentService departmentService){
        hashMap = new HashMap<>();
        this.departmentService = departmentService;
    }
    
    private boolean isEmployeeExists(Integer empId) throws EmployeeNotFound {
        if(!hashMap.containsKey(empId)) {
            throw new EmployeeNotFound();
        }
        return true;
    }

    public List<Employee> getAllEmployees(String attribute) {
        if(attribute.isEmpty()) {
            return new ArrayList<>(hashMap.values());
        }
        return sortEmployees(attribute);
    }

    public Employee getEmployeeById(Integer empId) throws EmployeeNotFound {
        try{
            if(isEmployeeExists(empId)) {
                logger.debug("Employee {} details were fetched", empId);
            }
        }
        catch (EmployeeNotFound employeeNotFound){
            logger.error("Employee {} not exists", empId);
            throw employeeNotFound;
        }
        return hashMap.get(empId);
    }

    public List<Employee> getEmpsOfDepartment(Integer depId){
        List<Employee> employeeList = new ArrayList<>();
        hashMap.forEach((key, value)-> {
            if(value.getDepId() == depId)
                employeeList.add(value);
        });
        return employeeList;
    }

    public Employee createEmployee(Employee employee) throws DepartmentNotFound {
        Integer empId = counter.incrementAndGet();
        Integer depId = employee.getDepId();
        employee.setEmpId(empId);
        try{
            if(departmentService.isDepartmentExists(depId)){
                hashMap.put(empId, employee);
                logger.debug("Employee {} details fetched", empId);
            }
        }
        catch(DepartmentNotFound departmentNotFound){
            counter.decrementAndGet();
            logger.error("Department {} was not exists", depId);
            throw departmentNotFound;
        }
        return employee;
    }

    public Employee updateEmployee(Integer empId, Employee employee) throws DepartmentNotFound, EmployeeNotFound {
        try{
            if(isEmployeeExists(empId) && departmentService.isDepartmentExists(employee.getDepId())) {
                hashMap.get(empId).setName(employee.getName());
                logger.debug("Employee {} details updated", empId);
            }
        }
        catch(DepartmentNotFound departmentNotFound){
            logger.error("Department {} not exists", empId);
            throw departmentNotFound;
        }
        catch(EmployeeNotFound employeeNotFound){
            logger.error("Employee {} not exists", empId);
            throw employeeNotFound;
        }
        return employee;
    }

    public void deleteEmployee(Integer empId) throws EmployeeNotFound {
        try{
            if(isEmployeeExists(empId)) {
                hashMap.remove(empId);
                logger.debug("Employee {} was deleted", empId);
            }
        }
        catch(EmployeeNotFound employeeNotFound){
            logger.error("Employee {} not exists", empId);
            throw employeeNotFound;
        }
    }

    public List<Employee> sortEmployees(String attribute){
        List<Employee> employeeList = new ArrayList<>();
        hashMap.forEach((key, value)-> {
            employeeList.add(value);
        });

        if(attribute.equals("age")) {
            try {
                Collections.sort(employeeList, new AgeSorter());
            }
            catch(NullPointerException nullPointerException){
               throw nullPointerException;
           }
        }
        else if(attribute.equals("name")){
            Collections.sort(employeeList, new NameSorter());
        }
        else{
           throw new IllegalArgumentException();
        }
        return employeeList;
    }

    public void addEmployeeForTests(Employee employee){
        hashMap.put(employee.getEmpId(), employee);
    }
}
