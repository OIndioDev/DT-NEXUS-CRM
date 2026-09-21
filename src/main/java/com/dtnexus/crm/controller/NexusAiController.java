package com.dtnexus.crm.controller;

import com.dtnexus.crm.service.AiService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class NexusAiController {

    private final AiService aiService;

    // Injeção de dependência via construtor (boa prática no Spring moderno)
    public NexusAiController(AiService aiService) {
        this.aiService = aiService;
    }

    public record PromptRequest(
            @NotBlank(message = "O prompt não pode estar vazio.") String prompt
    ) {}

    public record RespostaResponse(String resposta) {}

    @PostMapping("/chat")
    public ResponseEntity<RespostaResponse> processarMensagem(
            @RequestBody @Valid PromptRequest request) {

        String resposta = aiService.gerarResposta(request.prompt());

        return ResponseEntity.ok(new RespostaResponse(resposta));
    }

    @GetMapping("/status")
    public ResponseEntity<String> statusAi() {
        return ResponseEntity.ok("IA Operacional");
    }
}