package service;

import com.freshworks.ems.exceptions.DepartmentNotFound;
import com.freshworks.ems.exceptions.EmployeeNotFound;
import com.freshworks.ems.model.Employee;
import com.freshworks.ems.service.DepartmentService;
import com.freshworks.ems.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.doNothing;

public class EmployeeServiceTest {
    private final EmployeeService EMPLOYEE_SERVICE = mock(EmployeeService.class);
    private DepartmentService departmentService;
    private EmployeeService employeeService;
    private Employee employee;
    private Integer existingEmployeeId, nonExistingEmployeeId;

    @Before
    public void setUp(){
        departmentService = new DepartmentService();
        employeeService = new EmployeeService(departmentService);
        existingEmployeeId = new Integer(1);
        nonExistingEmployeeId = new Integer(10);
        employee = new Employee(1,1,"akhil",21);
        employeeService.addEmployeeForTests(new Employee(1,1,"akhil"));
        employeeService.addEmployeeForTests(new Employee(2,2,"mahesh"));
    }

    @Test
    public void shouldEmployeeGetCreated() throws DepartmentNotFound {
        assertSame(employee, employeeService.createEmployee(employee));
    }

    @Test
    public void shouldGetEmployee() throws EmployeeNotFound {
        assertEquals(existingEmployeeId, employeeService.getEmployeeById(existingEmployeeId).getEmpId());
    }

    @Test(expected = EmployeeNotFound.class)
    public void shouldThrowNotFoundExceptionWhileGettingDepartmentWithInvalidId() throws EmployeeNotFound {
        assertEquals(nonExistingEmployeeId, employeeService.getEmployeeById(nonExistingEmployeeId).getEmpId());
    }

    @Test
    public void shouldEmployeesGetByDepartmentId() throws Exception {
        Integer existingDepartmentId = 1;
        when(EMPLOYEE_SERVICE.getEmpsOfDepartment(eq(existingDepartmentId)))
                .thenReturn(Collections.singletonList(new Employee()));
       assertEquals(employee.getEmpId(), employeeService.getEmpsOfDepartment(1).get(0).getEmpId());
    }

    @Test
    public void shouldDeleteEmployee() throws EmployeeNotFound {
        doNothing().when(EMPLOYEE_SERVICE).deleteEmployee(eq(existingEmployeeId));
        employeeService.deleteEmployee(existingEmployeeId);
    }

    @Test(expected = EmployeeNotFound.class)
    public void shouldThrowNotFoundExceptionWhileDeletingNonExistingEmployee() throws EmployeeNotFound {
        employeeService.deleteEmployee(nonExistingEmployeeId);
    }

    @Test
    public void shouldUpdateEmployee() throws Exception {
        Employee updatedEmployee = new Employee(1,"mahesh");
        assertEquals(updatedEmployee.getName(), employeeService.updateEmployee(existingEmployeeId, updatedEmployee).getName());
    }

    @Test(expected = EmployeeNotFound.class)
    public void shouldThrowNotFoundExceptionWhileUpdatingNonExistingEmployee() throws Exception {
        employeeService.updateEmployee(nonExistingEmployeeId, employee);
    }

    @Test
    public void shouldEmployeesGetSortedByAge() throws Exception {
        employeeService.createEmployee(new Employee( 1, "akhil", 54));
        employeeService.createEmployee(new Employee( 1, "mahesh", 31));
        employeeService.createEmployee(new Employee( 1, "ramesh", 21));
        List<Employee> sortedEmployeesList = employeeService.sortEmployees("age");
        assertEquals("ramesh", sortedEmployeesList.get(0).getName());
        assertEquals("mahesh", sortedEmployeesList.get(1).getName());
        assertEquals("akhil", sortedEmployeesList.get(2).getName());
    }

    @Test
    public void shouldEmployeesGetSortedByName() throws Exception {
        employeeService.createEmployee(new Employee(1, "mahesh"));
        employeeService.createEmployee(new Employee(1, "ramesh"));
        employeeService.createEmployee(new Employee( 1, "akhil"));
        List<Employee> sortedEmployeesList = employeeService.sortEmployees("name");
        assertEquals("akhil", sortedEmployeesList.get(0).getName());
        assertEquals("mahesh", sortedEmployeesList.get(1).getName());
        assertEquals("ramesh", sortedEmployeesList.get(2).getName());
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionWhileSortingEmployeesWithAgeAsNull() throws DepartmentNotFound {
        employeeService.createEmployee(new Employee( 1, "akhil"));
        employeeService.createEmployee(new Employee( 1, "mahesh"));
        employeeService.createEmployee(new Employee( 1, "ramesh"));
        employeeService.sortEmployees("age");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhileSortingWithInvalidAttribute(){
        employeeService.sortEmployees("email");
    }
}