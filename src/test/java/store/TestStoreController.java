package store;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenx.ms.commons.config.Profiles;
import com.tenx.ms.commons.rest.RestConstants;
import com.tenx.ms.commons.rest.dto.ResourceCreated;
import com.tenx.ms.commons.tests.AbstractIntegrationTest;
import com.tenx.ms.retail.RetailServiceApp;
import com.tenx.ms.retail.store.rest.dto.Store;
import org.apache.commons.io.FileUtils;
import org.flywaydb.test.annotation.FlywayTest;
import org.flywaydb.test.junit.FlywayTestExecutionListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@WebIntegrationTest(randomPort = true)
@SpringApplicationConfiguration(classes = RetailServiceApp.class)
@ActiveProfiles(Profiles.TEST_NOAUTH)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, FlywayTestExecutionListener.class})
public class TestStoreController extends AbstractIntegrationTest {

    private static final String API_VERSION = RestConstants.VERSION_ONE;
    private static final String REQUEST_URI = "%s" + API_VERSION + "/stores/";
    private final RestTemplate template = new TestRestTemplate();

    @Autowired
    private ObjectMapper mapper;

    @Value("classpath:stores/stores_success.json")
    private File successRequest;

    @Value("classpath:stores/stores_missing_name.json")
    private File missingNameRequest;

    @Test
    @FlywayTest
    public void testCreateStore() {
        Long storeId = createStore(successRequest);
        Store store = getStore(storeId);
        assertNotNull(store);
        assertEquals("Store's ID does not match", storeId, store.getStoreId());
    }

    @Test
    @FlywayTest
    public void testCreateStoreInvalidField() {
        try {
            ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()), FileUtils.readFileToString(missingNameRequest), HttpMethod.POST);
            assertEquals("HTTP Status code incorrect", HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
            ResourceCreated<Long> result = mapper.readValue(response.getBody(), new TypeReference<ResourceCreated<Long>>() {});
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    @FlywayTest
    public void getStoreByIdNotFound() {
        Long storeId = 999999L;
        ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + storeId, null, HttpMethod.GET);
        assertEquals("HTTP Status code is incorrect", HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @FlywayTest
    public void getStoreByIdSuccess() {
        Long storeId = createStore(successRequest);
        Store store = getStore(storeId);
        assertNotNull(store);
    }

    @Test
    @FlywayTest
    public void getStores() {
        Long firstStoreId = createStore(successRequest);
        Long secondStoreId = createStore(successRequest);
        try {
            ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()), null, HttpMethod.GET);
            assertEquals("HTTP Status code is incorrect", HttpStatus.OK, response.getStatusCode());

            List<Store> stores = mapper.readValue(response.getBody(), new TypeReference<List<Store>>() {});
            assertNotNull("Store list shouldn't be null", stores);
            assertThat("Store count does not match", stores.size(), equalTo(2));
            assertEquals(stores.get(0).getStoreId(), firstStoreId);
            assertEquals(stores.get(1).getStoreId(), secondStoreId);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    private Long createStore(File file) {
        try {
            ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()), FileUtils.readFileToString(file), HttpMethod.POST);
            assertEquals("HTTP Status code incorrect", HttpStatus.OK, response.getStatusCode());
            ResourceCreated<Long> result = mapper.readValue(response.getBody(), new TypeReference<ResourceCreated<Long>>() {});
            return result.getId();
        } catch (IOException e) {
            fail(e.getMessage());
        }
        return null;
    }

    private Store getStore(Long storeId) {
        Store store = null;
        try {
            ResponseEntity<String> response = getJSONResponse(template, String.format(REQUEST_URI, basePath()) + storeId, null, HttpMethod.GET);
            String received = response.getBody();
            assertEquals(String.format("HTTP Status code incorrect %s", response.getBody()), HttpStatus.OK, response.getStatusCode());
            store = mapper.readValue(received, Store.class);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        return store;
    }


}
