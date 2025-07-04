package org.acme.service;

import com.google.gson.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.dao.ChangeHistoryDao;
import org.acme.dao.FileDao;
import org.acme.db.ChangeHistoryDb;
import org.acme.db.FileDb;
import org.acme.model.rest.*;
import org.acme.util.*;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import java.io.*;
import java.util.*;

@ApplicationScoped
public class FileService {

	@Inject FileDao fileDao;

	@Inject
	ChangeHistoryDao changeHistoryDao;

	@Inject DataService dataService;

	public String multipartToString(MultipartFormDataInput input){
		StringBuilder stringBuilder = new StringBuilder();
		input.getFormDataMap().forEach((key, value) -> {
			try {
				stringBuilder.append(value.get(0).getBodyAsString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		return stringBuilder.toString();
	}

	public String getMultipartName(MultipartFormDataInput input){

		StringBuilder sb = new StringBuilder();
		input.getFormDataMap().forEach((key, value) -> {
			sb.append(value.get(0).getFileName());
		});
		return sb.toString();
	}


	public TableRest multipartCsvToTable(MultipartFormDataInput input) {
		String csv = multipartToString(input);
		return dataService.csv2table(csv);
	}

	public TableRest multipartJsonToTable(MultipartFormDataInput input) {
		TableRest rs = new TableRest();
		String json = multipartToString(input);
		processJson(JsonParser.parseString(json), rs);
		return rs;
	}

	public void processJson(JsonElement jsonElement, TableRest rs) {
		if (jsonElement.isJsonArray()) {
			JsonArray jsonArray = jsonElement.getAsJsonArray();

			for (int i = 0; i < jsonArray.size(); i++) {
				if( i == 0){
					setGridHeadersFromJson((JsonObject) jsonArray.get(i), rs);
				}
				setGridValuesFromJson((JsonObject) jsonArray.get(i), i, rs);
			}
		} else if (jsonElement.isJsonObject()) {
			JsonObject jsonObject = jsonElement.getAsJsonObject();

			setGridValuesFromJson(jsonObject, 0,rs);
			setGridHeadersFromJson(jsonObject, rs);
		}
	}

	private void setGridHeadersFromJson(JsonObject jsonObject, TableRest rs ) {
		for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			rs.addHeader(entry.getKey());
		}
	}

	private void setGridValuesFromJson(JsonObject jsonObject, int line, TableRest rs) {
		for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			JsonElement value = entry.getValue();
			String valueString = null;

			if(!value.isJsonNull()) {
				valueString = value.getAsString();
			}
			rs.addValue(line, valueString);
		}
	}

	public String exportAsJson(int idFile) {
		TableRest table = dataService.getFileAsTable(idFile);

		if(table != null) {
			GridRest grid = dataService.table2grid(table);
			List<LinkedHashMap<String, Object>> data = grid.getValues();
			Gson gson = new Gson();
			return gson.toJson(data);
		}

		return null;
	}

	public String exportAsCsv(int idFile) {
		TableRest table = dataService.getFileAsTable(idFile);

		if(table != null) {
			StringBuilder csv = new StringBuilder();

			//Headers
			csv.append(String.join(",", table.getHeader()));
			csv.append("\n");

			//Values
			for(List<String> row : table.getValues()) {
				csv.append(String.join(",", row));
				csv.append("\n");
			}
			return csv.toString();
		}

		return null;
	}


	@Transactional
	public void addChangeHistory(int idFile, TableRest table, String changeType) {
		String csv = dataService.table2csv(table);

		ChangeHistoryDb changes = new ChangeHistoryDb();
		changes.setIdFile(idFile);
		changes.setDescription(changeType);
		changes.setCreationDate(new Date());
		changes.setFileContent(csv);
		changeHistoryDao.addChangeHistory(changes);
	}

	@Transactional
	public List<FileRest> getFiles() {
		List<FileRest> lst = new ArrayList<>();
		List<FileDb> dbs = fileDao.getFiles();
		for (FileDb db: dbs) {
			FileRest rs = new FileRest();
			rs.setId(db.getId());
			rs.setName(db.getName());
			rs.setType(db.getType());
			rs.setUnsavedChanges(dataService.fileHasUnsavedChanges(db.getId()));
			lst.add(rs);
		}

		return lst;
	}

	@Transactional
	public boolean renameFile(int id, String name) {
		FileDb db = fileDao.getFileById(id);
		if(db != null) {
			db.setName(name);
			fileDao.putFile(db);
			return true;
		}
		return false;
	}

	@Transactional
	public boolean deleteFiles(String ids) {
		boolean allFound = true;
		for (int id: Utils.text2IntArray(ids)) {
			FileDb db = fileDao.getFileById(id);
			changeHistoryDao.deleteHistoryOfFile(id);

			if(db != null) {
				fileDao.deleteFile(db);
			} else {
				allFound = false;
			}
		}
		return allFound;
	}

	@Transactional
	public boolean newFile(String name, String type) {
		Map<String, String> columns = Map.of(
				"list", "Value",
				"map", "Key,Value",
				"table", "Column 1,Column 2,Column 3"
		);
		if(type != null && columns.get(type) != null) {
			fileDao.addFile(name, type, columns.get(type) + "\n");
			return true;
		}

		return false;
	}

	@Transactional
	public GridRest importCsv(MultipartFormDataInput input) {
		TableRest rs = multipartCsvToTable(input);
		String name = getMultipartName(input);
		fileDao.addFile(name, "grid", dataService.table2csv(rs));
		return dataService.table2grid(rs);
	}

	@Transactional
	public GridRest importJson(MultipartFormDataInput input) {
		TableRest rs = multipartJsonToTable(input);
		String name = getMultipartName(input);
		fileDao.addFile(name, "grid", dataService.table2csv(rs));
		return dataService.table2grid(rs);
	}

	public String exportAsHtml(int idFile) {
		TableRest table = dataService.getFileAsTable(idFile);

		if(table != null) {
			StringBuilder sb = new StringBuilder();
			sb.append("<table border=\"1\">");

			sb.append("<tr>");
			for (String header: table.getHeader()) {
				sb.append("<th>");
				sb.append(header);
				sb.append("</th>");
			}
			sb.append("</tr>");




			for (List<String> row : table.getValues()) {
				sb.append("<tr>");

				for (String col : row) {
					sb.append("<td>");
					sb.append(col);
					sb.append("</td>");
				}

				sb.append("</tr>");

			}
			sb.append("</table>");
			return sb.toString();
		}

		return null;
	}

	@Transactional
	public Boolean saveFile(int id) {
		ChangeHistoryDb change = changeHistoryDao.getLastChangeOfFile(id);
		FileDb file = fileDao.getFileById(id);

		if(change != null && file != null) {
			file.setFileContent(change.getFileContent());
			file.setCreationDate(new Date());
			fileDao.putFile(file);
			return true;
		}

		return false;
	}

	@Transactional
	public Boolean saveFileAs(int id, String newName) {
		LastVersionFileRest lastVersion = dataService.getLastVersionFile(id);

		if(lastVersion != null) {
			fileDao.addFile(newName, "table", lastVersion.getFileContent());
			return true;
		}

		return false;
	}

	public Boolean clone(String ids) {
		boolean result = true;
		for (int id: Utils.text2IntArray(ids)) {
			FileDb db = fileDao.getFileById(id);
			if(db != null) {
				fileDao.addFile("Copy of " + db.getName(), db.getType(), db.getFileContent());
			} else {
				result = false;
			}
		}

		return result;
	}

}
