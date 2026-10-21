package com.example.appnhathuoc.api;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.appnhathuoc.BuildConfig;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GeminiApiClient {

    public static class ChatMessage {
        private final String role;
        private final String text;

        public ChatMessage(String role, String text) {
            this.role = role;
            this.text = text;
        }

        public String getRole() {
            return role;
        }

        public String getText() {
            return text;
        }
    }

    private static final String GEMINI_API_KEY = BuildConfig.GEMINI_API_KEY;
    private static final String GEMINI_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";
    private static final String TAG = "GeminiApiClient";

    private static final ExecutorService executor = Executors.newFixedThreadPool(3);
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface GeminiCallback {
        void onSuccess(String aiResponse);
        void onError(String error);
    }

    public static void generateHealthPlanAdvice(int age, String gender, double bmi, String bmiCategory,
                                                int tdee, double weightKg, String goal, GeminiCallback callback) {
        String healthPrompt = "Khách hàng của nhà thuốc có chỉ số sức khỏe:\n" +
                "- Tuổi: " + age + "\n" +
                "- Giới tính: " + (gender == null || gender.trim().isEmpty() ? "Chưa cung cấp" : gender.trim()) + "\n" +
                "- Chỉ số BMI: " + String.format(java.util.Locale.getDefault(), "%.1f", bmi) + " (" + bmiCategory + ")\n" +
                "- Nhu cầu Calo TDEE: " + tdee + " kcal/ngày\n" +
                "- Cân nặng: " + weightKg + " kg\n" +
                "- Mục tiêu sức khỏe: " + (goal.isEmpty() ? "Sức khỏe toàn diện & Duy trì thể lực" : goal) + "\n\n" +
                "Hãy tạo lộ trình cá nhân hóa dựa trên đầy đủ các chỉ số trên. Nếu thiếu dữ liệu quan trọng, hãy nói rõ giả định hoặc hỏi thêm, không tự suy đoán. " +
                "Đóng vai Dược sĩ tư vấn dinh dưỡng và sức khỏe tại nhà thuốc, trả lời chu đáo, chi tiết, ấm áp gồm:\n" +
                "1. Lời khuyên & Đánh giá thể trạng từ Dược sĩ.\n" +
                "2. Chế độ dinh dưỡng & thực đơn khuyên dùng (Phân bổ calo, protein, tinh bột, lượng nước uống).\n" +
                "3. Lộ trình rèn luyện thể thao & nghỉ ngơi hợp lý.\n" +
                "4. Lưu ý về thực phẩm bổ sung & vi chất một cách thận trọng; không tự đề xuất liều thuốc/thực phẩm bổ sung nếu thiếu thông tin bệnh nền, dị ứng, thai kỳ hoặc thuốc đang dùng.\n\n" +
                "Yêu cầu: Trả lời bằng tiếng Việt, ấm áp, tận tâm, xưng Dược sĩ tư vấn/mình, tuyệt đối không xưng AI, Gemini hay MEDCARE.";

        generateAiResponse(healthPrompt, callback);
    }

    public static void generateAiResponse(String prompt, GeminiCallback callback) {
        List<ChatMessage> conversation = new ArrayList<>();
        conversation.add(new ChatMessage("user", prompt));
        generateAiResponse(conversation, callback);
    }

    public static void generateAiResponse(List<ChatMessage> conversation, GeminiCallback callback) {
        executor.execute(() -> {
            if (GEMINI_API_KEY.trim().isEmpty()) {
                mainHandler.post(() -> callback.onError(
                        "Chưa cấu hình Gemini API key. Hãy thêm GEMINI_API_KEY vào local.properties rồi chạy lại ứng dụng."));
                return;
            }
            if (conversation == null || conversation.isEmpty()) {
                mainHandler.post(() -> callback.onError("Chưa có nội dung để gửi đến Gemini."));
                return;
            }

            HttpURLConnection conn = null;
            try {
                URL url = new URL(GEMINI_ENDPOINT);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("x-goog-api-key", GEMINI_API_KEY.trim());
                conn.setDoOutput(true);
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(30000);

                JSONObject jsonBody = new JSONObject();
                JSONArray contents = new JSONArray();
                for (ChatMessage message : conversation) {
                    if (message == null || message.getText() == null || message.getText().trim().isEmpty()) {
                        continue;
                    }
                    JSONObject contentObj = new JSONObject();
                    JSONArray parts = new JSONArray();
                    JSONObject partObj = new JSONObject();
                    partObj.put("text", message.getText());
                    parts.put(partObj);
                    contentObj.put("role", "model".equals(message.getRole()) ? "model" : "user");
                    contentObj.put("parts", parts);
                    contents.put(contentObj);
                }
                jsonBody.put("contents", contents);

                JSONObject systemInstruction = new JSONObject();
                JSONArray instructionParts = new JSONArray();
                JSONObject instructionPart = new JSONObject();
                instructionPart.put("text",
                        "Bạn là dược sĩ tư vấn trực tuyến của nhà thuốc, giao tiếp bằng tiếng Việt tự nhiên, thân thiện, " +
                        "ấm áp như một cuộc trò chuyện với khách hàng. Hãy ghi nhớ các lượt trao đổi trước để trả lời đúng ngữ cảnh, " +
                        "đặt câu hỏi ngắn để làm rõ khi thiếu thông tin. Không chẩn đoán bệnh và không tự kê đơn; trước khi nói về thuốc " +
                        "hoặc liều dùng cần hỏi tuổi, dị ứng, bệnh nền, thai kỳ/cho con bú và thuốc đang dùng khi phù hợp. " +
                        "Không bịa thông tin; nêu rõ giới hạn tư vấn từ xa. Với dấu hiệu nguy hiểm hoặc triệu chứng nặng, khuyên liên hệ " +
                        "cấp cứu/cơ sở y tế ngay. Xưng mình hoặc Dược sĩ tư vấn, không nhận mình là bác sĩ.");
                instructionParts.put(instructionPart);
                systemInstruction.put("parts", instructionParts);
                jsonBody.put("systemInstruction", systemInstruction);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(jsonBody.toString().getBytes("UTF-8"));
                }

                int responseCode = conn.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    String errorBody = readResponseBody(conn.getErrorStream());
                    String error = describeHttpError(responseCode, errorBody);
                    mainHandler.post(() -> callback.onError(error));
                    return;
                }

                JSONObject respJson = new JSONObject(readResponseBody(conn.getInputStream()));
                JSONArray candidates = respJson.optJSONArray("candidates");
                if (candidates != null && candidates.length() > 0) {
                    JSONObject content = candidates.getJSONObject(0).optJSONObject("content");
                    JSONArray parts = content == null ? null : content.optJSONArray("parts");
                    if (parts != null) {
                        StringBuilder answer = new StringBuilder();
                        for (int i = 0; i < parts.length(); i++) {
                            JSONObject part = parts.optJSONObject(i);
                            if (part != null && !part.optBoolean("thought", false)) {
                                answer.append(part.optString("text", ""));
                            }
                        }
                        String aiText = answer.toString().trim();
                        if (!aiText.isEmpty()) {
                            mainHandler.post(() -> callback.onSuccess(aiText));
                            return;
                        }
                    }
                }

                JSONObject promptFeedback = respJson.optJSONObject("promptFeedback");
                String blockReason = promptFeedback == null ? "" : promptFeedback.optString("blockReason", "");
                if (!blockReason.isEmpty()) {
                    mainHandler.post(() -> callback.onError(
                            "Gemini đã chặn yêu cầu (" + blockReason + "). Vui lòng điều chỉnh nội dung rồi thử lại."));
                    return;
                }

                mainHandler.post(() -> callback.onError(
                        "Gemini không trả về nội dung. Hãy kiểm tra giới hạn an toàn hoặc thử câu hỏi khác."));
            } catch (Exception e) {
                Log.e(TAG, "Gemini request failed (" + e.getClass().getSimpleName() + ")");
                String error = "Không thể kết nối Gemini (" + e.getClass().getSimpleName() +
                        "). Hãy kiểm tra mạng và thử lại.";
                mainHandler.post(() -> callback.onError(error));
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        });
    }

    private static String readResponseBody(InputStream stream) throws Exception {
        if (stream == null) return "";
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        return response.toString();
    }

    private static String describeHttpError(int responseCode, String responseBody) {
        String message;
        switch (responseCode) {
            case 400:
                message = "Yêu cầu Gemini không hợp lệ (HTTP 400). Kiểm tra model và cấu hình yêu cầu.";
                break;
            case 401:
            case 403:
                message = "Gemini từ chối API key (HTTP " + responseCode +
                        "). Kiểm tra API key Gemini, quyền truy cập Generative Language API và giới hạn ứng dụng của key.";
                break;
            case 404:
                message = "Không tìm thấy model Gemini (HTTP 404). Kiểm tra tên model hoặc quyền truy cập model.";
                break;
            case 429:
                message = "Gemini đã hết quota hoặc vượt giới hạn tốc độ (HTTP 429). Kiểm tra quota/billing và thử lại sau.";
                break;
            default:
                message = "Gemini trả về lỗi HTTP " + responseCode + ".";
                break;
        }

        try {
            JSONObject error = new JSONObject(responseBody).optJSONObject("error");
            if (error != null) {
                String detail = error.optString("message", "").trim();
                if (!detail.isEmpty()) {
                    message += " Chi tiết: " + detail.replace(GEMINI_API_KEY, "[đã ẩn]");
                }
            }
        } catch (Exception ignored) {
            // Error bodies are optional and may not be JSON.
        }
        return message;
    }

}