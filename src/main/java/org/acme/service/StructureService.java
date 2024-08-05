package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.model.rest.ColumnEditRest;
import org.acme.model.rest.ColumnHeaderRest;
import org.acme.model.rest.GridRest;
import java.util.List;

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

	public GridRest getColumns() {
		GridRest grid = new GridRest();
		grid.addHeader(new ColumnHeaderRest("headerName","headerName", true, true));
		grid.addHeader(new ColumnHeaderRest("field","field", true, false));
		grid.addHeader(new ColumnHeaderRest("Editable","editable", true, false));

		List<ColumnHeaderRest> header = gridService.getGrid().getHeader();
		for (int i = 0; i < header.size(); i++) {
			ColumnHeaderRest headerColumn = header.get(i);
			grid.addValue(i, "field", headerColumn.getField());
			grid.addValue(i, "editable", headerColumn.getEditable());
			grid.addValue(i, "headerName", headerColumn.getHeaderName());
		}
		return grid;
	}
}
