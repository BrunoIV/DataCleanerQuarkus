package org.acme.model.rest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValueEditRest {
	private String headerName;
	private String value;
	private Integer rowIndex;
}
