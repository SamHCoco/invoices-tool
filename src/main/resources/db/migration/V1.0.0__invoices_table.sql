create table if not exists `fee_transaction` (
    `id` bigint unsigned auto_increment primary key,
    `company` varchar(255) not null,
    `date` date not null,
    `amount` decimal(19,4) not null,
    `type` varchar(100) not null,
    `invoice_serial_number` varchar(255)
);