DROP TABLE  IF EXISTS  t1_batch_purchase_body;
DROP TABLE  IF EXISTS  t1_batch_purchase_batch_body;

CREATE TABLE IF NOT EXISTS t1_batch_purchase_body(iRowId INTEGER PRIMARY KEY AUTOINCREMENT,iTag1 INTEGER DEFAULT 0,iTag2 INTEGER DEFAULT 0,iTag3 INTEGER DEFAULT 0,iTag4 INTEGER DEFAULT 0,iTag5 INTEGER DEFAULT 0,iTag6 INTEGER DEFAULT 0,iTag7 INTEGER DEFAULT 0,iTag8 INTEGER DEFAULT 0,slNo INTEGER DEFAULT 0,iProduct INTEGER DEFAULT 0,fQty INTEGER DEFAULT 0,fRate TEXT(50) DEFAULT null,fDiscount TEXT(50) DEFAULT null,fAddCharges TEXT(50) DEFAULT null,fVatPer TEXT(50) DEFAULT null,fVAT TEXT(50) DEFAULT null,sRemarks TEXT(50) DEFAULT null,sUnits TEXT(50) DEFAULT null,fNet TEXT(50) DEFAULT null, iDocType TEXT(50) DEFAULT null ,iTransId INTEGER DEFAULT 0, sDocNo TEXT(50) DEFAULT null);
CREATE TABLE IF NOT EXISTS t1_batch_purchase_batch_body(iRowId INTEGER DEFAULT 0,batchName TEXT(50) DEFAULT null,mfDate TEXT(50) DEFAULT null,expDate TEXT(50) DEFAULT null,batchQty INTEGER DEFAULT 0,slNo INTEGER DEFAULT 0,iProduct INTEGER DEFAULT 0, iDocType TEXT(50) DEFAULT null ,iTransId INTEGER DEFAULT 0, sDocNo TEXT(50) DEFAULT null);
