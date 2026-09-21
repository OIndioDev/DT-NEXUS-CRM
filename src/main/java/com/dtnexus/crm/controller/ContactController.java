package com.dtnexus.crm.controller;

import com.dtnexus.crm.model.Contact;
import com.dtnexus.crm.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@Controller
public class ContactController {

    @Autowired
    private ContactService contactService;

    // Lista fixa de colunas do Kanban (6 colunas, iguais às do index.html)
    private final List<ColumnDto> kanbanColumns = Arrays.asList(
        new ColumnDto(1L, "Novo Lead"),
        new ColumnDto(2L, "Em Negociação"),
        new ColumnDto(3L, "Proposta Enviada"),
        new ColumnDto(4L, "Pós-Venda"),
        new ColumnDto(5L, "Fechado"),
        new ColumnDto(6L, "Cancelado")
    );

    // NOTA: os endpoints de anotações (/api/contacts/{id}/notes e /api/notes/{id})
    // agora vivem exclusivamente no NoteController, persistidos no banco.

    // 1. Página Principal (com suporte a filtro de busca)
    @GetMapping("/")
    public String index(@RequestParam(required = false) String keyword, Model model) {
        List<Contact> contacts = (keyword != null && !keyword.trim().isEmpty())
                ? contactService.searchContacts(keyword)
                : contactService.findAll();

        BigDecimal valorFunil = contacts.stream()
                .map(c -> {
                    try {
                        return c.getValue() != null ? BigDecimal.valueOf(c.getValue()) : BigDecimal.ZERO;
                    } catch (Exception e) {
                        return BigDecimal.ZERO;
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalLeads = contacts.size();

        long fechados = contacts.stream()
                .filter(c -> "Fechado".equalsIgnoreCase(c.getStatusColumn()))
                .count();

        double taxaConversao = totalLeads > 0 ? ((double) fechados / totalLeads) * 100 : 0.0;

        model.addAttribute("contacts", contacts);
        model.addAttribute("contact", new Contact());
        model.addAttribute("kanbanColumns", kanbanColumns);
        model.addAttribute("valorFunil", valorFunil);
        model.addAttribute("totalLeads", totalLeads);
        model.addAttribute("taxaConversao", String.format("%.1f", taxaConversao).replace(",", "."));
        model.addAttribute("keyword", keyword);

        return "index";
    }

    // 2. Salvar ou Atualizar Contato
    @PostMapping("/save")
    public String saveContact(@ModelAttribute Contact contact) {
        // Se for uma edição (ID preenchido), recupera o contato atual do banco para não perder o status do Kanban
        if (contact.getId() != null) {
            contactService.findById(contact.getId()).ifPresent(existing -> {
                if (contact.getStatusColumn() == null || contact.getStatusColumn().trim().isEmpty()) {
                    contact.setStatusColumn(existing.getStatusColumn());
                }
            });
        } else {
            // Se for novo contato e não tiver status definido, joga para a primeira coluna padrão
            if (contact.getStatusColumn() == null || contact.getStatusColumn().trim().isEmpty()) {
                contact.setStatusColumn("Novo Lead");
            }
        }

        contactService.save(contact);
        return "redirect:/";
    }

    // 3. Carregar dados para Edição
    @GetMapping("/edit/{id}")
    public String editContact(@PathVariable Long id, Model model) {
        Contact contact = contactService.findById(id).orElse(new Contact());

        List<Contact> contacts = contactService.findAll();

        BigDecimal valorFunil = contacts.stream()
                .map(c -> {
                    try {
                        return c.getValue() != null ? BigDecimal.valueOf(c.getValue()) : BigDecimal.ZERO;
                    } catch (Exception e) {
                        return BigDecimal.ZERO;
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalLeads = contacts.size();
        long fechados = contacts.stream().filter(c -> "Fechado".equalsIgnoreCase(c.getStatusColumn())).count();
        double taxaConversao = totalLeads > 0 ? ((double) fechados / totalLeads) * 100 : 0.0;

        model.addAttribute("contact", contact);
        model.addAttribute("contacts", contacts);
        model.addAttribute("kanbanColumns", kanbanColumns);
        model.addAttribute("valorFunil", valorFunil);
        model.addAttribute("totalLeads", totalLeads);
        model.addAttribute("taxaConversao", String.format("%.1f", taxaConversao).replace(",", "."));

        return "index";
    }

    // 4. Deletar Contato
    @GetMapping("/delete/{id}")
    public String deleteContact(@PathVariable Long id) {
        contactService.delete(id);
        return "redirect:/";
    }

    // 5. Endpoint AJAX para Atualizar Status via Drag and Drop do Kanban
    @PostMapping("/update-status/{id}")
    @ResponseBody
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            Contact contact = contactService.findById(id).orElse(null);
            if (contact != null) {
                contact.setStatusColumn(status);
                contactService.save(contact);
                return ResponseEntity.ok().body("Status atualizado com sucesso!");
            }
            return ResponseEntity.badRequest().body("Contato não encontrado.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar status: " + e.getMessage());
        }
    }

    // 6. Endpoint REST PATCH para atualização via Kanban (drag and drop) e Webhook do n8n
    @PatchMapping("/api/contacts/{id}/status")
    @ResponseBody
    public ResponseEntity<?> updateContactStatusPatch(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            Contact contact = contactService.findById(id).orElse(null);
            if (contact != null) {
                String novoStatus = body.get("statusColumn");
                if (novoStatus != null && !novoStatus.trim().isEmpty()) {
                    contact.setStatusColumn(novoStatus);
                    contactService.save(contact);
                    return ResponseEntity.ok(contact);
                }
                return ResponseEntity.badRequest().body("O campo 'statusColumn' é obrigatório no corpo da requisição.");
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao processar PATCH: " + e.getMessage());
        }
    }

    // 6.1 Endpoint REST GET para retornar todos os contatos em JSON (Usado pelo n8n - Relatório Diário do Pipeline)
    @GetMapping("/api/contacts")
    @ResponseBody
    public List<Contact> getAllContacts() {
        return contactService.findAll();
    }

    // Classe auxiliar interna para representar as colunas do Kanban
    public static class ColumnDto {
        private Long id;
        private String name;

        public ColumnDto(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
    }
}