package org.acme.service;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.acme.model.rest.ColumnHeaderRest;
import org.acme.model.rest.GridRest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class StructureServiceTest {
	private static final int ROW_INDEX = 1;
	private static final String HEADER_NAME = "id";
	private static final String NEW_COLUMN_NAME = "new_column";
	private static final String NEW_VALUE = "new value";

	@Inject
	private StructureService structureService;

	@InjectMock
	private GridService gridService;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}


	@Test
	public void testAddColumn() {
		GridRest grid = getExampleGrid();
		int cols = grid.getHeader().size();
		Mockito.when(gridService.getGrid()).thenReturn(grid);

		GridRest newGrid = this.structureService.addColumn(NEW_COLUMN_NAME, 0);
		assertEquals(cols + 1, newGrid.getHeader().size());
		assertEquals(NEW_COLUMN_NAME, newGrid.getHeader().get(0).getHeaderName());
	}

	@Test
	public void testAddRow() {
		GridRest grid = getExampleGrid();
		int rows = grid.getValues().size();
		Mockito.when(gridService.getGrid()).thenReturn(grid);

		GridRest newGrid = this.structureService.addRow(0);
		assertEquals(rows + 1, newGrid.getValues().size());
	}

	private GridRest getExampleGrid() {
		GridRest grid = new GridRest();
		grid.addHeader(new ColumnHeaderRest("id"));
		grid.addHeader(new ColumnHeaderRest("name"));

		grid.addValue(0, "id", "id_0");
		grid.addValue(0, "name", "name_0");
		grid.addValue(1, "id", "id_1");
		grid.addValue(1, "name", "name_1");
		return grid;
	}
}
