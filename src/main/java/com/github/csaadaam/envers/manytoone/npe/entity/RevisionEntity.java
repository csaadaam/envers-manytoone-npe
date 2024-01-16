package com.github.csaadaam.envers.manytoone.npe.entity;

import com.github.csaadaam.envers.manytoone.npe.listener.RevisionListener;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@org.hibernate.envers.RevisionEntity(RevisionListener.class)
public class RevisionEntity {
    @Id
    @GeneratedValue
    @RevisionNumber
    private int id;
    @RevisionTimestamp
    private long timestamp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Transient
    public LocalDateTime getRevisionDate() {
        return new Timestamp(timestamp).toLocalDateTime();
    }
}
