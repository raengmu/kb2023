CREATE SCHEMA blogSearch;

CREATE TABLE IF NOT EXISTS tb_searchQueryCount (
    category VARCHAR(32) NOT NULL,
    query VARCHAR(128) NOT NULL,
    cnt BIGINT NOT NULL,
    createdAt DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS tb_searchCache (
    id BIGINT NOT NULL UNIQUE PRIMARY KEY AUTO_INCREMENT,
    cacheKey CHAR(16) NOT NULL,
    page INT NOT NULL,   -- 0 base index
    pageSize INT NOT NULL,
    resultRaw CLOB NOT NULL,
    createdAt DATETIME NOT NULL
);
