import type { ArchitectureReviewReport, ProblemDetail } from './types';

const apiBase = import.meta.env.VITE_GUARDIAN_API_BASE ?? '';

async function parseError(response: Response): Promise<Error> {
  const contentType = response.headers.get('content-type') ?? '';

  if (contentType.includes('application/problem+json') || contentType.includes('application/json')) {
    const payload = (await response.json()) as ProblemDetail;
    return new Error(payload.detail || payload.title || `Request failed with status ${response.status}`);
  }

  const text = await response.text();
  return new Error(text || `Request failed with status ${response.status}`);
}

export async function scanZip(file: File): Promise<ArchitectureReviewReport> {
  const formData = new FormData();
  formData.append('file', file);

  const response = await fetch(`${apiBase}/api/v1/scans/upload`, {
    method: 'POST',
    body: formData
  });

  if (!response.ok) {
    throw await parseError(response);
  }

  return response.json() as Promise<ArchitectureReviewReport>;
}

export async function scanLocalPath(path: string): Promise<ArchitectureReviewReport> {
  const response = await fetch(`${apiBase}/api/v1/scans/local`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ path })
  });

  if (!response.ok) {
    throw await parseError(response);
  }

  return response.json() as Promise<ArchitectureReviewReport>;
}

export async function healthCheck(): Promise<boolean> {
  try {
    const response = await fetch(`${apiBase}/api/v1/health`);
    return response.ok;
  } catch {
    return false;
  }
}
