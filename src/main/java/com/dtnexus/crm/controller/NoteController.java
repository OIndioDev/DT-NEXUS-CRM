package com.dtnexus.crm.controller;

import com.dtnexus.crm.model.Note;
import com.dtnexus.crm.repository.NoteRepository;
import com.dtnexus.crm.web.NoteRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class NoteController {

    private final NoteRepository noteRepository;

    public NoteController(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    // GET /api/contacts/{contactId}/notes -> lista todas as anotações do lead
    @GetMapping("/contacts/{contactId}/notes")
    public ResponseEntity<List<Note>> listar(@PathVariable Long contactId) {
        if (contactId == null) {
            return ResponseEntity.badRequest().build();
        }
        List<Note> notes = noteRepository.findByContactIdOrderByUpdatedAtDesc(contactId);
        return ResponseEntity.ok(notes);
    }

    // POST /api/contacts/{contactId}/notes -> cria uma nova anotação
    @PostMapping("/contacts/{contactId}/notes")
    public ResponseEntity<Note> criar(@PathVariable Long contactId, @Valid @RequestBody NoteRequest request) {
        if (contactId == null || request == null) {
            return ResponseEntity.badRequest().build();
        }
        Note nota = new Note(contactId, request.content());
        return ResponseEntity.ok(noteRepository.save(nota));
    }

    // PUT /api/notes/{noteId} -> edita o texto de uma anotação existente (Corrigido tipo de parâmetro)
    @PutMapping("/notes/{noteId}")
    public ResponseEntity<Note> editar(@PathVariable("noteId") Long noteId, @Valid @RequestBody NoteRequest request) {
        if (noteId == null) {
            return ResponseEntity.badRequest().build();
        }
        return noteRepository.findById(noteId)
                .map(nota -> {
                    nota.setContent(request.content());
                    nota.setUpdatedAt(LocalDateTime.now());
                    return ResponseEntity.ok(noteRepository.save(nota));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/notes/{noteId} -> exclui uma anotação (Corrigido tipo de parâmetro)
    @DeleteMapping("/notes/{noteId}")
    public ResponseEntity<Void> excluir(@PathVariable("noteId") Long noteId) {
        if (noteId == null) {
            return ResponseEntity.badRequest().build();
        }
        if (!noteRepository.existsById(noteId)) {
            return ResponseEntity.notFound().build();
        }
        noteRepository.deleteById(noteId);
        return ResponseEntity.noContent().build();
    }
}
