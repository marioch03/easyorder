import { isAxiosError } from "axios";

// ─── Parsed Error Shape ───────────────────────────────────────────────────────

export interface ApiErrorResponse {
  success: false;
  message: string;
  errors?: string[];
  stack?: string;
}

export interface ParsedError {
  message: string;
  errors: string[];
  statusCode: number;
}

// ─── Main Error Handler ───────────────────────────────────────────────────────

export const parseApiError = (error: unknown): ParsedError => {
  // Axios error with a response from the server
  if (isAxiosError(error) && error.response) {
    const data = error.response.data as ApiErrorResponse;

    return {
      message: data.message ?? "Something went wrong.",
      errors: data.errors ?? [],
      statusCode: error.response.status,
    };
  }

  // Axios error with no response (network down, timeout, etc.)
  if (isAxiosError(error) && !error.response) {
    return {
      message: "Network error. Please check your connection.",
      errors: [],
      statusCode: 0,
    };
  }

  // Unknown/unexpected error
  return {
    message:
      error instanceof Error ? error.message : "An unexpected error occurred.",
    errors: [],
    statusCode: 500,
  };
};
