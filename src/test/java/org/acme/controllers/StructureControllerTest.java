package org.acme.controllers;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.acme.model.rest.GridRest;
import org.acme.service.FileService;
import org.acme.service.StructureService;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.*;

@QuarkusTest
public class StructureControllerTest {
	private static final int ID_FILE = 0;
	private static final String INDEXES = "0,4,2";
	private static final int POSITION = 3;
	private static final String NAME = "my_column";

	@InjectMock
	private StructureService structureService;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}


	@Test
	public void testAddColumnEndpoint() {
		Mockito.when(structureService.addColumn(anyString(), anyInt(), anyInt())).thenReturn(new GridRest());

		given()
				.contentType(ContentType.URLENC)
				.formParam("idFile", ID_FILE)
				.formParam("position", POSITION)
				.formParam("name", NAME)
				.when()
				.post("/structure/addColumn")
				.then()
				.statusCode(200)
				.body(notNullValue());
	}

	@Test
	public void testAddRowEndpoint() {
		Mockito.when(structureService.addRow(anyInt(), anyInt())).thenReturn(new GridRest());

		given()
				.contentType(ContentType.URLENC)
				.formParam("idFile", ID_FILE)
				.formParam("position", POSITION)
				.when()
				.post("/structure/addRow")
				.then()
				.statusCode(200)
				.body(notNullValue());
	}


	@Test
	public void testDeleteRowsEndpoint() {
		Mockito.when(structureService.deleteRows(anyList(), anyInt())).thenReturn(new GridRest());

		given()
				.contentType(ContentType.URLENC)
				.formParam("indexes", INDEXES)
				.formParam("idFile", ID_FILE)
				.when()
				.post("/structure/deleteRows")
				.then()
				.statusCode(200)
				.body(notNullValue());
	}

	@Test
	public void testDeleteColumnsEndpoint() {
		Mockito.when(structureService.deleteColumns(anyList(), anyInt())).thenReturn(new GridRest());

		given()
				.contentType(ContentType.URLENC)
				.formParam("indexes", INDEXES)
				.formParam("idFile", ID_FILE)
				.when()
				.post("/structure/deleteColumns")
				.then()
				.statusCode(200)
				.body(notNullValue());
	}

	@Test
	public void testJoinColumnsEndpoint() {
		Mockito.when(structureService.joinColumns(anyList(), anyInt())).thenReturn(new GridRest());

		given()
				.contentType(ContentType.URLENC)
				.formParam("indexes", INDEXES)
				.formParam("idFile", ID_FILE)
				.when()
				.post("/structure/joinColumns")
				.then()
				.statusCode(200)
				.body(notNullValue());
	}
}
