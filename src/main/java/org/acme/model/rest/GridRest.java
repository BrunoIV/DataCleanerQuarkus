package org.acme.model.rest;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class GridRest {
	private List<Map<String, Object>> values;
	private List<ColumnHeaderRest> header;
}
