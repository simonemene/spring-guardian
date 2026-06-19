package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Detects static mutable in-memory caches that should normally be Spring-managed.
 *
 * @author Simone Meneghetti
 */
public class StaticMutableCacheAdvisorRule implements SpringRule {

    @Override
    public String id() {
        return "ADV016_STATIC_MUTABLE_CACHE_MAP";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();
        for (JavaSourceFile file : context.javaFiles()) {
            if (SourceFilePredicates.isTestSource(file)) {
                continue;
            }
            String[] lines = file.content().split("\\R", -1);
            for (int index = 0; index < lines.length; index++) {
                String line = lines[index];
                String lower = line.strip().toLowerCase(Locale.ROOT);
                if (!isCandidate(lower)) {
                    continue;
                }
                findings.add(new Finding(
                        id(),
                        Severity.INFO,
                        "Static mutable cache should be Spring-managed",
                        file.relativePath(),
                        index + 1,
                        line.strip(),
                        "A static mutable Map or ConcurrentHashMap behaves like an unmanaged cache and bypasses Spring lifecycle, metrics, invalidation and test controls.",
                        "Use Spring Cache with a configured cache provider such as Caffeine, or move idempotency state to a repository or dedicated service."
                ));
            }
        }
        return findings;
    }

    private boolean isCandidate(String lower) {
        if (lower.isBlank() || lower.startsWith("import ") || lower.startsWith("//") || lower.startsWith("*") || lower.startsWith("/*")) {
            return false;
        }
        boolean staticField = lower.contains("static") && (lower.contains(" map<") || lower.contains(" concurrenthashmap<") || lower.contains(" hashmap<"));
        boolean mutableInitialization = lower.contains("new concurrenthashmap") || lower.contains("new hashmap");
        boolean cacheName = lower.contains("cache") || lower.contains("registry") || lower.contains("processed") || lower.contains("idempot");
        return staticField && mutableInitialization && cacheName;
    }
}
