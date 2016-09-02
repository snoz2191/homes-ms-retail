package order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenx.ms.commons.config.Profiles;
import com.tenx.ms.commons.rest.RestConstants;
import com.tenx.ms.commons.tests.AbstractIntegrationTest;
import com.tenx.ms.retail.RetailServiceApp;
import com.tenx.ms.retail.order.domain.enums.OrderStatusEnum;
import com.tenx.ms.retail.order.rest.dto.OrderResponse;
import org.apache.commons.io.FileUtils;
import org.flywaydb.test.annotation.FlywayTest;
import org.flywaydb.test.junit.FlywayTestExecutionListener;
import org.junit.Before;
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
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.springframework.test.util.AssertionErrors.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@WebIntegrationTest(randomPort = true)
@SpringApplicationConfiguration(classes = RetailServiceApp.class)
@ActiveProfiles(Profiles.TEST_NOAUTH)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, FlywayTestExecutionListener.class})
public class TestOrderController extends AbstractIntegrationTest {
    private static final String API_VERSION = RestConstants.VERSION_ONE;
    private static final String STORE_REQUEST_URI = "%s" + API_VERSION + "/stores/";
    private static final String PRODUCT_REQUEST_URI = "%s" + API_VERSION + "/products/";
    private static final String STOCK_REQUEST_URI = "%s" + API_VERSION + "/stock/";
    private static final String ORDER_REQUEST_URI = "%s" + API_VERSION + "/orders/";
    private final RestTemplate template = new TestRestTemplate();

    @Autowired
    private ObjectMapper mapper;

    @Value("classpath:stores/stores_success.json")
    private File successStore;

    @Value("classpath:product/success/products_success.json")
    private File successProduct;

    @Value("classpath:stock/success/stock_success.json")
    private File successStock;

    @Value("classpath:order/success/order_success.json")
    private File successOrder;

    @Value("classpath:order/success/order_success_backordered.json")
    private File successOrderBackordered;

    @Value("classpath:order/errors/missing_first_name.json")
    private File missingFirstName;

    @Value("classpath:order/errors/missing_last_name.json")
    private File missingLastName;

    @Value("classpath:order/errors/missing_email.json")
    private File missingEmail;

    @Value("classpath:order/errors/missing_phone.json")
    private File missingPhone;

    @Value("classpath:order/errors/empty_products.json")
    private File emptyProducts;

    @Value("classpath:order/errors/missing_products_id.json")
    private File missingProductId;

    @Value("classpath:order/errors/missing_products_count.json")
    private File missingProductCount;

    @Value("classpath:order/errors/invalid_first_name.json")
    private File invalidFirstName;

    @Value("classpath:order/errors/invalid_last_name.json")
    private File invalidLastName;

    @Value("classpath:order/errors/invalid_email.json")
    private File invalidEmail;

    @Value("classpath:order/errors/invalid_phone.json")
    private File invalidPhone;

    @Value("classpath:order/errors/order_invalid_product.json")
    private File orderInvalidProduct;

    private static final String STORE_ID = "1";
    private static final String PRODUCT_ID = "1";

    @Before
    public void initialSetup() {
        try {
            getJSONResponse(template, String.format(STORE_REQUEST_URI, basePath()), FileUtils.readFileToString(successStore), HttpMethod.POST);
            getJSONResponse(template, String.format(PRODUCT_REQUEST_URI, basePath()) + STORE_ID, FileUtils.readFileToString(successProduct), HttpMethod.POST);
            getJSONResponse(template, String.format(STOCK_REQUEST_URI, basePath()) + STORE_ID + "/" + PRODUCT_ID, FileUtils.readFileToString(successStock), HttpMethod.POST);
        } catch (IOException e) {
            fail("Couldn't create initial data");
        }
    }

    @Test
    @FlywayTest
    public void testParametersValidation() {
        List<File> fileList = Arrays.asList(missingFirstName, missingLastName, missingEmail, missingPhone,
                emptyProducts, missingProductId, missingProductCount, invalidFirstName, invalidLastName, invalidEmail, invalidPhone);
        for (File file: fileList) {
            try {
                ResponseEntity<String> response = getJSONResponse(template, String.format(ORDER_REQUEST_URI, basePath()) + STORE_ID, FileUtils.readFileToString(file), HttpMethod.POST);
                assertEquals("HTTP Status code incorrect", HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
            } catch (IOException e) {
                fail(e.getMessage());
            }
        }
    }

    @Test
    @FlywayTest
    public void testCreateOrderInvalidStore() {
        Long invalidStoreId = 999L;
        try {
            ResponseEntity<String> response = getJSONResponse(template, String.format(ORDER_REQUEST_URI, basePath()) + invalidStoreId, FileUtils.readFileToString(successOrder), HttpMethod.POST);
            assertEquals("HTTP Status code incorrect", HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    @FlywayTest
    public void testCreateOrderInvalidProduct() {
        try {
            ResponseEntity<String> response = getJSONResponse(template, String.format(ORDER_REQUEST_URI, basePath()) + STORE_ID, FileUtils.readFileToString(orderInvalidProduct), HttpMethod.POST);
            assertEquals("HTTP Status code incorrect", HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    @FlywayTest
    public void testCreateOrderSuccess() {
        try {
            ResponseEntity<String> response = getJSONResponse(template, String.format(ORDER_REQUEST_URI, basePath()) + STORE_ID, FileUtils.readFileToString(successOrder), HttpMethod.POST);
            assertEquals("HTTP Status code incorrect", HttpStatus.OK, response.getStatusCode());
            OrderResponse orderResponse = mapper.readValue(response.getBody(), OrderResponse.class);
            assertEquals("Order Status should be ORDERED", OrderStatusEnum.ORDERED, orderResponse.getStatus());
            assertThat("Backordered Items should be empty", orderResponse.getBackorderedItems().size(), is(0));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    @FlywayTest
    public void testCreateOrderBackordered() {
        try {
            ResponseEntity<String> response = getJSONResponse(template, String.format(ORDER_REQUEST_URI, basePath()) + STORE_ID, FileUtils.readFileToString(successOrderBackordered), HttpMethod.POST);
            assertEquals("HTTP Status code incorrect", HttpStatus.OK, response.getStatusCode());
            OrderResponse orderResponse = mapper.readValue(response.getBody(), OrderResponse.class);
            assertEquals("Order Status should be ORDERED", OrderStatusEnum.ORDERED, orderResponse.getStatus());
            assertThat("Backordered Items should not be empty", orderResponse.getBackorderedItems().size(), is(1));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
