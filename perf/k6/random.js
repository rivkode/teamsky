import http from "k6/http";
import { check, sleep } from "k6";
import { BASE_URL, chapters, commonThresholds, randomUsers, requestParams } from "./config.js";

export const options = {
  scenarios: {
    random_read: {
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
    "http_req_duration{endpoint:random}": ["p(95)<500"],
  },
};

export default function () {
  const userId = randomUsers[(__VU + __ITER) % randomUsers.length];
  const url = `${BASE_URL}/api/problems/random?chapterId=${chapters[0]}&userId=${userId}`;

  const response = http.get(url, requestParams({ endpoint: "random", scenario: "random_read" }));

  check(response, {
    "random status is 200 or 409": (r) => r.status === 200 || r.status === 409,
  });

  sleep(1);
}
