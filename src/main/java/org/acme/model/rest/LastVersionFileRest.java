package org.acme.model.rest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LastVersionFileRest {
	private Integer id;
	private String fileContent;
	private String fileType;
}
