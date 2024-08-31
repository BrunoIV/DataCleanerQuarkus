package org.acme.service;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectSpy;
import jakarta.inject.Inject;
import org.acme.dao.FileDao;
import org.acme.db.FileDb;
import org.acme.model.rest.GridRest;
import org.acme.model.rest.TableRest;
import org.acme.model.rest.ValueEditRest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;

@QuarkusTest
public class DataServiceTest {
	private static final int ROW_INDEX = 1;
	private static final String HEADER_NAME = "id";
	private static final int COL_INDEX = 0;
	private static final String NEW_VALUE = "new value";

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
	public void testModifyValue() {
		GridRest grid = getExampleGrid();
		int rows = grid.getValues().size();

		TableRest table = getExampleTable();

		FileDb file = new FileDb();
		file.setFileContent(dataService.table2csv(getExampleTable()));
		Mockito.when(this.fileDao.getFileById(anyInt())).thenReturn(file);


		Mockito.when(this.dataServiceSpy.getFileAsTable(0)).thenReturn(table);


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
		//TODO
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
