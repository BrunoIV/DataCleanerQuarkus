package org.acme.service;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectSpy;
import jakarta.inject.Inject;
import org.acme.dao.FileDao;
import org.acme.db.FileDb;
import org.acme.model.rest.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

@QuarkusTest
public class DataServiceTest {
	private static final int ROW_INDEX = 1;
	private static final String HEADER_NAME = "id";
	private static final int COL_INDEX = 0;
	private static final String NEW_VALUE = "new value";
	private static final int ID_FILE = 0;
	private static final List<Integer> COLUMNS = Arrays.asList(1, 0);

	@Inject
	private DataService dataService;

	@InjectMock
	private FileDao fileDao;


	@InjectSpy
	private DataService dataServiceSpy;


	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testNormalize() {
		TableRest table = getExampleTable();
		Mockito.when(this.dataServiceSpy.getFileAsTable(anyInt())).thenReturn(table);

		dataService.normalize("lowercase", COLUMNS, ID_FILE);
		assertEquals(table.getValue(0, 0), "id_0");

		dataService.normalize("trim", COLUMNS, ID_FILE);
		assertEquals(table.getValue(0, 0), "id_0");

		dataService.normalize("uppercase", COLUMNS, ID_FILE);
		assertEquals(table.getValue(0, 0), "ID_0");

		dataService.normalize("capitalize", COLUMNS, ID_FILE);
		assertEquals(table.getValue(0, 0), "Id_0");
	}


	@Test
	public void testModifyValue() {
		TableRest table = getExampleTable();
		int rows = table.getValues().size();

		Mockito.when(this.dataServiceSpy.getFileAsTable(anyInt())).thenReturn(table);

		ValueEditRest value = new ValueEditRest();
		value.setValue(NEW_VALUE);
		value.setRowIndex(ROW_INDEX);
		value.setColIndex(COL_INDEX);
		value.setIdFile(0);

		GridRest newGrid = this.dataService.modifyValue(value);
		assertEquals(NEW_VALUE, newGrid.getValues().get(ROW_INDEX).get(HEADER_NAME));
		assertEquals(rows, newGrid.getValues().size());
	}

	@Test
	public void testValidate() {
		TableRest table = getExampleTable();
		Mockito.when(this.dataServiceSpy.getFileAsTable(anyInt())).thenReturn(table);

		List<ValidationRest> rest = dataService.validate("validate_email", COLUMNS, ID_FILE);
		assertFalse(rest.isEmpty());
	}

	@Test
	public void testCsvToTable() {
		String csv = "ID,Valor,Activo\n5,51,true\n6,52,true";

		TableRest table = this.dataService.csv2table(csv);
		assertEquals(table.getValues().size(), 2);
		assertEquals(table.getHeader().size(), 3);
	}

	@Test
	public void testTable2grid() {
		TableRest table = getExampleTable();
		GridRest grid = this.dataService.table2grid(table);
		assertEquals(grid.getHeader().size() - 1, table.getHeader().size());
		assertEquals(grid.getValues().size(), table.getValues().size());
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
		assertEquals(2, result.getValues().size()); //2 rows (first is header)
		assertEquals(4, result.getHeader().size()); //3 columns + "auto-incremental"
	}

	@Test
	public void testFillAutoIncremental() {
		TableRest table = getExampleTable();
		int rows = table.getValues().size();
		Mockito.when(this.dataServiceSpy.getFileAsTable(anyInt())).thenReturn(table);
		this.dataService.fillAutoIncremental(COLUMNS, ID_FILE);
		for (int i = 0; i < rows; i++) {
			assertEquals("" + (i+1), table.getValues().get(i).get(COLUMNS.get(0)));
		}
	}


	@Test
	public void testFillFixedValue() {
		GridRest grid = getExampleGrid();
		int rows = grid.getValues().size();
		TableRest table = getExampleTable();
		Mockito.when(this.dataServiceSpy.getFileAsTable(anyInt())).thenReturn(table);
		this.dataService.fillFixedValue(NEW_VALUE, COLUMNS, ID_FILE);
		for (int i = 0; i < rows; i++) {
			assertEquals(NEW_VALUE, table.getValues().get(i).get(COLUMNS.get(0)));
		}
	}


	private TableRest getExampleTable() {
		TableRest table = new TableRest();
		table.addHeader("id");
		table.addHeader("name");

		table.addValue(0, "id_0");
		table.addValue(0, "name_0");
		table.addValue(1, "name_1");
		table.addValue(1, "name_1");
		return table;
	}

	private GridRest getExampleGrid() {
		GridRest grid = new GridRest();
		grid.addValue(0, "id", "id_0");
		grid.addValue(0, "name", "name_0");
		grid.addValue(1, "id", "id_1");
		grid.addValue(1, "name", "name_1");
		return grid;
	}
}
