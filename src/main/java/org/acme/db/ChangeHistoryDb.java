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
	private Long id;

	@Column(name = "id_file")
	private Long ifFile;

	@Column(name = "file_content")
	private String fileContent;

	@Column(name = "creationDate")
	private Date creationDate;
}
