package resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.freshworks.ems.exceptions.DepartmentNotFound;
import com.freshworks.ems.model.Department;
import com.freshworks.ems.model.Employee;
import com.freshworks.ems.resources.DepartmentResource;
import com.freshworks.ems.service.DepartmentService;
import com.freshworks.ems.service.EmployeeService;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;
import org.junit.Test;
import util.ResourceTestHelper;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.util.Collections;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class DepartmentResourceTest extends BaseResourceTest{
     private static final DepartmentService DEPARTMENT_SERVICE = mock(DepartmentService.class);

     @ClassRule
     public static final ResourceTestRule RESOURCE_TEST_RULE
             = ResourceTestHelper.resourceTestRuleBuilder(resourceObject());

    private String BASE_URL;
    private static EmployeeService employeeService;
    private static DepartmentService departmentService;
    private Department inputDepartment, expectedDepartment;
    private String inputDepartmentAsJsonString, updatedDepartmentAsJsonString;

     private static DepartmentResource resourceObject() {
         employeeService = new EmployeeService();
         departmentService = new DepartmentService(employeeService);
         return new DepartmentResource(departmentService);
    }

    @Test
    public void shouldThrowsNotFoundErrorWithInvalidUrl() {
        Response response = resourceTestHelper.requestBuilder("ems").get(Response.class);
        assertNotFoundResponse(response);
    }

    @Test
    public void shouldCreateDepartment() throws JsonProcessingException {
        when(DEPARTMENT_SERVICE.createDepartment(inputDepartment)).thenReturn(expectedDepartment);
        Response response = resourceTestHelper.requestBuilder(BASE_URL).post(Entity.json(inputDepartment));
        inputDepartment.setId(1);
        assertCreatedResponse(response, inputDepartment);
    }

    @Test
    public void shouldThrowUnprocessibleEntityErrorWhileCreatingDepartmentWithDepartmentId(){
        inputDepartmentAsJsonString = "{\"depId\":1,\"name\":\"google\"}";
        Response response = resourceTestHelper.requestBuilder(BASE_URL).post(Entity.json(inputDepartmentAsJsonString));
        assertUnprocessibleEntityResponse(response);
    }

    @Test
    public void shouldThrowUnprocessibleEntityErrorWhileCreatingDepartmentWithEmptyName(){
        inputDepartmentAsJsonString = "{\"name\":\"\"}";
        Response response = resourceTestHelper.requestBuilder(BASE_URL).post(Entity.json(inputDepartmentAsJsonString));
        assertUnprocessibleEntityResponse(response);
    }

    @Test
    public void shouldThrowUnprocessibleEntityErrorWhileCreatingDepartmentWithEmptyFields(){
         inputDepartmentAsJsonString = "{}";
         Response response = resourceTestHelper.requestBuilder(BASE_URL).post(Entity.json(inputDepartmentAsJsonString));
         assertUnprocessibleEntityResponse(response);
    }

    @Test
    public void shouldThrowBadRequestWhileCreatingDepartmentWithNonExistingFields(){
        final String inputDepartment = "{\"depId\":null,\"name\":\"google\",\"age\":null}";
        Response response = resourceTestHelper.requestBuilder(BASE_URL).post(Entity.json(inputDepartment));
        assertBadRequestResponse(response);
    }

    @Test
    public void shouldThrowMethodNotAllowedErrorWhileCreatingDepartmentWithInvalidHttpMethod() {
        inputDepartment = new Department(1,"google");
        Response response =  resourceTestHelper.requestBuilder(BASE_URL + inputDepartment.getDepId())
                .post(Entity.json(inputDepartment));
        assertMethodNotAllowedResponse(response);
    }

    @Test
    public void shouldGetDepartment() throws DepartmentNotFound, JsonProcessingException {
         expectedDepartment.setId(1);
         Integer expectedDepartmentId = expectedDepartment.getDepId();
         when(DEPARTMENT_SERVICE.getDepartmentById(eq(expectedDepartmentId))).thenReturn(expectedDepartment);
         Response response = resourceTestHelper.requestBuilder(BASE_URL + expectedDepartmentId).get();
         assertOkResponse(response, expectedDepartment);
    }

    @Test
    public void shouldThrowNotFoundErrorWhileGettingDetailsOfNonExistingDepartment(){
        Integer expectedDepartmentId = 112;
        Response response = resourceTestHelper.requestBuilder(BASE_URL + expectedDepartmentId).get();
        assertNotFoundResponse(response);
    }

    @Test
    public void shouldGetEmployeesOfADepartment() throws Exception {
        inputDepartment.setId(2);
        Integer expectedDepartmentId = inputDepartment.getDepId();
        when(DEPARTMENT_SERVICE.getEmpsOfDepartment(eq(expectedDepartmentId)))
                .thenReturn(Collections.singletonList(new Employee()));
        Response response = resourceTestHelper.requestBuilder(BASE_URL + expectedDepartmentId + "/employees").get();
        assertOkResponse(response);
    }

    @Test
    public void shouldThrowDepartmentNotFoundExceptionWhileGettingEmployeesOfNonExistingDepartment(){
        inputDepartment.setId(123);
        Integer inputDepartmentId = inputDepartment.getDepId();
        Response response = resourceTestHelper.requestBuilder(BASE_URL + inputDepartmentId + "/employees").get();
        assertNotFoundResponse(response);
    }

    @Test
    public void shouldDeleteDepartment() throws DepartmentNotFound {
        inputDepartment.setId(1);
        Integer removingDepartmentId = inputDepartment.getDepId();
        doNothing().when(DEPARTMENT_SERVICE).removeDepartment(removingDepartmentId);
        Response response = resourceTestHelper.requestBuilder(BASE_URL + removingDepartmentId).delete();
        assertNoContentResponse(response);
    }

    @Test
    public void shouldThrowDepartmentFoundErrorWhileDeletingANonExistingDepartment() {
        Integer removingDepartmentId = 123;
        Response response = resourceTestHelper.requestBuilder(BASE_URL + removingDepartmentId).delete();
        assertNotFoundResponse(response);
    }

    @Test
    public void shouldThrowMethodNotAllowedErrorDeletingDepartmentWithInvalidHttpMethod() {
        Response response = resourceTestHelper.requestBuilder(BASE_URL).delete();
        assertMethodNotAllowedResponse(response);
    }


    @Test
    public void shouldUpdateDepartment() throws DepartmentNotFound, JsonProcessingException {
        inputDepartment.setId(2);
        Integer actualDepartmentId = inputDepartment.getDepId();
        Department updatedDepartment  = new Department( "freshworks");
        when(DEPARTMENT_SERVICE.updateDepartment(actualDepartmentId, updatedDepartment)).thenReturn(updatedDepartment);
        Response response = resourceTestHelper.requestBuilder(BASE_URL + actualDepartmentId)
                                              .put(Entity.json(updatedDepartment));
        assertOkResponse(response, updatedDepartment);
    }

    @Test
    public void shouldThrowDepartmentFoundErrorWhileUpdatingANonExistingDepartment(){
        inputDepartment.setId(123);
        Integer actualDepartmentId = inputDepartment.getDepId();
        updatedDepartmentAsJsonString = "{\"name\":\"freshworks\"}";
        Response response = resourceTestHelper.requestBuilder(BASE_URL + actualDepartmentId)
                                              .put(Entity.json(updatedDepartmentAsJsonString));
        assertNotFoundResponse(response);
    }

    @Test
    public void shouldThrowUnprocessibleEntityErrorWhileUpdatingDepartmentWithEmptyFields() {
        Department actualDepartment = inputDepartment;
        actualDepartment.setId(1);
        updatedDepartmentAsJsonString = "{}";
        Integer actualDepartmentId = actualDepartment.getDepId();
        Response response = resourceTestHelper.requestBuilder(BASE_URL + actualDepartmentId)
                                              .put(Entity.json(updatedDepartmentAsJsonString));
        assertUnprocessibleEntityResponse(response);
    }

    @Test
    public void shouldThrowMethodNotAllowedErrorUpdatingDepartmentWithInvalidHttpMethod() {
        inputDepartmentAsJsonString = "{\"name\":\"freshworks\"}";
        Response response =  resourceTestHelper.requestBuilder(BASE_URL).put(Entity.json(inputDepartmentAsJsonString));
        assertMethodNotAllowedResponse(response);
    }


    @Override
    protected ResourceTestRule getResourceTestRule(){
        return RESOURCE_TEST_RULE;
    }

    @Override
    protected void populateStubData(){
        super.populateStubData();
        BASE_URL = "http://localhost:8080/departments/";
        inputDepartment = new Department( "google");
        expectedDepartment = inputDepartment;
    }

    @Override
    protected DepartmentService serviceMock(){
        return DEPARTMENT_SERVICE;
    }
}
