// Task demo API ile eşleşen tipler.

export type TaskStatus = 'TODO' | 'IN_PROGRESS' | 'DONE';

export interface Task {
  id: number;
  title: string;
  description: string | null;
  status: TaskStatus;
  priority: number;
  createdAt: string; // ISO LocalDateTime
}

export interface TaskRequest {
  title: string;
  description?: string;
  status: TaskStatus;
  priority: number;
}

// Backend GlobalExceptionHandler -> ErrorResponse
export interface ApiErrorBody {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  fieldErrors?: Record<string, string>;
}
