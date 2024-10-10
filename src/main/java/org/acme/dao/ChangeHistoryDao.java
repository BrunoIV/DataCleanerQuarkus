package org.acme.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.acme.db.ChangeHistoryDb;
import org.acme.db.FileDb;

import java.util.List;


@ApplicationScoped
public class ChangeHistoryDao {

	@Inject
	EntityManager entityManager;

	@Transactional
	public void addChangeHistory(ChangeHistoryDb db) {
		entityManager.persist(db);
	}


	public ChangeHistoryDb getLastChangeOfFile(int idFile) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<ChangeHistoryDb> query = cb.createQuery(ChangeHistoryDb.class);
		Root<ChangeHistoryDb> root = query.from(ChangeHistoryDb.class);

		Predicate namePredicate = cb.equal(root.get("idFile"), idFile);
		query.select(root).where(namePredicate).orderBy(cb.desc(root.get("id")));
		TypedQuery<ChangeHistoryDb> typedQuery = entityManager.createQuery(query);
		typedQuery.setMaxResults(1);

		List<ChangeHistoryDb> results = typedQuery.getResultList();

		if (!results.isEmpty()) {
			return results.get(0);
		} else {
			return null;
		}
	}

	public String getFileContentById(int id) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<ChangeHistoryDb> query = cb.createQuery(ChangeHistoryDb.class);
		Root<ChangeHistoryDb> root = query.from(ChangeHistoryDb.class);

		Predicate namePredicate = cb.equal(root.get("id"), id);
		query.select(root).where(namePredicate);
		TypedQuery<ChangeHistoryDb> typedQuery = entityManager.createQuery(query);

		ChangeHistoryDb db = typedQuery.getSingleResult();
		if(db != null) {
			return db.getFileContent();
		}

		return "";
	}

	public List<ChangeHistoryDb> lstChanges(int idFile) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<ChangeHistoryDb> query = cb.createQuery(ChangeHistoryDb.class);
		Root<ChangeHistoryDb> root = query.from(ChangeHistoryDb.class);

		Predicate namePredicate = cb.equal(root.get("idFile"), idFile);
		query.select(root).where(namePredicate).orderBy(cb.desc(root.get("id")));

		TypedQuery<ChangeHistoryDb> typedQuery = entityManager.createQuery(query);
		return typedQuery.getResultList();
	}
}
