import { clsx } from "clsx";
import { twMerge } from "tailwind-merge";

export function cn(...inputs) {
  return twMerge(clsx(inputs));
}

export const API_BASE =
  process.env.NODE_ENV === "production"
    ? "https://production.com"
    : "http://localhost:8080/v1";
