package util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.testing.junit.ResourceTestRule;
import javax.ws.rs.client.Invocation;

public class ResourceTestHelper {
    private ResourceTestRule resourceTestRule;
    private static final ObjectMapper objectMapper = Jackson.newObjectMapper();
    public ResourceTestHelper(ResourceTestRule resourceTestRule) {
        this.resourceTestRule = resourceTestRule;
    }

    public static ResourceTestRule resourceTestRuleBuilder(Object resourceObject) {
        return ResourceTestRule.builder().addResource(resourceObject).build();
    }

    public static String convertToJsonString(Object object) throws JsonProcessingException {
        if (object instanceof String) {
            return (String) object;
        }
        return objectMapper.writeValueAsString(object);
    }

    public  Invocation.Builder requestBuilder(String url) {
        return requestBuilderWithUrl(url);
    }

    public Invocation.Builder requestBuilderWithUrl(String url) {
        Invocation.Builder builder = this.resourceTestRule.client().target(url).request();
        return builder;
    }
}
