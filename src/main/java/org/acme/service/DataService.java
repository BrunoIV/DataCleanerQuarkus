package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.dao.ChangeHistoryDao;
import org.acme.dao.FileDao;
import org.acme.db.ChangeHistoryDb;
import org.acme.db.FileDb;
import org.acme.model.rest.*;
import org.acme.util.Utils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@ApplicationScoped
public class DataService {
	@Inject
	FileService fileService;

	@Inject
	FileDao fileDao;

	@Inject
	ChangeHistoryDao changeHistoryDao;

	private static final String UPPERCASE = "uppercase";
	private static final String LOWERCASE = "lowercase";
	private static final String TRIM = "trim";
	private static final String CAPITALIZE = "capitalize";
	private static final String VALIDATE_EMAIL = "validate_email";
	private static final String VALIDATE_NUMBER = "validate_number";
	private static final String VALIDATE_ALPHA = "validate_alpha";
	private static final String VALIDATE_ALPHANUMERIC = "validate_alphanumeric";


	@Transactional
	public GridRest normalize(String functionName, List<Integer> columnList, int idFile) {
		TableRest table = this.getFileAsTable(idFile);

		if(table != null) {
			for (List<String> row: table.getValues()) {
				for (int column: columnList) {
					String value = row.get(column);
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
						row.set(column, value);
					}
				}
			}
			fileService.addChangeHistory(idFile, table, functionName);
			return table2grid(table);
		}

		return null;
	}

	public List<ValidationRest> validate(String functionName, List<Integer> columnList, int idFile) {
		TableRest table = this.getFileAsTable(idFile);
		List<ValidationRest> validationErrors = new ArrayList<>();

		if(table != null) {
			for (int i = 0; i < table.getValues().size(); i++){
				for (int column : columnList) {
					String value = table.getValue(i, column);
					String error = checkValidValue(functionName, value);
					if(error != null) {
						ValidationRest err = new ValidationRest();
						err.setLine(i);
						err.setColumn(table.getHeader().get(column));
						err.setError(error + " \"" + value + "\"");
						validationErrors.add(err);
					}
				}
			}
		}

		return validationErrors;
	}

	private String checkValidValue(String functionName, String value) {
		switch (functionName) {
			case VALIDATE_EMAIL:
				if (!Utils.isValidEmail(value)) {
					return "Invalid E-mail";
				}
				break;
			case VALIDATE_NUMBER:
				if (!Utils.isValidNumber(value)) {
					return "Invalid number";
				}
				break;
			case VALIDATE_ALPHA:
				if (!Utils.isAlpha(value)) {
					return "Invalid non-alpha characters";
				}
				break;
			case VALIDATE_ALPHANUMERIC:
				if (!Utils.isAlphanumeric(value)) {
					return "Invalid non-alphanumeric characters";
				}
				break;
			default:
				break;
		}
		return null;
	}

	@Transactional
	public GridRest modifyValue(ValueEditRest value) {
		TableRest table = this.getFileAsTable(value.getIdFile());

		if(table != null) {
			table.getValues().get(value.getRowIndex()).set(value.getColIndex(), value.getValue());
			String headerName = table.getHeader().get(value.getColIndex());

			fileService.addChangeHistory(value.getIdFile(), table,
					String.format(
							"Modified the value of column \"%s\" in row %d to \"%s\".",
							headerName,
							value.getRowIndex() + 1,
							value.getValue()
					)
			);
			return table2grid(table);
		}

		return null;
	}

	@Transactional
	public TableRest csv2table(String csv) {
		TableRest table = new TableRest();

		if(csv == null) {
			return table;
		}

		InputStream inputStream = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
		Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

		CSVParser parser = null;
		try {

			CSVFormat csvFormat = CSVFormat.Builder.create()
					.setIgnoreEmptyLines(true)
					.setEscape('\\')
					.setQuoteMode(QuoteMode.NONE)
					.build();

			parser = new CSVParser(reader, csvFormat);
			List<CSVRecord> list = parser.getRecords();
			for (int line = 0; line < list.size(); line++) {
				for (int j = 0; j < list.get(line).size(); j++) {

					//First line (0) is header
					if (line == 0) {
						table.addHeader(list.get(line).get(j));
					} else {
						//Second line (1) is first (0) line of data
						table.addValue(line - 1, list.get(line).get(j));
					}
				}
			}
		} catch (IOException e) {
			return null;
		}

		return table;
	}

	public GridRest table2grid(TableRest table) {
		GridRest grid = new GridRest();

		List<String> headers = table.getHeader();
		for (String header : headers) {
			grid.addHeader(new ColumnHeaderRest(header));
		}

		List<List<String>> values = table.getValues();
		for (int row = 0; row < values.size(); row++) {
			for (int col = 0; col < values.get(row).size(); col++) {
				grid.addValue(row, headers.get(col), table.getValue(row, col));
			}
		}
		return grid;
	}

	public boolean fileHasUnsavedChanges(int idFile) {
		FileDb db = this.fileDao.getFileById(idFile);

		if(db != null) {
			ChangeHistoryDb changes = changeHistoryDao.getLastChangeOfFile(idFile);
			return (changes != null
					&& changes.getCreationDate() != null
					&& changes.getCreationDate().after(db.getCreationDate()));
		}

		return false;
	}

	public LastVersionFileRest getLastVersionFile(int idFile) {

		//If the file is deleted it doesn't matter the history table
		FileDb db = this.fileDao.getFileById(idFile);

		if(db == null) {
			return null;
		}

		LastVersionFileRest rs = new LastVersionFileRest();
		rs.setId(idFile);

		if(fileHasUnsavedChanges(idFile)) {
			ChangeHistoryDb changes = changeHistoryDao.getLastChangeOfFile(idFile);
			if(changes != null) {
				rs.setFileContent(changes.getFileContent());
				//rs.setFileType(changes.getType());
			}
		} else {
			rs.setFileContent(db.getFileContent());
			rs.setFileType(db.getType());
		}

		return rs;
	}

	@Transactional
	public TableRest getFileAsTable(int idFile) {
		LastVersionFileRest lastVersion = getLastVersionFile(idFile);
		if(lastVersion != null) {
			return csv2table(lastVersion.getFileContent());
		}
		return null;
	}

	@Transactional
	public String table2csv(TableRest table) {
		List<String> headers = table.getHeader();
		int columns = headers.size();

		StringBuilder csv = new StringBuilder();
		for(int i = 0; i < columns; i++) {
			csv.append(headers.get(i));
			if(i < columns - 1) {
				csv.append(",");
			}
		}
		csv.append("\n");

		List<List<String>> rows = table.getValues();
		for (List<String> row: rows) {

			int i = 0;
			for (String col : row) {
				csv.append(col);

				//If the last character is "," te CSV parser things that is a empty column
				if(i < columns - 1) {
					csv.append(",");
				}
				i++;
			}
			csv.append("\n");
		}
		return csv.toString();
	}

	@Transactional
	public GridRest getData(int idFile) {
		LastVersionFileRest lastVersion = getLastVersionFile(idFile);
		if(lastVersion == null) {
			return null;
		}

		String csv = lastVersion.getFileContent();
		TableRest table = csv2table(csv);
		GridRest grid = table2grid(table);
		grid.setUnsavedChanges(fileHasUnsavedChanges(idFile));
		grid.setFileType(lastVersion.getFileType());
		return grid;
	}

	@Transactional
	public GridRest getDataHistory(int idHistory) {
		String csv = changeHistoryDao.getFileContentById(idHistory);
		TableRest table = csv2table(csv);
		GridRest grid = table2grid(table);
		grid.setUnsavedChanges(false);
		return grid;
	}



	public GridRest fillAutoIncremental(List<Integer> columnList, int idFile) {
		TableRest table = this.getFileAsTable(idFile);

		if(table != null) {

			for (int i = 0; i < table.getValues().size(); i++){
				for (int column : columnList) {
					table.setValue(i, column, ""+ (i+1));
				}
			}

			fileService.addChangeHistory(idFile, table, "Fill auto-incremental");
			return table2grid(table);
		}

		return null;
	}

	public GridRest fillFixedValue(String newValue, List<Integer> columnList, int idFile) {
		TableRest table = this.getFileAsTable(idFile);

		if(table != null) {

			for (int i = 0; i < table.getValues().size(); i++){
				for (int column : columnList) {
					table.setValue(i, column, newValue);
				}
			}

			fileService.addChangeHistory(idFile, table, "Fill fixed value");
			return table2grid(table);
		}

		return null;
	}

	/**
	 * Search the selected columns for rows with a number between min and max
	 * @param min
	 * @param max
	 * @param columnList
	 * @param idFile
	 * @return
	 */
	public GridRest searchNumber(Integer min, Integer max, List<Integer> columnList, int idFile) {
		TableRest table = this.getFileAsTable(idFile);

		if(table != null) {

			for (int i = table.getValues().size() - 1; i >= 0; i--) {
				for (int column : columnList) {
					Integer value = Integer.parseInt(table.getValues().get(i).get(column));

					if((max != null && value > max) || (min != null && value < min)) {
						table.getValues().remove(i);
					}
				}
			}

			ChangeHistoryDb db = new ChangeHistoryDb();
			db.setIdFile(idFile);

			db.setDescription("Search number " + getDescriptionSearchNumber(min, max));
			db.setFileContent(table2csv(table));
			db.setCreationDate(new Date());
			changeHistoryDao.addChangeHistory(db);
		}
		return table2grid(table);
	}

	/**
	 * Generates the description of the searchNumber function for the "history log"
	 * @param min
	 * @param max
	 * @return
	 */
	private String getDescriptionSearchNumber(Integer min, Integer max) {
		if(min != null && max != null) {
			return "between " + min + " and " + max;
		} else if(min != null) {
			return "> " + min;
		} else {
			return "< " + max;
		}
	}

	public GridRest percentile(int value, boolean delete, List<Integer> columnList, int idFile) {
		TableRest table = this.getFileAsTable(idFile);

		if(table != null) {
			Map<Integer, Double> values = new HashMap<>();

			//Average value for each column
			for (int column : columnList) {
				double avg = 0.0;

				for (int i = table.getValues().size() - 1; i >= 0; i--) {
					Integer val = Integer.parseInt(table.getValues().get(i).get(column));
					avg+=val;
				}

				values.put(column, avg / table.getValues().size());
			}


			for (int column : columnList) {
				double avg = 0.0;

				for (int i = table.getValues().size() - 1; i >= 0; i--) {
					if(values.get(column) <= value) {

					}
				}

				values.put(column, avg / table.getValues().size());
			}



			ChangeHistoryDb db = new ChangeHistoryDb();
			db.setIdFile(idFile);
			db.setDescription("Percentile");
			db.setFileContent(table2csv(table));
			db.setCreationDate(new Date());
			changeHistoryDao.addChangeHistory(db);
		}
		return table2grid(table);
	}


	public List<ChangesRest> getHistory(int idFile) {
		List<ChangesRest> listChangesRest = new ArrayList<>();

		List<ChangeHistoryDb> changesDb = changeHistoryDao.lstChanges(idFile);
		if(changesDb != null) {
			for (ChangeHistoryDb change: changesDb) {
				ChangesRest rs = new ChangesRest();
				rs.setIdFile(change.getIdFile());
				rs.setId(change.getId());
				rs.setDate(Utils.formatDate(change.getCreationDate()));
				rs.setDescription(change.getDescription());
				listChangesRest.add(rs);
			}
		}


		return listChangesRest;
	}
}
