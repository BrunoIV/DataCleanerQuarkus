package org.acme.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.model.rest.ColumnHeaderRest;
import org.acme.model.rest.GridRest;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ImportService {

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

	public GridRest multipartCsvToGrid(MultipartFormDataInput input) {
		String csv = multipartToString(input);
		InputStream inputStream = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
		Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

		CSVParser parser = null;
		GridRest rs = new GridRest();
		try {

			CSVFormat csvFormat = CSVFormat.Builder.create()
					.setIgnoreEmptyLines(true)
					.setQuoteMode(QuoteMode.NONE)
					.build();

			parser = new CSVParser(reader, csvFormat);
			List<CSVRecord> list = parser.getRecords();
			for (int line = 0; line < list.size(); line++) {
				for (int j = 0; j < list.get(line).size(); j++) {
					if (line == 0) {
						rs.addHeader(new ColumnHeaderRest("Column " + j, "column_" + j, true, j == 0));
					}
					rs.addValue(line, "column_" + j, list.get(line).get(j));
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
}
