package org.acme.controllers;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.acme.model.rest.ChangesRest;
import org.acme.model.rest.GridRest;
import org.acme.model.rest.ValueEditRest;
import org.acme.service.DataService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.*;


@QuarkusTest
public class DataControllerTest {
	private static final int ID_FILE = 0;
	private static final int MIN_VALUE = 0;
	private static final int MAX_VALUE = 100;
	private static final boolean DELETE = false;
	private static final String NEW_VALUE = "new value";
	private static final String FN_NAME = "function";



	private static final List<Integer> COLUMNS = Arrays.asList(1,2,3);

	@InjectMock
	private DataService dataService;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testGetDataEndpoint() {
		Mockito.when(dataService.getData(anyInt())).thenReturn(new GridRest());

		given()
				.pathParam("idFile", ID_FILE)
				.when()
				.get("/data/getData/{idFile}")
				.then()
				.statusCode(200)
				.body(notNullValue())
				.contentType(ContentType.JSON);
	}


	@Test
	public void testNormalizeEndpoint() {
		Mockito.when(dataService.normalize(anyString(), any(), anyInt())).thenReturn(new GridRest());

		given()
				.contentType(ContentType.URLENC)
				.formParam("functionName", FN_NAME)
				.formParam("columns", StringUtils.join(COLUMNS, ","))
				.formParam("idFile", ID_FILE)
				.when()
				.post("/data/normalize")
				.then()
				.statusCode(200)
				.body(notNullValue());
	}


	@Test
	public void testValidateEndpoint() {
		Mockito.when(dataService.validate(anyString(), any(), anyInt())).thenReturn(new ArrayList());

		given()
				.contentType(ContentType.URLENC)
				.formParam("functionName", FN_NAME)
				.formParam("columns", StringUtils.join(COLUMNS, ","))
				.formParam("idFile", ID_FILE)
				.when()
				.post("/data/validate")
				.then()
				.statusCode(200)
				.body(notNullValue());
	}


	@Test
	public void testModifyValueEndpoint() {
		Mockito.when(dataService.modifyValue(any(ValueEditRest.class))).thenReturn(new GridRest());

		given()
				.contentType(ContentType.JSON)
				.body(new ValueEditRest())
				.when()
				.post("/data/modifyValue")
				.then()
				.statusCode(200)
				.body(notNullValue());
	}


	@Test
	public void testFillAutoIncrementalEndpoint() {
		Mockito.when(dataService.fillAutoIncremental(anyList(), anyInt())).thenReturn(new GridRest());

		given()
				.contentType(ContentType.URLENC)
				.formParam("columns", StringUtils.join(COLUMNS, ","))
				.formParam("idFile", ID_FILE)
				.when()
				.post("/data/fillAutoIncremental")
				.then()
				.statusCode(200)
				.body(notNullValue());
	}

	@Test
	public void testFillFixedValueEndpoint() {
		Mockito.when(dataService.fillFixedValue(anyString(), anyList(), anyInt())).thenReturn(new GridRest());

		given()
				.contentType(ContentType.URLENC)
				.formParam("columns", StringUtils.join(COLUMNS, ","))
				.formParam("idFile", ID_FILE)
				.formParam("newValue", NEW_VALUE)
				.when()
				.post("/data/fillFixedValue")
				.then()
				.statusCode(200)
				.body(notNullValue());
	}


	@Test
	public void testSearchNumberEndpoint() {
		Mockito.when(dataService.searchNumber(anyInt(), anyInt(), any(), anyInt())).thenReturn(new GridRest());

		given()
				.contentType(ContentType.URLENC)
				.formParam("min", MIN_VALUE)
				.formParam("max", MAX_VALUE)
				.formParam("columns", StringUtils.join(COLUMNS, ","))
				.formParam("idFile", ID_FILE)
				.when()
				.post("/data/searchNumber")
				.then()
				.statusCode(200)
				.body(notNullValue());
	}

	@Test
	public void testPercentileEndpoint() {
		Mockito.when(dataService.percentile(anyInt(),anyBoolean(), any(), anyInt())).thenReturn(new GridRest());

		given()
				.contentType(ContentType.URLENC)
				.formParam("value", MIN_VALUE)
				.formParam("delete", DELETE)
				.formParam("columns", StringUtils.join(COLUMNS, ","))
				.formParam("idFile", ID_FILE)
				.when()
				.post("/data/percentile")
				.then()
				.statusCode(200)
				.body(notNullValue());
	}

	@Test
	public void testGetHistoryEndpoint() {
		List<ChangesRest> changes = new ArrayList<>();
		changes.add(new ChangesRest());
		Mockito.when(dataService.getHistory(anyInt())).thenReturn(changes);

		given()
				.pathParam("id", ID_FILE)
				.when()
				.get("/data/getHistory/{id}")
				.then()
				.statusCode(200)
				.body(notNullValue())
				.body("", hasSize(changes.size()))
				.contentType(ContentType.JSON);

	}

	@Test
	public void testGetHistoryDataEndpoint() {
		Mockito.when(dataService.getDataHistory(anyInt())).thenReturn(new GridRest());

		given()
				.pathParam("id", ID_FILE)
				.when()
				.get("/data/getData/history/{id}")
				.then()
				.statusCode(200)
				.body(notNullValue())
				.contentType(ContentType.JSON);

	}
}
