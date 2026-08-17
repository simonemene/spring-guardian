package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.JavaSourceFile;
import com.example.guardian.core.model.ProjectScanContext;
import com.example.guardian.core.model.Severity;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Detects remote HTTP calls while a database transaction is open.
 *
 * @author Simone Meneghetti
 */
public class RemoteCallInsideTransactionRule implements SpringRule {

    private static final Set<String> HTTP_TYPES = Set.of(
            "RestTemplate", "RestClient", "WebClient", "HttpClient", "CloseableHttpClient", "OkHttpClient"
    );

    @Override
    public String id() {
        return "TX101_REMOTE_CALL_INSIDE_TRANSACTION";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        Set<String> feignTypes = new HashSet<>();
        for (JavaSourceFile file : context.javaFiles()) {
            file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class).stream()
                    .filter(clazz -> AstUtils.hasAnnotation(clazz, "FeignClient"))
                    .forEach(clazz -> feignTypes.add(clazz.getNameAsString()));
        }

        List<Finding> findings = new ArrayList<>();
        for (JavaSourceFile file : context.javaFiles()) {
            if (AstUtils.isTestSource(file.relativePath())) {
                continue;
            }
            for (ClassOrInterfaceDeclaration clazz : file.compilationUnit().findAll(ClassOrInterfaceDeclaration.class)) {
                boolean classTransactional = AstUtils.hasAnnotation(clazz, "Transactional");
                Set<String> remoteFields = remoteFields(clazz, feignTypes);
                if (remoteFields.isEmpty()) {
                    continue;
                }

                for (MethodDeclaration method : clazz.getMethods()) {
                    if (!classTransactional && !AstUtils.hasAnnotation(method, "Transactional")) {
                        continue;
                    }
                    for (MethodCallExpr call : method.findAll(MethodCallExpr.class)) {
                        if (!usesRemoteField(call, remoteFields)) {
                            continue;
                        }
                        findings.add(new Finding(
                                id(),
                                Severity.MAJOR,
                                "Remote call inside transactional boundary",
                                file.relativePath(),
                                AstUtils.line(call).orElse(null),
                                "Transactional method " + method.getNameAsString() + "() performs remote call: " + compact(call.toString()),
                                "Network latency and timeouts can keep database connections, locks and transaction resources open much longer than intended. A remote failure can also couple database rollback to an external side effect.",
                                "Shorten the database transaction boundary. Persist/prepare state transactionally, perform the remote call outside the transaction, and use an outbox/compensation strategy when atomic cross-system behavior is required."
                        ));
                    }
                }
            }
        }
        return findings;
    }

    private Set<String> remoteFields(ClassOrInterfaceDeclaration clazz, Set<String> feignTypes) {
        Set<String> fields = new HashSet<>();
        for (FieldDeclaration field : clazz.getFields()) {
            field.getVariables().forEach(variable -> {
                String type = simpleType(variable.getType().asString());
                if (HTTP_TYPES.contains(type) || feignTypes.contains(type)) {
                    fields.add(variable.getNameAsString());
                }
            });
        }
        return fields;
    }

    private boolean usesRemoteField(MethodCallExpr call, Set<String> remoteFields) {
        if (call.getScope().isEmpty()) {
            return false;
        }
        String scope = call.getScope().get().toString().toLowerCase(Locale.ROOT);
        for (String field : remoteFields) {
            String normalized = field.toLowerCase(Locale.ROOT);
            if (scope.equals(normalized) || scope.startsWith(normalized + ".") || scope.startsWith(normalized + "(")) {
                return true;
            }
        }
        return false;
    }

    private String simpleType(String type) {
        String raw = type == null ? "" : type;
        int generic = raw.indexOf('<');
        if (generic >= 0) {
            raw = raw.substring(0, generic);
        }
        int dot = raw.lastIndexOf('.');
        return dot >= 0 ? raw.substring(dot + 1) : raw;
    }

    private String compact(String text) {
        String compact = text == null ? "" : text.replaceAll("\\s+", " ").trim();
        return compact.length() <= 180 ? compact : compact.substring(0, 177) + "...";
    }
}
