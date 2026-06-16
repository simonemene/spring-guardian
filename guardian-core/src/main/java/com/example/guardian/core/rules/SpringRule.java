package com.example.guardian.core.rules;

import com.example.guardian.core.model.Finding;
import com.example.guardian.core.model.ProjectScanContext;

import java.util.List;

public interface SpringRule {
    String id();
    List<Finding> evaluate(ProjectScanContext context);
}
