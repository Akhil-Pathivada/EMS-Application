package resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.After;
import org.junit.Before;
import util.ResourceTestHelper;
import javax.ws.rs.core.Response;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.reset;

public abstract class BaseResourceTest {
    protected ResourceTestHelper resourceTestHelper;

    @Before
    public void before() {
        populateStubData();
    }

    @After
    public void tearDown(){
        reset(serviceMock());
    }

    protected void assertOkResponse(Response response){
        assertEquals(200, response.getStatus());
    }

    protected void assertOkResponse(Response response, Object expectedResponse) throws JsonProcessingException {
        assertEquals(200, response.getStatus());
        String expectedResponseString = ResourceTestHelper.convertToJsonString(expectedResponse);
        assertEquals(expectedResponseString, response.readEntity(String.class));
    }

    protected void assertCreatedResponse(Response response, Object expectedResponse) throws JsonProcessingException {
        assertEquals(201, response.getStatus());
        String expectedResponseString = ResourceTestHelper.convertToJsonString(expectedResponse);
        assertEquals(expectedResponseString, response.readEntity(String.class));
    }

    protected void assertNotFoundResponse(Response response) {
        assertEquals(404, response.getStatus());
    }

    protected void assertMethodNotAllowedResponse(Response response){
        assertEquals(405, response.getStatus());
    }

    protected void assertUnprocessibleEntityResponse(Response response){
        assertEquals(422, response.getStatus());
    }

    protected void assertNoContentResponse(Response response){
        assertEquals(204, response.getStatus());
    }

    protected void assertBadRequestResponse(Response response){
        assertEquals(400, response.getStatus());
    }

    protected void populateStubData() {
        resourceTestHelper = new ResourceTestHelper(getResourceTestRule());
    }

    protected abstract ResourceTestRule getResourceTestRule();

    protected abstract Object serviceMock();

}
