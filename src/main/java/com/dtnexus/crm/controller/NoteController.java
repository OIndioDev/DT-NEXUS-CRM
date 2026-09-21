package com.dtnexus.crm.controller;

import com.dtnexus.crm.model.Note;
import com.dtnexus.crm.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class NoteController {

    @Autowired
    private NoteRepository noteRepository;

    // GET /api/contacts/{contactId}/notes -> lista todas as anotações do lead
    @GetMapping("/contacts/{contactId}/notes")
    public List<Note> listar(@PathVariable Long contactId) {
        return noteRepository.findByContactIdOrderByUpdatedAtDesc(contactId);
    }

    // POST /api/contacts/{contactId}/notes -> cria uma nova anotação (não sobrescreve as existentes)
    @PostMapping("/contacts/{contactId}/notes")
    public ResponseEntity<Note> criar(@PathVariable Long contactId, @RequestBody Map<String, String> body) {
        String conteudo = body.get("content");
        if (conteudo == null || conteudo.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Note nota = new Note(contactId, conteudo);
        return ResponseEntity.ok(noteRepository.save(nota));
    }

    // PUT /api/notes/{noteId} -> edita o texto de uma anotação existente
    @PutMapping("/notes/{noteId}")
    public ResponseEntity<Note> editar(@PathVariable Long noteId, @RequestBody Map<String, String> body) {
        return noteRepository.findById(noteId)
                .map(nota -> {
                    nota.setContent(body.get("content"));
                    nota.setUpdatedAt(LocalDateTime.now());
                    return ResponseEntity.ok(noteRepository.save(nota));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/notes/{noteId} -> exclui uma anotação
    @DeleteMapping("/notes/{noteId}")
    public ResponseEntity<Void> excluir(@PathVariable Long noteId) {
        if (!noteRepository.existsById(noteId)) {
            return ResponseEntity.notFound().build();
        }
        noteRepository.deleteById(noteId);
        return ResponseEntity.noContent().build();
    }
}