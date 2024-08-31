package org.acme.service;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectSpy;
import jakarta.inject.Inject;
import org.acme.model.rest.ColumnHeaderRest;
import org.acme.model.rest.GridRest;
import org.acme.model.rest.TableRest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@QuarkusTest
public class StructureServiceTest {
	private static final int ROW_INDEX = 1;
	private static final String NEW_COLUMN_NAME = "new_column";
	private static final int ID_FILE = 0;
	private static final int NUMBER_ROWS = 5;
	private static final int NEW_COLUMN_POSITION = 1;




	@InjectMock
	private DataService dataService;

	@Inject
	private StructureService structureService;

	@InjectMock private FileService fileService;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testAddColumn() {
		TableRest table = getExampleTable();
		int cols = table.getHeader().size();

		Mockito.when(dataService.getFileAsTable(anyInt())).thenReturn(table);
		Mockito.when(dataService.table2grid(any(TableRest.class))).thenReturn(getExampleGrid());

		//As the response of "addColumn" is mocked I use "table" for know if the new column exists
		GridRest grid = structureService.addColumn(NEW_COLUMN_NAME, NEW_COLUMN_POSITION, ID_FILE);
		assertNotNull(grid);
		assertEquals(cols + 1, table.getHeader().size());
		assertEquals(NEW_COLUMN_NAME, table.getHeader().get(NEW_COLUMN_POSITION));
	}

	@Test
	public void testAddRow() {
		TableRest table = getExampleTable();
		int originalNumberItems = table.getValues().size();

		Mockito.when(dataService.getFileAsTable(anyInt())).thenReturn(table);
		Mockito.when(dataService.table2grid(any(TableRest.class))).thenReturn(getExampleGrid());
		Mockito.doNothing().when(fileService).putTable(anyInt(), any(TableRest.class));

		//As the response of "addRow" is mocked I use "table" for know if the new row exists
		GridRest grid = structureService.addRow(ROW_INDEX, ID_FILE);
		assertNotNull(grid);
		assertEquals(originalNumberItems + 1, table.getValues().size() );
	}

	@Test
	public void testDeleteRows() {
		TableRest table = getExampleTable();
		int originalNumberItems = table.getValues().size();

		Mockito.when(dataService.getFileAsTable(anyInt())).thenReturn(table);
		Mockito.when(dataService.table2grid(any(TableRest.class))).thenReturn(getExampleGrid());
		Mockito.doNothing().when(fileService).putTable(anyInt(), any(TableRest.class));

		List<Integer> rowsToDelete = Arrays.asList(3,1,2);
		GridRest grid = structureService.deleteRows(rowsToDelete, ID_FILE);
		assertNotNull(grid);
		assertEquals(table.getValues().size(), originalNumberItems - rowsToDelete.size());
	}


	@Test
	public void testDeleteColumns() {
		TableRest table = getExampleTable();
		int originalNumberItems = table.getHeader().size();

		Mockito.when(dataService.getFileAsTable(anyInt())).thenReturn(table);
		Mockito.when(dataService.table2grid(any(TableRest.class))).thenReturn(getExampleGrid());
		Mockito.doNothing().when(fileService).putTable(anyInt(), any(TableRest.class));

		List<Integer> colsToDelete = Arrays.asList(1,2);
		GridRest grid = structureService.deleteColumns(colsToDelete, ID_FILE);
		assertNotNull(grid);
		assertEquals(table.getHeader().size(), originalNumberItems - colsToDelete.size());
	}

	@Test
	public void testJoinColumns() {
		//TODO: method is unfinished
	}

	private GridRest getExampleGrid() {
		GridRest grid = new GridRest();
		grid.addHeader(new ColumnHeaderRest("id"));
		grid.addHeader(new ColumnHeaderRest("name"));
		grid.addHeader(new ColumnHeaderRest("surname"));

		for(int i = 0; i < NUMBER_ROWS; i++){
			grid.addValue(i, "id", "id_" + i);
			grid.addValue(i, "name", "name_" + i);
			grid.addValue(i, "surname", "surname_" + i);
		}

		return grid;
	}

	private TableRest getExampleTable() {
		TableRest table = new TableRest();
		table.addHeader("id");
		table.addHeader("name");
		table.addHeader("surname");

		for(int i = 0; i < NUMBER_ROWS; i++){
			table.addValue(i, 0, "id_" + i);
			table.addValue(i, 1, "name_" + i);
			table.addValue(i, 2, "surname_" + i);
		}

		return table;
	}
}
