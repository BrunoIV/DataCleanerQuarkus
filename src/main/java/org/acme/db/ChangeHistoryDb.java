package org.acme.db;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "ChangeHistory")
public class ChangeHistoryDb {

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	@Id
	private Integer id;

	@Column(name = "id_file")
	private Integer idFile;

	@Column(name = "description")
	private String description;

	@Column(name = "file_content", length = 2147483647)
	private String fileContent;

	@Column(name = "creationDate")
	private Date creationDate;
}
