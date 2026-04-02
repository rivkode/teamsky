import { SharedArray } from "k6/data";

export const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";
export const TEST_ID = __ENV.TEST_ID || `teamsky-${Date.now()}`;

export const commonTags = {
  testid: TEST_ID,
  project: "teamsky",
};

export const commonThresholds = {
  http_req_failed: ["rate<0.01"],
  http_req_duration: ["p(95)<1000", "p(99)<2000"],
};

export const chapters = new SharedArray("chapters", function () {
  return [1];
});

export const randomUsers = new SharedArray("randomUsers", function () {
  return Array.from({ length: 200 }, (_, index) => index + 1000);
});

export const submitUsers = new SharedArray("submitUsers", function () {
  return Array.from({ length: 200 }, (_, index) => index + 2000);
});

export function requestParams(tags = {}) {
  return {
    tags: { ...commonTags, ...tags },
  };
}

export function jsonParams(tags = {}) {
  return {
    headers: { "Content-Type": "application/json" },
    ...requestParams(tags),
  };
}
