package org.acme.service;


import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.acme.model.rest.ColumnHeaderRest;
import org.acme.model.rest.GridRest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class FileServiceTest {

	private static final int ROWS_GRID = 5;

	@Inject
	private FileService fileService;

	@InjectMock
	private GridService gridService;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testExportAsCsv() {

	}

	private GridRest getExampleGrid() {
		GridRest grid = new GridRest();
		grid.addHeader(new ColumnHeaderRest("id"));
		grid.addHeader(new ColumnHeaderRest("name"));
		grid.addHeader(new ColumnHeaderRest("surname"));

		for(int i = 0; i < ROWS_GRID; i++){
			grid.addValue(i, "id", "id_" + i);
			grid.addValue(i, "name", "name_" + i);
			grid.addValue(i, "surname", "surname_" + i);
		}

		return grid;
	}
}
