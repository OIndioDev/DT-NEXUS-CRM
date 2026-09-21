package com.dtnexus.crm.service;

import com.dtnexus.crm.model.Contact;
import com.dtnexus.crm.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    public List<Contact> findAll() {
        return contactRepository.findAll();
    }

    public List<Contact> searchContacts(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return contactRepository.findByNameContainingIgnoreCaseOrCompanyContainingIgnoreCase(keyword, keyword);
        }
        return contactRepository.findAll();
    }

    public Optional<Contact> findById(Long id) {
        return contactRepository.findById(id);
    }

    public Contact save(Contact contact) {
        return contactRepository.save(contact);
    }

    public void delete(Long id) {
        contactRepository.deleteById(id);
    }

	protected void findById11(Object object, Long id) {
		// TODO Auto-generated method stub
		
	}

	public void findById1(Object object, Long id) {
		// TODO Auto-generated method stub
		
	}

	public void findById(Object object, Long id) {
		// TODO Auto-generated method stub
		
	}
}