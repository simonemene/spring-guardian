import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ArchitectureReviewReport, ReportLanguage, ScanProfile } from './spring-guardian.model';

@Injectable({ providedIn: 'root' })
export class SpringGuardianApiService {
  private readonly baseUrl = '/api/v1/scans';

  constructor(private readonly http: HttpClient) {}

  scanZip(file: File, language: ReportLanguage, profile: ScanProfile): Observable<ArchitectureReviewReport> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<ArchitectureReviewReport>(`${this.baseUrl}/upload`, formData, {
      params: this.scanParams(language, profile)
    });
  }

  scanFolder(files: File[], language: ReportLanguage, profile: ScanProfile): Observable<ArchitectureReviewReport> {
    const formData = new FormData();
    for (const file of files) {
      const relativePath = this.relativePathOf(file);
      formData.append('files', file, relativePath);
    }
    return this.http.post<ArchitectureReviewReport>(`${this.baseUrl}/upload-folder`, formData, {
      params: this.scanParams(language, profile)
    });
  }

  scanLocalPath(path: string, language: ReportLanguage, profile: ScanProfile): Observable<ArchitectureReviewReport> {
    const body = { path, ...profile };
    return this.http.post<ArchitectureReviewReport>(`${this.baseUrl}/local`, body, {
      params: this.languageParams(language).set('_ts', String(Date.now())),
      headers: new HttpHeaders({ 'Cache-Control': 'no-cache', Pragma: 'no-cache' })
    });
  }

  private scanParams(language: ReportLanguage, profile: ScanProfile): HttpParams {
    return this.languageParams(language)
      .set('projectType', profile.projectType)
      .set('architectureStyle', profile.architectureStyle)
      .set('releaseTarget', profile.releaseTarget)
      .set('knownIssuesAccepted', String(profile.knownIssuesAccepted));
  }

  private languageParams(language: ReportLanguage): HttpParams {
    return new HttpParams().set('language', language);
  }

  private relativePathOf(file: File): string {
    const withBrowserPath = file as File & { webkitRelativePath?: string };
    return withBrowserPath.webkitRelativePath && withBrowserPath.webkitRelativePath.trim().length > 0
      ? withBrowserPath.webkitRelativePath
      : file.name;
  }
}
