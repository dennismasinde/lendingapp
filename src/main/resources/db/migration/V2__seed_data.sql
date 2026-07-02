-- ============================================
-- SEED DATA FOR LENDING APPLICATION
-- ============================================

-- ============================================
-- 1. CUSTOMERS
-- ============================================

INSERT INTO customers (
    first_name, last_name, email, phone_number, date_of_birth,
    national_id, address, city, country, postal_code,
    employment_status, employer_name, monthly_income, credit_score,
    is_active, created_at, updated_at, version
) VALUES
-- Customer 1: High-income employed customer
(
    'John', 'Mbugua', 'john.mbugua@email.com', '+254712345678',
    '1985-03-15', 'ID12345678', '123 Kenyatta Avenue, Nairobi',
    'Nairobi', 'Kenya', '00100',
    'EMPLOYED', 'Safaricom PLC', 250000.00, 85.50,
    true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
),
-- Customer 2: Self-employed business owner
(
    'Mary', 'Wanjiru', 'mary.wanjiru@email.com', '+254723456789',
    '1990-07-22', 'ID87654321', '456 Moi Avenue, Mombasa',
    'Mombasa', 'Kenya', '80100',
    'SELF_EMPLOYED', 'Wanjiru Enterprises', 180000.00, 72.00,
    true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
),
-- Customer 3: Young professional
(
    'Peter', 'Ochieng', 'peter.ochieng@email.com', '+254734567890',
    '1995-11-05', 'ID11223344', '789 Kisumu Road, Kisumu',
    'Kisumu', 'Kenya', '40100',
    'EMPLOYED', 'KCB Bank', 120000.00, 68.50,
    true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
),
-- Customer 4: Business owner with high credit
(
    'Grace', 'Akinyi', 'grace.akinyi@email.com', '+254745678901',
    '1988-09-30', 'ID55667788', '101 Thika Road, Nairobi',
    'Nairobi', 'Kenya', '00100',
    'SELF_EMPLOYED', 'Akinyi Holdings', 450000.00, 92.00,
    true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
),
-- Customer 5: Recently employed graduate
(
    'David', 'Mwangi', 'david.mwangi@email.com', '+254756789012',
    '1998-02-14', 'ID99887766', '202 Nakuru Highway, Nakuru',
    'Nakuru', 'Kenya', '20100',
    'EMPLOYED', 'Equity Bank', 85000.00, 55.00,
    true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
),
-- Customer 6: Retired civil servant
(
    'Sarah', 'Kiprop', 'sarah.kiprop@email.com', '+254767890123',
    '1965-06-25', 'ID44332211', '303 Eldoret Road, Eldoret',
    'Eldoret', 'Kenya', '30100',
    'RETIRED', 'Government of Kenya', 95000.00, 78.00,
    true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
),
-- Customer 7: Inactive customer
(
    'Michael', 'Otieno', 'michael.otieno@email.com', '+254778901234',
    '1992-12-01', 'ID66778899', '404 Nyeri Road, Nyeri',
    'Nyeri', 'Kenya', '10100',
    'UNEMPLOYED', 'N/A', 0.00, 35.00,
    false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
),
-- Customer 8: High-net-worth individual
(
    'Elizabeth', 'Njeri', 'elizabeth.njeri@email.com', '+254789012345',
    '1983-04-18', 'ID22446688', '505 Runda Estate, Nairobi',
    'Nairobi', 'Kenya', '00100',
    'EMPLOYED', 'Kenya Airways', 350000.00, 95.00,
    true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
),
-- Customer 9: Small business owner
(
    'James', 'Kariuki', 'james.kariuki@email.com', '+254790123456',
    '1987-08-10', 'ID33557799', '606 Meru Road, Meru',
    'Meru', 'Kenya', '60200',
    'SELF_EMPLOYED', 'Kariuki General Store', 65000.00, 62.00,
    true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
),
-- Customer 10: Tech entrepreneur
(
    'Margaret', 'Wambui', 'margaret.wambui@email.com', '+254801234567',
    '1991-10-29', 'ID11223355', '707 Westlands, Nairobi',
    'Nairobi', 'Kenya', '00100',
    'EMPLOYED', 'Microsoft Kenya', 320000.00, 88.00,
    true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
);

-- ============================================
-- 2. CUSTOMER LOAN LIMITS
-- ============================================

INSERT INTO customer_loan_limits (
    customer_id, max_loan_amount, total_outstanding_limit, available_limit,
    max_number_of_loans, is_active, last_review_date, risk_level,
    created_at, updated_at, version
) VALUES
      (1, 500000.00, 300000.00, 200000.00, 3, true, CURRENT_TIMESTAMP, 'LOW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
      (2, 300000.00, 150000.00, 150000.00, 2, true, CURRENT_TIMESTAMP, 'MEDIUM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
      (3, 200000.00, 100000.00, 100000.00, 2, true, CURRENT_TIMESTAMP, 'HIGH', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
      (4, 1000000.00, 500000.00, 500000.00, 5, true, CURRENT_TIMESTAMP, 'LOW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
      (5, 150000.00, 0.00, 150000.00, 1, true, CURRENT_TIMESTAMP, 'HIGH', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
      (6, 200000.00, 0.00, 200000.00, 2, true, CURRENT_TIMESTAMP, 'MEDIUM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
      (7, 50000.00, 0.00, 50000.00, 1, false, CURRENT_TIMESTAMP, 'HIGH', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
      (8, 800000.00, 200000.00, 600000.00, 4, true, CURRENT_TIMESTAMP, 'LOW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
      (9, 100000.00, 0.00, 100000.00, 1, true, CURRENT_TIMESTAMP, 'HIGH', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
      (10, 600000.00, 250000.00, 350000.00, 3, true, CURRENT_TIMESTAMP, 'LOW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

-- ============================================
-- 3. LOAN PRODUCTS
-- ============================================

INSERT INTO loan_products (
    name, description, min_amount, max_amount, interest_rate,
    min_tenure, max_tenure, tenure_type, is_active,
    created_at, updated_at, version
) VALUES
      (
          'Personal Loan', 'Short-term personal loan for individuals',
          10000.00, 500000.00, 12.50, 1, 12, 'MONTHS', true,
          CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
      ),
      (
          'Business Loan', 'Long-term financing for businesses',
          500000.00, 5000000.00, 10.00, 6, 60, 'MONTHS', true,
          CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
      ),
      (
          'Emergency Loan', 'Quick emergency loans with fast approval',
          1000.00, 50000.00, 18.00, 7, 30, 'DAYS', true,
          CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
      ),
      (
          'Education Loan', 'Student loans for education financing',
          5000.00, 200000.00, 8.50, 3, 24, 'MONTHS', true,
          CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
      ),
      (
          'SME Loan', 'Small and medium enterprise financing',
          100000.00, 2000000.00, 11.00, 3, 36, 'MONTHS', true,
          CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
      ),
      (
          'Mortgage Loan', 'Home loan for property purchase',
          1000000.00, 20000000.00, 9.50, 60, 360, 'MONTHS', false,
          CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
      ),
      (
          'Car Loan', 'Vehicle financing loan',
          200000.00, 3000000.00, 10.50, 12, 60, 'MONTHS', true,
          CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
      ),
      (
          'Salary Advance', 'Short-term salary advance',
          5000.00, 100000.00, 5.00, 1, 1, 'MONTHS', true,
          CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
      );

-- ============================================
-- 4. FEES
-- ============================================

INSERT INTO fees (
    loan_product_id, name, fee_type, calculation_type,
    amount, percentage, days_after_due, application_timing,
    is_active, created_at, updated_at
) VALUES
-- Personal Loan Fees
(1, 'Processing Fee', 'SERVICE', 'PERCENTAGE', 0.00, 2.50, NULL, 'ORIGINATION', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Late Payment Fee', 'LATE', 'FIXED', 1000.00, 0.00, 5, 'OVERDUE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Daily Late Fee', 'LATE', 'FIXED', 150.00, 0.00, 3, 'OVERDUE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Early Repayment Fee', 'EARLY_REPAYMENT', 'PERCENTAGE', 0.00, 2.00, NULL, 'POST_DISBURSEMENT', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Business Loan Fees
(2, 'Origination Fee', 'SERVICE', 'PERCENTAGE', 0.00, 1.50, NULL, 'ORIGINATION', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Late Payment Penalty', 'LATE', 'PERCENTAGE', 0.00, 5.00, 7, 'OVERDUE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Processing Fee', 'PROCESSING', 'FIXED', 5000.00, 0.00, NULL, 'ORIGINATION', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Daily Interest Late Fee', 'DAILY', 'PERCENTAGE', 0.00, 0.50, 10, 'OVERDUE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Emergency Loan Fees
(3, 'Emergency Processing Fee', 'SERVICE', 'FIXED', 500.00, 0.00, NULL, 'ORIGINATION', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Late Fee', 'LATE', 'FIXED', 200.00, 0.00, 3, 'OVERDUE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Education Loan Fees
(4, 'Administration Fee', 'SERVICE', 'PERCENTAGE', 0.00, 1.00, NULL, 'ORIGINATION', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Late Payment Fee', 'LATE', 'FIXED', 500.00, 0.00, 10, 'OVERDUE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- SME Loan Fees
(5, 'SME Processing Fee', 'PROCESSING', 'PERCENTAGE', 0.00, 2.00, NULL, 'ORIGINATION', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'SME Late Fee', 'LATE', 'FIXED', 2000.00, 0.00, 7, 'OVERDUE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Daily Overdue Fee', 'DAILY', 'FIXED', 500.00, 0.00, 5, 'OVERDUE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Car Loan Fees
(7, 'Car Loan Processing Fee', 'PROCESSING', 'FIXED', 5000.00, 0.00, NULL, 'ORIGINATION', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 'Car Loan Late Fee', 'LATE', 'PERCENTAGE', 0.00, 3.00, 5, 'OVERDUE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Salary Advance Fees
(8, 'Salary Advance Fee', 'SERVICE', 'FIXED', 100.00, 0.00, NULL, 'ORIGINATION', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 'Salary Advance Late Fee', 'LATE', 'FIXED', 200.00, 0.00, 1, 'OVERDUE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ============================================
-- 5. LOANS (With different statuses and types)
-- ============================================

-- Customer 1: John Mbugua - Personal Loan (OPEN - Installment)
INSERT INTO loans (
    customer_id, product_id, principal_amount, total_amount, outstanding_balance,
    interest_rate, tenure, tenure_type, disbursement_date, due_date,
    status, loan_type, billing_cycle_type, is_consolidated,
    created_at, updated_at, version
) VALUES (
             1, 1, 200000.00, 225000.00, 180000.00,
             12.50, 12, 'MONTHS', CURRENT_TIMESTAMP - INTERVAL '3 months',
             CURRENT_TIMESTAMP + INTERVAL '9 months',
             'OPEN', 'INSTALLMENT', 'INDIVIDUAL', false,
             CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
         );

-- Customer 1: John Mbugua - Emergency Loan (OPEN - Lump Sum)
INSERT INTO loans (
    customer_id, product_id, principal_amount, total_amount, outstanding_balance,
    interest_rate, tenure, tenure_type, disbursement_date, due_date,
    status, loan_type, billing_cycle_type, is_consolidated,
    created_at, updated_at, version
) VALUES (
             1, 3, 30000.00, 34000.00, 34000.00,
             18.00, 14, 'DAYS', CURRENT_TIMESTAMP - INTERVAL '2 days',
             CURRENT_TIMESTAMP + INTERVAL '12 days',
             'OPEN', 'LUMP_SUM', 'INDIVIDUAL', false,
             CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
         );

-- Customer 2: Mary Wanjiru - Business Loan (OVERDUE)
INSERT INTO loans (
    customer_id, product_id, principal_amount, total_amount, outstanding_balance,
    interest_rate, tenure, tenure_type, disbursement_date, due_date,
    status, loan_type, billing_cycle_type, is_consolidated,
    created_at, updated_at, version
) VALUES (
             2, 2, 150000.00, 165000.00, 165000.00,
             10.00, 6, 'MONTHS', CURRENT_TIMESTAMP - INTERVAL '5 months',
             CURRENT_TIMESTAMP - INTERVAL '2 days',
             'OVERDUE', 'LUMP_SUM', 'INDIVIDUAL', false,
             CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
         );

-- Customer 3: Peter Ochieng - Education Loan (OPEN - Installment)
INSERT INTO loans (
    customer_id, product_id, principal_amount, total_amount, outstanding_balance,
    interest_rate, tenure, tenure_type, disbursement_date, due_date,
    status, loan_type, billing_cycle_type, is_consolidated,
    created_at, updated_at, version
) VALUES (
             3, 4, 100000.00, 108500.00, 85000.00,
             8.50, 24, 'MONTHS', CURRENT_TIMESTAMP - INTERVAL '6 months',
             CURRENT_TIMESTAMP + INTERVAL '18 months',
             'OPEN', 'INSTALLMENT', 'INDIVIDUAL', false,
             CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
         );

-- Customer 4: Grace Akinyi - SME Loan (OPEN - Installment) - FIXED
INSERT INTO loans (
    customer_id, product_id, principal_amount, total_amount, outstanding_balance,
    interest_rate, tenure, tenure_type, disbursement_date, due_date,
    status, loan_type, billing_cycle_type, is_consolidated, consolidated_group_id,
    created_at, updated_at, version
) VALUES (
             4, 5, 500000.00, 555000.00, 400000.00,
             11.00, 12, 'MONTHS', CURRENT_TIMESTAMP - INTERVAL '4 months',
             CURRENT_TIMESTAMP + INTERVAL '8 months',
             'OPEN', 'INSTALLMENT', 'CONSOLIDATED', true, 'GRP001',
             CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
         );

-- Customer 4: Grace Akinyi - Consolidated Loan 2 (OPEN - Lump Sum) - FIXED
INSERT INTO loans (
    customer_id, product_id, principal_amount, total_amount, outstanding_balance,
    interest_rate, tenure, tenure_type, disbursement_date, due_date,
    status, loan_type, billing_cycle_type, is_consolidated, consolidated_group_id,
    created_at, updated_at, version
) VALUES (
             4, 7, 200000.00, 221000.00, 221000.00,
             10.50, 24, 'MONTHS', CURRENT_TIMESTAMP - INTERVAL '2 months',
             CURRENT_TIMESTAMP + INTERVAL '22 months',
             'OPEN', 'LUMP_SUM', 'CONSOLIDATED', true, 'GRP001',
             CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
         );

-- Customer 6: Sarah Kiprop - Personal Loan (PENDING)
INSERT INTO loans (
    customer_id, product_id, principal_amount, total_amount, outstanding_balance,
    interest_rate, tenure, tenure_type, disbursement_date, due_date,
    status, loan_type, billing_cycle_type, is_consolidated,
    created_at, updated_at, version
) VALUES (
             6, 1, 100000.00, 112500.00, 112500.00,
             12.50, 12, 'MONTHS', CURRENT_TIMESTAMP + INTERVAL '1 day',
             CURRENT_TIMESTAMP + INTERVAL '1 year',
             'PENDING', 'LUMP_SUM', 'INDIVIDUAL', false,
             CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
         );

-- Customer 8: Elizabeth Njeri - Personal Loan (CLOSED)
INSERT INTO loans (
    customer_id, product_id, principal_amount, total_amount, outstanding_balance,
    interest_rate, tenure, tenure_type, disbursement_date, due_date,
    status, loan_type, billing_cycle_type, is_consolidated,
    closed_at, created_at, updated_at, version
) VALUES (
             8, 1, 150000.00, 168750.00, 0.00,
             12.50, 6, 'MONTHS', CURRENT_TIMESTAMP - INTERVAL '8 months',
             CURRENT_TIMESTAMP - INTERVAL '2 months',
             'CLOSED', 'LUMP_SUM', 'INDIVIDUAL', false,
             CURRENT_TIMESTAMP - INTERVAL '2 months',
             CURRENT_TIMESTAMP - INTERVAL '8 months',
             CURRENT_TIMESTAMP - INTERVAL '2 months', 0
         );

-- Customer 10: Margaret Wambui - Car Loan (APPROVED)
INSERT INTO loans (
    customer_id, product_id, principal_amount, total_amount, outstanding_balance,
    interest_rate, tenure, tenure_type, disbursement_date, due_date,
    status, loan_type, billing_cycle_type, is_consolidated,
    created_at, updated_at, version
) VALUES (
             10, 7, 250000.00, 276250.00, 276250.00,
             10.50, 24, 'MONTHS', CURRENT_TIMESTAMP + INTERVAL '2 days',
             CURRENT_TIMESTAMP + INTERVAL '24 months',
             'APPROVED', 'LUMP_SUM', 'INDIVIDUAL', false,
             CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
         );

-- Customer 5: David Mwangi - Emergency Loan (CANCELLED)
INSERT INTO loans (
    customer_id, product_id, principal_amount, total_amount, outstanding_balance,
    interest_rate, tenure, tenure_type, disbursement_date, due_date,
    status, loan_type, billing_cycle_type, is_consolidated,
    cancelled_at, created_at, updated_at, version
) VALUES (
             5, 3, 10000.00, 11300.00, 11300.00,
             18.00, 15, 'DAYS', NULL,
             CURRENT_TIMESTAMP + INTERVAL '15 days',
             'CANCELLED', 'LUMP_SUM', 'INDIVIDUAL', false,
             CURRENT_TIMESTAMP,
             CURRENT_TIMESTAMP - INTERVAL '5 days',
             CURRENT_TIMESTAMP, 0
         );

-- ============================================
-- 6. INSTALLMENTS
-- ============================================

-- Installments for Loan 1 (John Mbugua - Personal Loan - Installment)
DO $$
DECLARE
loan_id_val BIGINT := 1;
    installment_amt DECIMAL := 18750.00;
    principal_amt DECIMAL := 16667.00;
    interest_amt DECIMAL := 2083.00;
    i INTEGER;
    due_date_val TIMESTAMP;
BEGIN
FOR i IN 1..12 LOOP
        due_date_val := CURRENT_TIMESTAMP + (i * INTERVAL '1 month');
INSERT INTO installments (
    loan_id, installment_number, amount, principal_amount, interest_amount,
    due_date, outstanding_balance, status, created_at, updated_at
) VALUES (
             loan_id_val, i, installment_amt, principal_amt, interest_amt,
             due_date_val, installment_amt, 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
         );
END LOOP;
END $$;

-- Installments for Loan 4 (Peter Ochieng - Education Loan - Installment)
DO $$
DECLARE
loan_id_val BIGINT := 4;
    installment_amt DECIMAL := 4521.00;
    principal_amt DECIMAL := 4167.00;
    interest_amt DECIMAL := 354.00;
    i INTEGER;
    due_date_val TIMESTAMP;
BEGIN
FOR i IN 1..24 LOOP
        due_date_val := CURRENT_TIMESTAMP - INTERVAL '6 months' + (i * INTERVAL '1 month');
        -- Mark first 6 installments as paid
        IF i <= 6 THEN
            INSERT INTO installments (
                loan_id, installment_number, amount, principal_amount, interest_amount,
                due_date, paid_date, outstanding_balance, status, created_at, updated_at
            ) VALUES (
                loan_id_val, i, installment_amt, principal_amt, interest_amt,
                due_date_val, due_date_val + INTERVAL '2 days', 0.00, 'PAID',
                CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
            );
ELSE
            INSERT INTO installments (
                loan_id, installment_number, amount, principal_amount, interest_amount,
                due_date, outstanding_balance, status, created_at, updated_at
            ) VALUES (
                loan_id_val, i, installment_amt, principal_amt, interest_amt,
                due_date_val, installment_amt, 'PENDING',
                CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
            );
END IF;
END LOOP;
END $$;

-- Installments for Loan 5 (Grace Akinyi - SME Loan - Installment)
DO $$
DECLARE
loan_id_val BIGINT := 5;
    installment_amt DECIMAL := 46250.00;
    principal_amt DECIMAL := 41667.00;
    interest_amt DECIMAL := 4583.00;
    i INTEGER;
    due_date_val TIMESTAMP;
BEGIN
FOR i IN 1..12 LOOP
        due_date_val := CURRENT_TIMESTAMP - INTERVAL '4 months' + (i * INTERVAL '1 month');
        IF i <= 2 THEN
            -- First 2 installments paid
            INSERT INTO installments (
                loan_id, installment_number, amount, principal_amount, interest_amount,
                due_date, paid_date, outstanding_balance, status, created_at, updated_at
            ) VALUES (
                loan_id_val, i, installment_amt, principal_amt, interest_amt,
                due_date_val, due_date_val + INTERVAL '3 days', 0.00, 'PAID',
                CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
            );
ELSE
            INSERT INTO installments (
                loan_id, installment_number, amount, principal_amount, interest_amount,
                due_date, outstanding_balance, status, created_at, updated_at
            ) VALUES (
                loan_id_val, i, installment_amt, principal_amt, interest_amt,
                due_date_val, installment_amt, 'PENDING',
                CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
            );
END IF;
END LOOP;
END $$;

-- ============================================
-- 7. REPAYMENTS
-- ============================================

-- Repayments for Loan 1 (John Mbugua - Personal Loan)
INSERT INTO repayments (
    loan_id, customer_id, amount, repayment_date, method,
    transaction_reference, status, principal_amount, interest_amount,
    fee_amount, penalty_amount, is_reversed, created_at, updated_at
) VALUES
      (1, 1, 20000.00, CURRENT_TIMESTAMP - INTERVAL '2 months', 'BANK_TRANSFER', 'TXN1001', 'COMPLETED', 18000.00, 2000.00, 0.00, 0.00, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (1, 1, 15000.00, CURRENT_TIMESTAMP - INTERVAL '1 month', 'MOBILE_MONEY', 'TXN1002', 'COMPLETED', 13500.00, 1500.00, 0.00, 0.00, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Repayments for Loan 2 (Mary Wanjiru - Business Loan - Overdue)
INSERT INTO repayments (
    loan_id, customer_id, amount, repayment_date, method,
    transaction_reference, status, principal_amount, interest_amount,
    fee_amount, penalty_amount, is_reversed, created_at, updated_at
) VALUES
    (2, 2, 30000.00, CURRENT_TIMESTAMP - INTERVAL '4 months', 'BANK_TRANSFER', 'TXN2001', 'COMPLETED', 28000.00, 2000.00, 0.00, 0.00, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Repayments for Loan 4 (Peter Ochieng - Education Loan)
INSERT INTO repayments (
    loan_id, customer_id, amount, repayment_date, method,
    transaction_reference, status, principal_amount, interest_amount,
    fee_amount, penalty_amount, is_reversed, created_at, updated_at
) VALUES
      (4, 3, 4500.00, CURRENT_TIMESTAMP - INTERVAL '5 months', 'CARD', 'TXN4001', 'COMPLETED', 4000.00, 500.00, 0.00, 0.00, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (4, 3, 4500.00, CURRENT_TIMESTAMP - INTERVAL '4 months', 'CARD', 'TXN4002', 'COMPLETED', 4000.00, 500.00, 0.00, 0.00, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (4, 3, 4500.00, CURRENT_TIMESTAMP - INTERVAL '3 months', 'CARD', 'TXN4003', 'COMPLETED', 4000.00, 500.00, 0.00, 0.00, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (4, 3, 4500.00, CURRENT_TIMESTAMP - INTERVAL '2 months', 'CARD', 'TXN4004', 'COMPLETED', 4000.00, 500.00, 0.00, 0.00, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (4, 3, 4500.00, CURRENT_TIMESTAMP - INTERVAL '1 month', 'CARD', 'TXN4005', 'COMPLETED', 4000.00, 500.00, 0.00, 0.00, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Repayments for Loan 8 (Elizabeth Njeri - Closed Loan)
INSERT INTO repayments (
    loan_id, customer_id, amount, repayment_date, method,
    transaction_reference, status, principal_amount, interest_amount,
    fee_amount, penalty_amount, is_reversed, created_at, updated_at
) VALUES
      (8, 8, 25000.00, CURRENT_TIMESTAMP - INTERVAL '7 months', 'BANK_TRANSFER', 'TXN8001', 'COMPLETED', 23000.00, 2000.00, 0.00, 0.00, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (8, 8, 25000.00, CURRENT_TIMESTAMP - INTERVAL '6 months', 'BANK_TRANSFER', 'TXN8002', 'COMPLETED', 23000.00, 2000.00, 0.00, 0.00, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (8, 8, 25000.00, CURRENT_TIMESTAMP - INTERVAL '5 months', 'BANK_TRANSFER', 'TXN8003', 'COMPLETED', 23000.00, 2000.00, 0.00, 0.00, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (8, 8, 25000.00, CURRENT_TIMESTAMP - INTERVAL '4 months', 'BANK_TRANSFER', 'TXN8004', 'COMPLETED', 23000.00, 2000.00, 0.00, 0.00, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (8, 8, 25000.00, CURRENT_TIMESTAMP - INTERVAL '3 months', 'BANK_TRANSFER', 'TXN8005', 'COMPLETED', 23000.00, 2000.00, 0.00, 0.00, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (8, 8, 25000.00, CURRENT_TIMESTAMP - INTERVAL '2 months', 'BANK_TRANSFER', 'TXN8006', 'COMPLETED', 23000.00, 2000.00, 0.00, 0.00, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ============================================
-- 8. NOTIFICATIONS
-- ============================================

INSERT INTO notifications (
    customer_id, subject, content, type, channel,
    sent_at, is_sent, is_read, template_id,
    created_at, updated_at
) VALUES
      (1, 'Loan Approved', 'Your loan of KES 200,000 has been approved', 'LOAN_APPROVED', 'EMAIL',
       CURRENT_TIMESTAMP - INTERVAL '3 months', true, true, 'LOAN_APPROVED_TEMPLATE',
       CURRENT_TIMESTAMP - INTERVAL '3 months', CURRENT_TIMESTAMP - INTERVAL '3 months'),

      (1, 'Payment Reminder', 'Your payment of KES 18,750 is due on ' || (CURRENT_TIMESTAMP + INTERVAL '1 month')::date, 'REPAYMENT_REMINDER', 'SMS',
       CURRENT_TIMESTAMP - INTERVAL '5 days', true, false, 'PAYMENT_REMINDER_TEMPLATE',
       CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '5 days'),

      (2, 'Overdue Notice', 'Your loan payment is overdue. Please make payment immediately.', 'OVERDUE_NOTICE', 'EMAIL',
       CURRENT_TIMESTAMP - INTERVAL '2 days', true, false, 'OVERDUE_TEMPLATE',
       CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days'),

      (3, 'Payment Received', 'Your payment of KES 4,500 has been received', 'PAYMENT_ACKNOWLEDGMENT', 'EMAIL',
       CURRENT_TIMESTAMP - INTERVAL '1 month', true, true, 'PAYMENT_ACKNOWLEDGMENT_TEMPLATE',
       CURRENT_TIMESTAMP - INTERVAL '1 month', CURRENT_TIMESTAMP - INTERVAL '1 month'),

      (4, 'Loan Created', 'Your loan application has been created successfully', 'LOAN_CREATED', 'EMAIL',
       CURRENT_TIMESTAMP - INTERVAL '4 months', true, true, 'LOAN_CREATED_TEMPLATE',
       CURRENT_TIMESTAMP - INTERVAL '4 months', CURRENT_TIMESTAMP - INTERVAL '4 months'),

      (4, 'Loan Disbursed', 'Your loan of KES 500,000 has been disbursed', 'LOAN_DISBURSED', 'PUSH',
       CURRENT_TIMESTAMP - INTERVAL '4 months', true, true, 'LOAN_DISBURSED_TEMPLATE',
       CURRENT_TIMESTAMP - INTERVAL '4 months', CURRENT_TIMESTAMP - INTERVAL '4 months'),

      (6, 'Loan Pending', 'Your loan application is pending review', 'LOAN_CREATED', 'EMAIL',
       CURRENT_TIMESTAMP - INTERVAL '1 day', true, false, 'LOAN_CREATED_TEMPLATE',
       CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day'),

      (8, 'Loan Closed', 'Your loan has been successfully closed', 'LOAN_CLOSED', 'EMAIL',
       CURRENT_TIMESTAMP - INTERVAL '2 months', true, true, 'LOAN_CLOSED_TEMPLATE',
       CURRENT_TIMESTAMP - INTERVAL '2 months', CURRENT_TIMESTAMP - INTERVAL '2 months'),

      (10, 'Loan Approved', 'Your car loan of KES 250,000 has been approved', 'LOAN_APPROVED', 'EMAIL',
       CURRENT_TIMESTAMP - INTERVAL '3 days', true, false, 'LOAN_APPROVED_TEMPLATE',
       CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '3 days');

-- ============================================
-- 9. NOTIFICATION VARIABLES
-- ============================================

INSERT INTO notification_variables (notification_id, variable_key, variable_value)
SELECT n.id, 'loanAmount', CASE
                               WHEN n.type = 'LOAN_APPROVED' AND n.customer_id = 1 THEN '200000'
                               WHEN n.type = 'LOAN_APPROVED' AND n.customer_id = 10 THEN '250000'
                               WHEN n.type = 'LOAN_DISBURSED' AND n.customer_id = 4 THEN '500000'
                               ELSE '100000'
    END
FROM notifications n
WHERE n.type IN ('LOAN_APPROVED', 'LOAN_DISBURSED');

