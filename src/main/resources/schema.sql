-- DROP TABLES
/*
DROP TABLE IF EXISTS abc_permission;
DROP TABLE IF EXISTS abc_user;
DROP TABLE IF EXISTS access_person;
DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS area;
DROP TABLE IF EXISTS area_common;
DROP TABLE IF EXISTS area_group;
DROP TABLE IF EXISTS area_group_common;
DROP TABLE IF EXISTS area_subsidiary;
DROP TABLE IF EXISTS auxiliary_account;
DROP TABLE IF EXISTS closing_sheet;
DROP TABLE IF EXISTS company;
DROP TABLE IF EXISTS confidential;
DROP TABLE IF EXISTS confidential_account;
DROP TABLE IF EXISTS debit_credit;
DROP TABLE IF EXISTS dicc_category;
DROP TABLE IF EXISTS entity;
DROP TABLE IF EXISTS exchange_money;
DROP TABLE IF EXISTS exchange_rate;
DROP TABLE IF EXISTS group_access_entity;
DROP TABLE IF EXISTS group_common;
DROP TABLE IF EXISTS group_entity;
DROP TABLE IF EXISTS group_role;
DROP TABLE IF EXISTS group_role_common;
DROP TABLE IF EXISTS invitation;
DROP TABLE IF EXISTS report;
DROP TABLE IF EXISTS report_file;
DROP TABLE IF EXISTS role_common;
DROP TABLE IF EXISTS role_entity;
DROP TABLE IF EXISTS subsidiary;
DROP TABLE IF EXISTS transaction;
DROP TABLE IF EXISTS transaction_account;
DROP TABLE IF EXISTS transaction_type;

*/

-- Table: abc_permission
CREATE TABLE IF NOT EXISTS abc_permission (
  abc_permission_id serial PRIMARY KEY,
  area_subsidiary_id int NOT NULL,
  user_id int NOT NULL,
  status boolean NOT NULL,
  dic_category int NOT NULL
);

-- Table: abc_user
CREATE TABLE IF NOT EXISTS abc_user (
    user_id serial PRIMARY KEY,
    access_person_id int NOT NULL,
    dicc_category varchar(255) NOT NULL,
    date_created date NOT NULL
    );

-- Table: access_person
CREATE TABLE IF NOT EXISTS access_person (
     access_person_id serial PRIMARY KEY,
     username varchar(255) NOT NULL,
    email varchar(255) NOT NULL,
    secret varchar(255) NOT NULL,
    address varchar(255) NOT NULL,
    no_fono varchar(255) NOT NULL,
    ext_no_fono varchar(50) NOT NULL,
    country_identity varchar(255) NOT NULL,
    no_identity varchar(255) NOT NULL,
    ext_no_identity varchar(50) NOT NULL,
    first_name varchar(255) NOT NULL,
    last_name varchar(255) NOT NULL,
    gender_person int NOT NULL,
    birthday date NOT NULL,
    dicc_category varchar(255) NOT NULL,
    date_creation date NOT NULL,
    user_uuid varchar(255) NOT NULL
    );

-- Table: account
CREATE TABLE IF NOT EXISTS account (
                                       account_id serial PRIMARY KEY,
                                       company_id int NOT NULL,
                                       account_account_id int NULL,
                                       code_account varchar(15) NOT NULL,
    name_account varchar(150) NOT NULL,
    clasificator boolean NOT NULL,
    "level" int NOT NULL,
    report boolean NOT NULL,
    status boolean NOT NULL,
    money_rub boolean NOT NULL
    );

-- Table: area
CREATE TABLE IF NOT EXISTS area (
                                    area_id serial PRIMARY KEY,
                                    company_id int NOT NULL,
                                    area_name varchar(255) NOT NULL,
    date_created date NOT NULL,
    status boolean NOT NULL,
    common_id int NULL
    );

-- Table: area_common
CREATE TABLE IF NOT EXISTS area_common (
                                           area_common_id int NOT NULL,
                                           area_name varchar(100) NOT NULL,
    dicc_category varchar(255) NOT NULL,
    PRIMARY KEY (area_common_id)
    );

-- Table: area_group
CREATE TABLE IF NOT EXISTS area_group (
                                          area_group_id int NOT NULL,
                                          area_subsidiary_id int NOT NULL,
                                          group_id int NOT NULL,
                                          dicc_category varchar(255) NOT NULL,
    status boolean NOT NULL,
    date_created date NOT NULL,
    PRIMARY KEY (area_group_id)
    );

-- Table: area_group_common
CREATE TABLE IF NOT EXISTS area_group_common (
                                                 area_group_common_id int NOT NULL,
                                                 area_common_id int NOT NULL,
                                                 group_common_id int NOT NULL,
                                                 status boolean NOT NULL,
                                                 date_created int NOT NULL,
                                                 PRIMARY KEY (area_group_common_id)
    );

-- Table: area_subsidiary
CREATE TABLE IF NOT EXISTS area_subsidiary (
                                               area_subsidiary_id serial PRIMARY KEY,
                                               subsidiary_id int NOT NULL,
                                               area_id int NOT NULL,
                                               dicc_category varchar(255) NOT NULL,
    date_created date NOT NULL
    );

-- Table: auxiliary_account
CREATE TABLE IF NOT EXISTS auxiliary_account (
                                                 auxiliary_account_id serial PRIMARY KEY,
                                                 code_account varchar(15) NOT NULL,
    name_description varchar(150) NOT NULL
    );

-- Table: closing_sheet
CREATE TABLE IF NOT EXISTS closing_sheet (
                                             balance_sheet_id serial PRIMARY KEY,
                                             company_id int NOT NULL,
                                             user_user_id int NOT NULL,
                                             description Varchar(30) NOT NULL,
    date timestamp NOT NULL
    );

-- Table: company
CREATE TABLE IF NOT EXISTS company (
                                       company_id serial PRIMARY KEY,
                                       company_name varchar(255) NOT NULL,
    dicc_category varchar(255) NOT NULL,
    nit varchar(20) NULL,
    address varchar(20) NOT NULL,
    logo_uuid bytea NULL,
    opening_date date NOT NULL,
    deadline date NOT NULL,
    contact_email Varchar(100) NOT NULL,
    contact_name Varchar(100) NOT NULL
    );

-- Table: confidential
CREATE TABLE IF NOT EXISTS confidential (
                                            confidential_id serial PRIMARY KEY,
                                            company_id int NOT NULL,
                                            code_account varchar(15) NOT NULL,
    name_description varchar(150) NOT NULL,
    clasificator boolean NOT NULL,
    "level" int NOT NULL,
    report boolean NOT NULL,
    status boolean NOT NULL,
    money_rub boolean NOT NULL
    );

-- Table: confidential_account
CREATE TABLE IF NOT EXISTS confidential_account (
                                                    confidential_account_id serial PRIMARY KEY,
                                                    Confidential_confidential_id int NOT NULL,
                                                    account_account_id int NOT NULL
);

-- Table: debit_credit
CREATE TABLE IF NOT EXISTS debit_credit (
                                            debit_credit_id serial PRIMARY KEY,
                                            transaction_account_id int NOT NULL,
                                            amount_credit decimal(12,2) NOT NULL,
    amount_debit decimal(12,2) NOT NULL,
    exchange_rate_id int NOT NULL
    );

-- Table: dicc_category
CREATE TABLE IF NOT EXISTS dicc_category (
    dic_category_id varchar(255) NOT NULL,
    detail varchar(500) NOT NULL,
    date_created date NOT NULL,
    status boolean NOT NULL,
    PRIMARY KEY (dic_category_id)
    );

-- Table: entity
CREATE TABLE IF NOT EXISTS entity (
                                      entity_id serial PRIMARY KEY,
                                      company_company_id int NOT NULL,
                                      entity_name varchar(50) NOT NULL,
    nit Varchar(50) NULL,
    social_reason Varchar(100) NOT NULL,
    "foreign" boolean NOT NULL
    );

-- Table: exchange_money
CREATE TABLE IF NOT EXISTS exchange_money (
                                              exchange_money_id serial PRIMARY KEY,
                                              company_id int NOT NULL,
                                              money_name Varchar(30) NOT NULL,
    abbreviation_name Varchar(5) NOT NULL,
    is_principal boolean NOT NULL
    );

-- Table: exchange_rate
CREATE TABLE IF NOT EXISTS exchange_rate (
                                             exchange_rate_id serial PRIMARY KEY,
                                             money_name varchar(50) NOT NULL,
    company_id int NOT NULL,
    currency decimal(12,2) NOT NULL,
    abbreviation_name int NOT NULL,
    date timestamp NOT NULL
    );

-- Table: group_access_entity
CREATE TABLE IF NOT EXISTS group_access_entity (
                                                   group_access_entity_id serial PRIMARY KEY,
                                                   group_id int NOT NULL,
                                                   date_created date NOT NULL,
                                                   abc_permission_id int NOT NULL
);

-- Table: group_common
CREATE TABLE IF NOT EXISTS group_common (
                                            group_common_id serial PRIMARY KEY,
                                            name varchar(255) NOT NULL,
    description varchar(500) NOT NULL,
    dicc_category varchar(255) NOT NULL
    );

-- Table: group_entity
CREATE TABLE IF NOT EXISTS group_entity (
                                            group_id serial PRIMARY KEY,
                                            name varchar(255) NOT NULL,
    description varchar(500) NOT NULL,
    dicc_category varchar(255) NOT NULL,
    status boolean NOT NULL,
    date_created date NOT NULL,
    common_id int NULL
    );

-- Table: group_role
CREATE TABLE IF NOT EXISTS group_role (
                                          group_role_id serial PRIMARY KEY,
                                          role_id int NOT NULL,
                                          group_id int NOT NULL,
                                          status boolean NOT NULL,
                                          date_created date NOT NULL
);

-- Table: group_role_common
CREATE TABLE IF NOT EXISTS group_role_common (
                                                 group_role_common_id int NOT NULL,
                                                 group_common_id int NOT NULL,
                                                 role_common_id int NOT NULL,
                                                 status boolean NOT NULL,
                                                 date_created date NOT NULL,
                                                 PRIMARY KEY (group_role_common_id)
    );

-- Table: invitation
CREATE TABLE IF NOT EXISTS invitation (
                                          invitation_id serial PRIMARY KEY,
                                          user_user_id int NOT NULL,
                                          company_company_id int NOT NULL,
                                          invitation_status Varchar(10) NOT NULL,
    status boolean NOT NULL
    );

-- Table: report
CREATE TABLE IF NOT EXISTS report (
                                      report_id int NOT NULL,
                                      date timestamp NOT NULL,
                                      user_user_id int NOT NULL,
                                      transaction_type int NOT NULL,
                                      PRIMARY KEY (report_id)
    );

-- Table: report_file
CREATE TABLE IF NOT EXISTS report_file (
                                           report_file_id serial PRIMARY KEY,
                                           report_id int NOT NULL,
                                           url Varchar(1000) NOT NULL,
    mime_type Varchar(30) NOT NULL,
    size Varchar(30) NOT NULL,
    date timestamp NOT NULL
    );

-- Table: role_common
CREATE TABLE IF NOT EXISTS role_common (
                                           role_common_id int NOT NULL,
                                           name varchar(200) NOT NULL,
    description varchar(500) NOT NULL,
    dicc_category varchar(255) NOT NULL,
    status boolean NOT NULL,
    date_created date NOT NULL,
    PRIMARY KEY (role_common_id)
    );

-- Table: role_entity
CREATE TABLE IF NOT EXISTS role_entity (
                                           role_id serial NOT NULL,
                                           name varchar(200) NOT NULL,
    description varchar(500) NOT NULL,
    dicc_category varchar(255) NOT NULL,
    status boolean NOT NULL,
    date_created date NOT NULL,
    common_id int NULL,
    PRIMARY KEY (role_id)
    );

-- Table: subsidiary
CREATE TABLE IF NOT EXISTS subsidiary (
                                          subsidiary_id serial PRIMARY KEY,
                                          company_id int NOT NULL,
                                          subsidiary_name varchar(255) NOT NULL,
    address varchar(255) NOT NULL
    );

-- Table: transaction
CREATE TABLE IF NOT EXISTS transaction (
                                           transaction_id serial PRIMARY KEY,
                                           transaction_type_id int NOT NULL,
                                           transaction_number int NOT NULL,
                                           glosa_general varchar(255) NOT NULL,
    date timestamp NOT NULL,
    exchange_rate_id int NOT NULL
    );

-- Table: transaction_account
CREATE TABLE IF NOT EXISTS transaction_account (
                                                   transaction_account_id serial PRIMARY KEY,
                                                   user_id int NOT NULL,
                                                   entity_id int NOT NULL,
                                                   transaction_id int NOT NULL,
                                                   account_id int NOT NULL,
                                                   auxiliary_account_id int NULL,
                                                   glosa_detail varchar(500) NULL,
    "check" varchar(20) NULL,
    reference varchar(80) NULL,
    due_date date NULL,
    company_company_id int NOT NULL
    );

-- Table: transaction_type
CREATE TABLE IF NOT EXISTS transaction_type (
                                                transaction_type_id serial PRIMARY KEY,
                                                company_id int NOT NULL,
                                                type varchar(50) NOT NULL,
    description varchar(255) NOT NULL
    );
