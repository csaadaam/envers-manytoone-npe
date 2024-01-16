package com.github.csaadaam.envers.manytoone.npe;

import com.github.csaadaam.envers.manytoone.npe.entity.Document;
import com.github.csaadaam.envers.manytoone.npe.entity.DocumentAuthorEmployee;
import com.github.csaadaam.envers.manytoone.npe.entity.Employee;
import com.github.csaadaam.envers.manytoone.npe.repository.DocumentRepository;
import com.github.csaadaam.envers.manytoone.npe.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;

@SpringBootApplication
public class EnversManyToOneNpeApplication {
    private static final Logger log = LoggerFactory.getLogger(EnversManyToOneNpeApplication.class);


    public static void main(String[] args) {
        SpringApplication.run(EnversManyToOneNpeApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(DocumentRepository documentRepository, EmployeeRepository employeeRepository) {

        return args -> {
            Employee bilbo = new Employee("Bilbo Baggins");
            log.info("Preloading " + employeeRepository.save(bilbo));

            Employee frodo = new Employee("Frodo Baggins");
            log.info("Preloading " + employeeRepository.save(frodo));

            Document document = new Document("Title");
            final ArrayList<DocumentAuthorEmployee> authors = new ArrayList<>();
            document.setAuthors(authors);

            authors.add(new DocumentAuthorEmployee(document, bilbo, 1, "merry"));
            authors.add(new DocumentAuthorEmployee(document, frodo, 2, "nice"));

            final Document saved = documentRepository.save(document); //NPE in org.hibernate.envers.internal.entities.EntityConfiguration.getRelationDescription
            log.info("Preloading " + saved);

        };
    }

}
