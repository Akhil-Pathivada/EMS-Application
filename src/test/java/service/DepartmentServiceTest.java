package service;

import com.freshworks.ems.exceptions.DepartmentNotFound;
import com.freshworks.ems.model.Department;
import com.freshworks.ems.model.Employee;
import com.freshworks.ems.service.DepartmentService;
import com.freshworks.ems.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import java.util.Collections;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;

public class DepartmentServiceTest {
    private final EmployeeService EMPLOYEE_SERVICE = mock(EmployeeService.class);
    private final DepartmentService DEPARTMENT_SERVICE = mock(DepartmentService.class);
    private DepartmentService departmentService;
    private Department inputDepartment, updatedDepartment;
    private Integer existingDepartmentId, nonExistingDepartmentId;

    @Before
    public void setUp(){
        departmentService = new DepartmentService(EMPLOYEE_SERVICE);
        inputDepartment = new Department( "google");
        existingDepartmentId = new Integer(1);
        nonExistingDepartmentId = new Integer(123);
    }

    @Test
    public void shouldCreateDepartment() {
        assertSame(inputDepartment, departmentService.createDepartment(inputDepartment));
    }

    @Test
    public void shouldGetDepartmentById() throws Exception {
        inputDepartment.setId(1);
        assertEquals(inputDepartment.toString(), departmentService.getDepartmentById(existingDepartmentId).toString());
    }

    @Test(expected = DepartmentNotFound.class)
    public void shouldThrowNotFoundExceptionWhileGettingDepartmentByInvalidId() throws Exception {
        departmentService.isDepartmentExists(nonExistingDepartmentId);
    }

    @Test
    public void shouldGetEmployeesOfDepartment() throws Exception {
        when(departmentService.getEmpsOfDepartment(existingDepartmentId))
                .thenReturn(Collections.singletonList(new Employee()));
    }

    @Test(expected = DepartmentNotFound.class)
    public void shouldThrowNotFoundExceptionWhileGettingEmployeesOfNonExistingDepartment() throws Exception {
        departmentService.getEmpsOfDepartment(nonExistingDepartmentId);
    }

    @Test
    public void shouldDeleteDepartment() throws Exception{
        doNothing().when(DEPARTMENT_SERVICE).removeDepartment(existingDepartmentId);
        departmentService.removeDepartment(existingDepartmentId);
    }

    @Test(expected = DepartmentNotFound.class)
    public void shouldThrowNotFoundExceptionWhileDeletingDepartmentByInvalidId() throws Exception{
       departmentService.removeDepartment(nonExistingDepartmentId);
   }

    @Test
    public void shouldUpdateDepartment() throws Exception {
        updatedDepartment = new Department("amazon");
        assertEquals(updatedDepartment.getName(), departmentService.updateDepartment(1, updatedDepartment).getName());
    }

   @Test(expected = DepartmentNotFound.class)
    public void shouldThrowNotFoundExceptionWhileUpdatingDepartmentByNonExistingId() throws Exception {
       updatedDepartment = new Department("amazon");
       departmentService.updateDepartment(nonExistingDepartmentId, updatedDepartment);
   }
}