package stock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenx.ms.commons.config.Profiles;
import com.tenx.ms.commons.rest.RestConstants;
import com.tenx.ms.commons.tests.AbstractIntegrationTest;
import com.tenx.ms.retail.RetailServiceApp;
import com.tenx.ms.retail.product.domain.ProductEntity;
import com.tenx.ms.retail.product.rest.dto.Product;
import com.tenx.ms.retail.product.util.ProductConverter;
import com.tenx.ms.retail.stock.rest.dto.Stock;
import com.tenx.ms.retail.stock.services.StockService;
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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.springframework.test.util.AssertionErrors.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@WebIntegrationTest(randomPort = true)
@SpringApplicationConfiguration(classes = RetailServiceApp.class)
@ActiveProfiles(Profiles.TEST_NOAUTH)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, FlywayTestExecutionListener.class})
public class TestStockController extends AbstractIntegrationTest {
    private static final String API_VERSION = RestConstants.VERSION_ONE;
    private static final String STORE_REQUEST_URI = "%s" + API_VERSION + "/stores/";
    private static final String PRODUCT_REQUEST_URI = "%s" + API_VERSION + "/products/";
    private static final String STOCK_REQUEST_URI = "%s" + API_VERSION + "/stock/";
    private final RestTemplate template = new TestRestTemplate();

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private StockService stockService;

    @Autowired
    private ProductConverter productConverter;

    @Value("classpath:stores/stores_success.json")
    private File successStore;

    @Value("classpath:product/success/products_success.json")
    private File successProduct;

    @Value("classpath:stock/success/stock_success.json")
    private File successStock;

    @Value("classpath:stock/success/stock_success_no_count.json")
    private File successStockNoCount;

    private static final String STORE_ID = "1";
    private static final String PRODUCT_ID = "1";
    private ProductEntity testProduct;

    @Before
    public void initialSetup() {
        try {
            getJSONResponse(template, String.format(STORE_REQUEST_URI, basePath()), FileUtils.readFileToString(successStore), HttpMethod.POST);
            getJSONResponse(template, String.format(PRODUCT_REQUEST_URI, basePath()) + STORE_ID, FileUtils.readFileToString(successProduct), HttpMethod.POST);
            ResponseEntity<String> response = getJSONResponse(template, String.format(PRODUCT_REQUEST_URI, basePath()) + STORE_ID + "/"+ PRODUCT_ID, null, HttpMethod.GET);
            testProduct = productConverter.convertToProductEntity(mapper.readValue(response.getBody(), Product.class));
            testProduct.setProductId(Long.parseLong(PRODUCT_ID));
        } catch (IOException e) {
            fail("Couldn't create initial data");
        }
    }

    @Test
    @FlywayTest
    public void testCreateStockMinimal() {
        try {
            ResponseEntity<String> response = getJSONResponse(template, String.format(STOCK_REQUEST_URI, basePath()) + STORE_ID + "/" + PRODUCT_ID, FileUtils.readFileToString(successStock), HttpMethod.POST);
            assertEquals("HTTP Status code incorrect", HttpStatus.NO_CONTENT, response.getStatusCode());
            Stock createdStock = retrieveStock();
            assertNotNull("Stock should not be null", createdStock);
            assertEquals("Store Id doesn't match", STORE_ID, createdStock.getStoreId().toString());
            assertEquals("Product Id doesn't match", PRODUCT_ID, createdStock.getStoreId().toString());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    @FlywayTest
    public void testCreateStockNoCount() {
        try {
            ResponseEntity<String> response = getJSONResponse(template, String.format(STOCK_REQUEST_URI, basePath()) + STORE_ID + "/" + PRODUCT_ID, FileUtils.readFileToString(successStockNoCount), HttpMethod.POST);
            assertEquals("HTTP Status code incorrect", HttpStatus.NO_CONTENT, response.getStatusCode());
            Stock createdStock = retrieveStock();
            assertNotNull("Stock should not be null", createdStock);
            assertEquals("Store Id doesn't match", STORE_ID, createdStock.getStoreId().toString());
            assertEquals("Product Id doesn't match", PRODUCT_ID, createdStock.getProductId().toString());
            assertThat("Stock Count should be different that 0", createdStock.getCount(), is(0L));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    @FlywayTest
    public void testUpdateStock() {
        try {
            ResponseEntity<String> initialResponse = getJSONResponse(template, String.format(STOCK_REQUEST_URI, basePath()) + STORE_ID + "/" + PRODUCT_ID, FileUtils.readFileToString(successStockNoCount), HttpMethod.POST);
            assertEquals("HTTP Status code incorrect", HttpStatus.NO_CONTENT, initialResponse.getStatusCode());
            ResponseEntity<String> response = getJSONResponse(template, String.format(STOCK_REQUEST_URI, basePath()) + STORE_ID + "/" + PRODUCT_ID, FileUtils.readFileToString(successStock), HttpMethod.POST);
            assertEquals("HTTP Status code incorrect", HttpStatus.NO_CONTENT, response.getStatusCode());
            Stock updatedStock = retrieveStock();
            assertThat("Stock Count should be different that 0", updatedStock.getCount(), is(not(0L)));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    @FlywayTest
    public void testCreateStockInvalidProduct() {
        long invalidProductId = 8888L;
        try {
            ResponseEntity<String> response = getJSONResponse(template, String.format(STOCK_REQUEST_URI, basePath()) + STORE_ID + "/" + invalidProductId, FileUtils.readFileToString(successStockNoCount), HttpMethod.POST);
            assertEquals("HTTP Status code incorrect", HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    @FlywayTest
    public void testCreateStockInvalidStore() {
        long invalidStoreId = 8888L;
        try {
            ResponseEntity<String> response = getJSONResponse(template, String.format(STOCK_REQUEST_URI, basePath()) + invalidStoreId + "/" + PRODUCT_ID, FileUtils.readFileToString(successStockNoCount), HttpMethod.POST);
            assertEquals("HTTP Status code incorrect", HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    private Stock retrieveStock() {
        return stockService.findStockByProduct(testProduct);
    }
}
