import http from "k6/http";
import { check, sleep } from "k6";
import { BASE_URL, chapters, commonThresholds, jsonParams, randomUsers, submitUsers } from "./config.js";

const HOT_PROBLEM_ID = Number(__ENV.HOT_PROBLEM_ID || 1002);
const DETAIL_USER_ID = Number(__ENV.DETAIL_USER_ID || 1);
const DETAIL_PROBLEM_ID = Number(__ENV.DETAIL_PROBLEM_ID || 1003);

export const options = {
  scenarios: {
    random_read: {
      executor: "constant-arrival-rate",
      rate: 20,
      timeUnit: "1s",
      duration: "2m",
      preAllocatedVUs: 20,
      maxVUs: 100,
      tags: { endpoint: "random", scenario: "mixed_load" },
      exec: "randomScenario",
    },
    detail_read: {
      executor: "constant-arrival-rate",
      rate: 8,
      timeUnit: "1s",
      duration: "2m",
      preAllocatedVUs: 10,
      maxVUs: 50,
      tags: { endpoint: "detail", scenario: "mixed_load" },
      exec: "detailScenario",
    },
    submit_write: {
      executor: "constant-arrival-rate",
      rate: 10,
      timeUnit: "1s",
      duration: "2m",
      preAllocatedVUs: 10,
      maxVUs: 50,
      tags: { endpoint: "submit", scenario: "mixed_load" },
      exec: "submitScenario",
    },
    skip_flow: {
      executor: "constant-arrival-rate",
      rate: 2,
      timeUnit: "1s",
      duration: "2m",
      preAllocatedVUs: 5,
      maxVUs: 20,
      tags: { endpoint: "skip", scenario: "mixed_load" },
      exec: "skipScenario",
    },
  },
  thresholds: {
    ...commonThresholds,
    "http_req_duration{endpoint:random}": ["p(95)<500"],
    "http_req_duration{endpoint:detail}": ["p(95)<500"],
    "http_req_duration{endpoint:submit}": ["p(95)<800"],
  },
};

export function randomScenario() {
  const userId = randomUsers[(__VU + __ITER) % randomUsers.length];
  const response = http.post(
    `${BASE_URL}/api/problems/random`,
    JSON.stringify({ chapterId: chapters[0], userId }),
    jsonParams({ endpoint: "random", scenario: "mixed_load" }),
  );

  check(response, {
    "mixed random status is 200 or 409": (r) => r.status === 200 || r.status === 409,
  });
  sleep(0.3);
}

export function detailScenario() {
  const response = http.post(
    `${BASE_URL}/api/problems/detail`,
    JSON.stringify({ userId: DETAIL_USER_ID, problemId: DETAIL_PROBLEM_ID }),
    jsonParams({ endpoint: "detail", scenario: "mixed_load" }),
  );

  check(response, {
    "mixed detail status is 200": (r) => r.status === 200,
  });
  sleep(0.3);
}

export function submitScenario() {
  const userId = submitUsers[(__VU * 1000 + __ITER) % submitUsers.length];
  const selectedChoices = __ITER % 2 === 0 ? [1, 2] : [1, 3];
  const response = http.post(
    `${BASE_URL}/api/problems/submit`,
    JSON.stringify({
      problemId: HOT_PROBLEM_ID,
      userId,
      answerType: "OBJECTIVE",
      selectedChoices,
    }),
    jsonParams({ endpoint: "submit", scenario: "mixed_load" }),
  );

  check(response, {
    "mixed submit status is 200": (r) => r.status === 200,
  });
  sleep(0.2);
}

export function skipScenario() {
  const userId = randomUsers[(__VU + __ITER) % randomUsers.length];
  const response = http.post(
    `${BASE_URL}/api/problems/skip`,
    JSON.stringify({
      chapterId: chapters[0],
      userId,
      problemId: 1001,
    }),
    jsonParams({ endpoint: "skip", scenario: "mixed_load" }),
  );

  check(response, {
    "mixed skip status is 200 or 404 or 409": (r) => [200, 404, 409].includes(r.status),
  });
  sleep(0.5);
}
