package com.tenx.ms.retail.store.rest;

import com.tenx.ms.commons.rest.RestConstants;
import com.tenx.ms.commons.rest.dto.ResourceCreated;
import com.tenx.ms.retail.exceptions.UpdateViolationException;
import com.tenx.ms.retail.store.rest.dto.Store;
import com.tenx.ms.retail.store.services.StoreService;
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

@Api(value = "stores", description = "Retail API that contains store related endpoints")
@RestController("storeControllerV1")
@RequestMapping(RestConstants.VERSION_ONE + "/stores")
public class StoreController {
    private static final Logger LOGGER = LoggerFactory.getLogger(StoreController.class);

    @Autowired
    private StoreService storeService;


    @ApiOperation(value = "Create a Store")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful creation of the store"),
            @ApiResponse(code = 412, message = "Some conditions for creating a store were not met"),
            @ApiResponse(code = 500, message = "An internal error has occurred")
    })
    @RequestMapping(method = RequestMethod.POST)
    public ResourceCreated<Long> createStore(@ApiParam(name = "store", value = "The store entity", required = true) @Validated @RequestBody Store store) {
        LOGGER.debug("Creating store {}", store);

        return new ResourceCreated<>(storeService.createStore(store));
    }

    @ApiOperation(value = "Get a Specific Store by store ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful retrieval of store "),
            @ApiResponse(code = 500, message = "An internal error has occurred")
    })
    @RequestMapping(value = {"/{storeId:\\d+}"}, method = RequestMethod.GET)
    public Store getStoreById(@ApiParam(name = "store id", value = "The id of the store to get its details", required = true) @PathVariable long storeId) {
        LOGGER.debug("Fetching store with id {}", storeId);

        return storeService.getStoreById(storeId).get();
    }

    @ApiOperation(value = "Get a list of all the Stores")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful retrieval of store "),
            @ApiResponse(code = 500, message = "An internal error has occurred")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query",
                    value = "Results page you want to retrieve (0..N)", defaultValue = "0"),
            @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query",
                    value = "Number of records per page", defaultValue = "20"),
    })
    @RequestMapping(method = RequestMethod.GET)
    public List<Store> getStores(Pageable pageable) {
        LOGGER.debug("Fetching all stores {}", pageable);

        return storeService.getAllStores(pageable);
    }

    @ResponseStatus(value = HttpStatus.PRECONDITION_FAILED)
    @ExceptionHandler(UpdateViolationException.class)
    protected void handleUpdateViolationException(UpdateViolationException ex,
                                                  HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.PRECONDITION_FAILED.value(), ex.getMessage());
    }
}
