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

    // Status operacionais do patio e da oficina.
    private final List<ColumnDto> kanbanColumns = Arrays.asList(
        new ColumnDto(1L, "Recepção / Diagnóstico"),
        new ColumnDto(2L, "Aguardando Peças"),
        new ColumnDto(3L, "Na Oficina / Elevador"),
        new ColumnDto(4L, "Teste de Rodagem"),
        new ColumnDto(5L, "Pronto / Lavagem"),
        new ColumnDto(6L, "Entregue / Pago")
    );

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
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b != null ? b : BigDecimal.ZERO)); // Corrigido Null Safety

        model.addAttribute("contacts", contacts);
        model.addAttribute("contact", new Contact());
        model.addAttribute("kanbanColumns", kanbanColumns);
        addOperationalMetrics(model, contacts, valorFunil);
        model.addAttribute("keyword", keyword);

        return "index";
    }

    // 2. Salvar ou Atualizar Contato
    @PostMapping("/save")
    public String saveContact(@ModelAttribute Contact contact) {
        if (contact.getId() != null) {
            contactService.findById(contact.getId()).ifPresent(existing -> {
                if (contact.getStatusColumn() == null || contact.getStatusColumn().trim().isEmpty()) {
                    contact.setStatusColumn(existing.getStatusColumn());
                }
            });
        } else {
            if (contact.getStatusColumn() == null || contact.getStatusColumn().trim().isEmpty()) {
                contact.setStatusColumn("Recepção / Diagnóstico");
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
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b != null ? b : BigDecimal.ZERO)); // Corrigido Null Safety

        model.addAttribute("contact", contact);
        model.addAttribute("contacts", contacts);
        model.addAttribute("kanbanColumns", kanbanColumns);
        addOperationalMetrics(model, contacts, valorFunil);

        return "index";
    }

    private void addOperationalMetrics(Model model, List<Contact> contacts, BigDecimal totalValue) {
        BigDecimal serviceRevenue = contacts.stream()
            .map(contact -> contact.getServiceValue() == null ? BigDecimal.ZERO : BigDecimal.valueOf(contact.getServiceValue()))
            .reduce(BigDecimal.ZERO, (a, b) -> a.add(b != null ? b : BigDecimal.ZERO)); // Corrigido Null Safety
            
        BigDecimal partsRevenue = contacts.stream()
            .map(contact -> contact.getPartsValue() == null ? BigDecimal.ZERO : BigDecimal.valueOf(contact.getPartsValue()))
            .reduce(BigDecimal.ZERO, (a, b) -> a.add(b != null ? b : BigDecimal.ZERO)); // Corrigido Null Safety
            
        int activeVehicles = contacts.size();
        BigDecimal averageTicket = activeVehicles == 0
            ? BigDecimal.ZERO
            : totalValue.divide(BigDecimal.valueOf(activeVehicles), 2, java.math.RoundingMode.HALF_UP);

        model.addAttribute("activeVehicles", activeVehicles);
        model.addAttribute("serviceRevenue", serviceRevenue);
        model.addAttribute("partsRevenue", partsRevenue);
        model.addAttribute("averageTicket", averageTicket);
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
            @RequestBody Map<String, Object> body) {
        try {
            Contact contact = contactService.findById(id).orElse(null);
            if (contact != null) {
                String novoStatus = textValue(body.get("statusColumn"));
                if (novoStatus != null && !novoStatus.trim().isEmpty()) {
                    contact.setStatusColumn(novoStatus);
                }
                if (body.containsKey("name")) contact.setName(textValue(body.get("name")));
                if (body.containsKey("vehicleModel")) contact.setVehicleModel(textValue(body.get("vehicleModel")));
                if (body.containsKey("vehiclePlate")) contact.setVehiclePlate(textValue(body.get("vehiclePlate")));
                if (body.containsKey("vehicleChassis")) contact.setVehicleChassis(textValue(body.get("vehicleChassis")));
                if (body.containsKey("vehicleKm")) contact.setVehicleKm(integerValue(body.get("vehicleKm")));
                if (body.containsKey("vehicleInspectionExternal")) contact.setVehicleInspectionExternal(textValue(body.get("vehicleInspectionExternal")));
                if (body.containsKey("vehicleInspectionInternal")) contact.setVehicleInspectionInternal(textValue(body.get("vehicleInspectionInternal")));
                if (body.containsKey("vehicleFluidStatus")) contact.setVehicleFluidStatus(textValue(body.get("vehicleFluidStatus")));
                if (body.containsKey("vehicleFuelLevel")) contact.setVehicleFuelLevel(textValue(body.get("vehicleFuelLevel")));
                if (body.containsKey("vehicleSignatureAccepted")) contact.setVehicleSignatureAccepted(booleanValue(body.get("vehicleSignatureAccepted")));
                if (body.containsKey("serviceInterest")) contact.setServiceInterest(textValue(body.get("serviceInterest")));
                if (body.containsKey("serviceValue")) contact.setServiceValue(doubleValue(body.get("serviceValue")));
                
                contactService.save(contact);
                return ResponseEntity.ok().body(contact);
            }
            return ResponseEntity.badRequest().body("Contato não encontrado.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar dados: " + e.getMessage());
        }
    }

    // Métodos utilitários de conversão segura para tratar os dados vindos do n8n/JSON
    private String textValue(Object obj) {
        return obj == null ? null : obj.toString();
    }

    private Integer integerValue(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Number) return ((Number) obj).intValue();
        try {
            return Integer.parseInt(obj.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private Double doubleValue(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Number) return ((Number) obj).doubleValue();
        try {
            return Double.parseDouble(obj.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private Boolean booleanValue(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Boolean) return (Boolean) obj;
        return Boolean.parseBoolean(obj.toString());
    }

    // DTO interno para mapeamento das colunas do Kanban
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
