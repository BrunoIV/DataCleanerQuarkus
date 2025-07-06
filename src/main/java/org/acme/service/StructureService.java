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
			TableRest output = new TableRest();
			int firstIndex = indexes.get(0);

			int i = 0;
			for (String header: table.getHeader()) {

				//Only keep the first of "join indexes"
				if(!indexes.contains(i) || firstIndex == i) {
					output.addHeader(header);
				}
				i++;
			}

			i = 0;
			for (List<String> row : table.getValues()) {
				output.addRow();

				StringBuilder newValue = new StringBuilder();
				for (int numberColumn = 0; numberColumn < table.getHeader().size(); numberColumn++) {
					String value = row.get(numberColumn);

					if(!indexes.contains(numberColumn) || firstIndex == numberColumn) {
						output.addValue(i, value);
					}

					if(indexes.contains(numberColumn)) {
						newValue.append(value);
					}
				}

				output.setValue(i, firstIndex, newValue.toString());
				i++;
			}
			fileService.addChangeHistory(idFile, output, "Join columns");
			return dataService.table2grid(output);
		}
		return null;
	}
}
