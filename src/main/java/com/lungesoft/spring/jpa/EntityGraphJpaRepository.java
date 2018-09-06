package com.lungesoft.spring.jpa;

import org.hibernate.jpa.QueryHints;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.lang.Nullable;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Map;

@NoRepositoryBean
public class EntityGraphJpaRepository<T, ID> extends SimpleJpaRepository<T, ID> {

    private final EntityManager entityManager;

    public EntityGraphJpaRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    public EntityGraphJpaRepository(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
        this.entityManager = em;
    }

    @Override
    protected <S extends T> TypedQuery<S> getQuery(@Nullable Specification<S> spec, Class<S> domainClass, Sort sort) {
        TypedQuery<S> query = super.getQuery(spec, domainClass, sort);
        Map<String, Object> hints = query.getHints();
        if (!hints.containsKey(QueryHints.HINT_LOADGRAPH)) {
            return query.setHint(QueryHints.HINT_LOADGRAPH, entityManager.getEntityGraph(getDomainClass().getSimpleName()));
        } else {
            return query;
        }
    }

}
