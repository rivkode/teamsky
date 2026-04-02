import http from "k6/http";
import { check, sleep } from "k6";
import { BASE_URL, commonThresholds, requestParams } from "./config.js";

const DETAIL_USER_ID = Number(__ENV.DETAIL_USER_ID || 1);
const DETAIL_PROBLEM_ID = Number(__ENV.DETAIL_PROBLEM_ID || 1003);

export const options = {
  scenarios: {
    detail_read: {
      executor: "ramping-vus",
      startVUs: 1,
      stages: [
        { duration: "30s", target: 10 },
        { duration: "1m", target: 30 },
        { duration: "30s", target: 0 },
      ],
      gracefulRampDown: "10s",
    },
  },
  thresholds: {
    ...commonThresholds,
    "http_req_duration{endpoint:detail}": ["p(95)<500"],
  },
};

export default function () {
  const url = `${BASE_URL}/api/problems/detail?userId=${DETAIL_USER_ID}&problemId=${DETAIL_PROBLEM_ID}`;

  const response = http.get(url, requestParams({ endpoint: "detail", scenario: "detail_read" }));

  check(response, {
    "detail status is 200": (r) => r.status === 200,
  });

  sleep(1);
}
