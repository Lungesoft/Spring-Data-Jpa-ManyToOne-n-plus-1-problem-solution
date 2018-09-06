package com.lungesoft.spring.jpa;

import org.hibernate.jpa.graph.internal.AbstractGraphNode;
import org.hibernate.jpa.graph.internal.EntityGraphImpl;
import org.hibernate.jpa.graph.internal.SubgraphImpl;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

@Configuration
public class BaseEntityGraphsConfigurator {

    private final EntityManager entityManager;

    public BaseEntityGraphsConfigurator(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @PostConstruct
    public void start() {
        entityManager.getEntityManagerFactory().getMetamodel().getEntities().forEach(entityType ->
                generateEntityGraphFor(entityType.getJavaType())
        );
    }

    private void generateEntityGraphFor(Class<?> entityClass) {
        EntityGraphImpl<?> graph = (EntityGraphImpl<?>) entityManager.createEntityGraph(entityClass);
        Set<ViewedField> viewedFields = new HashSet<>();
        Queue<AbstractGraphNode> currentSubGraphQueue = new ArrayDeque<>();

        currentSubGraphQueue.offer(graph);
        while (!currentSubGraphQueue.isEmpty()) {
            AbstractGraphNode currentGraphNode = currentSubGraphQueue.poll();
            Class currentGraphNodeType = retrieveJavaType(currentGraphNode);
            for (Field field : currentGraphNodeType.getDeclaredFields()) {
                if (!isNeedEntityGraphFor(field)) {
                    continue;
                }
                Class newSubGraphType = field.getType();
                ViewedField currentViewingField = new ViewedField(currentGraphNodeType, newSubGraphType, field.getName());
                if (!viewedFields.contains(currentViewingField) && newSubGraphType.isAnnotationPresent(Entity.class)) {
                    SubgraphImpl newSubGraph = currentGraphNode.addSubgraph(field.getName());
                    currentSubGraphQueue.offer(newSubGraph);
                    viewedFields.add(currentViewingField);
                }
            }
        }
        entityManager.getEntityManagerFactory().addNamedEntityGraph(entityClass.getSimpleName(), graph);
    }

    private Class<? extends AbstractGraphNode> retrieveJavaType(AbstractGraphNode currentGraphNode) {
        if (currentGraphNode.getClass() == EntityGraphImpl.class) {
            return ((EntityGraphImpl) currentGraphNode).getEntityType().getJavaType();
        } else if (currentGraphNode.getClass() == SubgraphImpl.class) {
            return ((SubgraphImpl) currentGraphNode).getClassType();
        }
        throw new IllegalStateException("unknown AbstractGraphNode implementation");
    }

    private boolean isNeedEntityGraphFor(Field field) {
        ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
        return manyToOne != null && manyToOne.fetch() == FetchType.EAGER;
    }

    private static class ViewedField {

        private final Class entityType;
        private final Class fieldType;
        private final String fieldTitle;

        public ViewedField(Class entityType, Class fieldType, String fieldTitle) {
            this.entityType = entityType;
            this.fieldType = fieldType;
            this.fieldTitle = fieldTitle;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ViewedField that = (ViewedField) o;
            return Objects.equals(entityType, that.entityType) &&
                    Objects.equals(fieldType, that.fieldType) &&
                    Objects.equals(fieldTitle, that.fieldTitle);
        }

        @Override
        public int hashCode() {
            return Objects.hash(entityType, fieldType, fieldTitle);
        }
    }
}
