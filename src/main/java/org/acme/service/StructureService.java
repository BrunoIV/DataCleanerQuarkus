package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.model.rest.GridRest;
import org.acme.model.rest.TableRest;

import java.util.*;

@ApplicationScoped
public class StructureService {

	@Inject FileService fileService;

	@Inject DataService dataService;

	public GridRest addColumn(String name, int columnPosition, int idFile) {
		TableRest table = dataService.getFileAsTable(idFile);

		if(table != null) {
			table.addHeader(columnPosition, name);

			for(int i = 0; i < table.getValues().size(); i++) {
				table.addValue(i, columnPosition, "");
			}

			fileService.addChangeHistory(idFile, table, "Add column");
			return dataService.table2grid(table);
		}

		return null;
	}

	@Transactional
	public GridRest addRow(int position, int idFile) {
		TableRest table = dataService.getFileAsTable(idFile);

		if(table != null) {

			List<String> list = new ArrayList<>(Collections.nCopies(table.getHeader().size(), ""));
			table.getValues().add(position, list);

			fileService.addChangeHistory(idFile, table, "Add row");
			return dataService.table2grid(table);
		}
		return null;
	}

	public GridRest deleteRows(List<Integer> indexes, int idFile) {
		TableRest table = dataService.getFileAsTable(idFile);

		if(table != null) {
			//From bigger to smaller to avoid problems with changes of indexes
			indexes.sort(Comparator.reverseOrder());
			for (int index: indexes) {
				table.getValues().remove(index);
			}

			fileService.addChangeHistory(idFile, table, "Delete row(s)");
			return dataService.table2grid(table);
		}

		return null;
	}

	public GridRest deleteColumns(List<Integer> indexes, int idFile) {
		TableRest table = dataService.getFileAsTable(idFile);

		if(table != null) {
			//From bigger to smaller to avoid problems with changes of indexes
			indexes.sort(Comparator.reverseOrder());
			for (int index: indexes) {
				table.getHeader().remove(index);
			}

			List<List<String>> rows = table.getValues();
			for (List<String> row : rows) {
				for (int index: indexes) {
					row.remove(index );
				}
			}

			fileService.addChangeHistory(idFile, table, "Delete column(s)");
			return dataService.table2grid(table);
		}

		return null;
	}

	@Transactional
	public GridRest joinColumns(List<Integer> indexes, int idFile) {
		TableRest table = dataService.getFileAsTable(idFile);

		if(table != null) {

			List<List<String>> rows = table.getValues();
			for (List<String> row : rows) {
				StringBuilder newValue = new StringBuilder();
				for (int column: indexes) {
					newValue.append(row.get(column));
				}

				row.set(indexes.get(0), newValue.toString());
			}
			fileService.addChangeHistory(idFile, table, "Join columns");
			return dataService.table2grid(table);
		}
		return null;
	}
}
