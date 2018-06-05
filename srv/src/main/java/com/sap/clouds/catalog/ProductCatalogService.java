package com.sap.clouds.catalog;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.cloud.sdk.service.prov.api.EntityData;
import com.sap.cloud.sdk.service.prov.api.ExtensionHelper;
import com.sap.cloud.sdk.service.prov.api.MessageContainer;
import com.sap.cloud.sdk.service.prov.api.annotations.AfterCreate;
import com.sap.cloud.sdk.service.prov.api.annotations.BeforeCreate;
import com.sap.cloud.sdk.service.prov.api.annotations.BeforeDelete;
import com.sap.cloud.sdk.service.prov.api.annotations.BeforeUpdate;
import com.sap.cloud.sdk.service.prov.api.constants.HttpStatusCodes;
import com.sap.cloud.sdk.service.prov.api.exits.BeforeCreateResponse;
import com.sap.cloud.sdk.service.prov.api.exits.BeforeDeleteResponse;
import com.sap.cloud.sdk.service.prov.api.exits.BeforeUpdateResponse;
import com.sap.cloud.sdk.service.prov.api.request.CreateRequest;
import com.sap.cloud.sdk.service.prov.api.request.DeleteRequest;
import com.sap.cloud.sdk.service.prov.api.request.GenericRequest;
import com.sap.cloud.sdk.service.prov.api.request.UpdateRequest;
import com.sap.cloud.sdk.service.prov.api.response.CreateResponse;
import com.sap.cloud.sdk.service.prov.api.response.CreateResponseAccessor;
import com.sap.cloud.sdk.service.prov.api.response.ErrorResponse;
import com.sap.cloud.sdk.service.prov.api.response.ErrorResponseBuilder;

public class ProductCatalogService {
	private static final Logger LOG = LoggerFactory.getLogger(ProductCatalogService.class.getName());
	private static final String IMAGE_DEFAULT_LOCATION = "image/default.jpg";
	private static final String ENTITY_PRODUCTS = "Products", ENTITY_STOCKS = "Stocks";
	private static final String ENTITY_CURRENCIES = "Currencies", ENTITY_DIMENSIONS = "DimensionUnits", ENTITY_WEIGHTS = "WeightUnits", ENTITY_BASEUNITS = "BaseUnits",
			ENTITY_CATEGORIES = "Categories", ENTITY_SUPPLIERS = "Suppliers";
	private static final String ERR_NEGATIVE_NUMBER = "negativeNumber", ERR_VALUE_DOES_NOT_EXIST = "valueDoesNotExist", ERR_VALUE_EXISTS = "valueExists", ERR_STOCK_DATA_CREATION = "failedStockDataCreation",
			ERR_STOCK_DATA_DELETION = "failedStockDataDeletion", ERR_VALUE_IS_MANDATORY = "valueIsMandatory", ERR_GENERIC_MESSAGE = "genericMessage";
	private static final String SERVICE_NAME = "clouds.products.CatalogService";
	private static final String ELEMENT_PRODUCT_ID = "ID", ELEMENT_PRODUCT_PRICE = "price", ELEMENT_PRODUCT_CURRENCY = "currency", ELEMENT_PRODUCT_HEIGHT = "height", ELEMENT_PRODUCT_WIDTH = "width",
			ELEMENT_PRODUCT_DEPTH = "depth", ELEMENT_PRODUCT_DIMENSION_UNIT = "dimensionUnit_code", ELEMENT_PRODUCT_WEIGHT = "weight", ELEMENT_PRODUCT_WEIGHT_UNIT = "weightUnit_code",
			ELEMENT_PRODUCT_STOCK = "stock_ID", ELEMENT_PRODUCT_BASE_UNIT = "baseUnit_code", ELEMENT_PRODUCT_CATEGORY = "category_ID", ELEMENT_PRODUCT_SUPPLIER = "supplier_ID",
			ELEMENT_PRODUCT_NAME = "name", ELEMENT_PRODUCT_DESCRIPTION = "description", ELEMENT_PRODUCT_PRICE_RANGE = "priceRange_code", ELEMENT_PRODUCT_IMAGE = "image";
	private static final String ELEMENT_STOCK_ID = "ID", ELEMENT_STOCK_QUANTITY = "quantity", ELEMENT_STOCK_AVAILABILITY = "availability_code", ELEMENT_STOCK_MINIMUMQUANTITY = "minimumQuantity";
	private static final String ELEMENT_CURRENCY_ID = "code", ELEMENT_DIMENSION_ID = "code", ELEMENT_WEIGHT_ID = "code", ELEMENT_BASEUNIT_ID = "code", ELEMENT_CATEGORY_ID = "ID", ELEMENT_SUPPLIER_ID = "ID";
	private static final List<String> PRODUCT_ELEMENTS_NONNEGATIVE = Arrays.asList(ELEMENT_PRODUCT_PRICE, ELEMENT_PRODUCT_HEIGHT, ELEMENT_PRODUCT_WIDTH, ELEMENT_PRODUCT_DEPTH, ELEMENT_PRODUCT_WEIGHT);
	private static final List<String> PRODUCT_ELEMENTS_MANDATORY = Arrays.asList(ELEMENT_PRODUCT_ID, ELEMENT_PRODUCT_NAME, ELEMENT_PRODUCT_DESCRIPTION, ELEMENT_PRODUCT_CATEGORY, ELEMENT_PRODUCT_SUPPLIER,
			ELEMENT_PRODUCT_BASE_UNIT, ELEMENT_PRODUCT_PRICE, ELEMENT_PRODUCT_CURRENCY);
	private static final Map<String, String> PRODUCT_ELEMENTS_VALUEHELP = new HashMap<String, String>() {
		private static final long serialVersionUID = 5238989057589476607L;
		{
			put(ELEMENT_PRODUCT_CURRENCY, ENTITY_CURRENCIES);
			put(ELEMENT_PRODUCT_DIMENSION_UNIT, ENTITY_DIMENSIONS);
			put(ELEMENT_PRODUCT_WEIGHT_UNIT, ENTITY_WEIGHTS);
			put(ELEMENT_PRODUCT_BASE_UNIT, ENTITY_BASEUNITS);
			put(ELEMENT_PRODUCT_CATEGORY, ENTITY_CATEGORIES);
			put(ELEMENT_PRODUCT_SUPPLIER, ENTITY_SUPPLIERS);
		}
	};
	private static final Map<String, String> ID_ELEMENTS_IN_ENTITIES = new HashMap<String, String>() {
		private static final long serialVersionUID = 3000049089863922216L;
		{
			put(ELEMENT_PRODUCT_CURRENCY, ELEMENT_CURRENCY_ID);
			put(ELEMENT_PRODUCT_DIMENSION_UNIT, ELEMENT_DIMENSION_ID);
			put(ELEMENT_PRODUCT_WEIGHT_UNIT, ELEMENT_WEIGHT_ID);
			put(ELEMENT_PRODUCT_BASE_UNIT, ELEMENT_BASEUNIT_ID);
			put(ELEMENT_PRODUCT_CATEGORY, ELEMENT_CATEGORY_ID);
			put(ELEMENT_PRODUCT_SUPPLIER, ELEMENT_SUPPLIER_ID);
		}
	};

	@BeforeCreate(entitySet = { ENTITY_PRODUCTS }, serviceName = SERVICE_NAME)
	public BeforeCreateResponse beforeCreateProducts(CreateRequest createRequest, ExtensionHelper extensionHelper) {
		// Validate product data
		Map<String, Object> productData = createRequest.getData().asMap();
		Map<String, String> validationErrors = validateProduct(productData, createRequest, extensionHelper);
		if (!validationErrors.isEmpty()) {
			return BeforeCreateResponse.setError(constructErrorResponse(validationErrors, createRequest.getMessageContainer()).response());
		}

		// Set default values
		if (productData.containsKey(ELEMENT_PRODUCT_PRICE)) {
			Integer priceRange = determinePriceRange(((BigDecimal) productData.get(ELEMENT_PRODUCT_PRICE)).floatValue());
			productData.put(ELEMENT_PRODUCT_PRICE_RANGE, priceRange);
		}
		productData.put(ELEMENT_PRODUCT_IMAGE, new String(IMAGE_DEFAULT_LOCATION));
		productData.put(ELEMENT_PRODUCT_STOCK, UUID.randomUUID().toString());

		return BeforeCreateResponse.setSuccess().setEntityData(EntityData.createFromMap(productData, Arrays.asList(ELEMENT_PRODUCT_ID), ENTITY_PRODUCTS)).response();
	}

	@AfterCreate(entitySet = { ENTITY_PRODUCTS }, serviceName = SERVICE_NAME)
	public CreateResponse afterCreateProducts(CreateRequest createRequest, CreateResponseAccessor responseAccessor, ExtensionHelper helper) {
		// Create dependent stock data
		Map<String, Object> stockData = new HashMap<String, Object>();
		stockData.put(ELEMENT_STOCK_ID, createRequest.getData().getElementValue(ELEMENT_PRODUCT_STOCK));
		stockData.put(ELEMENT_STOCK_QUANTITY, new BigDecimal(0));
		stockData.put(ELEMENT_STOCK_AVAILABILITY, new Integer(1));
		stockData.put(ELEMENT_STOCK_MINIMUMQUANTITY, new BigDecimal(0));

		try {
			helper.getHandler().executeInsert(EntityData.createFromMap(stockData, Arrays.asList(ELEMENT_STOCK_ID), ENTITY_STOCKS), false);
			return responseAccessor.getOriginalResponse();
		} catch (Exception e) {
			LOG.warn(e.getMessage());
			return CreateResponse.setError(ErrorResponse.getBuilder().setMessage(ERR_STOCK_DATA_CREATION).addErrorDetail(ERR_STOCK_DATA_CREATION, ELEMENT_PRODUCT_STOCK).response());
		}
	}

	@BeforeUpdate(entitySet = { ENTITY_PRODUCTS }, serviceName = SERVICE_NAME)
	public BeforeUpdateResponse beforeUpdateProducts(UpdateRequest updateRequest, ExtensionHelper extensionHelper) {
		// Validate product data
		Map<String, Object> productData = updateRequest.getData().asMap();
		Map<String, String> validationErrors = validateProduct(productData, updateRequest, extensionHelper);
		if (!validationErrors.isEmpty()) {
			return BeforeUpdateResponse.setError(constructErrorResponse(validationErrors, updateRequest.getMessageContainer()).response());
		}

		// Determine price range if necessary
		if (productData.containsKey(ELEMENT_PRODUCT_PRICE)) {
			Integer priceRange = determinePriceRange(((BigDecimal) productData.get(ELEMENT_PRODUCT_PRICE)).floatValue());
			productData.put(ELEMENT_PRODUCT_PRICE_RANGE, priceRange);
		}

		return BeforeUpdateResponse.setSuccess().setEntityData(EntityData.createFromMap(productData, Arrays.asList(ELEMENT_PRODUCT_ID), ENTITY_PRODUCTS)).response();
	}

	@BeforeDelete(entity = ENTITY_PRODUCTS, serviceName=SERVICE_NAME)
	public BeforeDeleteResponse beforeDeleteProducts(DeleteRequest deleteRequest, ExtensionHelper extensionHelper){
		// Delete dependent stock data
		Map<String, Object> keys = new HashMap<String, Object>();
		keys.put(ELEMENT_PRODUCT_ID, deleteRequest.getKeys().get(ELEMENT_PRODUCT_ID) );
		try {
			EntityData entityData = extensionHelper.getHandler().executeRead(ENTITY_PRODUCTS, keys, Arrays.asList(ELEMENT_PRODUCT_STOCK));
			keys.put(ELEMENT_STOCK_ID, entityData.getElementValue(ELEMENT_PRODUCT_STOCK));
			extensionHelper.getHandler().executeDelete(ENTITY_STOCKS, keys);
			return BeforeDeleteResponse.setSuccess().response();
		} catch (Exception e) {
			LOG.warn(e.getMessage());
			return BeforeDeleteResponse.setError(ErrorResponse.getBuilder().setMessage(ERR_STOCK_DATA_DELETION).addErrorDetail(ERR_STOCK_DATA_DELETION, ELEMENT_PRODUCT_STOCK).response());
		}
	}

	private ErrorResponseBuilder constructErrorResponse(Map<String, String> validationErrors, MessageContainer messageContainer) {
		validationErrors.entrySet().stream().forEach(entry -> messageContainer.addErrorMessage(entry.getValue(), entry.getKey(), entry.getKey()));
		return ErrorResponse.getBuilder().setMessage(ERR_GENERIC_MESSAGE).setStatusCode(HttpStatusCodes.BAD_REQUEST.getStatusCode()).addContainerMessages();
	}

	private Map<String, String> validateProduct(Map<String, Object> productData, GenericRequest request, ExtensionHelper extensionHelper) {
		Map<String, String> validationErrors = new HashMap<String, String>();

		PRODUCT_ELEMENTS_NONNEGATIVE.forEach(element -> checkNonNegative(productData, validationErrors, element));
		PRODUCT_ELEMENTS_MANDATORY.forEach(element -> checkMandatory(productData, validationErrors, element, request));
		PRODUCT_ELEMENTS_VALUEHELP.forEach((element, entityName) -> checkElementExists(element, entityName, productData, validationErrors, extensionHelper));
		if (request instanceof CreateRequest) {
			checkElementDoesNotExist(ELEMENT_PRODUCT_ID, ENTITY_PRODUCTS, productData, validationErrors, extensionHelper);
		}
		return validationErrors;
	}

	private void checkMandatory(Map<String, Object> entityData, Map<String, String> validationErrors, String element, GenericRequest request) {
		Object value = entityData.get(element);
		boolean valueIsEmpty = value instanceof String && ((String) value).trim().isEmpty();
		if (request instanceof CreateRequest && (value == null || valueIsEmpty)
			|| request instanceof UpdateRequest && entityData.containsKey(element) && (value == null || valueIsEmpty)) {
			validationErrors.put(element, ERR_VALUE_IS_MANDATORY);
		}
	}

	private void checkElementExists(String element, String entityName, Map<String, Object> entityData, Map<String, String> validationErrors, ExtensionHelper extensionHelper) {
		Object elementValue = entityData.get(element);
		if (!entityData.containsKey(element) || elementValue == null) {
			return;
		}
		String entityId = ID_ELEMENTS_IN_ENTITIES.get(element);
		Map<String, Object> keys = new HashMap<String, Object>();
		keys.put(entityId, String.valueOf(elementValue));
		try {
			if (extensionHelper.getHandler().executeRead(entityName, keys, Arrays.asList(entityId)) == null) {
				validationErrors.put(element, ERR_VALUE_DOES_NOT_EXIST);
			}
		} catch (Exception e) {
			LOG.warn(e.getMessage());
		}
	}

	private void checkElementDoesNotExist(String elementID, String entityName, Map<String, Object> entityData, Map<String, String> validationErrors, ExtensionHelper extensionHelper) {
		Object elementValue = entityData.get(elementID);
		if (elementValue == null) {
			return;
		}
		Map<String, Object> keys = new HashMap<String, Object>();
		keys.put(elementID, String.valueOf(elementValue));
		try {
			if (extensionHelper.getHandler().executeRead(entityName, keys, Arrays.asList(elementID)) != null) {
				validationErrors.put(elementID, ERR_VALUE_EXISTS);
			}
		} catch (Exception e) {
			LOG.warn(e.getMessage());
		}
	}

	private void checkNonNegative(Map<String, Object> productData, Map<String, String> validationErrors, String element) {
		if (!productData.containsKey(element)) {
			return;
		}
		Object elementValue = productData.get(element);
		if (elementValue != null && ((BigDecimal) elementValue).floatValue() < 0) {
			validationErrors.put(element, ERR_NEGATIVE_NUMBER);
		}
	}

	// Determine a price range which is used for filtering in the product list
	private Integer determinePriceRange(float value) {
		Integer range = new Integer(0);
		if (value < 100.00f) {
			range = 1;
		} else if (value < 500.00f) {
			range = 2;
		} else if (value < 1000.00f) {
			range = 3;
		} else {
			range = 4;
		}
		return range;
	}
}
