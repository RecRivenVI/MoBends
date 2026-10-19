package io.github.recrivenvi.mobends.core.client.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import org.junit.jupiter.api.Test;

class LocalizationCompletenessTest {
    private static final Set<String> RESTORED_LANGUAGES =
            Set.of("fr_fr", "it_it", "no_no", "pl_pl", "pt_br", "ru_ru", "zh_cn");

    @Test
    void everyBundledTranslationCoversTheEnglishInterface() throws IOException {
        Set<String> englishKeys = load("en_us").keySet();

        for (String language : RESTORED_LANGUAGES) {
            assertEquals(
                    englishKeys,
                    load(language).keySet(),
                    () -> language + " must cover the complete current interface");
        }
    }

    private static JsonObject load(String language) throws IOException {
        String path = "/assets/mobends/lang/" + language + ".json";
        try (InputStream stream = LocalizationCompletenessTest.class.getResourceAsStream(path)) {
            assertNotNull(stream, () -> "Missing language resource " + path);
            return JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
                    .getAsJsonObject();
        }
    }
}
