package com.github.csaadaam.envers.manytoone.npe.listener;

import com.github.csaadaam.envers.manytoone.npe.util.BeanUtil;
import jakarta.persistence.EntityManager;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.EntityTrackingRevisionListener;
import org.hibernate.envers.RevisionType;

import java.util.List;

public class RevisionListener implements org.hibernate.envers.RevisionListener, EntityTrackingRevisionListener {
    @Override
    public void entityChanged(Class entityClass, String entityName, Object entityId, RevisionType revisionType, Object revisionEntity) {
        AuditReader reader = AuditReaderFactory.get(BeanUtil.getBean(EntityManager.class));
        List<Number> revisions = reader.getRevisions(entityClass, entityId);
        Number revisionNumber = revisions.get(revisions.size() - 1);

        // Here comes the NPE when saving Document entity
        Object obj = reader.find(entityClass, entityId, revisionNumber);
    }

    @Override
    public void newRevision(Object revisionEntity) {

    }
}
