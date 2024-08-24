package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.model.rest.ColumnHeaderRest;
import org.acme.model.rest.GridRest;

import java.util.*;

@ApplicationScoped
public class StructureService {

	@Inject GridService gridService;
	@Inject FileService fileService;

	public GridRest addColumn(String name, int columnPosition) {
		GridRest grid = gridService.getGrid();

		ColumnHeaderRest newColumn = new ColumnHeaderRest(name);

		//Move the "draggable" icon from the first column
		if(columnPosition == 1) {
			newColumn.setRowDrag(true);

			//Removes the "drag" from the old 1st column
			if(grid.getHeader().size() > 1) {
				grid.getHeader().get(1).setRowDrag(false);
			}
		}

		grid.addHeader(columnPosition, newColumn);
		fileService.saveCurrentFile();
		return grid;
	}

	public GridRest addRow(int position) {
		GridRest grid = gridService.getGrid();
		grid.getValues().add(position, new LinkedHashMap<>());
		fileService.saveCurrentFile();
		return grid;
	}

	public GridRest deleteRows(List<Integer> indexes) {
		GridRest grid = this.gridService.getGrid();

		//From bigger to smaller to avoid problems with changes of indexes
		indexes.sort(Comparator.reverseOrder());
		for (int index: indexes) {
			grid.getValues().remove(index);
		}
		fileService.saveCurrentFile();
		return grid;
	}

	public GridRest deleteColumns(List<Integer> indexes) {
		GridRest grid = this.gridService.getGrid();

		//From bigger to smaller to avoid problems with changes of indexes
		indexes.sort(Comparator.reverseOrder());

		//Removes headers
		List<String> keys = new ArrayList<>();
		for (int index: indexes) {
			keys.add(grid.getHeader().get(index).getField());
			grid.getHeader().remove(index);
		}

		//Removes cells line by line
		for (Map<String, Object> row: grid.getValues()) {
			for (String key : keys) {
				row.remove(key);
			}
		}

		fileService.saveCurrentFile();
		return grid;
	}
}
