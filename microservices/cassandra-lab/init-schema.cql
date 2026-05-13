-- Cassandra schema for Money Transfer System
-- Run: ./cassandra-node-1/bin/cqlsh -f init-schema.cql
--
-- 3-node cluster with RF=3
-- Every node has a copy of every row => great for teaching Consistency Levels

-- Create keyspace with RF=2 (both nodes get a copy)
CREATE KEYSPACE IF NOT EXISTS mts
WITH replication = {
    'class': 'SimpleStrategy',
    'replication_factor': 2
};

USE mts;

-- Transaction history table
-- Partition key: account_number (all transactions for an account in one partition)
-- Clustering key: timestamp DESC (latest first), event_id (uniqueness)
CREATE TABLE IF NOT EXISTS transaction_history (
    account_number  TEXT,
    timestamp       TIMESTAMP,
    event_id        TEXT,
    amount          DOUBLE,
    type            TEXT,
    from_account    TEXT,
    to_account      TEXT,
    payment_mode    TEXT,
    status          TEXT,
    PRIMARY KEY (account_number, timestamp, event_id)
) WITH CLUSTERING ORDER BY (timestamp DESC, event_id ASC);

-- Sample data
INSERT INTO transaction_history (account_number, timestamp, event_id, amount, type, from_account, to_account, payment_mode, status)
VALUES ('A001', '2026-05-13T10:00:00.000Z', 'evt-001', 500.0, 'WITHDRAWAL', 'A001', 'A002', 'IMPS', 'SUCCESS');

INSERT INTO transaction_history (account_number, timestamp, event_id, amount, type, from_account, to_account, payment_mode, status)
VALUES ('A002', '2026-05-13T10:00:00.000Z', 'evt-001', 500.0, 'DEPOSIT', 'A001', 'A002', 'IMPS', 'SUCCESS');

INSERT INTO transaction_history (account_number, timestamp, event_id, amount, type, from_account, to_account, payment_mode, status)
VALUES ('A001', '2026-05-13T11:30:00.000Z', 'evt-002', 1000.0, 'WITHDRAWAL', 'A001', 'A003', 'NEFT', 'SUCCESS');

INSERT INTO transaction_history (account_number, timestamp, event_id, amount, type, from_account, to_account, payment_mode, status)
VALUES ('A003', '2026-05-13T11:30:00.000Z', 'evt-002', 1000.0, 'DEPOSIT', 'A001', 'A003', 'NEFT', 'SUCCESS');

-- Verify
SELECT * FROM transaction_history WHERE account_number = 'A001';


-- ============================================================
-- CONSISTENCY LEVEL TEACHING EXAMPLES
-- ============================================================
--
-- With RF=2 (2 replicas on 2 nodes), here's what each CL means:
--
-- ┌─────────────────────┬──────────────────────────────────────────────────────┐
-- │ Consistency Level   │ What it means (RF=2)                                │
-- ├─────────────────────┼──────────────────────────────────────────────────────┤
-- │ ONE                 │ 1 of 2 nodes must respond => fast, less consistent  │
-- │ TWO / ALL           │ 2 of 2 nodes must respond => slow, most consistent  │
-- │ QUORUM              │ majority = 2 of 2 must respond (same as ALL)        │
-- │ LOCAL_QUORUM        │ majority in local DC (for multi-DC setups)          │
-- └─────────────────────┴──────────────────────────────────────────────────────┘
--
-- TRY THESE IN CQLSH:
--
-- CONSISTENCY ONE;
-- SELECT * FROM mts.transaction_history WHERE account_number = 'A001';
--
-- CONSISTENCY ALL;
-- SELECT * FROM mts.transaction_history WHERE account_number = 'A001';
--
-- DEMO: Stop one node (./stop-node.sh 2), then:
--   CONSISTENCY ONE;    => still WORKS (1 of 1 remaining node responds)
--   CONSISTENCY ALL;    => FAILS! (only 1 of 2 nodes available)
--
-- This demonstrates the tradeoff:
--   Higher CL = more consistent, less available
--   Lower CL  = less consistent, more available
--
-- FORMULA: Read succeeds if available_nodes >= CL
-- STRONG CONSISTENCY: Write CL + Read CL > RF
--   e.g. ALL write (2) + ONE read (1) > RF(2) => strongly consistent!
--
-- ============================================================
