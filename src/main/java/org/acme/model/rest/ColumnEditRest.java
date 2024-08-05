package org.acme.model.rest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ColumnEditRest {
	private String headerName;
	private String field;
	private Boolean editable;
	private Integer rowIndex;
}
