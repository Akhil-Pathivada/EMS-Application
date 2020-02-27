package com.freshworks.ems.service;

import com.freshworks.ems.exceptions.DepartmentNotFound;
import com.freshworks.ems.exceptions.EmployeeNotFound;
import com.freshworks.ems.model.Department;
import com.freshworks.ems.model.Employee;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DepartmentService {
    private final Map<Integer, Department> hashMap;
    private final Logger logger = LoggerFactory.getLogger(DepartmentService.class);
    private EmployeeService employeeService;
    private AtomicInteger counter = new AtomicInteger(0);
    private Object object;

    public DepartmentService() {
        hashMap = new HashMap<>();
        hashMap.put(1, new Department(1, "google"));
    }

    public DepartmentService(Object object) {
        hashMap = new HashMap<>();
        this.object = object;
        loadInitialDepartmentData();
    }

    public DepartmentService(EmployeeService employeeService) {
        this.employeeService = employeeService;
        hashMap = new HashMap<>();
        hashMap.put(1, new Department(1, "google"));
        hashMap.put(2, new Department(2, "amazon"));
    }
    
     @SuppressWarnings(value = "unchecked")
    private void loadInitialDepartmentData() {
        JSONArray departmentList = (JSONArray) object;
        departmentList.forEach(dep -> parseDepartmentObject((JSONObject) dep));
    }

    private void parseDepartmentObject(JSONObject department) {
        JSONObject departmentObject = (JSONObject) department.get("department");
        Integer depId = counter.incrementAndGet();
        String name = (String) departmentObject.get("name");
        Department depObj = new Department(depId, name);
        hashMap.put(depId, depObj);
    }

    public void createEmployeeServiceObject(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public List<Department> getAllDepartments() {
        return new ArrayList<>(hashMap.values());
    }

    public Department getDepartmentById(Integer depId) throws DepartmentNotFound{
        try {
            if (isDepartmentExists(depId)) {
                logger.debug("Department {} details were fetched", depId);
            }
        }
        catch (DepartmentNotFound departmentNotFound) {
            logger.error("Department {} was not found", depId);
            throw departmentNotFound;
        }
        return hashMap.get(depId);
    }

    public Department createDepartment(Department department) {
        Integer depId = counter.incrementAndGet();
        department.setId(depId);
        hashMap.put(depId, department);
        logger.debug("Department {} was created {}", depId, department);
        return department;
    }

    public List<Employee> getEmpsOfDepartment(Integer depId) throws Exception {
        try {
            if (isDepartmentExists(depId)) {
                logger.debug("Employees of Department {} were fetched", depId);
            }
        }
        catch (DepartmentNotFound departmentNotFound) {
            logger.error("Department {} was not found", depId);
            throw departmentNotFound;
        }
        return employeeService.getEmpsOfDepartment(depId);
    }

    public void removeDepartment(Integer depId) throws DepartmentNotFound {
        try {
            if (isDepartmentExists(depId)) {
                List<Employee> employeeList = employeeService.getEmpsOfDepartment(depId);
                employeeList.forEach(emp -> {
                    try {
                        employeeService.deleteEmployee(emp.getEmpId());
                    }
                    catch (EmployeeNotFound employeeNotFound) {
                        employeeNotFound.printStackTrace();
                    }
                });
                hashMap.remove(depId);
                logger.debug("Department {} was deleted ", depId);
            }
        }
        catch (DepartmentNotFound departmentNotFound) {
            logger.error("Department {} was not found", depId);
            throw departmentNotFound;
        }
    }

    public Department updateDepartment(Integer depId, Department depObj) throws DepartmentNotFound {
        try {
            if (isDepartmentExists(depId)) {
                hashMap.get(depId).setName(depObj.getName());
                logger.debug("Department {} was Updated", depId);
            }
        }
        catch (DepartmentNotFound departmentNotFound) {
            logger.error("Department {} was not found", depId);
            throw departmentNotFound;
        }
        return hashMap.get(depId);
    }

    public boolean isDepartmentExists(Integer depId) throws DepartmentNotFound {
        if (!hashMap.containsKey(depId)) {
            throw new DepartmentNotFound();
        }
        return true;
    }
}




