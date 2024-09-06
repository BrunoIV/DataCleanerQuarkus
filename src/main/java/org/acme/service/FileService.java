package org.acme.service;

import com.google.gson.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.dao.FileDao;
import org.acme.db.FileDb;
import org.acme.model.rest.ColumnHeaderRest;
import org.acme.model.rest.FileRest;
import org.acme.model.rest.GridRest;
import org.acme.model.rest.TableRest;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@ApplicationScoped
public class FileService {

	@Inject FileDao fileDao;

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


	@Deprecated
	public GridRest csv2grid(String csv) {
		GridRest rs = new GridRest();

		if(csv == null) {
			return rs;
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
					if (line == 0) {
						rs.addHeader(new ColumnHeaderRest(list.get(line).get(j), true, j == 0));
					} else {
						//fix first line for "values" is 0
						String id = list.get(0).get(j);
						rs.addValue(line - 1, id, list.get(line).get(j));
					}
				}
			}
		} catch (IOException e) {
			return null;
		}
		return rs;
	}


	public TableRest multipartJsonToTable(MultipartFormDataInput input) {
		TableRest rs = new TableRest();
		String json = multipartToString(input);
		processJson(JsonParser.parseString(json), rs);
		return rs;
	}

	private void processJson(JsonElement jsonElement, TableRest rs) {
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
	public void putTable(int idFile, TableRest table) {
		String csv = dataService.table2csv(table);
		FileDb db = this.fileDao.getFileById(idFile);

		if(db != null) {
			db.setFileContent(csv);
			fileDao.putFile(db);
		}
	}

	@Transactional
	public List<FileRest> getFiles() {
		List<FileRest> lst = new ArrayList<>();
		List<FileDb> dbs = fileDao.getFiles();
		for (FileDb db: dbs) {
			FileRest rs = new FileRest();
			rs.setId(db.getId());
			rs.setName(db.getName());
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
	public boolean deleteFile(int id) {
		FileDb db = fileDao.getFileById(id);
		if(db != null) {
			fileDao.deleteFile(db);
			return true;
		}
		return false;
	}

	@Transactional
	public boolean createFile(String name) {
		fileDao.addFile(name, "");
		return true;
	}

	@Transactional
	public GridRest importCsv(MultipartFormDataInput input) {
		TableRest rs = multipartCsvToTable(input);
		String name = getMultipartName(input);
		fileDao.addFile(name, dataService.table2csv(rs));
		return dataService.table2grid(rs);
	}

	@Transactional
	public GridRest importJson(MultipartFormDataInput input) {
		TableRest rs = multipartJsonToTable(input);
		String name = getMultipartName(input);
		fileDao.addFile(name, dataService.table2csv(rs));
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
}
