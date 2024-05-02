package com.soma.doubanen.repositories;

import com.soma.doubanen.domains.enums.MediaType;
import jakarta.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SearchRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID>
    implements SearchRepository<T, ID> {

  private final EntityManager entityManager;

  public SearchRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
    super(domainClass, entityManager);
    this.entityManager = entityManager;
  }

  public SearchRepositoryImpl(
      JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager) {
    super(entityInformation, entityManager);
    this.entityManager = entityManager;
  }

  @Override
  public List<T> searchBy(String text, int page, int limit, String constraint, String... fields) {

    SearchResult<T> result = getSearchResult(text, page, limit, constraint, fields);

    return result.hits();
  }

  private MediaType getMediaType(String value) {
    if (value.equals("Music")) {
      return MediaType.Music;
    } else if (value.equals("Movie")) {
      return MediaType.Movie;
    }
    return MediaType.Book;
  }

  private SearchResult<T> getSearchResult(
      String text, int page, int limit, String constraint, String[] fields) {
    SearchSession searchSession = Search.session(entityManager);
    int offset = limit * (page - 1);
    if (!constraint.equals("All"))
      return searchSession
          .search(getDomainClass())
          .where(
              f ->
                  f.bool()
                      .must(f.match().fields(fields).matching(text).fuzzy(1))
                      .must(f.match().field("type").matching(getMediaType(constraint))))
          .fetch(offset, limit);
    return searchSession
        .search(getDomainClass())
        .where(f -> f.bool().must(f.match().fields(fields).matching(text).fuzzy(1)))
        .fetch(offset, limit);
  }
}
