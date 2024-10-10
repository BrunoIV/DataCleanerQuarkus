package org.acme.model.rest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangesRest {
	private Integer id;
	private Integer idFile;
	private String date;
	private String description;
}
