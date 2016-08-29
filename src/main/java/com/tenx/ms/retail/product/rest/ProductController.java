package com.tenx.ms.retail.product.rest;

import com.tenx.ms.commons.rest.RestConstants;
import com.tenx.ms.commons.rest.dto.ResourceCreated;
import com.tenx.ms.retail.exceptions.UpdateViolationException;
import com.tenx.ms.retail.product.rest.dto.Product;
import com.tenx.ms.retail.product.services.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Api(value = "products", description = "Retail API that contains product related endpoints")
@RestController("productControllerV1")
@RequestMapping(RestConstants.VERSION_ONE + "/products")
public class ProductController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @ApiOperation(value = "Create a Product for a given Store")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful creation of the product"),
            @ApiResponse(code = 412, message = "Some conditions for creating a product were not met"),
            @ApiResponse(code = 500, message = "An internal error has occurred")
    })
    @RequestMapping(value = {"/{storeId:\\d+}"}, method = RequestMethod.POST)
    public ResourceCreated<Long> createProduct(
            @ApiParam(name = "storeId", value = "The store id for which the product will be created", required = true) @PathVariable long storeId,
            @ApiParam(name = "product", value = "The product entity", required = true) @Validated @RequestBody Product product) {
        LOGGER.debug("Creating product {}", product);

        return new ResourceCreated<>(productService.createProduct(product));
    }

    @ApiOperation(value = "Get a Specific Product for a Store by product ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful retrieval of store "),
            @ApiResponse(code = 500, message = "An internal error has occurred")
    })
    @RequestMapping(value = {"/{storeId:\\d+}/{productId:\\d+}"}, method = RequestMethod.GET)
    public Product getProductById(
            @ApiParam(name = "store id", value = "The id of the store", required = true) @PathVariable long storeId,
            @ApiParam(name = "product id", value = "The id of the product to get its details", required = true) @PathVariable long productId) {
        LOGGER.debug("Fetching product with id {}", productId);

        return productService.getProductById(storeId, productId).get();
    }

    @ApiOperation(value = "Get a list of all the Product for a Store")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful retrieval of the product "),
            @ApiResponse(code = 500, message = "An internal error has occurred")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query",
                    value = "Results page you want to retrieve (0..N)", defaultValue = "0"),
            @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query",
                    value = "Number of records per page", defaultValue = "20"),
    })
    @RequestMapping(value = {"/{storeId:\\d+}"}, method = RequestMethod.GET)
    public List<Product> getProduct( @ApiParam(name = "store id", value = "The id of the store", required = true) @PathVariable long storeId,
                                  Pageable pageable) {
        LOGGER.debug("Fetching all product from store {}", storeId);

        return productService.getAllProducts(storeId, pageable);
    }

    @ResponseStatus(value = HttpStatus.PRECONDITION_FAILED)
    @ExceptionHandler(UpdateViolationException.class)
    protected void handleUpdateViolationException(UpdateViolationException ex,
                                                  HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.PRECONDITION_FAILED.value(), ex.getMessage());
    }
}
