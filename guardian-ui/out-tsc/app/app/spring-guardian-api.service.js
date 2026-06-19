import { Injectable } from '@angular/core';
import { HttpHeaders, HttpParams } from '@angular/common/http';
import * as i0 from "@angular/core";
import * as i1 from "@angular/common/http";
export class SpringGuardianApiService {
    constructor(http) {
        this.http = http;
        this.baseUrl = '/api/v1/scans';
    }
    scanZip(file, language) {
        const formData = new FormData();
        formData.append('file', file);
        return this.http.post(`${this.baseUrl}/upload`, formData, {
            params: this.languageParams(language)
        });
    }
    scanFolder(files, language) {
        const formData = new FormData();
        for (const file of files) {
            const relativePath = this.relativePathOf(file);
            formData.append('files', file, relativePath);
        }
        return this.http.post(`${this.baseUrl}/upload-folder`, formData, {
            params: this.languageParams(language)
        });
    }
    scanLocalPath(path, language) {
        const body = { path };
        return this.http.post(`${this.baseUrl}/local`, body, {
            params: this.languageParams(language).set('_ts', String(Date.now())),
            headers: new HttpHeaders({ 'Cache-Control': 'no-cache', Pragma: 'no-cache' })
        });
    }
    languageParams(language) {
        return new HttpParams().set('language', language);
    }
    relativePathOf(file) {
        const withBrowserPath = file;
        return withBrowserPath.webkitRelativePath && withBrowserPath.webkitRelativePath.trim().length > 0
            ? withBrowserPath.webkitRelativePath
            : file.name;
    }
    static { this.ɵfac = function SpringGuardianApiService_Factory(__ngFactoryType__) { return new (__ngFactoryType__ || SpringGuardianApiService)(i0.ɵɵinject(i1.HttpClient)); }; }
    static { this.ɵprov = /*@__PURE__*/ i0.ɵɵdefineInjectable({ token: SpringGuardianApiService, factory: SpringGuardianApiService.ɵfac, providedIn: 'root' }); }
}
(() => { (typeof ngDevMode === "undefined" || ngDevMode) && i0.ɵsetClassMetadata(SpringGuardianApiService, [{
        type: Injectable,
        args: [{ providedIn: 'root' }]
    }], () => [{ type: i1.HttpClient }], null); })();
//# sourceMappingURL=spring-guardian-api.service.js.map