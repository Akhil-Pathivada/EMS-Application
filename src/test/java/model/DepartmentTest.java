package model;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freshworks.ems.model.Department;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Before;
import org.junit.Test;
import java.io.File;
import java.io.IOException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DepartmentTest {
    public static final ObjectMapper MAPPER = new ObjectMapper();
    Department department;

    @Before
    public void setUp(){
        department = new Department( "google");
    }

    @Test
    public void shouldGetDepartmentId(){
        department.setId(1);
        assertEquals((Integer) 1, department.getDepId());
    }

    @Test
    public void shouldGetEmployeeName(){
        assertEquals("google", department.getName());
    }

    @Test
    public void shouldSetDepartmentId(){
        department.setId(2);
        assertEquals((Integer) 2, department.getDepId());
    }

    @Test
    public void shouldSetEmployeeName(){
        department.setName("Microsoft");
        assertEquals("Microsoft", department.getName());
    }

    @Test
    public void shouldDepartmentSerializesToJson() throws Exception {
        final Department actualDepartment = new Department(1, "google");
        final String expectedDepartment = "{\"depId\":1,\"name\":\"google\"}";
        String jsonString = MAPPER.writeValueAsString(actualDepartment);
        assertEquals(jsonString, expectedDepartment);
    }

    @Test
    public void shouldJsonDeserializesToDepartment() throws IOException {
        File file = new File("src/test/java/fixtures/DepartmentBean.json");
        final Department expectedDepartment = new Department(2, "amazon");
        Department actualDepartment = MAPPER.readValue(file , Department.class);
        assertTrue(EqualsBuilder.reflectionEquals(expectedDepartment, actualDepartment));
    }

    @Test
    public void shouldJsonDeserializesToDepartmentWithOptionalParameters() throws IOException {
        File file = new File("src/test/java/fixtures/DepartmentBeanWithOptional.json");
        final Department expectedDepartment = new Department("amazon");
        Department actualDepartment = MAPPER.readValue(file , Department.class);
        assertTrue(EqualsBuilder.reflectionEquals(expectedDepartment, actualDepartment));
    }

    @Test(expected = JsonMappingException.class)
    public void shouldJsonDeserializesToDepartmentWithInvalidParameters() throws IOException {
        File file = new File("src/test/java/fixtures/DepartmentBeanWithInvalid.json");
        MAPPER.readValue(file , Department.class);
    }
}
