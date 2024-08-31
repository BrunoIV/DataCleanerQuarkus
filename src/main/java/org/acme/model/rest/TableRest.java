package org.acme.model.rest;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TableRest {
	private List<List<String>> values = new ArrayList<>();
	private List<String> header = new ArrayList<>();

	public void addHeader(String name) {
		this.header.add(name);
	}

	public void addHeader(int position, String name) {
		this.header.add(position, name);
	}

	public String getValue(int row, int col) {
		return values.get(row).get(col);
	}

	public void addValue(int row, String value) {
		if(values.size() < row + 1) {
			this.addRow();
		}

		this.values.get(row).add(value);
	}

	public void addValue(int row, int col, String value) {
		if(values.size() < row + 1) {
			this.addRow();
		}

		this.values.get(row).add(col, value);
	}

	public void addRow() {
		this.values.add(new ArrayList<>());
	}

	public void addRow(int position) {

	}

	public void addColumn(int position){

	}
}
