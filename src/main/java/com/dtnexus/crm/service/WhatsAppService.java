package com.dtnexus.crm.service;

import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class WhatsAppService {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppService.class);

    private final String apiUrl;
    private final String apiKey;
    private final String instanceName;

    private final RestTemplate restTemplate = new RestTemplate();

    public WhatsAppService(
            @Value("${whatsapp.api-url:http://localhost:8080}") String apiUrl,
            @Value("${whatsapp.api-key:}") String apiKey,
            @Value("${whatsapp.instance-name:dt-nexus-instance}") String instanceName) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.instanceName = instanceName;
    }

    // Método para criar a instância caso não exista
    public void criarInstancia() {
        try {
            String url = apiUrl + "/instance/create";
            HttpHeaders headers = new HttpHeaders();
            headers.set("apikey", apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("instanceName", instanceName);
            body.put("qrcode", true);
            body.put("integration", "WHATSAPP-BAILEYS");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(url, request, String.class);
        } catch (Exception e) {
            log.debug("WhatsApp instance creation was skipped or failed: {}", e.getMessage());
        }
    }

    // Método para buscar o QR Code atualizado da instância
    public ResponseEntity<String> obterQRCode() {
        try {
            criarInstancia(); // Tarante que a instância está criada
            String url = apiUrl + "/instance/connect/" + instanceName;
            HttpHeaders headers = new HttpHeaders();
            headers.set("apikey", apiKey);

            HttpEntity<String> request = new HttpEntity<>(headers);
                ResponseEntity<String> response = restTemplate.exchange(
                    url, Objects.requireNonNull(HttpMethod.GET), request, String.class);
            return response;
        } catch (Exception e) {
            log.error("WhatsApp API connection failed", e);
            return ResponseEntity.internalServerError().body("{\"error\":\"WhatsApp service unavailable\"}");
        }
    }
}