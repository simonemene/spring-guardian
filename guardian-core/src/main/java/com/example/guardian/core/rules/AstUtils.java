package com.example.guardian.core.rules;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;

import java.util.Optional;

/**
 * Internal utility methods for JavaParser-based rule evaluation.
 *
 * @author p15518 - Simone Meneghetti
 */
final class AstUtils {

    private AstUtils() {
    }

    static boolean hasAnnotation(NodeWithAnnotations<?> node, String simpleName) {
        return node.getAnnotations().stream()
                .anyMatch(a -> a.getName().getIdentifier().equals(simpleName)
                        || a.getNameAsString().endsWith("." + simpleName));
    }

    static boolean hasAnyAnnotation(NodeWithAnnotations<?> node, String... simpleNames) {
        for (String simpleName : simpleNames) {
            if (hasAnnotation(node, simpleName)) {
                return true;
            }
        }
        return false;
    }

    static Optional<Integer> line(Node node) {
        return node.getBegin().map(position -> position.line);
    }

    static int methodLines(CallableDeclaration<?> method) {
        if (method.getBegin().isEmpty() || method.getEnd().isEmpty()) {
            return 0;
        }
        return method.getEnd().get().line - method.getBegin().get().line + 1;
    }

    static int nodeLines(Node node) {
        if (node.getBegin().isEmpty() || node.getEnd().isEmpty()) {
            return 0;
        }
        return node.getEnd().get().line - node.getBegin().get().line + 1;
    }

    static boolean isTestSource(String relativePath) {
        String path = relativePath.replace("\\", "/");
        return path.contains("/src/test/")
                || path.startsWith("src/test/")
                || path.contains("/test/");
    }

    static boolean hasRequestMapping(MethodDeclaration method) {
        return hasAnyAnnotation(method,
                "RequestMapping",
                "GetMapping",
                "PostMapping",
                "PutMapping",
                "PatchMapping",
                "DeleteMapping");
    }

    static boolean isSpringComponent(ClassOrInterfaceDeclaration clazz) {
        return hasAnyAnnotation(clazz,
                "Component",
                "Service",
                "Repository",
                "Controller",
                "RestController",
                "Configuration");
    }

}
