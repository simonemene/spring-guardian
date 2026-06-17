import { ChangeEvent, CSSProperties, DragEvent, useEffect, useMemo, useState } from 'react';
import { healthCheck, scanLocalPath, scanZip } from './api';
import {
  collectCategories,
  filterFindings,
  formatDate,
  formatNumber,
  getSeverityCount,
  scoreLabel,
  severityOrder
} from './reportUtils';
import type { ArchitectureReviewReport, FindingGroup, Severity } from './types';
import './styles.css';

type SeverityFilter = Severity | 'ALL';

type ScanMode = 'upload' | 'local';

function App() {
  const [report, setReport] = useState<ArchitectureReviewReport | null>(null);
  const [isScanning, setIsScanning] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [mode, setMode] = useState<ScanMode>('upload');
  const [file, setFile] = useState<File | null>(null);
  const [localPath, setLocalPath] = useState('/scan');
  const [serverOnline, setServerOnline] = useState<boolean | null>(null);
  const [severityFilter, setSeverityFilter] = useState<SeverityFilter>('ALL');
  const [categoryFilter, setCategoryFilter] = useState('ALL');
  const [query, setQuery] = useState('');
  const [showRawJson, setShowRawJson] = useState(false);

  useEffect(() => {
    healthCheck().then(setServerOnline);
  }, []);

  const categories = useMemo(() => collectCategories(report), [report]);

  const visibleFindings = useMemo(() => {
    if (!report) {
      return [];
    }

    return filterFindings(report.findings, severityFilter, categoryFilter, query);
  }, [categoryFilter, query, report, severityFilter]);

  async function runScan() {
    setError(null);
    setIsScanning(true);

    try {
      const nextReport = mode === 'upload'
        ? await scanZip(requireSelectedFile(file))
        : await scanLocalPath(localPath.trim());

      setReport(nextReport);
      setShowRawJson(false);
      setServerOnline(true);
    } catch (scanError) {
      setError(scanError instanceof Error ? scanError.message : 'Unexpected scan error');
    } finally {
      setIsScanning(false);
    }
  }

  function requireSelectedFile(selectedFile: File | null): File {
    if (!selectedFile) {
      throw new Error('Select a .zip file before starting the scan.');
    }

    if (!selectedFile.name.toLowerCase().endsWith('.zip')) {
      throw new Error('Only .zip files are supported.');
    }

    return selectedFile;
  }

  function onFileChange(event: ChangeEvent<HTMLInputElement>) {
    setFile(event.target.files?.[0] ?? null);
  }

  function onDrop(event: DragEvent<HTMLLabelElement>) {
    event.preventDefault();
    const droppedFile = event.dataTransfer.files?.[0];
    if (droppedFile) {
      setFile(droppedFile);
      setMode('upload');
    }
  }

  function exportJson() {
    if (!report) {
      return;
    }

    const blob = new Blob([JSON.stringify(report, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `${report.projectName || 'spring-guardian'}-report.json`;
    link.click();
    URL.revokeObjectURL(url);
  }

  return (
    <main className="page-shell">
      <section className="hero panel">
        <div>
          <p className="eyebrow">Spring Boot architecture review</p>
          <h1>Spring Guardian Dashboard</h1>
          <p className="hero-copy">
            Upload a Spring project ZIP or scan a server-mounted path. The dashboard groups findings by rule,
            explains the risk and highlights the remediation actions that matter first.
          </p>
        </div>

        <div className="status-card">
          <span className={`status-dot ${serverOnline ? 'online' : 'offline'}`} />
          <div>
            <strong>{serverOnline === null ? 'Checking API' : serverOnline ? 'API online' : 'API offline'}</strong>
            <span>/api/v1/health</span>
          </div>
        </div>
      </section>

      <section className="scanner-grid">
        <div className="panel scanner-panel">
          <div className="section-heading">
            <div>
              <p className="eyebrow">Scan input</p>
              <h2>Choose project source</h2>
            </div>
          </div>

          <div className="mode-switch" role="tablist" aria-label="Scan mode">
            <button className={mode === 'upload' ? 'active' : ''} onClick={() => setMode('upload')} type="button">
              ZIP upload
            </button>
            <button className={mode === 'local' ? 'active' : ''} onClick={() => setMode('local')} type="button">
              Server path
            </button>
          </div>

          {mode === 'upload' ? (
            <label className="drop-zone" onDragOver={(event) => event.preventDefault()} onDrop={onDrop}>
              <input type="file" accept=".zip,application/zip" onChange={onFileChange} />
              <span className="drop-title">Drop your Spring project ZIP here</span>
              <span className="drop-subtitle">or click to select a file</span>
              {file && <strong className="selected-file">{file.name}</strong>}
            </label>
          ) : (
            <div className="field-group">
              <label htmlFor="localPath">Path visible from the server container</label>
              <input
                id="localPath"
                value={localPath}
                onChange={(event) => setLocalPath(event.target.value)}
                placeholder="/scan/my-project"
              />
              <small>Docker Compose mounts <code>./sample-projects</code> as <code>/scan</code> read-only.</small>
            </div>
          )}

          {error && <div className="error-box">{error}</div>}

          <button className="primary-action" disabled={isScanning} onClick={runScan} type="button">
            {isScanning ? 'Scanning project...' : 'Run architecture scan'}
          </button>
        </div>

        <div className="panel guide-panel">
          <p className="eyebrow">How to read the result</p>
          <h2>Start from actions, not raw logs</h2>
          <ol>
            <li>Fix CRITICAL findings first because they usually indicate production/security blockers.</li>
            <li>Use grouped findings to understand each rule once and then review the affected classes.</li>
            <li>Export JSON for CI, pull request comments or internal architecture governance.</li>
          </ol>
        </div>
      </section>

      {report ? (
        <>
          <ReportOverview report={report} onExportJson={exportJson} />
          <RecommendedActions report={report} />
          <CategoryBreakdown report={report} />

          <section className="panel findings-panel">
            <div className="section-heading with-actions">
              <div>
                <p className="eyebrow">Grouped findings</p>
                <h2>Rules and affected components</h2>
              </div>
              <button className="secondary-action" onClick={() => setShowRawJson((current) => !current)} type="button">
                {showRawJson ? 'Hide raw JSON' : 'Show raw JSON'}
              </button>
            </div>

            <div className="filters">
              <select value={severityFilter} onChange={(event) => setSeverityFilter(event.target.value as SeverityFilter)}>
                <option value="ALL">All severities</option>
                {severityOrder.map((severity) => (
                  <option key={severity} value={severity}>{severity}</option>
                ))}
              </select>

              <select value={categoryFilter} onChange={(event) => setCategoryFilter(event.target.value)}>
                <option value="ALL">All categories</option>
                {categories.map((category) => (
                  <option key={category} value={category}>{category}</option>
                ))}
              </select>

              <input
                value={query}
                onChange={(event) => setQuery(event.target.value)}
                placeholder="Search rule, class, file, evidence..."
              />
            </div>

            <div className="result-count">
              Showing {formatNumber(visibleFindings.length)} grouped finding(s) out of {formatNumber(report.findings.length)}.
            </div>

            <div className="finding-list">
              {visibleFindings.map((finding) => (
                <FindingCard key={finding.ruleId} finding={finding} />
              ))}
            </div>

            {showRawJson && (
              <pre className="json-viewer">{JSON.stringify(report, null, 2)}</pre>
            )}
          </section>
        </>
      ) : (
        <section className="empty-state panel">
          <h2>No report yet</h2>
          <p>Run a scan to see score, risk level, recommended actions, categories and grouped findings.</p>
        </section>
      )}
    </main>
  );
}

interface ReportProps {
  report: ArchitectureReviewReport;
}

function ReportOverview({ report, onExportJson }: ReportProps & { onExportJson: () => void }) {
  return (
    <section className="panel overview-panel">
      <div className="section-heading with-actions">
        <div>
          <p className="eyebrow">Architecture score</p>
          <h2>{report.projectName}</h2>
          <p className="muted">Scanned {formatDate(report.scannedAt)}</p>
        </div>
        <button className="secondary-action" onClick={onExportJson} type="button">Export JSON</button>
      </div>

      <div className="overview-grid">
        <div className="score-card">
          <div className="score-ring" style={{ '--score': report.architectureScore } as CSSProperties}>
            <span>{report.architectureScore}</span>
          </div>
          <strong>{scoreLabel(report.architectureScore)}</strong>
          <span>{report.summary.executiveSummary}</span>
        </div>

        <Metric label="Risk" value={report.riskLevel} />
        <Metric label="Status" value={report.summary.status} />
        <Metric label="Rules" value={formatNumber(report.rulesExecuted)} />
        <Metric label="Java files" value={formatNumber(report.scannedJavaFiles)} />
        <Metric label="POM files" value={formatNumber(report.scannedPomFiles)} />
      </div>

      <div className="severity-grid">
        {severityOrder.map((severity) => (
          <div className={`severity-card severity-${severity.toLowerCase()}`} key={severity}>
            <span>{severity}</span>
            <strong>{formatNumber(getSeverityCount(report, severity))}</strong>
          </div>
        ))}
      </div>
    </section>
  );
}

function Metric({ label, value }: { label: string; value: string }) {
  return (
    <div className="metric-card">
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}

function RecommendedActions({ report }: ReportProps) {
  if (!report.recommendedActions.length) {
    return null;
  }

  return (
    <section className="panel actions-panel">
      <div className="section-heading">
        <div>
          <p className="eyebrow">Priority queue</p>
          <h2>Recommended actions</h2>
        </div>
      </div>

      <div className="action-list">
        {report.recommendedActions.slice(0, 8).map((action) => (
          <article className="action-card" key={`${action.priority}-${action.ruleId}`}>
            <div className="action-rank">#{action.priority}</div>
            <div>
              <div className="finding-header compact">
                <span className={`badge severity-${action.severity.toLowerCase()}`}>{action.severity}</span>
                <span className="rule-id">{action.ruleId}</span>
              </div>
              <h3>{action.title}</h3>
              <p><strong>Where:</strong> {action.location}</p>
              <p><strong>Why:</strong> {action.reason}</p>
              <p><strong>Fix:</strong> {action.action}</p>
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}

function CategoryBreakdown({ report }: ReportProps) {
  if (!report.findingsByCategory.length) {
    return null;
  }

  return (
    <section className="panel category-panel">
      <div className="section-heading">
        <div>
          <p className="eyebrow">Risk areas</p>
          <h2>Findings by category</h2>
        </div>
      </div>

      <div className="category-grid">
        {report.findingsByCategory.map((category) => (
          <article className="category-card" key={category.category}>
            <div className="category-card-header">
              <h3>{category.category}</h3>
              <strong>{formatNumber(category.findings)}</strong>
            </div>
            <p>{category.explanation}</p>
            <div className="mini-severities">
              <span>C {category.criticalFindings}</span>
              <span>M {category.majorFindings}</span>
              <span>m {category.minorFindings}</span>
              <span>I {category.infoFindings}</span>
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}

function FindingCard({ finding }: { finding: FindingGroup }) {
  const [expanded, setExpanded] = useState(false);
  const visibleComponents = expanded ? finding.affectedComponents : finding.affectedComponents.slice(0, 6);
  const hiddenCount = finding.affectedComponents.length - visibleComponents.length;

  return (
    <article className="finding-card">
      <div className="finding-header">
        <span className={`badge severity-${finding.severity.toLowerCase()}`}>{finding.severity}</span>
        <span className="badge neutral">{finding.category}</span>
        <span className="rule-id">{finding.ruleId}</span>
        <span className="occurrences">{formatNumber(finding.occurrences)} occurrence(s)</span>
      </div>

      <h3>{finding.title}</h3>
      <p className="explanation-text">{finding.explanation}</p>

      <div className="remediation-grid">
        <div>
          <strong>Why it matters</strong>
          <p>{finding.whyItMatters}</p>
        </div>
        <div>
          <strong>Suggested fix</strong>
          <p>{finding.suggestedFix}</p>
        </div>
      </div>

      <div className="component-table" role="table" aria-label={`Affected components for ${finding.ruleId}`}>
        <div className="component-row header" role="row">
          <span>Component</span>
          <span>Location</span>
          <span>Evidence</span>
        </div>
        {visibleComponents.map((component, index) => (
          <div className="component-row" role="row" key={`${component.filePath}-${component.line ?? 0}-${index}`}>
            <span>
              <strong>{component.name}</strong>
              <small>{component.type}</small>
            </span>
            <span className="path-cell">{component.filePath}{component.line ? `:${component.line}` : ''}</span>
            <span>{component.evidence}</span>
          </div>
        ))}
      </div>

      {hiddenCount > 0 && (
        <button className="link-button" onClick={() => setExpanded(true)} type="button">
          Show {formatNumber(hiddenCount)} more affected component(s)
        </button>
      )}

      {expanded && finding.affectedComponents.length > 6 && (
        <button className="link-button" onClick={() => setExpanded(false)} type="button">
          Collapse components
        </button>
      )}
    </article>
  );
}

export default App;
