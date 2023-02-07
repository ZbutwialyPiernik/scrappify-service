create table "site"
(
    "id"                  BIGSERIAL NOT NULL PRIMARY KEY,
    "name"                VARCHAR   NOT NULL UNIQUE,
    "host"                VARCHAR   NOT NULL UNIQUE
);

create table "product"
(
    "id"                  BIGSERIAL NOT NULL PRIMARY KEY,
    "name"                VARCHAR   NOT NULL,
    "product_code"        VARCHAR   NOT NULL,
    "url"                 VARCHAR   NOT NULL,
    "fetch_cron"          VARCHAR   NOT NULL,
    "site_id"             BIGINT    NOT NULL
);

create table "product_price"
(
    "id"         BIGSERIAL      NOT NULL PRIMARY KEY,
    "price"      DECIMAL(21, 2) NOT NULL,
    "currency"   VARCHAR,
    "product_id" BIGINT         NOT NULL
);

alter table "product"
    add constraint "site" foreign key ("site_id")
        references "site" ("id") on update RESTRICT on delete RESTRICT;

alter table "product_price"
    add constraint "product" foreign key ("product_id")
        references "product" ("id") on update RESTRICT on delete RESTRICT;
