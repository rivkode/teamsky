import http from "k6/http";
import { check, sleep } from "k6";
import { BASE_URL, commonThresholds, jsonParams, submitUsers } from "./config.js";

const HOT_PROBLEM_ID = Number(__ENV.HOT_PROBLEM_ID || 1002);

export const options = {
  scenarios: {
    submit_hot_problem: {
      executor: "constant-arrival-rate",
      rate: 20,
      timeUnit: "1s",
      duration: "2m",
      preAllocatedVUs: 20,
      maxVUs: 100,
    },
  },
  thresholds: {
    ...commonThresholds,
    "http_req_duration{endpoint:submit}": ["p(95)<800"],
  },
};

export default function () {
  const userId = submitUsers[(__VU * 1000 + __ITER) % submitUsers.length];
  const selectedChoices = __ITER % 2 === 0 ? [1, 2] : [1, 3];
  const payload = JSON.stringify({
    problemId: HOT_PROBLEM_ID,
    userId,
    answerType: "OBJECTIVE",
    selectedChoices,
  });

  const response = http.post(
    `${BASE_URL}/api/problems/submit`,
    payload,
    jsonParams({ endpoint: "submit", scenario: "hot_problem" }),
  );

  check(response, {
    "submit status is 200": (r) => r.status === 200,
  });

  sleep(0.2);
}
