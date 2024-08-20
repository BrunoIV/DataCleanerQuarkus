package org.acme.service;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.acme.model.rest.GridRest;
import org.acme.model.rest.ValueEditRest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class DataServiceTest {
	private static final int ROW_INDEX = 1;
	private static final String HEADER_NAME = "id";
	private static final String NEW_VALUE = "new value";

	@Inject
	private DataService dataService;

	@InjectMock
	private GridService gridService;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}


	@Test
	public void testModifyValue() {
		GridRest grid = getExampleGrid();
		int rows = grid.getValues().size();
		Mockito.when(gridService.getGrid()).thenReturn(grid);

		ValueEditRest value = new ValueEditRest();
		value.setValue(NEW_VALUE);
		value.setRowIndex(ROW_INDEX);
		value.setHeaderName(HEADER_NAME);

		GridRest newGrid = this.dataService.modifyValue(value);
		assertEquals(NEW_VALUE, newGrid.getValues().get(ROW_INDEX).get(HEADER_NAME));
		assertEquals(rows, newGrid.getValues().size());
	}

	@Test
	public void testValidate() {
		//TODO
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
