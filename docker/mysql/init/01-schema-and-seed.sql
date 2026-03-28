SET NAMES utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;
SET character_set_results = utf8mb4;

CREATE TABLE IF NOT EXISTS chapters (
    id BIGINT NOT NULL PRIMARY KEY,
    title VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS problems (
    id BIGINT NOT NULL PRIMARY KEY,
    chapter_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    answer_format VARCHAR(30) NOT NULL,
    problem_type VARCHAR(30) NOT NULL,
    explanation TEXT NOT NULL,
    CONSTRAINT fk_problems_chapter FOREIGN KEY (chapter_id) REFERENCES chapters (id)
);

CREATE TABLE IF NOT EXISTS problem_choices (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    problem_id BIGINT NOT NULL,
    sequence INT NOT NULL,
    content TEXT NOT NULL,
    CONSTRAINT fk_problem_choices_problem FOREIGN KEY (problem_id) REFERENCES problems (id)
);

CREATE TABLE IF NOT EXISTS solve_attempts (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    chapter_id BIGINT NOT NULL,
    problem_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    is_correct BIT NULL,
    answer_status VARCHAR(20) NULL,
    created_at DATETIME NOT NULL,
    INDEX idx_solve_attempts_user_chapter (user_id, chapter_id),
    INDEX idx_solve_attempts_problem (problem_id),
    INDEX idx_solve_attempts_user_chapter_status (user_id, chapter_id, status),
    INDEX idx_solve_attempts_user_problem_status_id (user_id, problem_id, status, id),
    CONSTRAINT fk_solve_attempts_chapter FOREIGN KEY (chapter_id) REFERENCES chapters (id),
    CONSTRAINT fk_solve_attempts_problem FOREIGN KEY (problem_id) REFERENCES problems (id)
);

CREATE TABLE IF NOT EXISTS problem_statistics (
    problem_id BIGINT NOT NULL PRIMARY KEY,
    solved_user_count BIGINT NOT NULL,
    correct_user_count BIGINT NOT NULL,
    correct_rate INT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_problem_statistics_problem FOREIGN KEY (problem_id) REFERENCES problems (id)
);

CREATE TABLE IF NOT EXISTS problem_user_statistics (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    problem_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    latest_answer_status VARCHAR(20) NOT NULL,
    is_correct BIT NOT NULL,
    counted_as_solved BIT NOT NULL,
    counted_as_correct BIT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT uk_problem_user_statistics UNIQUE (problem_id, user_id),
    CONSTRAINT fk_problem_user_statistics_problem FOREIGN KEY (problem_id) REFERENCES problems (id)
);

CREATE TABLE IF NOT EXISTS problem_answer_keys (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    problem_id BIGINT NOT NULL,
    answer_format VARCHAR(30) NOT NULL,
    choice_sequence INT NULL,
    subjective_answer TEXT NULL,
    CONSTRAINT fk_problem_answer_keys_problem FOREIGN KEY (problem_id) REFERENCES problems (id)
);

CREATE TABLE IF NOT EXISTS solve_attempt_answers (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    solve_attempt_id BIGINT NOT NULL,
    answer_format VARCHAR(30) NOT NULL,
    choice_sequence INT NULL,
    subjective_answer TEXT NULL,
    CONSTRAINT fk_solve_attempt_answers_attempt FOREIGN KEY (solve_attempt_id) REFERENCES solve_attempts (id)
);

INSERT INTO chapters (id, title) VALUES
    (1, 'Java Basics'),
    (2, 'Spring Core')
ON DUPLICATE KEY UPDATE title = VALUES(title);

INSERT INTO problems (id, chapter_id, content, answer_format, problem_type, explanation) VALUES
    (1001, 1, 'Java에서 기본형 중 정수 타입이 아닌 것을 고르세요.', 'OBJECTIVE', 'SINGLE_ANSWER', '정답은 boolean 입니다.'),
    (1002, 1, 'JVM 메모리 영역에 대한 설명으로 옳은 것을 모두 고르세요.', 'OBJECTIVE', 'MULTIPLE_ANSWER', 'Heap, Method Area, PC Register 관련 설명이 정답입니다.'),
    (1003, 1, 'List와 Set의 차이로 가장 적절한 것을 고르세요.', 'OBJECTIVE', 'SINGLE_ANSWER', 'List는 중복 허용, Set은 중복 비허용이 핵심 차이입니다.'),
    (1004, 1, '예외 처리 방식으로 적절한 것을 모두 고르세요.', 'OBJECTIVE', 'MULTIPLE_ANSWER', '복구 가능성과 일관된 오류 응답이 핵심입니다.'),
    (2001, 2, 'Spring Bean의 기본 scope는 무엇인가요?', 'OBJECTIVE', 'SINGLE_ANSWER', '기본 scope는 singleton 입니다.'),
    (2002, 2, '트랜잭션 전파 속성에 대한 설명으로 옳은 것을 모두 고르세요.', 'OBJECTIVE', 'MULTIPLE_ANSWER', 'REQUIRED, REQUIRES_NEW, SUPPORTS가 정답입니다.'),
    (2003, 2, 'Spring에서 기본 Bean scope 이름을 입력하세요.', 'SUBJECTIVE', 'SINGLE_ANSWER', '기본 Bean scope 이름은 singleton 입니다.')
ON DUPLICATE KEY UPDATE
    chapter_id = VALUES(chapter_id),
    content = VALUES(content),
    answer_format = VALUES(answer_format),
    problem_type = VALUES(problem_type),
    explanation = VALUES(explanation);

INSERT INTO problem_choices (problem_id, sequence, content) VALUES
    (1001, 1, 'int'),
    (1001, 2, 'long'),
    (1001, 3, 'boolean'),
    (1001, 4, 'short'),
    (1001, 5, 'byte'),

    (1002, 1, 'Heap은 객체 인스턴스가 저장되는 영역이다.'),
    (1002, 2, 'Method Area는 클래스 메타데이터를 저장한다.'),
    (1002, 3, 'Stack은 모든 스레드가 공유한다.'),
    (1002, 4, 'PC Register는 스레드마다 하나씩 존재한다.'),
    (1002, 5, 'Native Method Stack은 JVM과 무관하다.'),

    (1003, 1, 'List는 순서를 보장하고 Set은 중복을 허용한다.'),
    (1003, 2, 'List는 중복을 허용하고 Set은 순서를 보장하지 않을 수 있다.'),
    (1003, 3, '둘 다 중복을 허용하지 않는다.'),
    (1003, 4, '둘 다 인덱스로 접근할 수 있다.'),
    (1003, 5, 'Set은 항상 정렬된다.'),

    (1004, 1, '복구 가능한 예외는 적절한 계층에서 처리하는 것이 좋다.'),
    (1004, 2, '모든 예외는 무조건 catch 후 무시해야 한다.'),
    (1004, 3, '의미 없는 try-catch 재포장은 피하는 편이 좋다.'),
    (1004, 4, '검증 실패는 일관된 응답 포맷으로 반환하는 것이 좋다.'),
    (1004, 5, '예외 메시지는 항상 내부 스택트레이스를 그대로 노출해야 한다.'),

    (2001, 1, 'prototype'),
    (2001, 2, 'singleton'),
    (2001, 3, 'request'),
    (2001, 4, 'session'),
    (2001, 5, 'application'),

    (2002, 1, 'REQUIRED는 기존 트랜잭션이 있으면 참여한다.'),
    (2002, 2, 'REQUIRES_NEW는 항상 새 트랜잭션을 시작한다.'),
    (2002, 3, 'MANDATORY는 트랜잭션이 없어도 새로 만든다.'),
    (2002, 4, 'SUPPORTS는 트랜잭션이 없어도 실행 가능하다.'),
    (2002, 5, 'NEVER는 트랜잭션 안에서 실행되어야 한다.')
ON DUPLICATE KEY UPDATE
    sequence = VALUES(sequence),
    content = VALUES(content);

INSERT INTO problem_answer_keys (problem_id, answer_format, choice_sequence, subjective_answer) VALUES
    (1001, 'OBJECTIVE', 3, NULL),
    (1002, 'OBJECTIVE', 1, NULL),
    (1002, 'OBJECTIVE', 2, NULL),
    (1002, 'OBJECTIVE', 4, NULL),
    (1003, 'OBJECTIVE', 2, NULL),
    (1004, 'OBJECTIVE', 1, NULL),
    (1004, 'OBJECTIVE', 3, NULL),
    (1004, 'OBJECTIVE', 4, NULL),
    (2001, 'OBJECTIVE', 2, NULL),
    (2002, 'OBJECTIVE', 1, NULL),
    (2002, 'OBJECTIVE', 2, NULL),
    (2002, 'OBJECTIVE', 4, NULL),
    (2003, 'SUBJECTIVE', NULL, 'singleton'),
    (2003, 'SUBJECTIVE', NULL, '싱글톤');

INSERT INTO solve_attempts (user_id, chapter_id, problem_id, status, is_correct, answer_status, created_at) VALUES
    (1, 1, 1003, 'SOLVED', b'1', 'CORRECT', '2026-03-26 10:00:00'),
    (1, 1, 1002, 'SKIPPED', NULL, NULL, '2026-03-26 10:05:00'),
    (2, 1, 1001, 'SOLVED', b'1', 'CORRECT', '2026-03-26 09:00:00'),
    (3, 1, 1001, 'SOLVED', b'1', 'CORRECT', '2026-03-26 09:01:00'),
    (4, 1, 1001, 'SOLVED', b'1', 'CORRECT', '2026-03-26 09:02:00'),
    (5, 1, 1001, 'SOLVED', b'1', 'CORRECT', '2026-03-26 09:03:00'),
    (6, 1, 1001, 'SOLVED', b'1', 'CORRECT', '2026-03-26 09:04:00'),
    (7, 1, 1001, 'SOLVED', b'1', 'CORRECT', '2026-03-26 09:05:00'),
    (8, 1, 1001, 'SOLVED', b'1', 'CORRECT', '2026-03-26 09:06:00'),
    (9, 1, 1001, 'SOLVED', b'1', 'CORRECT', '2026-03-26 09:07:00'),
    (10, 1, 1001, 'SOLVED', b'1', 'CORRECT', '2026-03-26 09:08:00'),
    (11, 1, 1001, 'SOLVED', b'1', 'CORRECT', '2026-03-26 09:09:00'),
    (12, 1, 1001, 'SOLVED', b'1', 'CORRECT', '2026-03-26 09:10:00'),
    (13, 1, 1001, 'SOLVED', b'1', 'CORRECT', '2026-03-26 09:11:00'),
    (14, 1, 1001, 'SOLVED', b'1', 'CORRECT', '2026-03-26 09:12:00'),
    (15, 1, 1001, 'SOLVED', b'1', 'CORRECT', '2026-03-26 09:13:00'),
    (16, 1, 1001, 'SOLVED', b'1', 'CORRECT', '2026-03-26 09:14:00'),
    (17, 1, 1001, 'SOLVED', b'1', 'CORRECT', '2026-03-26 09:15:00'),
    (18, 1, 1001, 'SOLVED', b'1', 'CORRECT', '2026-03-26 09:16:00'),
    (19, 1, 1001, 'SOLVED', b'1', 'CORRECT', '2026-03-26 09:17:00'),
    (20, 1, 1001, 'SOLVED', b'1', 'CORRECT', '2026-03-26 09:18:00'),
    (21, 1, 1001, 'SOLVED', b'1', 'CORRECT', '2026-03-26 09:19:00'),
    (22, 1, 1001, 'SOLVED', b'1', 'CORRECT', '2026-03-26 09:20:00'),
    (23, 1, 1001, 'SOLVED', b'0', 'INCORRECT', '2026-03-26 09:21:00'),
    (24, 1, 1001, 'SOLVED', b'0', 'INCORRECT', '2026-03-26 09:22:00'),
    (25, 1, 1001, 'SOLVED', b'0', 'INCORRECT', '2026-03-26 09:23:00'),
    (26, 1, 1001, 'SOLVED', b'0', 'INCORRECT', '2026-03-26 09:24:00'),
    (27, 1, 1001, 'SOLVED', b'0', 'INCORRECT', '2026-03-26 09:25:00'),
    (28, 1, 1001, 'SOLVED', b'0', 'INCORRECT', '2026-03-26 09:26:00'),
    (29, 1, 1001, 'SOLVED', b'0', 'INCORRECT', '2026-03-26 09:27:00'),
    (30, 1, 1001, 'SOLVED', b'0', 'INCORRECT', '2026-03-26 09:28:00'),
    (31, 1, 1001, 'SOLVED', b'0', 'INCORRECT', '2026-03-26 09:29:00'),
    (32, 1, 1001, 'SOLVED', b'0', 'INCORRECT', '2026-03-26 09:30:00'),
    (2, 1, 1004, 'SOLVED', b'1', 'CORRECT', '2026-03-26 11:00:00'),
    (3, 1, 1004, 'SOLVED', b'0', 'INCORRECT', '2026-03-26 11:01:00'),
    (4, 1, 1004, 'SOLVED', b'1', 'CORRECT', '2026-03-26 11:02:00'),
    (5, 1, 1004, 'SOLVED', b'1', 'CORRECT', '2026-03-26 11:03:00'),
    (6, 1, 1004, 'SOLVED', b'0', 'INCORRECT', '2026-03-26 11:04:00'),
    (7, 1, 1004, 'SOLVED', b'1', 'CORRECT', '2026-03-26 11:05:00'),
    (8, 1, 1004, 'SOLVED', b'1', 'CORRECT', '2026-03-26 11:06:00'),
    (9, 1, 1004, 'SOLVED', b'0', 'INCORRECT', '2026-03-26 11:07:00'),
    (10, 1, 1004, 'SOLVED', b'1', 'CORRECT', '2026-03-26 11:08:00'),
    (11, 1, 1004, 'SOLVED', b'1', 'CORRECT', '2026-03-26 11:09:00');

INSERT INTO problem_statistics (problem_id, solved_user_count, correct_user_count, correct_rate, version, updated_at) VALUES
    (1001, 31, 21, 68, 0, '2026-03-26 09:30:00'),
    (1002, 0, 0, NULL, 0, '2026-03-26 00:00:00'),
    (1003, 1, 1, NULL, 0, '2026-03-26 10:00:00'),
    (1004, 10, 7, NULL, 0, '2026-03-26 11:09:00'),
    (2001, 0, 0, NULL, 0, '2026-03-26 00:00:00'),
    (2002, 0, 0, NULL, 0, '2026-03-26 00:00:00'),
    (2003, 0, 0, NULL, 0, '2026-03-26 00:00:00')
ON DUPLICATE KEY UPDATE
    solved_user_count = VALUES(solved_user_count),
    correct_user_count = VALUES(correct_user_count),
    correct_rate = VALUES(correct_rate),
    updated_at = VALUES(updated_at);

INSERT INTO problem_user_statistics (problem_id, user_id, latest_answer_status, is_correct, counted_as_solved, counted_as_correct, created_at, updated_at) VALUES
    (1003, 1, 'CORRECT', b'1', b'1', b'1', '2026-03-26 10:00:00', '2026-03-26 10:00:00'),
    (1001, 2, 'CORRECT', b'1', b'1', b'1', '2026-03-26 09:00:00', '2026-03-26 09:00:00'),
    (1001, 3, 'CORRECT', b'1', b'1', b'1', '2026-03-26 09:01:00', '2026-03-26 09:01:00'),
    (1001, 4, 'CORRECT', b'1', b'1', b'1', '2026-03-26 09:02:00', '2026-03-26 09:02:00'),
    (1001, 5, 'CORRECT', b'1', b'1', b'1', '2026-03-26 09:03:00', '2026-03-26 09:03:00'),
    (1001, 6, 'CORRECT', b'1', b'1', b'1', '2026-03-26 09:04:00', '2026-03-26 09:04:00'),
    (1001, 7, 'CORRECT', b'1', b'1', b'1', '2026-03-26 09:05:00', '2026-03-26 09:05:00'),
    (1001, 8, 'CORRECT', b'1', b'1', b'1', '2026-03-26 09:06:00', '2026-03-26 09:06:00'),
    (1001, 9, 'CORRECT', b'1', b'1', b'1', '2026-03-26 09:07:00', '2026-03-26 09:07:00'),
    (1001, 10, 'CORRECT', b'1', b'1', b'1', '2026-03-26 09:08:00', '2026-03-26 09:08:00'),
    (1001, 11, 'CORRECT', b'1', b'1', b'1', '2026-03-26 09:09:00', '2026-03-26 09:09:00'),
    (1001, 12, 'CORRECT', b'1', b'1', b'1', '2026-03-26 09:10:00', '2026-03-26 09:10:00'),
    (1001, 13, 'CORRECT', b'1', b'1', b'1', '2026-03-26 09:11:00', '2026-03-26 09:11:00'),
    (1001, 14, 'CORRECT', b'1', b'1', b'1', '2026-03-26 09:12:00', '2026-03-26 09:12:00'),
    (1001, 15, 'CORRECT', b'1', b'1', b'1', '2026-03-26 09:13:00', '2026-03-26 09:13:00'),
    (1001, 16, 'CORRECT', b'1', b'1', b'1', '2026-03-26 09:14:00', '2026-03-26 09:14:00'),
    (1001, 17, 'CORRECT', b'1', b'1', b'1', '2026-03-26 09:15:00', '2026-03-26 09:15:00'),
    (1001, 18, 'CORRECT', b'1', b'1', b'1', '2026-03-26 09:16:00', '2026-03-26 09:16:00'),
    (1001, 19, 'CORRECT', b'1', b'1', b'1', '2026-03-26 09:17:00', '2026-03-26 09:17:00'),
    (1001, 20, 'CORRECT', b'1', b'1', b'1', '2026-03-26 09:18:00', '2026-03-26 09:18:00'),
    (1001, 21, 'CORRECT', b'1', b'1', b'1', '2026-03-26 09:19:00', '2026-03-26 09:19:00'),
    (1001, 22, 'CORRECT', b'1', b'1', b'1', '2026-03-26 09:20:00', '2026-03-26 09:20:00'),
    (1001, 23, 'INCORRECT', b'0', b'1', b'0', '2026-03-26 09:21:00', '2026-03-26 09:21:00'),
    (1001, 24, 'INCORRECT', b'0', b'1', b'0', '2026-03-26 09:22:00', '2026-03-26 09:22:00'),
    (1001, 25, 'INCORRECT', b'0', b'1', b'0', '2026-03-26 09:23:00', '2026-03-26 09:23:00'),
    (1001, 26, 'INCORRECT', b'0', b'1', b'0', '2026-03-26 09:24:00', '2026-03-26 09:24:00'),
    (1001, 27, 'INCORRECT', b'0', b'1', b'0', '2026-03-26 09:25:00', '2026-03-26 09:25:00'),
    (1001, 28, 'INCORRECT', b'0', b'1', b'0', '2026-03-26 09:26:00', '2026-03-26 09:26:00'),
    (1001, 29, 'INCORRECT', b'0', b'1', b'0', '2026-03-26 09:27:00', '2026-03-26 09:27:00'),
    (1001, 30, 'INCORRECT', b'0', b'1', b'0', '2026-03-26 09:28:00', '2026-03-26 09:28:00'),
    (1001, 31, 'INCORRECT', b'0', b'1', b'0', '2026-03-26 09:29:00', '2026-03-26 09:29:00'),
    (1001, 32, 'INCORRECT', b'0', b'1', b'0', '2026-03-26 09:30:00', '2026-03-26 09:30:00'),
    (1004, 2, 'CORRECT', b'1', b'1', b'1', '2026-03-26 11:00:00', '2026-03-26 11:00:00'),
    (1004, 3, 'INCORRECT', b'0', b'1', b'0', '2026-03-26 11:01:00', '2026-03-26 11:01:00'),
    (1004, 4, 'CORRECT', b'1', b'1', b'1', '2026-03-26 11:02:00', '2026-03-26 11:02:00'),
    (1004, 5, 'CORRECT', b'1', b'1', b'1', '2026-03-26 11:03:00', '2026-03-26 11:03:00'),
    (1004, 6, 'INCORRECT', b'0', b'1', b'0', '2026-03-26 11:04:00', '2026-03-26 11:04:00'),
    (1004, 7, 'CORRECT', b'1', b'1', b'1', '2026-03-26 11:05:00', '2026-03-26 11:05:00'),
    (1004, 8, 'CORRECT', b'1', b'1', b'1', '2026-03-26 11:06:00', '2026-03-26 11:06:00'),
    (1004, 9, 'INCORRECT', b'0', b'1', b'0', '2026-03-26 11:07:00', '2026-03-26 11:07:00'),
    (1004, 10, 'CORRECT', b'1', b'1', b'1', '2026-03-26 11:08:00', '2026-03-26 11:08:00'),
    (1004, 11, 'CORRECT', b'1', b'1', b'1', '2026-03-26 11:09:00', '2026-03-26 11:09:00')
ON DUPLICATE KEY UPDATE
    latest_answer_status = VALUES(latest_answer_status),
    is_correct = VALUES(is_correct),
    counted_as_solved = VALUES(counted_as_solved),
    counted_as_correct = VALUES(counted_as_correct),
    updated_at = VALUES(updated_at);
