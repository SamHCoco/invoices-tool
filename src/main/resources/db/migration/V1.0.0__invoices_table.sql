-- normalisation not required for intended use case
create table if not exists `fee_transaction` (
    `id` bigint unsigned auto_increment primary key,
    `company` varchar(255) not null,
    `date` date not null,
    `amount` decimal(7,2) not null,
    `type` varchar(100) not null,
    `invoice_serial_number` varchar(255),
    index `idx_fee_transaction_date` (`date`)
);