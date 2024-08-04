package org.acme.model.rest;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class GridRest {
	private List<Map<String, Object>> values = new ArrayList<>();
	private List<ColumnHeaderRest> header = new ArrayList<>();

	public void addHeader(ColumnHeaderRest header) {
		this.header.add(header);
	}

	public void addValue(int row, String key, Object value) {
		if(this.values.size() <= row) {
			this.values.add(new HashMap<>());
		}
		this.values.get(row).put(key, value);
	}
}
