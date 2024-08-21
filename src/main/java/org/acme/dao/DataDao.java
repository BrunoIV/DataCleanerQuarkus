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

import java.util.Date;


@ApplicationScoped
public class DataDao {

	@Inject
	EntityManager entityManager;

	@Transactional
	public void addFile(String name) {
		FileDb filesDb = new FileDb();
		filesDb.setName(name);
		filesDb.setFolder("/");
		filesDb.setCreationDate(new Date());
		entityManager.persist(filesDb);
	}

	public FileDb findFileById(int id) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<FileDb> query = cb.createQuery(FileDb.class);
		Root<FileDb> root = query.from(FileDb.class);

		Predicate namePredicate = cb.equal(root.get("id"), id);
		query.select(root).where(namePredicate);
		TypedQuery<FileDb> typedQuery = entityManager.createQuery(query);
		return typedQuery.getSingleResult();
	}
}
