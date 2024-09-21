package org.acme.model.rest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidationRest {
	private Integer line;
	private String column;
	private String error;
}
