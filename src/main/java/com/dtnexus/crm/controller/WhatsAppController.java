package com.dtnexus.crm.controller;

import com.dtnexus.crm.model.Contact;
import com.dtnexus.crm.repository.ContactRepository;
import com.dtnexus.crm.service.WhatsAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/whatsapp")
public class WhatsAppController {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private WhatsAppService whatsAppService;

    // Endpoint para buscar o QR Code da sessão do WhatsApp
    @GetMapping("/qrcode")
    public ResponseEntity<String> getQRCode() {
        return whatsAppService.obterQRCode();
    }

    @GetMapping("/conversas")
    public ResponseEntity<List<Contact>> listarConversas() {
        return ResponseEntity.ok(contactRepository.findAll());
    }

    @PostMapping("/enviar")
    public ResponseEntity<String> enviarMensagem(@RequestParam String telefone, @RequestParam String mensagem) {
        System.out.println("Disparando via Nexus Zap para " + telefone + ": " + mensagem);
        return ResponseEntity.ok("Mensagem enviada com sucesso!");
    }
}