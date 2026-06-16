package com.example.guardian.core.rules;

import com.example.guardian.core.model.*;
import com.github.javaparser.ast.body.FieldDeclaration;

import java.util.ArrayList;
import java.util.List;

public class FieldInjectionRule implements SpringRule {

    @Override
    public String id() {
        return "SPR002_FIELD_INJECTION";
    }

    @Override
    public List<Finding> evaluate(ProjectScanContext context) {
        List<Finding> findings = new ArrayList<>();

        for (JavaSourceFile file : context.javaFiles()) {
            if (AstUtils.isTestSource(file.relativePath())) {
                continue;
            }

            for (FieldDeclaration field : file.compilationUnit().findAll(FieldDeclaration.class)) {
                if (AstUtils.hasAnnotation(field, "Autowired")) {
                    findings.add(new Finding(
                            id(),
                            Severity.MAJOR,
                            "Field injection detected",
                            file.relativePath(),
                            AstUtils.line(field).orElse(null),
                            "Field '" + field.getVariables() + "' is annotated with @Autowired.",
                            "Field injection hides mandatory dependencies and makes testing/immutability harder.",
                            "Prefer constructor injection. With Lombok, use final fields plus @RequiredArgsConstructor."
                    ));
                }
            }
        }

        return findings;
    }
}
