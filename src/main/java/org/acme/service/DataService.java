package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.model.rest.ColumnHeaderRest;
import org.acme.model.rest.GridRest;
import org.acme.util.Utils;

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


	public GridRest normalize(String functionName, String columns) {
		List<Integer> columnList = Utils.text2IntArray(columns);

		for (int column: columnList) {
			ColumnHeaderRest header = this.gridService.getGrid().getHeader().get(column);

			if(header != null) {
				List<Map<String, Object>> gridValues = this.gridService.getGrid().getValues();
				for (Map<String, Object> values: gridValues) {
					String headerName = header.getHeaderName();
					String value = (String) values.get(headerName);

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

		return  gridService.getGrid();
	}


}
