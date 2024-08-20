package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.model.rest.ColumnEditRest;
import org.acme.model.rest.ColumnHeaderRest;
import org.acme.model.rest.GridRest;

@ApplicationScoped
public class StructureService {

	@Inject GridService gridService;

	public GridRest modifyColumn(ColumnEditRest attributes) {
		GridRest grid = gridService.getGrid();

		ColumnHeaderRest column = grid.getHeader().get(attributes.getRowIndex());
		column.setField(attributes.getField());
		column.setEditable(attributes.getEditable());
		column.setHeaderName(attributes.getHeaderName());

		return grid;
	}

}
