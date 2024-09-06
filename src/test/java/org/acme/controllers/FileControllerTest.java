package org.acme.controllers;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.acme.model.rest.GridRest;
import org.acme.service.FileService;
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
public class FileControllerTest {
	private static final int ID_FILE = 0;
	private static final String NAME = "my file.csv";

	@InjectMock
	private FileService fileService;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testGetFilesEndpoint() {
		Mockito.when(fileService.getFiles()).thenReturn(new ArrayList<>());

		given()
				.when()
				.get("/file/getFiles")
				.then()
				.statusCode(200)
				.body(notNullValue())
				.contentType(ContentType.JSON);
	}

	@Test
	public void testCreateFileEndpoint() {
		Mockito.when(fileService.createFile(anyString())).thenReturn(true);

		given()
				.contentType(ContentType.URLENC)
				.formParam("name", NAME)
				.when()
				.post("/file/createFile")
				.then()
				.statusCode(200)
				.body(is("true"));
	}


	@Test
	public void testDeleteFileEndpoint() {
		Mockito.when(fileService.deleteFile(anyInt())).thenReturn(true);

		given()
				.contentType(ContentType.URLENC)
				.formParam("id", ID_FILE)
				.when()
				.post("/file/deleteFile")
				.then()
				.statusCode(200)
				.body(is("true"));
	}

	@Test
	public void testRenameFileEndpoint() {
		Mockito.when(fileService.renameFile(anyInt(), anyString())).thenReturn(false);

		given()
				.contentType(ContentType.URLENC)
				.formParam("id", ID_FILE)
				.formParam("name", NAME)
				.when()
				.post("/file/renameFile")
				.then()
				.statusCode(200)
				.body(is("false"));
	}

	@Test
	public void testImportCsvEndpoint() {
		Mockito.when(fileService.importCsv(any(MultipartFormDataInput.class))).thenReturn(new GridRest());

		File testFile = new File("src/test/resources/test.csv");

		given()
				.multiPart("file", testFile)
				.when()
				.post("/file/import/csv")
				.then()
				.statusCode(200)
				.body(notNullValue());
	}


	@Test
	public void testImportJsonEndpoint() {
		Mockito.when(fileService.importJson(any(MultipartFormDataInput.class))).thenReturn(new GridRest());

		File testFile = new File("src/test/resources/test.json");

		given()
				.multiPart("file", testFile)
				.when()
				.post("/file/import/json")
				.then()
				.statusCode(200)
				.body(notNullValue());
	}

	@Test
	public void testExportJsonEndpoint() {
		String expected = "[{id:1, value:2}, {id:2, value:12}]";
		Mockito.when(fileService.exportAsJson(anyInt())).thenReturn(expected);

		given()
				.pathParam("id", ID_FILE)
				.when()
				.get("/file/export/json/{id}")
				.then()
				.contentType("application/octet-stream")
				.statusCode(200)
				.body(is(expected));
	}

	@Test
	public void testExportCsvEndpoint() {
		String expected = "5;51\n6;52";
		Mockito.when(fileService.exportAsCsv(anyInt())).thenReturn(expected);

		given()
				.pathParam("id", ID_FILE)
				.when()
				.get("/file/export/csv/{id}")
				.then()
				.contentType("application/octet-stream")
				.statusCode(200)
				.body(is(expected));
	}

	@Test
	public void testExportHtmlEndpoint() {
		String expected = "<table><tr><td>cell</td></tr></table>";
		Mockito.when(fileService.exportAsHtml(anyInt())).thenReturn(expected);

		given()
				.pathParam("id", ID_FILE)
				.when()
				.get("/file/export/html/{id}")
				.then()
				.contentType("application/octet-stream")
				.statusCode(200)
				.body(is(expected));
	}
}
