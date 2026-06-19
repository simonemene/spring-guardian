package com.example.guardian.core;

import com.example.guardian.core.model.AffectedComponent;
import com.example.guardian.core.model.FindingGroup;
import com.example.guardian.core.model.Severity;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Regression test for report model constructors used by older consumers.
 *
 * @author p15518 - Simone Meneghetti
 */
class ReportModelCompatibilityTest {

    @Test
    void keepsBackwardCompatibleAffectedComponentConstructor() {
        AffectedComponent component = new AffectedComponent(
                "CLASS",
                "CustomerService",
                "src/main/java/com/example/CustomerService.java",
                42,
                "technical evidence"
        );

        assertEquals("CLASS", component.type());
        assertEquals(42, component.line());
        assertEquals("", component.codeSnippet());
    }

    @Test
    void keepsBackwardCompatibleFindingGroupConstructorWithoutGuidance() {
        FindingGroup group = new FindingGroup(
                "SPR_ALT_TEST",
                Severity.MAJOR,
                "Architecture",
                "code",
                "Code",
                "Example finding",
                1,
                List.of(),
                "Why it matters",
                "Suggested fix",
                "Explanation"
        );

        assertEquals("SPR_ALT_TEST", group.ruleId());
        assertEquals(0, group.affectedComponents().size());
        assertNull(group.guidance());
    }
}
