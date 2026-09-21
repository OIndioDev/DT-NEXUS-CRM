package com.dtnexus.crm.repository;

import com.dtnexus.crm.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    
    // Busca contatos cujo nome ou empresa contenham o texto digitado (ignorando maiúsculas/minúsculas)
    List<Contact> findByNameContainingIgnoreCaseOrCompanyContainingIgnoreCase(String name, String company);

}