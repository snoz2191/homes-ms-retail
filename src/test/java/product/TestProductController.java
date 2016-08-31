package product;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenx.ms.commons.config.Profiles;
import com.tenx.ms.commons.rest.RestConstants;
import com.tenx.ms.commons.rest.dto.ResourceCreated;
import com.tenx.ms.commons.tests.AbstractIntegrationTest;
import com.tenx.ms.retail.RetailServiceApp;
import com.tenx.ms.retail.product.rest.dto.Product;
import com.tenx.ms.retail.store.rest.dto.Store;
import org.apache.commons.io.FileUtils;
import org.flywaydb.test.annotation.FlywayTest;
import org.flywaydb.test.junit.FlywayTestExecutionListener;
import org.junit.Assert;
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

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.springframework.test.util.AssertionErrors.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@WebIntegrationTest(randomPort = true)
@SpringApplicationConfiguration(classes = RetailServiceApp.class)
@ActiveProfiles(Profiles.TEST_NOAUTH)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, FlywayTestExecutionListener.class})
public class TestProductController extends AbstractIntegrationTest {
    private static final String API_VERSION = RestConstants.VERSION_ONE;
    private static final String STORE_REQUEST_URI = "%s" + API_VERSION + "/stores/";
    private static final String PRODUCT_REQUEST_URI = "%s" + API_VERSION + "/products/";
    private final RestTemplate template = new TestRestTemplate();

    @Autowired
    private ObjectMapper mapper;

    @Value("classpath:stores/stores_success.json")
    private File successStore;

    @Value("classpath:product/success/products_success.json")
    private File successProduct;

    @Value("classpath:product/success/product_second_success.json")
    private File secondSuccessProduct;

    @Value("classpath:product/errors/missing_name.json")
    private File missingName;

    @Value("classpath:product/errors/missing_description.json")
    private File missingDescription;

    @Value("classpath:product/errors/missing_price.json")
    private File missingPrice;

    @Value("classpath:product/errors/missing_sku.json")
    private File missingSku;

    @Value("classpath:product/errors/validation_min_length_sku.json")
    private File badMinSku;

    @Value("classpath:product/errors/validation_max_length_sku.json")
    private File badMaxSku;

    private static final String STORE_ID = "1";

    @Before
    public void createStore() {
        try {
            getJSONResponse(template, String.format(STORE_REQUEST_URI, basePath()), FileUtils.readFileToString(successStore), HttpMethod.POST);
        } catch (IOException e) {
            fail("Couldn't create initial store");
        }
    }

    @Test
    @FlywayTest
    public void testParametersValidation() {
        List<File> fileList = Arrays.asList(missingName, missingDescription, missingPrice, missingSku, badMinSku, badMaxSku);
        for (File file: fileList) {
            try {
                ResponseEntity<String> response = getJSONResponse(template, String.format(PRODUCT_REQUEST_URI, basePath()) + STORE_ID, FileUtils.readFileToString(file), HttpMethod.POST);
                assertEquals("HTTP Status code incorrect", HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
            } catch (IOException e) {
                fail(e.getMessage());
            }
        }
    }

    @Test
    @FlywayTest
    public void testCreateProduct() {
        Long productId = createProduct(successProduct);
        Product product = getProduct(productId);
        assertNotNull("Product shouldn't be null", product);
        assertEquals("Product's ID does not match", productId, product.getStoreId());
    }

    @Test
    @FlywayTest
    public void testGetProductByIdNotFound() {
        Long productId = 999999L;
        ResponseEntity<String> response = getJSONResponse(template, String.format(PRODUCT_REQUEST_URI, basePath()) + STORE_ID + "/" + productId, null, HttpMethod.GET);
        assertEquals("HTTP Status code is incorrect", HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @FlywayTest
    public void testGetProductByIdSuccess() {
        Long productId = createProduct(successProduct);
        Product product = getProduct(productId);
        assertNotNull("Product shouldn't be null", product);
    }

    @Test
    @FlywayTest
    public void testGetProducts() {
        Long firstProductId = createProduct(successProduct);
        Long secondProductId = createProduct(secondSuccessProduct);
        try {
            ResponseEntity<String> response = getJSONResponse(template, String.format(PRODUCT_REQUEST_URI, basePath()) + STORE_ID, null, HttpMethod.GET);
            assertEquals("HTTP Status code is incorrect", HttpStatus.OK, response.getStatusCode());

            List<Product> products = mapper.readValue(response.getBody(), new TypeReference<List<Product>>() {});
            assertNotNull("Product list shouldn't be null", products);
            assertThat("Product count does not match", products.size(), equalTo(2));
            assertEquals("Product Ids does not match", firstProductId, products.get(0).getProductId());
            assertEquals("Product Ids does not match", secondProductId, products.get(1).getProductId());
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    private Long createProduct(File file) {
        try {
            ResponseEntity<String> response = getJSONResponse(template, String.format(PRODUCT_REQUEST_URI, basePath()) + STORE_ID, FileUtils.readFileToString(file), HttpMethod.POST);
            assertEquals("HTTP Status code incorrect", HttpStatus.OK, response.getStatusCode());
            ResourceCreated<Long> result = mapper.readValue(response.getBody(), new TypeReference<ResourceCreated<Long>>() {});
            return result.getId();
        } catch (IOException e) {
            fail(e.getMessage());
        }
        return null;
    }

    private Product getProduct(Long productId) {
        Product product = null;
        try {
            ResponseEntity<String> response = getJSONResponse(template, String.format(PRODUCT_REQUEST_URI, basePath()) + STORE_ID + "/"+ productId, null, HttpMethod.GET);
            String received = response.getBody();
            assertEquals(String.format("HTTP Status code incorrect %s", response.getBody()), HttpStatus.OK, response.getStatusCode());
            product = mapper.readValue(received, Product.class);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        return product;
    }
}
