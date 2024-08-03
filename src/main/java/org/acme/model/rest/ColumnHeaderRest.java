package org.acme.model.rest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ColumnHeaderRest {
	private String headerName;
	private String field;
	private Boolean editable;

	public ColumnHeaderRest(String headerName, String field, boolean editable) {
		this.headerName = headerName;
		this.field = field;
		this.editable = editable;
	}
}
