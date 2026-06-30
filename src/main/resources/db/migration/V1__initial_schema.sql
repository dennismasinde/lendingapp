-- Enable UUID extension if needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Customers table
CREATE TABLE customers (
                           id BIGSERIAL PRIMARY KEY,
                           first_name VARCHAR(100) NOT NULL,
                           last_name VARCHAR(100) NOT NULL,
                           email VARCHAR(255) UNIQUE NOT NULL,
                           phone_number VARCHAR(20) UNIQUE,
                           date_of_birth DATE,
                           national_id VARCHAR(50) UNIQUE,
                           address VARCHAR(500),
                           city VARCHAR(100),
                           country VARCHAR(100),
                           postal_code VARCHAR(20),
                           employment_status VARCHAR(50) NOT NULL,
                           employer_name VARCHAR(200),
                           monthly_income DECIMAL(19,2),
                           credit_score DECIMAL(5,2),
                           is_active BOOLEAN DEFAULT TRUE,
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           created_by BIGINT,
                           updated_by BIGINT,
                           version BIGINT DEFAULT 0,
                           CONSTRAINT chk_credit_score CHECK (credit_score >= 0 AND credit_score <= 100)
);

-- Loan products table
CREATE TABLE loan_products (
                               id BIGSERIAL PRIMARY KEY,
                               name VARCHAR(200) UNIQUE NOT NULL,
                               description VARCHAR(1000),
                               min_amount DECIMAL(19,2) NOT NULL,
                               max_amount DECIMAL(19,2) NOT NULL,
                               interest_rate DECIMAL(5,2) NOT NULL,
                               min_tenure INTEGER NOT NULL,
                               max_tenure INTEGER NOT NULL,
                               tenure_type VARCHAR(20) NOT NULL,
                               is_active BOOLEAN DEFAULT TRUE,
                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               created_by BIGINT,
                               updated_by BIGINT,
                               version BIGINT DEFAULT 0,
                               CONSTRAINT chk_min_max_amount CHECK (min_amount <= max_amount),
                               CONSTRAINT chk_min_max_tenure CHECK (min_tenure <= max_tenure),
                               CONSTRAINT chk_interest_rate CHECK (interest_rate >= 0)
);

-- Fees table
CREATE TABLE fees (
                      id BIGSERIAL PRIMARY KEY,
                      loan_product_id BIGINT NOT NULL REFERENCES loan_products(id),
                      name VARCHAR(200) NOT NULL,
                      fee_type VARCHAR(50) NOT NULL,
                      calculation_type VARCHAR(50) NOT NULL,
                      amount DECIMAL(19,2) NOT NULL,
                      percentage DECIMAL(5,2),
                      days_after_due INTEGER,
                      application_timing VARCHAR(50),
                      is_active BOOLEAN DEFAULT TRUE,
                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      created_by BIGINT,
                      updated_by BIGINT,
                      version BIGINT DEFAULT 0,
                      CONSTRAINT chk_fee_amount CHECK (amount >= 0),
                      CONSTRAINT chk_fee_percentage CHECK (percentage >= 0 AND percentage <= 100)
);

-- Loans table
CREATE TABLE loans (
                       id BIGSERIAL PRIMARY KEY,
                       customer_id BIGINT NOT NULL REFERENCES customers(id),
                       product_id BIGINT NOT NULL REFERENCES loan_products(id),
                       principal_amount DECIMAL(19,2) NOT NULL,
                       total_amount DECIMAL(19,2) NOT NULL,
                       outstanding_balance DECIMAL(19,2) NOT NULL,
                       interest_rate DECIMAL(5,2) NOT NULL,
                       tenure INTEGER NOT NULL,
                       tenure_type VARCHAR(20) NOT NULL,
                       disbursement_date TIMESTAMP NOT NULL,
                       due_date TIMESTAMP,
                       status VARCHAR(20) NOT NULL,
                       loan_type VARCHAR(20) NOT NULL,
                       billing_cycle_type VARCHAR(20),
                       consolidated_due_date TIMESTAMP,
                       is_consolidated BOOLEAN DEFAULT FALSE,
                       consolidated_group_id VARCHAR(50),
                       closed_at TIMESTAMP,
                       cancelled_at TIMESTAMP,
                       written_off_at TIMESTAMP,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       created_by BIGINT,
                       updated_by BIGINT,
                       version BIGINT DEFAULT 0,
                       CONSTRAINT chk_principal_amount CHECK (principal_amount >= 0),
                       CONSTRAINT chk_total_amount CHECK (total_amount >= principal_amount),
                       CONSTRAINT chk_outstanding_balance CHECK (outstanding_balance >= 0)
);

-- Installments table
CREATE TABLE installments (
                              id BIGSERIAL PRIMARY KEY,
                              loan_id BIGINT NOT NULL REFERENCES loans(id),
                              installment_number INTEGER NOT NULL,
                              amount DECIMAL(19,2) NOT NULL,
                              principal_amount DECIMAL(19,2) NOT NULL,
                              interest_amount DECIMAL(19,2) NOT NULL,
                              due_date TIMESTAMP NOT NULL,
                              paid_date TIMESTAMP,
                              outstanding_balance DECIMAL(19,2) NOT NULL,
                              status VARCHAR(20) NOT NULL,
                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              created_by BIGINT,
                              updated_by BIGINT,
                              version BIGINT DEFAULT 0,
                              CONSTRAINT chk_installment_amount CHECK (amount >= 0),
                              CONSTRAINT chk_installment_principal CHECK (principal_amount >= 0),
                              CONSTRAINT chk_installment_interest CHECK (interest_amount >= 0)
);

-- Repayments table
CREATE TABLE repayments (
                            id BIGSERIAL PRIMARY KEY,
                            loan_id BIGINT NOT NULL REFERENCES loans(id),
                            customer_id BIGINT NOT NULL REFERENCES customers(id),
                            amount DECIMAL(19,2) NOT NULL,
                            repayment_date TIMESTAMP NOT NULL,
                            method VARCHAR(50) NOT NULL,
                            transaction_reference VARCHAR(100),
                            status VARCHAR(20) NOT NULL,
                            principal_amount DECIMAL(19,2) NOT NULL,
                            interest_amount DECIMAL(19,2) NOT NULL,
                            fee_amount DECIMAL(19,2) DEFAULT 0,
                            penalty_amount DECIMAL(19,2) DEFAULT 0,
                            installment_id BIGINT,
                            is_reversed BOOLEAN DEFAULT FALSE,
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            created_by BIGINT,
                            updated_by BIGINT,
                            version BIGINT DEFAULT 0,
                            CONSTRAINT chk_repayment_amount CHECK (amount >= 0)
);

-- Customer loan limits table
CREATE TABLE customer_loan_limits (
                                      id BIGSERIAL PRIMARY KEY,
                                      customer_id BIGINT NOT NULL UNIQUE REFERENCES customers(id),
                                      max_loan_amount DECIMAL(19,2) NOT NULL,
                                      total_outstanding_limit DECIMAL(19,2) NOT NULL,
                                      available_limit DECIMAL(19,2) NOT NULL,
                                      max_number_of_loans INTEGER NOT NULL,
                                      is_active BOOLEAN DEFAULT TRUE,
                                      last_review_date TIMESTAMP NOT NULL,
                                      risk_level VARCHAR(20),
                                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      created_by BIGINT,
                                      updated_by BIGINT,
                                      version BIGINT DEFAULT 0,
                                      CONSTRAINT chk_max_loan_amount CHECK (max_loan_amount >= 0),
                                      CONSTRAINT chk_total_outstanding_limit CHECK (total_outstanding_limit >= 0),
                                      CONSTRAINT chk_available_limit CHECK (available_limit >= 0)
);

-- Loan fees junction table
CREATE TABLE loan_fees (
                           id BIGSERIAL PRIMARY KEY,
                           loan_id BIGINT NOT NULL REFERENCES loans(id),
                           fee_id BIGINT NOT NULL REFERENCES fees(id),
                           amount DECIMAL(19,2) NOT NULL,
                           applied_date TIMESTAMP NOT NULL,
                           paid_date TIMESTAMP,
                           is_paid BOOLEAN DEFAULT FALSE,
                           application_timing VARCHAR(50) NOT NULL,
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           created_by BIGINT,
                           updated_by BIGINT,
                           version BIGINT DEFAULT 0,
                           CONSTRAINT chk_loan_fee_amount CHECK (amount >= 0)
);

-- Notifications table
CREATE TABLE notifications (
                               id BIGSERIAL PRIMARY KEY,
                               customer_id BIGINT NOT NULL REFERENCES customers(id),
                               subject VARCHAR(500) NOT NULL,
                               content TEXT NOT NULL,
                               type VARCHAR(50) NOT NULL,
                               channel VARCHAR(50) NOT NULL,
                               sent_at TIMESTAMP NOT NULL,
                               read_at TIMESTAMP,
                               is_sent BOOLEAN DEFAULT FALSE,
                               is_read BOOLEAN DEFAULT FALSE,
                               template_id VARCHAR(100),
                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               created_by BIGINT,
                               updated_by BIGINT,
                               version BIGINT DEFAULT 0
);

-- Notification variables table
CREATE TABLE notification_variables (
                                        notification_id BIGINT NOT NULL REFERENCES notifications(id) ON DELETE CASCADE,
                                        variable_key VARCHAR(100) NOT NULL,
                                        variable_value TEXT NOT NULL,
                                        PRIMARY KEY (notification_id, variable_key)
);

-- Create indexes for performance
CREATE INDEX idx_customers_email ON customers(email);
CREATE INDEX idx_customers_phone ON customers(phone_number);
CREATE INDEX idx_customers_credit_score ON customers(credit_score);
CREATE INDEX idx_loans_customer_id ON loans(customer_id);
CREATE INDEX idx_loans_product_id ON loans(product_id);
CREATE INDEX idx_loans_status ON loans(status);
CREATE INDEX idx_loans_due_date ON loans(due_date);
CREATE INDEX idx_loans_consolidated ON loans(consolidated_group_id);
CREATE INDEX idx_installments_loan_id ON installments(loan_id);
CREATE INDEX idx_installments_status ON installments(status);
CREATE INDEX idx_installments_due_date ON installments(due_date);
CREATE INDEX idx_repayments_loan_id ON repayments(loan_id);
CREATE INDEX idx_repayments_customer_id ON repayments(customer_id);
CREATE INDEX idx_repayments_status ON repayments(status);
CREATE INDEX idx_repayments_date ON repayments(repayment_date);
CREATE INDEX idx_notifications_customer_id ON notifications(customer_id);
CREATE INDEX idx_notifications_sent_at ON notifications(sent_at);
CREATE INDEX idx_notifications_is_sent ON notifications(is_sent);