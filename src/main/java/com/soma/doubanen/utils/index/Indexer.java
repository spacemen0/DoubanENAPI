package com.soma.doubanen.utils.index;

import jakarta.persistence.EntityManager;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component
public class Indexer {

  private static final int THREAD_NUMBER = 4;
  private final EntityManager entityManager;

  public Indexer(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public void indexPersistedData(String indexClassName) throws IndexException {

    try {
      SearchSession searchSession = Search.session(entityManager);

      Class<?> classToIndex = Class.forName(indexClassName);
      MassIndexer indexer =
          searchSession.massIndexer(classToIndex).threadsToLoadObjects(THREAD_NUMBER);

      indexer.startAndWait();
    } catch (ClassNotFoundException e) {
      throw new IndexException("Invalid class " + indexClassName, e);
    } catch (InterruptedException e) {
      throw new IndexException("Index Interrupted", e);
    }
  }
}
