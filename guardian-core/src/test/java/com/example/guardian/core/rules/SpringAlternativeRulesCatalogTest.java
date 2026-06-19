package com.example.guardian.core.rules;

import com.example.guardian.core.config.GuardianSettings;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpringAlternativeRulesCatalogTest {

    @Test
    void defaultRulesUseUnifiedAdvisorCatalogAndUniqueIds() {
        var rules = GuardianRules.defaultRules(GuardianSettings.defaults());
        var ids = rules.stream().map(SpringRule::id).toList();
        var uniqueIds = ids.stream().collect(Collectors.toSet());

        assertEquals(uniqueIds.size(), ids.size(), "Default rules must not contain duplicate rule ids");

        assertTrue(uniqueIds.contains("ADV001_MANUAL_OBJECT_MAPPER"));
        assertTrue(uniqueIds.contains("ADV003_MANUAL_REST_TEMPLATE"));
        assertTrue(uniqueIds.contains("SPR067_TIMER_SCHEDULING"));
        assertTrue(uniqueIds.contains("SPR072_SPRING_BEAN_CREATED_WITH_NEW"));

        assertFalse(uniqueIds.contains("SPR064_MANUAL_OBJECT_MAPPER"), "ADV001 is the canonical ObjectMapper advisor id");
        assertFalse(uniqueIds.contains("SPR076_MANUAL_REST_TEMPLATE"), "ADV003 is the canonical RestTemplate advisor id");
    }

    @Test
    void defaultRulesExposeTheTwentyEnterpriseSpringAlternatives() {
        var ids = GuardianRules.defaultRules(GuardianSettings.defaults()).stream()
                .map(SpringRule::id)
                .collect(Collectors.toSet());

        Set<String> expected = IntStream.rangeClosed(1, 20)
                .mapToObj(index -> String.format("SPR_ALT%03d", index))
                .collect(Collectors.toSet());

        for (String prefix : expected) {
            assertTrue(ids.stream().anyMatch(id -> id.startsWith(prefix)), "Missing Spring Alternative " + prefix);
        }
    }
}
