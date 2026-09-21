package com.dtnexus.crm.repository;

import com.dtnexus.crm.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {

    // Anotações de um lead específico, mais recentes primeiro
    List<Note> findByContactIdOrderByUpdatedAtDesc(Long contactId);
}