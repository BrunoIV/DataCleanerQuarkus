package org.acme.service;

import com.google.gson.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.acme.dao.FileDao;
import org.acme.db.FileDb;
import org.acme.model.rest.ColumnHeaderRest;
import org.acme.model.rest.FileRest;
import org.acme.model.rest.GridRest;
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

	@Inject GridService gridService;


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

	public GridRest multipartCsvToGrid(MultipartFormDataInput input) {
		String csv = multipartToString(input);
		return csv2grid(csv);
	}

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
						rs.addHeader(new ColumnHeaderRest(list.get(line).get(j), list.get(line).get(j), true, j == 0));
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


	public GridRest multipartJsonToGrid(MultipartFormDataInput input) {
		GridRest rs = new GridRest();
		String json = multipartToString(input);
		processJson(JsonParser.parseString(json), rs);
		return rs;
	}


	private void processJson(JsonElement jsonElement, GridRest rs) {
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

	private void setGridHeadersFromJson(JsonObject jsonObject, GridRest rs ) {
		boolean drag = true;
		for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			rs.addHeader(new ColumnHeaderRest(entry.getKey(), entry.getKey(),true, drag));
			drag=false;
		}
	}

	private void setGridValuesFromJson(JsonObject jsonObject, int line, GridRest rs) {
		for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			String key = entry.getKey();
			JsonElement value = entry.getValue();
			String valueString = null;

			if(!value.isJsonNull()) {
				valueString = value.getAsString();
			}
			rs.addValue(line, key, valueString);
		}
	}

	public String exportAsJson(GridRest grid) {
		List<LinkedHashMap<String, Object>> data = grid.getValues();
		Gson gson = new Gson();
		return gson.toJson(data);
	}

	public String exportAsCsv(GridRest grid) {
		int columns = grid.getHeader().size() - 2;

		StringBuilder csv = new StringBuilder();
		List<ColumnHeaderRest> headers = grid.getHeader();
		for(int i = 1; i < headers.size(); i++) {
			csv.append(headers.get(i).getHeaderName());
			if(i < columns + 1) {
				csv.append(",");
			}
		}
		csv.append("\n");

		List<LinkedHashMap<String, Object>> values = grid.getValues();
		for (Map<String, Object> data: values) {

			int i = 0;
			for (Map.Entry<String, Object> entry : data.entrySet()) {
				csv.append(entry.getValue());

				//If the last character is "," te CSV parser things that is a empty column
				if(i < columns) {
					csv.append(",");
				}
				i++;
			}
			csv.append("\n");
		}
		return csv.toString();
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
	public void saveCurrentFile() {
		FileDb fileDb = this.fileDao.getFileById(this.gridService.getIdFile());
		if(fileDb != null) {
			fileDb.setFileContent(exportAsCsv(this.gridService.getGrid()));
			this.fileDao.putFile(fileDb);
		}
	}

	@Transactional
	public GridRest importCsv(MultipartFormDataInput input) {
		GridRest rs = multipartCsvToGrid(input);
		String name = getMultipartName(input);
		fileDao.addFile(name, exportAsCsv(rs));
		this.gridService.setGrid(rs);
		return rs;
	}

	@Transactional
	public GridRest importJson(MultipartFormDataInput input) {
		GridRest rs = multipartJsonToGrid(input);
		String name = getMultipartName(input);
		fileDao.addFile(name, exportAsCsv(rs));
		this.gridService.setGrid(rs);
		return rs;
	}
}
