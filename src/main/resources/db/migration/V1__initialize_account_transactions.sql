CREATE TABLE IF NOT EXISTS accounts
(
    id            bigserial not null unique,
    balance       numeric(15, 2),
    creation_date timestamp not null
    );

CREATE TABLE IF NOT EXISTS transactions
(
    id                     bigserial      not null unique,
    amount                 numeric(15, 2) not null,
    transaction_date       timestamp      not null,
    unique_identifier      uuid           not null,
    transaction_type       int            not null,
    internal               boolean        not null,
    related_transaction_id bigint,
    target_account_id      bigint         not null
    );
