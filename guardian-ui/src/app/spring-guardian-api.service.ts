import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ArchitectureReviewReport, ReportLanguage } from './spring-guardian.model';

@Injectable({ providedIn: 'root' })
export class SpringGuardianApiService {
  private readonly baseUrl = '/api/v1/scans';

  constructor(private readonly http: HttpClient) {}

  scanZip(file: File, language: ReportLanguage): Observable<ArchitectureReviewReport> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<ArchitectureReviewReport>(`${this.baseUrl}/upload`, formData, {
      params: this.languageParams(language)
    });
  }

  scanFolder(files: File[], language: ReportLanguage): Observable<ArchitectureReviewReport> {
    const formData = new FormData();
    for (const file of files) {
      const relativePath = this.relativePathOf(file);
      formData.append('files', file, relativePath);
    }
    return this.http.post<ArchitectureReviewReport>(`${this.baseUrl}/upload-folder`, formData, {
      params: this.languageParams(language)
    });
  }

  scanLocalPath(path: string, language: ReportLanguage): Observable<ArchitectureReviewReport> {
    const body = { path };
    return this.http.post<ArchitectureReviewReport>(`${this.baseUrl}/local`, body, {
      params: this.languageParams(language).set('_ts', String(Date.now())),
      headers: new HttpHeaders({ 'Cache-Control': 'no-cache', Pragma: 'no-cache' })
    });
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
