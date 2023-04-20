CREATE TABLE "site"
(
    "id"   BIGSERIAL NOT NULL PRIMARY KEY,
    "name" VARCHAR   NOT NULL UNIQUE,
    "host" VARCHAR   NOT NULL UNIQUE
);

CREATE TABLE "product"
(
    "id"         BIGSERIAL NOT NULL PRIMARY KEY,
    "name"       VARCHAR   NOT NULL,
    "code"       VARCHAR   NOT NULL,
    "url"        VARCHAR   NOT NULL,
    "fetch_cron" VARCHAR   NOT NULL,
    "site_id"    BIGINT    NOT NULL
);

CREATE TABLE "product_snapshot"
(
    "id"         BIGSERIAL                NOT NULL PRIMARY KEY,
    "price"      DECIMAL(21, 2)           NOT NULL,
    "name"       VARCHAR,
    "fetch_time" TIMESTAMP WITH TIME ZONE NOT NULL,
    "currency"   VARCHAR,
    "product_id" BIGINT                   NOT NULL,
    UNIQUE (product_id, fetch_time)
);

ALTER TABLE "product"
    ADD CONSTRAINT "site" FOREIGN KEY ("site_id")
        REFERENCES "site" ("id") ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE "product_snapshot"
    ADD CONSTRAINT "product" FOREIGN KEY ("product_id")
        REFERENCES "product" ("id") ON UPDATE RESTRICT ON DELETE RESTRICT;
