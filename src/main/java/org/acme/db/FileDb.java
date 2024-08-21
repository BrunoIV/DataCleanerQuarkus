package org.acme.db;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "Files")
public class FileDb {

	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	@Id
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "folder")
	private String folder;

	@Column(name = "creationDate")
	private Date creationDate;
}
