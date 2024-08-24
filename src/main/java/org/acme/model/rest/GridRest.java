package org.acme.model.rest;

import lombok.Getter;
import lombok.Setter;
import org.acme.util.Utils;

import java.util.*;

@Getter
@Setter
public class GridRest {
	private List<LinkedHashMap<String, Object>> values = new ArrayList<>();
	private List<ColumnHeaderRest> header = new ArrayList<>();
	private Map<Integer, String> validationErrors = new HashMap<>();

	public GridRest() {
		ColumnHeaderRest col = new ColumnHeaderRest("#", "n",false,false);
		col.setValueGetter("node.rowIndex + 1");
		col.setWidth(50);
		col.setResizable(false);
		col.setCellClass("bg-fake-header");

		this.header.add(col);
	}

	public void addHeader(ColumnHeaderRest header) {
		this.header.add(header);
	}

	public void addHeader(int position, ColumnHeaderRest header) {
		this.header.add(position, header);
	}
	public void addValue(int row, String key, Object value) {
		if(this.values.size() <= row) {
			this.values.add(new LinkedHashMap<>());
		}
		this.values.get(row).put(key, value);
	}

	public void addValidationError(int line, String error) {
		this.validationErrors.put(line, error + "\n" + Utils.notNullString(this.validationErrors.get(line)));
	}

}
