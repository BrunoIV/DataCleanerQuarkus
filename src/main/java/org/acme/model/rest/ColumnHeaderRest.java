package org.acme.model.rest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ColumnHeaderRest {
	private String headerName;
	private String valueGetter;
	private String field;
	private Integer rowIndex;
	private Integer width;
	private Boolean editable = true;
	private Boolean rowDrag = false;
	private Boolean resizable;
	private String cellClass;
	private Boolean suppressMovable = true;

	public ColumnHeaderRest(String headerName, boolean editable, boolean rowDrag) {
		this.headerName = headerName;
		this.field = headerName;
		this.editable = editable;
		this.rowDrag = rowDrag;
	}

	public ColumnHeaderRest(String headerName) {
		this.headerName = headerName;
		this.field = headerName;
	}
}
