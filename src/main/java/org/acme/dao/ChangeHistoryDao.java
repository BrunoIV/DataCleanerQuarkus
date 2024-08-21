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
import org.acme.db.ChangeHistoryDb;


@ApplicationScoped
public class ChangeHistoryDao {

	@Inject
	EntityManager entityManager;

	@Transactional
	public void addChangeHistory(ChangeHistoryDb db) {
		entityManager.persist(db);
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
}
