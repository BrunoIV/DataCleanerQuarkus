package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.model.rest.ColumnHeaderRest;
import org.acme.model.rest.GridRest;

import java.util.HashMap;

@ApplicationScoped
public class StructureService {

	@Inject GridService gridService;

	public GridRest addColumn(String name, int columnPosition) {
		GridRest grid = gridService.getGrid();

		ColumnHeaderRest newColumn = new ColumnHeaderRest(name);

		//Move the "draggable" icon from the first column
		if(columnPosition == 1) {
			newColumn.setRowDrag(true);
			grid.getHeader().get(1).setRowDrag(false);
		}

		grid.addHeader(columnPosition, newColumn);
		return grid;
	}

	public GridRest addRow(int position) {
		gridService.getGrid().getValues().add(position, new HashMap<>());
		return gridService.getGrid();
	}
}
