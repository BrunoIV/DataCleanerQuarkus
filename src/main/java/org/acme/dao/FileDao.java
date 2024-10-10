package org.acme.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.acme.db.FileDb;

import java.util.Arrays;
import java.util.Date;
import java.util.List;


@ApplicationScoped
public class FileDao {

	@Inject
	EntityManager entityManager;

	public List<FileDb> getFiles() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<FileDb> query = cb.createQuery(FileDb.class);
		Root<FileDb> root = query.from(FileDb.class);
		query.select(root);
		TypedQuery<FileDb> typedQuery = entityManager.createQuery(query);
		return typedQuery.getResultList();
	}

	@Transactional
	public FileDb getFileById(int id) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<FileDb> query = cb.createQuery(FileDb.class);
		Root<FileDb> root = query.from(FileDb.class);

		Predicate namePredicate = cb.equal(root.get("id"), id);
		query.select(root).where(namePredicate);
		TypedQuery<FileDb> typedQuery = entityManager.createQuery(query);

		List<FileDb> result = typedQuery.getResultList();
		if(!result.isEmpty()) {
			return result.get(0);
		}
		return null;
	}

	@Transactional
	public void deleteFile(FileDb db) {
		if(db != null){
			entityManager.remove(db);
		}
	}

	public void putFile(FileDb db) {
		entityManager.persist(db);
	}


	@Transactional
	public void addFile(String name, String type, String content) {
		FileDb filesDb = new FileDb();
		filesDb.setName(name);
		filesDb.setType(type);
		filesDb.setFileContent(content);
		filesDb.setFolder("/");
		filesDb.setCreationDate(new Date());
		entityManager.persist(filesDb);
	}
}
