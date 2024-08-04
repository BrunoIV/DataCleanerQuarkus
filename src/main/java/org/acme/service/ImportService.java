package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import org.acme.model.rest.ColumnHeaderRest;
import org.acme.model.rest.GridRest;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@ApplicationScoped
public class ImportService {
	public GridRest multipartToGrid(MultipartFormDataInput input) {
		try {
			InputPart inputPart = input.getFormDataMap().get("file").get(0);
			String fileName = inputPart.getHeaders().getFirst("Content-Disposition");
			GridRest rs = new GridRest();

			if (fileName != null) {
				String[] contentDisposition = fileName.split(";");
				for (String cd : contentDisposition) {
					if (cd.trim().startsWith("filename")) {

						//Reads the file
						InputStream inputStream = inputPart.getBody(InputStream.class, null);
						Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

						CSVParser parser = new CSVParser(reader, CSVFormat.EXCEL);
						List<CSVRecord> list = parser.getRecords();

						for (int line = 0; line < list.size(); line++) {
							for (int j = 0; j < list.get(line).size(); j++) {
								if(line == 0) {
									rs.addHeader(new ColumnHeaderRest("Column " + j, "column_" + j, true));
								}
								rs.addValue(line, "column_"+j, list.get(line).get(j));
							}
						}

					}
				}

				return rs;
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}
}
