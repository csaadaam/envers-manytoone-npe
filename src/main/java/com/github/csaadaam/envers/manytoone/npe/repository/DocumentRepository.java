package com.github.csaadaam.envers.manytoone.npe.repository;

import com.github.csaadaam.envers.manytoone.npe.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> , RevisionRepository<Document, Long, Integer> {
}
