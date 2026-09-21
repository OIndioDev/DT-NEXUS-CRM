package com.dtnexus.crm.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class WhatsAppService {

    // URL da sua instância da Evolution API (ou serviço rodando local/Docker)
    private final String API_URL = "http://localhost:8080"; 
    private final String API_KEY = "SUA_API_KEY_GLOBAL"; // Chave da API
    private final String INSTANCE_NAME = "dt-nexus-instance";

    private final RestTemplate restTemplate = new RestTemplate();

    // Método para criar a instância caso não exista
    public void criarInstancia() {
        try {
            String url = API_URL + "/instance/create";
            HttpHeaders headers = new HttpHeaders();
            headers.set("apikey", API_KEY);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("instanceName", INSTANCE_NAME);
            body.put("qrcode", true);
            body.put("integration", "WHATSAPP-BAILEYS");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(url, request, String.class);
        } catch (Exception e) {
            System.out.println("Instância já existe ou erro ao criar: " + e.getMessage());
        }
    }

    // Método para buscar o QR Code atualizado da instância
    public ResponseEntity<String> obterQRCode() {
        try {
            criarInstancia(); // Tarante que a instância está criada
            String url = API_URL + "/instance/connect/" + INSTANCE_NAME;
            HttpHeaders headers = new HttpHeaders();
            headers.set("apikey", API_KEY);

            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            return response;
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"Erro ao conectar com a API do WhatsApp: " + e.getMessage() + "\"}");
        }
    }
}