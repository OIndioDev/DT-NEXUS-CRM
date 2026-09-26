package com.dtnexus.crm.service;

import com.dtnexus.crm.model.Contact;
import com.dtnexus.crm.repository.ContactRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContactService {

    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public List<Contact> findAll() {
        return contactRepository.findAll();
    }

    public List<Contact> searchContacts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return contactRepository.findAll();
        }
        return contactRepository.findByNameContainingIgnoreCase(keyword);
    }

    public Optional<Contact> findById(Long id) {
        return contactRepository.findById(id);
    }

    public Contact save(Contact contact) {
        if (contact == null) {
            throw new IllegalArgumentException("O contato não pode ser nulo.");
        }
        return contactRepository.save(contact);
    }

    public void delete(Long id) {
        // Corrigido Null safety: adicionado o @SuppressWarnings ou verificação estrita aceita pelo compilador
        if (id != null) {
            long primitiveId = id; // Faz o unboxing explícito e seguro para tirar o Warning
            if (contactRepository.existsById(primitiveId)) {
                contactRepository.deleteById(primitiveId);
            }
        }
    }
}
