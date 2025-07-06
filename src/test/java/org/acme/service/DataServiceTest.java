package org.acme.service;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectSpy;
import jakarta.inject.Inject;
import org.acme.dao.*;
import org.acme.db.*;
import org.acme.model.rest.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@QuarkusTest
public class DataServiceTest {
	private static final int ROW_INDEX = 1;
	private static final String HEADER_NAME = "id";
	private static final int COL_INDEX = 0;
	private static final String NEW_VALUE = "new value";
	private static final int ID_FILE = 0;
	private static final int ID_HISTORY = 1;

	private static final List<Integer> COLUMNS = Arrays.asList(1, 0);
	private static final String EXAMPLE_CSV = "a,b,c\nd,e,f\ng,h,i";

	private static final String EXPECTED_SIZE = "Expected size";
	private static final String EXPECTED_VALUE = "Expected value";



	@Inject
	private DataService dataService;

	@InjectMock
	private ChangeHistoryDao changeHistoryDao;

	@InjectSpy
	private DataService dataServiceSpy;


	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		Mockito.when(dataServiceSpy.getFileAsTable(anyInt())).thenReturn(getExampleTable());
	}

	@Test
	public void testSearchNumber1() {
		GridRest grid = dataService.searchNumber(3, 500, List.of(2), ID_FILE);
		assertEquals(1, grid.getValues().size());
	}

	@Test
	public void testSearchNumber2() {
		GridRest grid = dataService.searchNumber(1, 60, List.of(2), ID_FILE);
		assertEquals(1, grid.getValues().size());
	}

	@Test
	public void testSearchNumber3() {
		GridRest grid = dataService.searchNumber(8, 40, List.of(2), ID_FILE);
		assertEquals(0, grid.getValues().size());
	}

	@Test
	public void testGetDataHistory() {
		when(changeHistoryDao.getFileContentById(anyInt())).thenReturn(EXAMPLE_CSV);

		GridRest grid = dataService.getDataHistory(ID_HISTORY);
		assertNotNull(grid);
		assertEquals(4, grid.getHeader().size()); //3 columns (+1 fake header with the row_number)
		assertEquals(2, grid.getValues().size()); //3 rows, but first is header
	}

	@Test
	public void testNormalize() {
		GridRest grid = dataService.normalize("lowercase", COLUMNS, ID_FILE);
		assertEquals(grid.getValues().get(0).get("id"), "id_0");
	}


	@Test
	public void testSearchTextEndsWith() {
		GridRest grid = dataService.searchText("e_1", 2, List.of(1), ID_FILE);
		assertEquals(1, grid.getValues().size(), EXPECTED_SIZE);
		assertEquals("name_1", grid.getValues().get(0).get("name"), EXPECTED_VALUE);
	}

	@Test
	public void testSearchTextStartsWith() {
		GridRest grid = dataService.searchText("name_", 1, List.of(1), ID_FILE);
		assertEquals(2, grid.getValues().size(), EXPECTED_SIZE);
		assertEquals("name_0", grid.getValues().get(0).get("name"), EXPECTED_VALUE);
		assertEquals("name_1", grid.getValues().get(1).get("name"), EXPECTED_VALUE);

	}

	@Test
	public void testSearchTextContains() {
		GridRest grid = dataService.searchText("ame_", 0, List.of(1), ID_FILE);
		assertEquals(2, grid.getValues().size(), EXPECTED_SIZE);
		assertEquals("name_0", grid.getValues().get(0).get("name"), EXPECTED_VALUE);
		assertEquals("name_1", grid.getValues().get(1).get("name"), EXPECTED_VALUE);
	}


	@Test
	public void testModifyValue() {
		ValueEditRest value = new ValueEditRest();
		value.setValue(NEW_VALUE);
		value.setRowIndex(ROW_INDEX);
		value.setColIndex(COL_INDEX);
		value.setIdFile(0);

		GridRest newGrid = this.dataService.modifyValue(value);
		assertEquals(NEW_VALUE, newGrid.getValues().get(ROW_INDEX).get(HEADER_NAME), EXPECTED_VALUE);
	}

	@Test
	public void testValidate() {
		List<ValidationRest> rest = dataService.validate("validate_email", COLUMNS, ID_FILE);
		assertFalse(rest.isEmpty());
	}

	@Test
	public void testCsvToTable() {
		TableRest table = this.dataService.csv2table(EXAMPLE_CSV);
		assertEquals(2, table.getValues().size(), EXPECTED_SIZE);
		assertEquals(3, table.getHeader().size(), EXPECTED_SIZE);
	}

	@Test
	public void testTable2grid() {
		TableRest table = getExampleTable();
		GridRest grid = this.dataService.table2grid(table);
		assertEquals(grid.getHeader().size() - 1, table.getHeader().size(), EXPECTED_SIZE);
		assertEquals(grid.getValues().size(), table.getValues().size(), EXPECTED_SIZE);
	}


	@Test
	public void testGetFileAsTable() {

		LastVersionFileRest last = new LastVersionFileRest();
		Mockito.when(this.dataServiceSpy.getLastVersionFile(anyInt())).thenReturn(last);

		TableRest table = getExampleTable();
		Mockito.when(this.dataServiceSpy.csv2table(anyString())).thenReturn(table);

		TableRest result = this.dataService.getFileAsTable(ID_FILE);
		assertNotNull(result);
		assertInstanceOf(TableRest.class, result);

	}

	@Test
	public void testTable2Csv() {
		TableRest table = getExampleTable();
		String csv = this.dataService.table2csv(table);

		String[] lines = csv.split("\n");
		String[] cols = lines[0].split(",");

		//Including header
		assertEquals(lines.length, table.getValues().size() + 1);
		assertEquals(cols.length, table.getHeader().size());
	}

	@Test
	public void testGetData() {
		LastVersionFileRest last = new LastVersionFileRest();
		last.setFileContent("a,b,c\nd,e,f\nd,e,f");
		Mockito.when(this.dataServiceSpy.getLastVersionFile(anyInt())).thenReturn(last);
		GridRest result = this.dataService.getData(ID_FILE);
		assertNotNull(result);
		assertEquals(2, result.getValues().size(), EXPECTED_SIZE); //2 rows (first is header)
		assertEquals(4, result.getHeader().size(), EXPECTED_SIZE); //3 columns + "auto-incremental"
	}

	@Test
	public void testFillAutoIncremental() {
		GridRest grid = this.dataService.fillAutoIncremental(COLUMNS, ID_FILE);
		for (int i = 0; i < grid.getValues().size(); i++) {
			assertEquals("" + (i+1), grid.getValues().get(i).get("id"));
		}
	}

	@Test
	public void testGetHistory() {
		ChangeHistoryDb db1 = new ChangeHistoryDb();
		db1.setId(1);
		db1.setDescription("Desc1");

		ChangeHistoryDb db2 = new ChangeHistoryDb();
		db2.setId(2);
		db2.setDescription("Desc2");


		List<ChangeHistoryDb> changesDb = new ArrayList<>();
		changesDb.add(db1);
		changesDb.add(db2);

		when(changeHistoryDao.lstChanges(anyInt())).thenReturn(changesDb);

		List<ChangesRest> result = dataService.getHistory(ID_FILE);
		assertNotNull(result);
		assertEquals(2, result.size(), EXPECTED_SIZE);
		assertEquals(2, result.get(1).getId(),  EXPECTED_VALUE);
		assertEquals("Desc2", result.get(1).getDescription(),  EXPECTED_VALUE);
	}



	@Test
	public void testFillFixedValue() {
		final int idColumn = 1;

		GridRest grid = this.dataService.fillFixedValue(NEW_VALUE, List.of(idColumn), ID_FILE);
		for (int i = 0; i < grid.getValues().size(); i++) {
			//Added 1 because in grid, first column is a fake-header with the row_number
			String colname = grid.getHeader().get(idColumn + 1).getHeaderName();
			assertEquals(NEW_VALUE, grid.getValues().get(i).get(colname));
		}
	}


	private TableRest getExampleTable() {
		TableRest table = new TableRest();
		table.addHeader("id");
		table.addHeader("name");
		table.addHeader("number");

		table.addValue(0, "id_0");
		table.addValue(0, "name_0");
		table.addValue(0, "2");
		table.addValue(1, "id_1");
		table.addValue(1, "name_1");
		table.addValue(1, "76");
		return table;
	}

}
