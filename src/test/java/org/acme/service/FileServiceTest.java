package org.acme.service;


import com.google.gson.*;
import io.quarkus.test.*;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.*;
import jakarta.inject.Inject;
import org.acme.dao.*;
import org.acme.db.*;
import org.acme.model.rest.*;
import org.jboss.resteasy.plugins.providers.multipart.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@QuarkusTest
public class FileServiceTest {

	private static final int ROWS_GRID = 5;
	private static final int COLS_GRID = 3;


	private static final int ID_FILE = 0;
	private static final String IDS_FILE = "1,2,3";
	private static final String NAME_FILE = "my file";
	private static final String TYPE_FILE = "table";
	private static final String EXAMPLE_CSV = "a,b,c\nd,e,f\ng,h,i";

	private static final String EXPECTED_SIZE = "Expected size";
	private static final String EXPECTED_VALUE = "Expected value";

	@Inject
	private FileService fileService;


	@InjectSpy
	private FileService fileServiceSpy;

	@InjectMock
	private FileDao fileDao;

	@InjectMock
	private ChangeHistoryDao changeHistoryDao;


	@InjectMock
	private DataService dataService;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testMultipartToString() throws IOException {
		MultipartFormDataInput newForm = mock(MultipartFormDataInput.class);
		InputPart token = mock(InputPart.class);

		Map<String, List<InputPart>> paramsMap = new HashMap<>();
		paramsMap.put("Token", Arrays.asList(token));

		String expectedOutput = "expected token param body";
		when(newForm.getFormDataMap()).thenReturn(paramsMap);
		when(token.getBodyAsString()).thenReturn(expectedOutput);

		String result = this.fileService.multipartToString(newForm);
		assertNotNull(result);
		assertEquals(expectedOutput, result, EXPECTED_VALUE);
	}


	@Test
	public void testGetMultipartName()  {
		MultipartFormDataInput newForm = mock(MultipartFormDataInput.class);
		InputPart token = mock(InputPart.class);

		Map<String, List<InputPart>> paramsMap = new HashMap<>();
		paramsMap.put("fileName", Arrays.asList(token));

		String expectedFileName = "testFile.txt";

		when(newForm.getFormDataMap()).thenReturn(paramsMap);
		when(token.getFileName()).thenReturn(expectedFileName);

		String result = this.fileService.getMultipartName(newForm);

		assertNotNull(result);
		assertEquals(expectedFileName, result, EXPECTED_VALUE);
	}

	@Test
	public void testProcessJson() {
		String json = "[{id:1, value:2, active: true}, {id:2, value:12, active: false}]";
		TableRest rs = new TableRest();

		fileService.processJson(JsonParser.parseString(json), rs);
		assertEquals(2, rs.getValues().size(), EXPECTED_SIZE);
		assertEquals(3, rs.getHeader().size(), EXPECTED_SIZE);
	}

	@Test
	public void testProcessJsonObject() {
		String json = "{id:1, value:2, active: true}";
		TableRest rs = new TableRest();

		fileService.processJson(JsonParser.parseString(json), rs);
		assertEquals(1, rs.getValues().size(), EXPECTED_SIZE);
		assertEquals(3, rs.getHeader().size(), EXPECTED_SIZE);
	}

	@Test
	public void testGetFiles() {

		List<FileDb> dbs = new ArrayList<>();
		FileDb f = new FileDb();
		f.setId(0);
		dbs.add(f);
		when(fileDao.getFiles()).thenReturn(dbs);

		List<FileRest> result = this.fileService.getFiles();
		assertNotNull(result);
		assertEquals(result.size(), dbs.size(), EXPECTED_SIZE);
	}



	@Test
	public void testExportAsJson() {
		TableRest table = getExampleTable();
		when(dataService.getFileAsTable(anyInt())).thenReturn(table);
		when(dataService.table2grid(any(TableRest.class))).thenReturn(new GridRest());

		String result = fileService.exportAsJson(ID_FILE);
		assertNotNull(result);
		assertTrue(result.contains("["));
	}

	@Test
	public void testExportAsJsonNull() {
		when(dataService.getFileAsTable(anyInt())).thenReturn(null);
		String result = fileService.exportAsJson(ID_FILE);
		assertNull(result);
	}

	@Test
	public void testAddChangeHistory() {
		when(dataService.table2csv(any(TableRest.class))).thenReturn(EXAMPLE_CSV);
		Mockito.doNothing().when(changeHistoryDao).addChangeHistory(any(ChangeHistoryDb.class));

		fileService.addChangeHistory(ID_FILE, new TableRest(), "Modify Value");

		verify(changeHistoryDao).addChangeHistory(any(ChangeHistoryDb.class));
	}

	@Test
	public void testExportAsCsv() {
		TableRest table = getExampleTable();
		when(dataService.getFileAsTable(anyInt())).thenReturn(table);
		String result = fileService.exportAsCsv(ID_FILE);
		assertNotNull(result);
		assertTrue(result.contains(","));

		//+1 including header
		assertEquals(ROWS_GRID + 1, result.split("\n").length, EXPECTED_SIZE);
	}

	@Test
	public void testExportAsCsvNull() {
		when(dataService.getFileAsTable(anyInt())).thenReturn(null);
		String result = fileService.exportAsCsv(ID_FILE);
		assertNull(result);
	}

	@Test
	public void testRenameFile() {
		when(fileDao.getFileById(anyInt())).thenReturn(new FileDb());
		Mockito.doNothing().when(fileDao).putFile(any(FileDb.class));

		boolean result = this.fileService.renameFile(ID_FILE, NAME_FILE);
		assertTrue(result, EXPECTED_VALUE);
	}

	@Test
	public void testRenameFileNull() {
		when(fileDao.getFileById(anyInt())).thenReturn(null);
		boolean result = this.fileService.renameFile(ID_FILE, NAME_FILE);
		assertFalse(result, EXPECTED_VALUE);
	}

	@Test
	public void testDeleteFile() {
		when(fileDao.getFileById(anyInt())).thenReturn(new FileDb());
		Mockito.doNothing().when(fileDao).deleteFile(any(FileDb.class));

		boolean result = this.fileService.deleteFiles(IDS_FILE);
		assertTrue(result, EXPECTED_VALUE);
	}

	@Test
	public void testDeleteFileNull() {
		when(fileDao.getFileById(anyInt())).thenReturn(null);

		boolean result = this.fileService.deleteFiles(IDS_FILE);
		assertFalse(result, EXPECTED_VALUE);
	}

	@Test
	public void testNewFile() {
		Mockito.doNothing().when(fileDao).addFile(anyString(), anyString(), anyString());

		boolean result = this.fileService.newFile(NAME_FILE, TYPE_FILE);
		assertTrue(result, EXPECTED_VALUE);
	}


	@Test
	public void testNewFileNull() {
		boolean result = this.fileService.newFile(NAME_FILE, "");
		assertFalse(result, EXPECTED_VALUE);
	}

	@Test
	public void testExportAsHtml() {
		when(dataService.getFileAsTable(anyInt())).thenReturn(getExampleTable());

		String result = this.fileService.exportAsHtml(ID_FILE);
		assertNotNull(result);
		assertTrue(result.contains("<table"), EXPECTED_VALUE);
	}

	@Test
	public void testExportAsHtmlNull() {
		when(dataService.getFileAsTable(anyInt())).thenReturn(null);
		assertNull(this.fileService.exportAsHtml(ID_FILE));
	}


	@Test
	public void testSaveFile() {
		when(changeHistoryDao.getLastChangeOfFile(anyInt())).thenReturn(new ChangeHistoryDb());
		when(fileDao.getFileById(anyInt())).thenReturn(new FileDb());
		Mockito.doNothing().when(fileDao).putFile(any(FileDb.class));

		boolean result = this.fileService.saveFile(ID_FILE);
		assertTrue(result, EXPECTED_VALUE);
	}

	@Test
	public void testSaveFileNull() {
		when(changeHistoryDao.getLastChangeOfFile(anyInt())).thenReturn(null);
		when(fileDao.getFileById(anyInt())).thenReturn(new FileDb());
		Mockito.doNothing().when(fileDao).putFile(any(FileDb.class));

		boolean result = this.fileService.saveFile(ID_FILE);
		assertFalse(result, EXPECTED_VALUE);
	}


	@Test
	public void testSaveFileAs() {
		when(dataService.getLastVersionFile(anyInt())).thenReturn(new LastVersionFileRest());
		Mockito.doNothing().when(fileDao).addFile(anyString(), anyString(), anyString());

		boolean result = this.fileService.saveFileAs(ID_FILE, NAME_FILE);
		assertTrue(result, EXPECTED_VALUE);
	}

	@Test
	public void testSaveFileAsNull() {
		when(dataService.getLastVersionFile(anyInt())).thenReturn(null);
		Mockito.doNothing().when(fileDao).addFile(anyString(), anyString(), anyString());

		boolean result = this.fileService.saveFileAs(ID_FILE, NAME_FILE);
		assertFalse(result, EXPECTED_VALUE);
	}


	private TableRest getExampleTable() {
		TableRest table = new TableRest();

		for (int i = 0; i < COLS_GRID; i++) {
			table.addHeader("header_" + i);
		}

		for (int i = 0; i < ROWS_GRID; i++) {
			for (int j = 0; j < COLS_GRID; j++) {
				table.addValue(i, "header_" + j);
			}
		}

		return table;
	}
}
