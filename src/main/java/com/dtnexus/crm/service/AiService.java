package com.dtnexus.crm.service;

import org.springframework.ai.chat.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiService {

    private final ChatClient chatClient;

    public AiService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * Analisa o sentimento de um lead com base nas interações.
     */
    public String analyzeLeadSentiment(String leadName, String interactionsContent) {

        String prompt = String.format(
            "Analise as interações abaixo do lead %s e retorne estritamente uma destas opções de sentimento: "
            + "[Frio, Morno, Quente, Insatisfeito], seguido por um breve resumo de uma linha.\n"
            + "Interações: %s",
            leadName,
            interactionsContent
        );

        return chatClient.call(prompt);
    }

    /**
     * Processa uma mensagem enviada pelo chat da Nexus IA.
     */
    public String gerarResposta(String mensagem) {

        if (mensagem == null || mensagem.trim().isEmpty()) {
            return "Digite uma mensagem para a Nexus IA.";
        }

        String prompt = """
                Você é a Nexus IA, assistente inteligente integrada a um CRM.

                Responda de forma clara, objetiva e útil.
                Quando a pergunta estiver relacionada a vendas, leads, clientes,
                atendimento ou gestão comercial, procure responder considerando
                o contexto de um CRM.

                Mensagem do usuário:
                %s
                """.formatted(mensagem.trim());

        return chatClient.call(prompt);
    }
}
