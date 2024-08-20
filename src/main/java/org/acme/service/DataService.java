package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.model.rest.ColumnHeaderRest;
import org.acme.model.rest.GridRest;
import org.acme.model.rest.ValueEditRest;
import org.acme.util.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class DataService {
	@Inject
	GridService gridService;

	private static final String UPPERCASE = "uppercase";
	private static final String LOWERCASE = "lowercase";
	private static final String TRIM = "trim";
	private static final String CAPITALIZE = "capitalize";
	private static final String VALIDATE_EMAIL = "validate_email";
	private static final String VALIDATE_NUMBER = "validate_number";
	private static final String VALIDATE_ALPHA = "validate_alpha";
	private static final String VALIDATE_ALPHANUMERIC = "validate_alphanumeric";


	public GridRest normalize(String functionName, String columns) {
		List<Integer> columnList = Utils.text2IntArray(columns);

		for (int column: columnList) {
			ColumnHeaderRest header = this.gridService.getGrid().getHeader().get(column);

			if(header != null) {
				List<Map<String, Object>> gridValues = this.gridService.getGrid().getValues();
				for (Map<String, Object> values: gridValues) {
					String headerName = header.getHeaderName();
					String value = (String) values.get(headerName);

					if(value != null) {
						switch (functionName) {
							case LOWERCASE:
								value = value.toLowerCase();
								break;
							case UPPERCASE:
								value = value.toUpperCase();
								break;
							case TRIM:
								value = value.trim();
								break;
							case CAPITALIZE:
								value = Utils.capitalize(value);
								break;
							default:
								break;
						}
						values.put(headerName, value);
					}

				}
			}
		}

		return gridService.getGrid();
	}

	public GridRest validate(String functionName, String columns) {
		List<Integer> columnList = Utils.text2IntArray(columns);
		this.gridService.getGrid().setValidationErrors(new HashMap<>());

		for (int column: columnList) {
			ColumnHeaderRest header = this.gridService.getGrid().getHeader().get(column);

			if (header != null) {
				List<Map<String, Object>> rows = this.gridService.getGrid().getValues();

				for (int i = 0; i < rows.size(); i++) {
					String headerName = header.getHeaderName();
					String value = (String) rows.get(i).get(headerName);
					checkValidValue(i, functionName, value);
				}
			}
		}
		return gridService.getGrid();
	}

	private void checkValidValue(int rowIndex, String functionName, String value) {
		switch (functionName) {
			case VALIDATE_EMAIL:
				if (!Utils.isValidEmail(value)) {
					this.gridService.getGrid().addValidationError(rowIndex, "Invalid E-mail");
				}
				break;
			case VALIDATE_NUMBER:
				if (!Utils.isValidNumber(value)) {
					this.gridService.getGrid().addValidationError(rowIndex, "Invalid number");
				}
				break;
			case VALIDATE_ALPHA:
				if (!Utils.isAlpha(value)) {
					this.gridService.getGrid().addValidationError(rowIndex, "Invalid non-alpha characters");
				}
				break;
			case VALIDATE_ALPHANUMERIC:
				if (!Utils.isAlphanumeric(value)) {
					this.gridService.getGrid().addValidationError(rowIndex, "Invalid non-alphanumeric characters");
				}
				break;
			default:
				break;
		}
	}

	public GridRest modifyValue(ValueEditRest value) {
		Map<String, Object> valuesRow = this.gridService.getGrid().getValues().get(value.getRowIndex());

		if(valuesRow != null) {
			valuesRow.put(value.getHeaderName(), value.getValue());
		}

		return gridService.getGrid();
	}
}
