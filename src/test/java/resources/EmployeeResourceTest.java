package resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.freshworks.ems.exceptions.DepartmentNotFound;
import com.freshworks.ems.exceptions.EmployeeNotFound;
import com.freshworks.ems.model.Employee;
import com.freshworks.ems.resources.EmployeeResource;
import com.freshworks.ems.service.DepartmentService;
import com.freshworks.ems.service.EmployeeService;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import util.ResourceTestHelper;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.isA;

public class EmployeeResourceTest extends BaseResourceTest{
    private static final EmployeeService EMPLOYEE_SERVICE = mock(EmployeeService.class);

    @ClassRule
    public static final ResourceTestRule RESOURCE_TEST_RULE
                                = ResourceTestHelper.resourceTestRuleBuilder(resourceObject());

    private static DepartmentService departmentService;
    private static EmployeeService employeeService;
    private Employee inputEmployee, expectedEmployee, employeeWithNonExistingDepartmentId;
    private String BASE_URL;
    private String inputEmployeeAsJsonString, updatedEmployeeAsJsonString;

    private static EmployeeResource resourceObject() {
        departmentService = new DepartmentService();
        employeeService = new EmployeeService(departmentService);
        return new EmployeeResource(employeeService);
    }

    @Before
    public void setUp(){
        BASE_URL = "http://localhost:8080/employees/";
        inputEmployee = new Employee(1,"akhil");
        expectedEmployee = inputEmployee;
        employeeWithNonExistingDepartmentId = new Employee( 100, "mahesh");
        employeeService.addEmployeeForTests(new Employee(1,1,"akhil"));
    }

    @Test
    public void shouldInvalidUrlThrowsNotFoundError() {
        Response response = resourceTestHelper.requestBuilder("/ems").get(Response.class);
        assertNotFoundResponse(response);
    }

    @Test
    public void shouldCreateEmployee() throws Exception {
        when(EMPLOYEE_SERVICE.createEmployee(isA(Employee.class))).thenReturn(expectedEmployee);
        Response response = resourceTestHelper.requestBuilder(BASE_URL).post(Entity.json(inputEmployee));
        inputEmployee.setEmpId(1);
        assertCreatedResponse(response, inputEmployee);
    }

    @Test
    public void shouldThrowUnprocessibleEntityErrorWhileCreatingEmployeeByGivingEmployeeId() {
        inputEmployeeAsJsonString = "{\"empId\":1,\"depId\":1,\"name\":\"akhil\"}";
        Response response = resourceTestHelper.requestBuilder(BASE_URL).post(Entity.json(inputEmployeeAsJsonString ));
        assertUnprocessibleEntityResponse(response);
    }

    @Test
    public void shouldThrowUnprocessibleEntityErrorWhileCreatingEmployeeWithEmptyDetails() {
        inputEmployeeAsJsonString  = "{}";
        Response response = resourceTestHelper.requestBuilder(BASE_URL).post(Entity.json(inputEmployeeAsJsonString ));
        assertUnprocessibleEntityResponse(response);
    }

    @Test
    public void shouldThrowUnprocessibleEntityErrorWhileCreatingEmployeeWithEmptyName() {
        inputEmployeeAsJsonString  = "{\"name\":\"\"}";
        Response response = resourceTestHelper.requestBuilder(BASE_URL).post(Entity.json(inputEmployeeAsJsonString ));
        assertUnprocessibleEntityResponse(response);
    }

    @Test
    public void shouldThrowUnprocessibleEntityErrorWhileCreatingEmployeeWithoutDepartmentId(){
        inputEmployeeAsJsonString  = "{\"name\":\"akhil\"}";
        Response response = resourceTestHelper.requestBuilder(BASE_URL).post(Entity.json(inputEmployeeAsJsonString ));
        assertUnprocessibleEntityResponse(response);
    }

    @Test
    public void shouldThrowUnprocessibleEntityErrorWhileCreatingEmployeeWithBadEmailFormat() {
        inputEmployeeAsJsonString = "{\"depId\":1,\"name\":\"akhil\",\"email\":\"akhil.com\"}";
        Response response = resourceTestHelper.requestBuilder(BASE_URL).post(Entity.json(inputEmployeeAsJsonString));
        assertUnprocessibleEntityResponse(response);
    }

    @Test
    public void shouldThrowUnprocessibleEntityErrorWhileCreatingEmployeeWithNegativeAge() {
        inputEmployeeAsJsonString = "{\"depId\":1,\"name\":\"akhil\",\"age\":-1}";
        Response response = resourceTestHelper.requestBuilder(BASE_URL).post(Entity.json(inputEmployeeAsJsonString));
        assertUnprocessibleEntityResponse(response);
    }


    @Test
    public void shouldThrowBadRequestWhileEmployeeCreatesWithNonExistingDepartmentId(){
        Response response = resourceTestHelper.requestBuilder(BASE_URL)
                                              .post(Entity.json(employeeWithNonExistingDepartmentId));
        assertBadRequestResponse(response);
    }

    @Test
    public void shouldThrowBadRequestWhileEmployeeCreatesWithDepartmentIdAsString() {
        final String inputEmployee = "{\"empId\":null,\"depId\":\"string\",\"name\":\"akhil\"}";
        Response response = resourceTestHelper.requestBuilder(BASE_URL).post(Entity.json(inputEmployee));
        assertBadRequestResponse(response);
    }

    @Test
    public void shouldThrowBadRequestWhileCreatingEmployeeWithAgeAsString() {
        inputEmployeeAsJsonString = "{\"empId\":null,\"depId\":\"1\",\"name\":\"akhil\",\"age\":\"string\"}";
        Response response = resourceTestHelper.requestBuilder(BASE_URL).post(Entity.json(inputEmployeeAsJsonString));
        assertBadRequestResponse(response);
    }

    @Test
    public void shouldThrowBadRequestWhileCreatingEmployeeWithNonExistingFields(){
        inputEmployeeAsJsonString = "{\"empId\":null,\"depId\":1,\"name\":\"akhil\",\"address\":null}";
        Response response = resourceTestHelper.requestBuilder(BASE_URL).post(Entity.json(inputEmployeeAsJsonString));
        assertBadRequestResponse(response);
    }

    @Test
    public void shouldThrowMethodNotAllowedErrorWhileCreatingEmployeeWithInvalidHttpMethod() {
        inputEmployee = new Employee(13,1,"google");
        Response response = resourceTestHelper.requestBuilder( BASE_URL + inputEmployee.getEmpId())
                .post(Entity.json(inputEmployee));
        assertMethodNotAllowedResponse(response);
    }

    @Test
    public void shouldGetEmployeeById() throws EmployeeNotFound, JsonProcessingException {
        expectedEmployee.setEmpId(1);
        when(EMPLOYEE_SERVICE.getEmployeeById(expectedEmployee.getEmpId())).thenReturn(expectedEmployee);
        Response response = resourceTestHelper.requestBuilder(BASE_URL + expectedEmployee.getEmpId()).get();
        assertOkResponse(response, expectedEmployee);
    }

    @Test
    public void shouldThrowNotFoundErrorWhileGettingNonExistingEmployee() {
        Integer nonExistingEmployeeId = 122;
        Response response = resourceTestHelper.requestBuilder(BASE_URL + nonExistingEmployeeId).get();
        assertNotFoundResponse(response);
    }

    @Test
    public void shouldEmployeeGetDeletedById() throws EmployeeNotFound {
        Integer employeeId = inputEmployee.getDepId();
        doNothing().when(EMPLOYEE_SERVICE).deleteEmployee(eq(employeeId));
        Response response = resourceTestHelper.requestBuilder(BASE_URL + employeeId).delete();
        assertNoContentResponse(response);
    }

    @Test
    public void shouldThrowNotFoundErrorWhileDeletingNonExistingEmployee(){
        Integer deletingEmployeeId = 123;
        Response response = resourceTestHelper.requestBuilder(BASE_URL + deletingEmployeeId).delete();
        assertNotFoundResponse(response);
    }

    @Test
    public void shouldThrowMethodNotAllowedErrorDeletingEmployeeWithInvalidHttpMethod() {
        Response response = resourceTestHelper.requestBuilder(BASE_URL).delete();
        assertMethodNotAllowedResponse(response);
    }

    @Test
    public void shouldUpdateEmployee() throws EmployeeNotFound, DepartmentNotFound, JsonProcessingException {
        inputEmployee.setEmpId(1);
        Integer actualEmployeeId = inputEmployee.getEmpId();
        Employee updatedEmployee = new Employee( 1,"freshworks");
        when(EMPLOYEE_SERVICE.updateEmployee(actualEmployeeId, updatedEmployee)).thenReturn(updatedEmployee);
        Response response = resourceTestHelper.requestBuilder(BASE_URL + actualEmployeeId)
                                              .put(Entity.json(updatedEmployee));
        assertOkResponse(response, updatedEmployee);
    }

    @Test
    public void shouldThrowNotFoundErrorWhileUpdatingNonExistingEmployee(){
        inputEmployee.setEmpId(123);
        Integer updatingEmployeeId = inputEmployee.getEmpId();
        updatedEmployeeAsJsonString = "{\"depId\":1,\"name\":\"ramesh\"}";
        Response response = resourceTestHelper.requestBuilder(BASE_URL + updatingEmployeeId)
                                              .put(Entity.json(updatedEmployeeAsJsonString));
        assertNotFoundResponse(response);
    }

    @Test
    public void shouldThrowBadRequestWhileUpdatingEmployeeWithNonExistingDepartmentId() {
        inputEmployee.setEmpId(1);
        Integer actualEmployeeId = inputEmployee.getEmpId();
        updatedEmployeeAsJsonString = "{\"depId\":123,\"name\":\"ramesh\"}";
        Response response = resourceTestHelper.requestBuilder(BASE_URL + actualEmployeeId)
                                              .put(Entity.json(updatedEmployeeAsJsonString));
        assertBadRequestResponse(response);
    }

    @Test
    public void shouldThrowUnprocessibleEntityErrorWhileUpdatingEmployeeWithEmptyFields(){
        Employee actualEmployee = inputEmployee;
        actualEmployee.setEmpId(1);
        updatedEmployeeAsJsonString = "{}";
        Integer actualEmployeeId = actualEmployee.getDepId();
        Response response = resourceTestHelper.requestBuilder(BASE_URL + actualEmployeeId)
                                              .put(Entity.json(updatedEmployeeAsJsonString));
        assertUnprocessibleEntityResponse(response);
    }

    @Test
    public void shouldThrowMethodNotAllowedErrorWhileUpdatingEmployeeWithInvalidHttpMethod() {
        String inputEmployee = "{\"depId\":1,\"name\":\"google\"}";
        Response response = resourceTestHelper.requestBuilder(BASE_URL).put(Entity.json(inputEmployee));
        assertMethodNotAllowedResponse(response);
    }

    @Test
    public void shouldGetSortedEmployeesBasedOnAge() {
        Response response = resourceTestHelper.requestBuilder("http://localhost:8080/employees?=" + "age/").get();
        assertOkResponse(response);
    }

    @Test
    public void shouldGetSortedEmployeesBasedOnName() {
        Response response = resourceTestHelper.requestBuilder("http://localhost:8080/employees?=" + "name/").get();
        assertOkResponse(response);
    }

    @Test
    public void shouldThrowsBadRequestWhileSortingWithIllegalArguments() {
        Response response = resourceTestHelper.requestBuilder("http://localhost:8080/employees?value=" + "email/")
                                              .get();
        assertNotFoundResponse(response);
    }

    @Override
    protected void populateStubData(){
        super.populateStubData();
    }

    @Override
    protected EmployeeService serviceMock(){
        return EMPLOYEE_SERVICE;
    }

    @Override
    protected ResourceTestRule getResourceTestRule(){
        return RESOURCE_TEST_RULE;
    }
}