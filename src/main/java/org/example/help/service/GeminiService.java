package org.example.help.service;

import com.google.common.collect.ImmutableList;
import com.google.genai.Client;
import com.google.genai.types.*;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GeminiService {
    Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    String GEMINI_KEY = dotenv.get("GEMINI_KEY");
    Logger logger = Logger.getLogger(GeminiService.class.getName());
    Client client = Client.builder().apiKey(GEMINI_KEY).build();

    public String answerQuestion(List<String> champions, float avgKda, int avgCs) {
        try {
            // Sets the safety settings in the config.
            ImmutableList<SafetySetting> safetySettings =
                    ImmutableList.of(
                            SafetySetting.builder()
                                    .category("HARM_CATEGORY_HATE_SPEECH")
                                    .threshold("BLOCK_ONLY_HIGH")
                                    .build(),
                            SafetySetting.builder()
                                    .category("HARM_CATEGORY_DANGEROUS_CONTENT")
                                    .threshold("BLOCK_LOW_AND_ABOVE")
                                    .build());

            // Sets the system instruction in the config.
            final Content systemInstruction =
                    Content.builder()
                            .parts(ImmutableList.of(Part.builder().text("""
                                    You are an expert League of Legends strategist and analyst. \s
                                    Your goal is to recommend champions that best fit the player's playstyle, preferences, and recent performance data. \s
                                    
                                    Consider the player's frequently played champions, KDA, CS, role, and playstyle when making recommendations. \s
                                    Provide well-reasoned explanations for each recommendation, highlighting similarities in mechanics, synergy with their role, or how it complements their strengths and playstyle. \s
                                    
                                    Ensure your response is clear, concise, and tailored to the player's skill level and preferred playstyle. \s
                                    If the player has a specific tendency (e.g., aggressive laning, objective control, roaming), take that into account when suggesting champions. \s
                                    Response Format: The AI should reply exclusively in Korean using plain text formatting. 마크다운을 사용하지 않고 일반 텍스트로만 응답해야 합니다.
                                    """).build())).build();


            // Sets the Google Search tool in the config.
            Tool googleSearchTool = Tool.builder().googleSearch(GoogleSearch.builder().build()).build();

            GenerateContentConfig config =
                    GenerateContentConfig.builder()
                            .candidateCount(1)
                            .maxOutputTokens(2048)
                            .safetySettings(safetySettings)
                            .systemInstruction(systemInstruction)
                            .build();

            String model = "gemini-2.0-flash-001";

            String prompt = """
                    I am a League of Legends player. Recently, I have frequently played the following three champions:  \\n"
                    "1. %s  \\n"
                    "2. %s  \\n"
                    "3. %s  \\n"
                    "\\n"
                    "On average, my KDA is %.2f, and my CS per game is around %d.  \\n"
                    "Based on this data, please recommend a new champion that suits my playstyle.  \\n"
                    "Also, explain why this champion would be a good fit for me.
                    """.formatted(champions.get(0),champions.get(1),champions.get(2),avgKda,avgCs);
            GenerateContentResponse response = client.models.generateContent(model, prompt, config);




            logger.log(Level.INFO, response.text());

            return response.text();
        }  catch (Exception e) {
            logger.log(Level.SEVERE, "Gemini API 오류: " + e.getMessage());
            return "메뉴 추천 중 오류가 발생했습니다. 다시 시도해주세요.";

        }

    }
}
