export type Severity = 'CRITICAL' | 'MAJOR' | 'MINOR' | 'INFO';
export type ReportLanguage = 'it' | 'en';
export type ProjectType = 'WEB_API' | 'BATCH' | 'LIBRARY' | 'UNKNOWN';
export type ArchitectureStyle = 'AUTO_DETECTED' | 'LAYERED' | 'DOMAIN_DRIVEN_DESIGN' | 'HEXAGONAL' | 'LEGACY_LAYERED';
export type ReleaseTarget = 'PRODUCTION' | 'INTERNAL' | 'LEGACY_BASELINE';

export interface ScanProfile {
  projectType: ProjectType;
  architectureStyle: ArchitectureStyle;
  releaseTarget: ReleaseTarget;
  knownIssuesAccepted: boolean;
}

export interface ArchitectureReviewReport {
  projectName: string;
  projectRootPath?: string;
  scannedAt: string;
  profile: ScanProfile;
  capabilities: ProjectCapabilities;
  summary: ReportSummary;
  releaseReadiness: ReleaseReadiness;
  architectureScore: number;
  riskLevel: string;
  scannedJavaFiles: number;
  scannedPomFiles: number;
  rulesExecuted: number;
  findingsBySeverity: Partial<Record<Severity, number>>;
  findingsByCategory: CategorySummary[];
  findingsByType: CategorySummary[];
  architectureAreas: ArchitectureAreaReport[];
  qualityGates: QualityGate[];
  recommendedActions: RecommendedAction[];
  explanation: ReportExplanation;
  findings: FindingGroup[];
  architectMode?: ArchitectModeReport | null;
}

export interface ProjectCapabilities {
  usesSpringWeb: boolean;
  usesSpringSecurity: boolean;
  usesJpa: boolean;
  usesActuator: boolean;
  usesValidation: boolean;
  usesOpenApi: boolean;
  usesLombok: boolean;
  usesSpringBatch: boolean;
  hasControllerLayer: boolean;
  hasServiceLayer: boolean;
  hasRepositoryLayer: boolean;
  hasDomainLayer: boolean;
  hasApplicationLayer: boolean;
  hasInfrastructureLayer: boolean;
  detectedArchitecturalStyles: string[];
}

export interface ReleaseReadiness {
  status: string;
  label: string;
  explanation: string;
  releasable: boolean;
  blockers: string[];
  warnings: string[];
}

export interface QualityGate {
  code: string;
  name: string;
  status: string;
  explanation: string;
  required: boolean;
  failingFindings: number;
}

export interface ArchitectureAreaReport {
  code: string;
  name: string;
  description: string;
  findings: number;
  criticalFindings: number;
  majorFindings: number;
  readinessStatus: string;
}

export interface ReportSummary {
  totalFindings: number;
  criticalFindings: number;
  majorFindings: number;
  minorFindings: number;
  infoFindings: number;
  riskLevel: string;
  status: string;
  executiveSummary: string;
}

export interface CategorySummary {
  category: string;
  findings: number;
  criticalFindings: number;
  majorFindings: number;
  minorFindings: number;
  infoFindings: number;
  explanation: string;
}

export interface RecommendedAction {
  priority: number;
  severity: Severity;
  ruleId: string;
  title: string;
  location: string;
  reason: string;
  action: string;
}

export interface ReportExplanation {
  scoreMeaning: string;
  severityMeaning: string;
  howToUseThisReport: string;
  nextSteps: string[];
}

export interface FindingGroup {
  ruleId: string;
  severity: Severity;
  category: string;
  findingType: string;
  findingTypeLabel: string;
  title: string;
  occurrences: number;
  affectedComponents: AffectedComponent[];
  whyItMatters: string;
  suggestedFix: string;
  explanation: string;
  guidance: RuleGuidance;
}

export interface RuleGuidance {
  detectedProblem: string;
  riskImpact: string;
  recommendedApproach: string;
  springAlternative: string;
  documentationUrl: string;
  beforeExample: string;
  afterExample: string;
}

export interface AffectedComponent {
  type: string;
  name: string;
  filePath: string;
  line: number | null;
  evidence: string;
  codeSnippet?: string;
}


export interface ArchitectModeReport {
  fingerprint: SpringProjectFingerprint;
  maturityScore: SpringMaturityScore;
  architectureMap: SpringArchitectureMap;
  modernizationPlan: SpringModernizationPlan;
  productionReadiness: ProductionReadinessReport;
  upgradePath: SpringUpgradePath;
  openRewritePlan: OpenRewritePlan;
}

export interface SpringProjectFingerprint {
  buildTool: string;
  javaVersion: string;
  springBootVersion: string;
  multiModule: boolean;
  capabilities: ProjectCapabilities;
  springCapabilities: string[];
  detectedStarters: string[];
  detectedAnnotations: string[];
  detectedArchitecturalStyles: string[];
  summary: string;
}

export interface SpringMaturityScore {
  overallScore: number;
  status: string;
  areas: SpringMaturityAreaScore[];
  strengths: string[];
  weakAreas: string[];
}

export interface SpringMaturityAreaScore {
  code: string;
  name: string;
  score: number;
  status: string;
  drivers: string[];
  recommendations: string[];
}

export interface SpringArchitectureMap {
  modules: SpringModuleSummary[];
  dependencies: SpringModuleDependency[];
  cycles: SpringArchitectureCycle[];
  mermaidDiagram: string;
  globalRisks: string[];
}

export interface SpringModuleSummary {
  name: string;
  basePackage: string;
  controllers: number;
  services: number;
  repositories: number;
  entities: number;
  configurations: number;
  clients: number;
  events: number;
  batchComponents: number;
  risks: string[];
}

export interface SpringModuleDependency {
  fromModule: string;
  toModule: string;
  weight: number;
  examples: string[];
}

export interface SpringArchitectureCycle {
  modules: string[];
  remediation: string;
}

export interface SpringModernizationPlan {
  quickWins: ModernizationChecklistItem[];
  architecturalRefactorings: ModernizationChecklistItem[];
  productionReadinessFixes: ModernizationChecklistItem[];
  upgradePath: ModernizationChecklistItem[];
  checklist: ModernizationChecklistItem[];
  markdown: string;
}

export interface ModernizationChecklistItem {
  id: string;
  title: string;
  completed: boolean;
  severity: Severity;
  files: string[];
  whyItMatters: string;
  springAlternative: string;
  suggestedChange: string;
  effort: 'LOW' | 'MEDIUM' | 'HIGH';
  businessImpact: 'LOW' | 'MEDIUM' | 'HIGH';
  priority: number;
  relatedFindings: string[];
}

export interface ProductionReadinessReport {
  score: number;
  status: string;
  strengths: string[];
  risks: string[];
  requiredActions: ModernizationChecklistItem[];
}

export interface SpringUpgradePath {
  currentJavaVersion: string;
  currentSpringBootVersion: string;
  steps: UpgradeStep[];
}

export interface UpgradeStep {
  order: number;
  title: string;
  description: string;
  risk: string;
  springAlternative: string;
  evidence: string[];
  whyRecommended: string;
  actions: string[];
  effort: string;
  openRewriteRecipes: string[];
}

export interface OpenRewritePlan {
  recipeName: string;
  displayName: string;
  suggestions: OpenRewriteSuggestion[];
  yaml: string;
}

export interface OpenRewriteSuggestion {
  recipe: string;
  reason: string;
  relatedFindings: string[];
}
