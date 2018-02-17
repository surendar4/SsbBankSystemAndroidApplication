package com.example.surendar_5541.ssbbanksystemdemoapplication

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import java.text.SimpleDateFormat
import java.util.*


class DataBaseHandler(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private val myCR: ContentResolver = context.contentResolver
    private val myCP = MyContentProvider()

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE_BANK_CUSTOMERS = "CREATE TABLE $TABLE_BANK_CUSTOMERS($CUSTOMER_ID TEXT PRIMARY KEY, $CUSTOMER_NAME TEXT, $CUSTOMER_DOB DATE,$MOBILE INTEGER,$EMAIL TEXT)"
        val CREATE_TABLE_BANK_ACCOUNTS = "CREATE TABLE $TABLE_BANK_ACCOUNTS($ACCOUNT_NUMBER INTEGER PRIMARY KEY, $CUSTOMER_ID TEXT,$ACCOUNT_TYPE TEXT,$ACCOUNT_OPENED_ON DATETIME,$BANK_BALANCE REAL,FOREIGN KEY($CUSTOMER_ID) REFERENCES $TABLE_BANK_CUSTOMERS($CUSTOMER_ID))"
        val CREATE_TABLE_SSB_TRANSACTIONS = "CREATE TABLE $TABLE_SSB_TRANSACTIONS($TRANSACTION_ID INTEGER PRIMARY KEY AUTOINCREMENT,$DATE DATE,$SENDER_ACCOUNT_NUMBER INTEGER,$RECEIVER_ACCOUNT_NUMBER INTEGER,$TRANSACTION_MODE TEXT, $REFERENCE_NUMBER TEXT UNIQUE,$TRANSACTION_PURPOSE TEXT,$TRANSACTION_AMOUNT REAL,FOREIGN KEY($SENDER_ACCOUNT_NUMBER) REFERENCES $TABLE_BANK_ACCOUNTS($ACCOUNT_NUMBER))"
        val CREATE_TABLE_SSB_ACCOUNTS = "CREATE TABLE $TABLE_SSB_ACCOUNTS($CUSTOMER_ID TEXT PRIMARY KEY,$USERNAME TEXT UNIQUE,$PASSWORD TEXT, $PIN_STATUS TEXT,$PIN TEXT,FOREIGN KEY($CUSTOMER_ID) REFERENCES $TABLE_BANK_CUSTOMERS($CUSTOMER_ID))"
        val CREATE_TABLE_SSB_REGISTERED_BENEFICIARIES = "CREATE TABLE $TABLE_SSB_REGISTERED_BENEFICIARIES($CUSTOMER_ID TEXT,$BENEFICIARY_NICK_NAME TEXT,$BENEFICIARY_ACCOUNT_NUMBER INTEGER,$BENEFICIARY_NAME TEXT,$BENEFICIARY_IFSC TEXT,$BENEFICIARY_BANK TEXT,$BENEFICIARY_BRANCH_NAME TEXT,PRIMARY KEY($CUSTOMER_ID,$BENEFICIARY_ACCOUNT_NUMBER),UNIQUE($CUSTOMER_ID,$BENEFICIARY_NICK_NAME),FOREIGN KEY($CUSTOMER_ID) REFERENCES $TABLE_SSB_ACCOUNTS($CUSTOMER_ID))"
        val CREATE_TABLE_BANK_CUSTOMERS_ADDRESSES = "CREATE TABLE $TABLE_BANK_CUSTOMERS_ADDRESSES($CUSTOMER_ID TEXT PRIMARY KEY,$CUSTOMER_VILLAGE TEXT,$CUSTOMER_CITY TEXT,$CUSTOMER_DISTRICT TEXT,$CUSTOMER_STATE TEXT,$CUSTOMER_COUNTRY TEXT,$CUSTOMER_PIN_CODE INTEGER)"

        db.execSQL(CREATE_TABLE_BANK_CUSTOMERS)
        db.execSQL(CREATE_TABLE_BANK_ACCOUNTS)
        db.execSQL(CREATE_TABLE_SSB_TRANSACTIONS)
        db.execSQL(CREATE_TABLE_SSB_ACCOUNTS)
        db.execSQL(CREATE_TABLE_SSB_REGISTERED_BENEFICIARIES)
        db.execSQL(CREATE_TABLE_BANK_CUSTOMERS_ADDRESSES)
        fillBankInitialData(db)
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        if (!db.isReadOnly) {
            db.execSQL("PRAGMA foreign_keys=ON;")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }


    /*internal fun addBankCustomers(bankCustomer: BankCustomer) {  //Adds Bank Customers
        val values = ContentValues()
        values.put(CUSTOMER_ID, bankCustomer.customerId)
        values.put(CUSTOMER_NAME, bankCustomer.customerName)
        values.put(CUSTOMER_DOB, bankCustomer.dateOfBirth)
        values.put(MOBILE, bankCustomer.mobile.toString())
        values.put(EMAIL, bankCustomer.email)
        myCR.insert(myCP.CONTENT_BANK_CUSTOMERS_URI, values)
    } */

    internal fun getPassword(username: String): ByteArray {

        val projection = arrayOf(PASSWORD)
        val selection = USERNAME + "=?"
        val selectionArgs = arrayOf(username)
        val cursor = myCR.query(myCP.CONTENT_SSB_ACCOUNTS_URI, projection, selection, selectionArgs, null)
        if (cursor.moveToFirst()) {
            val password = cursor.getBlob(0)
            cursor.close()
            return password
        } else {
            return kotlin.ByteArray(1)
        }
    }

    /*  internal fun addBankAccount(bankAccount: BankAccount) { //Adds Bank Accounts for customers
          val values = ContentValues()
          values.put(ACCOUNT_NUMBER, bankAccount.accountNumber.toString())
          values.put(CUSTOMER_ID, bankAccount.customerId)
          values.put(ACCOUNT_OPENED_ON, bankAccount.accountOpenedOn)
          values.put(ACCOUNT_TYPE, bankAccount.accountType)
          values.put(BANK_BALANCE, bankAccount.balance)
          myCR.insert(myCP.CONTENT_BANK_ACCOUNTS_URI, values)
      }
   */
    internal fun getBankAccount(accountNumber: Number): BankAccount {
        val projection = arrayOf(ACCOUNT_NUMBER, CUSTOMER_ID, ACCOUNT_TYPE, ACCOUNT_OPENED_ON, BANK_BALANCE)
        val selection = ACCOUNT_NUMBER + "=?"
        val selectionArgs = arrayOf(accountNumber.toString())
        val cursor = myCR.query(myCP.CONTENT_BANK_ACCOUNTS_URI, projection, selection, selectionArgs, null)
        val bankAccount: BankAccount
        if (cursor.moveToFirst()) {
            bankAccount = BankAccount(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getLong(3), cursor.getFloat(4))
            cursor.close()
        } else {
            bankAccount = BankAccount(0, " ", " ", 0, 0.0f)
        }
        return bankAccount
    }

    /* internal fun addBankCustomerAddress(address: BankCustomerAddress) {
        val values = ContentValues()
        values.put(CUSTOMER_ID, address.customerId)
        values.put(CUSTOMER_VILLAGE, address.village)
        values.put(CUSTOMER_CITY, address.city)
        values.put(CUSTOMER_DISTRICT, address.district)
        values.put(CUSTOMER_STATE, address.state)
        values.put(CUSTOMER_COUNTRY, address.country)
        values.put(CUSTOMER_PIN_CODE, address.pinCode)
        myCR.insert(myCP.CONTENT_BANK_ADDRESSES_URI, values)
    } */

    internal fun isUserAlreadyRegistered(customerId: String): Boolean { //checks whether user already registered or not in SSB APP
        val projection = arrayOf(USERNAME)
        val selection = CUSTOMER_ID + "=?"
        val selectionArgs = arrayOf(customerId)
        val cursor = myCR.query(myCP.CONTENT_SSB_ACCOUNTS_URI, projection, selection, selectionArgs, null)
        cursor.moveToFirst()
        val count = cursor.count
        cursor.close()
        return count > 0
    }

    private fun fillBankInitialData(db: SQLiteDatabase) {
        val bankCustomers = mutableListOf<BankCustomer>()
        bankCustomers.add(BankCustomer("CID0000004", "sanjana", "1998-10-28", 7989220730, "kodavathpremalatha@gmail.com"))
        bankCustomers.add(BankCustomer("CID0000001", "surendar", "1996-06-10", 8714281098, "surendar.d@zohocorp.com"))
        bankCustomers.add(BankCustomer("CID0000002", "arun", "1995-04-05", 7010638753, "arun.roy@zohocorp.com"))
        bankCustomers.add(BankCustomer("CID0000003", "ramya", "1986-03-20", 8639004098, "ramya.s@zohocorp.com"))
        bankCustomers.add(BankCustomer("CID0000005", "PenZone", "20000-01-01", 9876543210, "penzone@zohocorp.com"))

        for (bankCustomer in bankCustomers) {
            val values = ContentValues()
            values.put(CUSTOMER_ID, bankCustomer.customerId)
            values.put(CUSTOMER_NAME, bankCustomer.customerName)
            values.put(CUSTOMER_DOB, bankCustomer.customerName)
            values.put(MOBILE, bankCustomer.mobile.toLong())
            values.put(EMAIL, bankCustomer.email)
            db.insert(TABLE_BANK_CUSTOMERS, null, values)
        }

        val bankAccounts = mutableListOf<BankAccount>()
        val now = Calendar.getInstance()
        now.set(Calendar.DAY_OF_MONTH, 1)
        now.set(Calendar.HOUR_OF_DAY, 0)
        now.set(Calendar.MINUTE, 0)
        now.set(Calendar.SECOND, 0)

        val openedOn = now.timeInMillis
        bankAccounts.add(BankAccount(20162600001, "CID0000001", "Savings", openedOn, 258000.00f))
        bankAccounts.add(BankAccount(20162600002, "CID0000002", "Savings", openedOn, 52000.00f))
        bankAccounts.add(BankAccount(20162600003, "CID0000003", "Savings", openedOn, 56000.00f))
        bankAccounts.add(BankAccount(20162600004, "CID0000004", "Savings", openedOn, 56000.00f))
        bankAccounts.add(BankAccount(20162600005, "CID0000005", "Current", openedOn, 0.00f))

        for (bankAccount in bankAccounts) {
            val values = ContentValues()
            values.put(ACCOUNT_NUMBER, bankAccount.accountNumber.toLong())
            values.put(CUSTOMER_ID, bankAccount.customerId)
            values.put(ACCOUNT_TYPE, bankAccount.accountType)
            values.put(ACCOUNT_OPENED_ON, bankAccount.accountOpenedOn)
            values.put(BANK_BALANCE, bankAccount.balance)
            db.insert(TABLE_BANK_ACCOUNTS, null, values)
        }

        val bankCustomerAddresses = mutableListOf<BankCustomerAddress>()
        bankCustomerAddresses.add(BankCustomerAddress("CID0000001", "Kootigal", "Maddur", "Warangal", "TS", "India", 506367))
        bankCustomerAddresses.add(BankCustomerAddress("CID0000004", "Kootigal", "Maddur", "Warangal", "TS", "India", 506367))
        bankCustomerAddresses.add(BankCustomerAddress("CID0000002", "Guduvanchery", "Chennai", "Chennai", "TN", "India", 603202))
        bankCustomerAddresses.add(BankCustomerAddress("CID0000003", "Guduvanchery", "Chennai", "Chennai", "TN", "India", 603202))
        bankCustomerAddresses.add(BankCustomerAddress("CID0000005", "ZOHO CORP", "CHENNAI", "CHENNAI", "TN", "INDIA", 603202))

        for (bankCustomerAddress in bankCustomerAddresses) {
            val values = ContentValues()
            values.put(CUSTOMER_ID, bankCustomerAddress.customerId)
            values.put(CUSTOMER_VILLAGE, bankCustomerAddress.village)
            values.put(CUSTOMER_CITY, bankCustomerAddress.city)
            values.put(CUSTOMER_DISTRICT, bankCustomerAddress.district)
            values.put(CUSTOMER_STATE, bankCustomerAddress.state)
            values.put(CUSTOMER_COUNTRY, bankCustomerAddress.country)
            values.put(CUSTOMER_PIN_CODE, bankCustomerAddress.pinCode)
            db.insert(TABLE_BANK_CUSTOMERS_ADDRESSES, null, values)
        }
    }

    internal fun getMobileCustomerIdUsingAccountNumber(accountNumber: String): Pair<String, String> { //Returns customer Mobile number and Account number ,this values used for verification
        val projection = arrayOf(MOBILE, "$TABLE_BANK_CUSTOMERS.$CUSTOMER_ID")
        val selection = ACCOUNT_NUMBER + "=?"
        val selectionArgs = arrayOf(accountNumber)
        val cursor = myCR.query(myCP.CONTENT_BANK_ACCOUNTS_URI, projection, selection, selectionArgs, null)
        if (cursor.moveToFirst()) {
            val mobile = cursor.getString(0)
            val userId = cursor.getString(1)
            cursor.close()
            return Pair(mobile, userId)
        }
        cursor.close()
        return Pair("NOT Found", "000000000")
    }

    internal fun getAccountNumber(customerId: String?): Number {
        val projection = arrayOf(ACCOUNT_NUMBER)
        val selection = CUSTOMER_ID + "=?"
        val selectionArgs = arrayOf(customerId)
        val cursor = myCR.query(myCP.CONTENT_BANK_ACCOUNTS_URI, projection, selection, selectionArgs, null)
        if (cursor.moveToFirst()) {
            val accountNumber = cursor.getString(0)
            cursor.close()
            return accountNumber.toLong()
        }
        return 0
    }

    internal fun getCustomerId(username: String): String {
        val projection = arrayOf(CUSTOMER_ID)
        val selection = USERNAME + "=?"
        val selectionArgs = arrayOf(username)
        val cursor = myCR.query(myCP.CONTENT_SSB_ACCOUNTS_URI, projection, selection, selectionArgs, null)
        if (cursor.moveToFirst()) {
            val userId = cursor.getString(0)
            cursor.close()
            return userId
        } else {
            cursor.close()
            return "Invalid"
        }
    }

    internal fun getBeneficiaries(customerId: String?): MutableList<BeneficiaryAccount> {
        val projection = arrayOf(CUSTOMER_ID, BENEFICIARY_NICK_NAME, BENEFICIARY_NAME, BENEFICIARY_ACCOUNT_NUMBER, BENEFICIARY_IFSC, BENEFICIARY_BANK, BENEFICIARY_BRANCH_NAME)
        val selection = CUSTOMER_ID + "=?"
        val selectionArgs = arrayOf(customerId)
        val cursor = myCR.query(myCP.CONTENT_SSB_BENEFICIAFIES_URI, projection, selection, selectionArgs, null)
        val beneficiaries = mutableListOf<BeneficiaryAccount>()
        if (cursor.moveToFirst()) {
            do {
                beneficiaries.add(BeneficiaryAccount(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getLong(3), cursor.getString(4), cursor.getString(5), cursor.getString(6)))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return beneficiaries
    }

    internal fun isUsernameAvailable(username: String): Boolean {
        val projection = arrayOf(CUSTOMER_ID)
        val selection = USERNAME + "=?"
        val selectionArgs = arrayOf(username)
        val cursor = myCR.query(myCP.CONTENT_SSB_ACCOUNTS_URI, projection, selection, selectionArgs, null)
        cursor.moveToFirst()
        val count = cursor.count
        cursor.close()
        return !(count > 0)
    }

    internal fun addBeneficiary(beneficiaryAccount: BeneficiaryAccount): Uri {
        val values = ContentValues()
        values.put(CUSTOMER_ID, beneficiaryAccount.customerId)
        values.put(BENEFICIARY_NAME, beneficiaryAccount.name)
        values.put(BENEFICIARY_ACCOUNT_NUMBER, beneficiaryAccount.accountNumber.toLong())
        values.put(BENEFICIARY_IFSC, beneficiaryAccount.ifscCode)
        values.put(BENEFICIARY_NICK_NAME, beneficiaryAccount.nickName)
        values.put(BENEFICIARY_BANK, beneficiaryAccount.bankName)
        values.put(BENEFICIARY_BRANCH_NAME, beneficiaryAccount.branchName)
        return myCR.insert(myCP.CONTENT_SSB_BENEFICIAFIES_URI, values)
    }

    internal fun addSsbAccount(ssbAccount: SsbAccount): Uri {
        val values = ContentValues()
        values.put(CUSTOMER_ID, ssbAccount.customerId)
        values.put(USERNAME, ssbAccount.username)
        values.put(PASSWORD, ssbAccount.password)
        values.put(PIN_STATUS, ssbAccount.pinStatus)
        values.put(PIN, ssbAccount.pin)
        return myCR.insert(myCP.CONTENT_SSB_ACCOUNTS_URI, values)
    }

    internal fun isAccountNumberExists(accountNumber: Number): Boolean {
        val projection = arrayOf(ACCOUNT_NUMBER)
        val selection = ACCOUNT_NUMBER + "=?"
        val selectionArgs = arrayOf(accountNumber.toString())
        val cursor = myCR.query(myCP.CONTENT_BANK_ACCOUNTS_URI, projection, selection, selectionArgs, null)
        val count = cursor.count
        cursor.close()
        return count > 0
    }

    internal fun getAccountBalance(accountNumber: Number): Float {
        val projection = arrayOf(BANK_BALANCE)
        val selection = ACCOUNT_NUMBER + "=?"
        val selectionArgs = arrayOf(accountNumber.toString())
        val cursor = myCR.query(myCP.CONTENT_BANK_ACCOUNTS_URI, projection, selection, selectionArgs, null)
        if (cursor.moveToFirst()) {
            val bal = cursor.getDouble(0)
            cursor.close()
            return bal.toFloat()
        } else {
            cursor.close()
            return 0.00f
        }
    }

    internal fun getCustomer(customerId: String?): BankCustomer {
        val projection = arrayOf(CUSTOMER_ID, CUSTOMER_NAME, CUSTOMER_DOB, MOBILE, EMAIL)
        val selection = CUSTOMER_ID + "=?"
        val selectionArgs = arrayOf(customerId)
        val cursor = myCR.query(myCP.CONTENT_BANK_CUSTOMERS_URI, projection, selection, selectionArgs, null)
        if (cursor.moveToFirst()) {
            val customer = BankCustomer(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getLong(3), cursor.getString(4))
            cursor.close()
            return customer
        }
        return BankCustomer("empty", " ", " ", 23, " ")
    }

    internal fun updateBalance(accountNumber: Number, amount: Float): Int {
        val values = ContentValues()
        val currentBal = getAccountBalance(accountNumber)
        val finalBal = currentBal + amount
        val where = ACCOUNT_NUMBER + "=?"
        val selectionArgs = arrayOf(accountNumber.toString())
        values.put(BANK_BALANCE, finalBal)
        return myCR.update(myCP.CONTENT_BANK_ACCOUNTS_URI, values, where, selectionArgs)
    }

    internal fun addTransaction(transaction: Transaction): Uri {
        val values = ContentValues()
        values.put(DATE, transaction.date)
        values.put(SENDER_ACCOUNT_NUMBER, transaction.senderAccountNumber.toLong())
        values.put(RECEIVER_ACCOUNT_NUMBER, transaction.receiverAccountNumber.toLong())
        values.put(REFERENCE_NUMBER, transaction.referenceNumber)
        values.put(TRANSACTION_MODE, transaction.transactionMode)
        values.put(TRANSACTION_PURPOSE, transaction.purpose)
        values.put(TRANSACTION_AMOUNT, transaction.amount)
        return myCR.insert(myCP.CONTENT_SSB_TRANSACTIONS_URI, values)

    }

    internal fun deleteBeneficiary(customerId: String?, accountNumber: Number) {
        val selection = "$CUSTOMER_ID = ? and $BENEFICIARY_ACCOUNT_NUMBER =?"
        val selectionArgs = arrayOf(customerId, accountNumber.toString())
        myCR.delete(myCP.CONTENT_SSB_BENEFICIAFIES_URI, selection, selectionArgs)
    }

    internal fun updateBeneficiary(benAccountNumber: Number, beneficiaryAccount: BeneficiaryAccount): Int {
        val values = ContentValues()
        values.put(BENEFICIARY_NAME, beneficiaryAccount.name)
        values.put(BENEFICIARY_ACCOUNT_NUMBER, beneficiaryAccount.accountNumber.toString())
        values.put(BENEFICIARY_IFSC, beneficiaryAccount.ifscCode)
        values.put(BENEFICIARY_NICK_NAME, beneficiaryAccount.nickName)
        values.put(BENEFICIARY_BANK, beneficiaryAccount.bankName)
        values.put(BENEFICIARY_BRANCH_NAME, beneficiaryAccount.branchName)
        val selection = "$CUSTOMER_ID = ? and $BENEFICIARY_ACCOUNT_NUMBER =?"
        val selectionArgs = arrayOf(beneficiaryAccount.customerId, benAccountNumber.toString())
        return myCR.update(myCP.CONTENT_SSB_BENEFICIAFIES_URI, values, selection, selectionArgs)
    }

    internal fun getMiniStatement(accountNumber: Number): MutableList<Statement> {
        val miniStatement = mutableListOf<Statement>()
        val projection = arrayOf(DATE, REFERENCE_NUMBER, TRANSACTION_AMOUNT, SENDER_ACCOUNT_NUMBER, RECEIVER_ACCOUNT_NUMBER)
        val selection = "$SENDER_ACCOUNT_NUMBER = ? or $RECEIVER_ACCOUNT_NUMBER = ?"
        val sortOrder = "$TRANSACTION_ID DESC LIMIT 10"
        val selectionArgs = arrayOf(accountNumber.toString(), accountNumber.toString())
        val cursor = myCR.query(myCP.CONTENT_SSB_TRANSACTIONS_URI, projection, selection, selectionArgs, sortOrder)
        if (cursor.moveToFirst()) {
            do {
                var name = " "
                var accountNumberR = " "
                if (cursor.getString(3) == accountNumber.toString()) {
                    if (isAccountNumberExists(cursor.getLong(4))) {
                        name = "@${getCustomerName(cursor.getLong(4))}"
                        accountNumberR = "/${cursor.getLong(4)}"
                    }
                    miniStatement.add(Statement(cursor.getLong(0), "${cursor.getString(1)}$name$accountNumberR", "${cursor.getFloat(2)} Dr", 0.00f))
                } else {
                    if (isAccountNumberExists(cursor.getLong(3))) {
                        name = "@${getCustomerName(cursor.getLong(3))}"
                    }

                    miniStatement.add(Statement(cursor.getLong(0), "${cursor.getString(1)}$name", "${cursor.getFloat(2)} Cr", 0.00f))
                }
            } while (cursor.moveToNext())
            cursor.close()
        }
        return miniStatement
    }

    internal fun getCustomerName(accountNumber: Number): String {
        val projection = arrayOf(CUSTOMER_NAME)
        val selection = ACCOUNT_NUMBER + "=?"
        val selectionArgs = arrayOf(accountNumber.toString())
        val cursor = myCR.query(myCP.CONTENT_BANK_ACCOUNTS_URI, projection, selection, selectionArgs, null)
        if (cursor.moveToFirst()) {
            val name = cursor.getString(0)
            cursor.close()
            return name
        } else {
            cursor.close()
            return " "
        }
    }

    internal fun getSsbAccount(username: String): SsbAccount {
        val projection = arrayOf(CUSTOMER_ID, USERNAME, PASSWORD, PIN_STATUS, PIN)
        val selection = USERNAME + "=?"
        val selectionArgs = arrayOf(username)
        val cursor = myCR.query(myCP.CONTENT_SSB_ACCOUNTS_URI, projection, selection, selectionArgs, null)
        if (cursor.moveToFirst()) {
            val ssbAccount = SsbAccount(cursor.getString(0), cursor.getString(1), cursor.getBlob(2), cursor.getInt(3), cursor.getBlob(4))
            cursor.close()
            return ssbAccount
        } else {
            cursor.close()
            return SsbAccount("empty", " ", ByteArray(1), 2, kotlin.ByteArray(1))
        }
    }

    internal fun getTransactionsBetweenDates(accountNumber: Number, fromDate: Long, toDate: Long): MutableList<Statement> {
        val projection = arrayOf(DATE, REFERENCE_NUMBER, TRANSACTION_AMOUNT, SENDER_ACCOUNT_NUMBER, RECEIVER_ACCOUNT_NUMBER)
        val selection = "$SENDER_ACCOUNT_NUMBER = ? or $RECEIVER_ACCOUNT_NUMBER = ?"
        val sortOrder = "$TRANSACTION_ID ASC"
        val selectionArgs = arrayOf(accountNumber.toString(), accountNumber.toString())
        val cursor = myCR.query(myCP.CONTENT_SSB_TRANSACTIONS_URI, projection, selection, selectionArgs, sortOrder)
        val statements = mutableListOf<Statement>()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        var startingBal = balanceOn(accountNumber, fromDate)

        if (cursor.moveToFirst()) {
            do {
                var name = " "
                if (sdf.format(cursor.getLong(0)) >= sdf.format(fromDate) && sdf.format(cursor.getLong(0)) <= sdf.format(toDate)) {
                    if (cursor.getString(3) == accountNumber.toString()) {
                        if (isAccountNumberExists(cursor.getLong(4))) {
                            name = "@${getCustomerName(cursor.getLong(4))}"
                        }
                        startingBal -= cursor.getFloat(2)
                        statements.add(Statement(cursor.getLong(0), "${cursor.getString(1)}$name", "${String.format("%.2f", cursor.getFloat(2))} Dr", String.format("%.2f", startingBal).toFloat()))
                    } else {
                        startingBal += cursor.getFloat(2)
                        if (isAccountNumberExists(cursor.getLong(3))) {
                            name = "@${getCustomerName(cursor.getLong(3))}"
                        }
                        statements.add(Statement(cursor.getLong(0), "${cursor.getString(1)}$name", "${String.format("%.2f", cursor.getFloat(2))} Cr", String.format("%.2f", startingBal).toFloat()))
                    }
                }
            } while (cursor.moveToNext())
            cursor.close()
        }
        return statements
    }

    internal fun balanceOn(accountNumber: Number, date: Long): Float {
        val calendar = Calendar.getInstance()
        val toDayDateInMillis = calendar.timeInMillis
        val allTransactions = getTransactionsBetweenGivenDates(accountNumber, date, toDayDateInMillis)
        var balOnDate = getAccountBalance(accountNumber)

        for (transaction in allTransactions) {
            if (transaction.amount[transaction.amount.length - 2] == 'C') {
                balOnDate -= transaction.amount.substring(0, transaction.amount.length - 3).toFloat()
            } else {
                balOnDate += transaction.amount.substring(0, transaction.amount.length - 3).toFloat()
            }
        }
        return balOnDate
    }

    private fun getTransactionsBetweenGivenDates(accountNumber: Number, fromDate: Long, toDate: Long): MutableList<Statement> {
        val projection = arrayOf(DATE, REFERENCE_NUMBER, TRANSACTION_AMOUNT, SENDER_ACCOUNT_NUMBER, RECEIVER_ACCOUNT_NUMBER)
        val selection = "$SENDER_ACCOUNT_NUMBER = ? or $RECEIVER_ACCOUNT_NUMBER = ?"
        val sortOrder = "$TRANSACTION_ID DESC"
        val selectionArgs = arrayOf(accountNumber.toString(), accountNumber.toString())
        val cursor = myCR.query(myCP.CONTENT_SSB_TRANSACTIONS_URI, projection, selection, selectionArgs, sortOrder)
        val statements = mutableListOf<Statement>()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        if (cursor.moveToFirst()) {
            do {
                if (sdf.format(cursor.getLong(0)) >= sdf.format(fromDate) && sdf.format(cursor.getLong(0)) <= sdf.format(toDate)) {
                    if (cursor.getString(3) == accountNumber.toString()) {

                        statements.add(Statement(cursor.getLong(0), cursor.getString(1), "${cursor.getFloat(2)} Dr", 0.00f))
                    } else {
                        statements.add(Statement(cursor.getLong(0), cursor.getString(1), "${cursor.getFloat(2)} Cr", 0.00f))
                    }
                }
            } while (cursor.moveToNext())
            cursor.close()
        }
        return statements
    }

    internal fun updateEasyPin(username: String, newPin: ByteArray, status: Int): Int {
        val values = ContentValues()
        values.put(PIN, newPin)
        values.put(PIN_STATUS, status)
        val selection = USERNAME + "=?"
        val selectionArgs = arrayOf(username)
        return myCR.update(myCP.CONTENT_SSB_ACCOUNTS_URI, values, selection, selectionArgs)
    }


    internal fun getTransactionWithUri(uri: Uri): Transaction {
        val projection = arrayOf(DATE, SENDER_ACCOUNT_NUMBER, RECEIVER_ACCOUNT_NUMBER, TRANSACTION_MODE, TRANSACTION_PURPOSE, REFERENCE_NUMBER, TRANSACTION_AMOUNT)
        val selection = TRANSACTION_ID + "=?"
        val selectionArgs = arrayOf(uri.lastPathSegment.toInt().toString())
        val cursor = myCR.query(myCP.CONTENT_SSB_TRANSACTIONS_URI, projection, selection, selectionArgs, null)

        if (cursor.moveToFirst()) {
            val transaction = Transaction(cursor.getLong(0), cursor.getLong(1), cursor.getLong(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getFloat(6))
            cursor.close()
            return transaction
        } else {
            cursor.close()
            return Transaction(0, 0, 0, " ", " ", " ", 0.0f)
        }
    }

    internal fun resetPassword(username: String, newPassword: ByteArray): Int {
        val values = ContentValues()
        values.put(PASSWORD, newPassword)
        val selection = USERNAME + "=?"
        val selectionArgs = arrayOf(username)
        return myCR.update(myCP.CONTENT_SSB_ACCOUNTS_URI, values, selection, selectionArgs)
    }

    companion object {
        val DATABASE_NAME = "ssb_t1.db"
        val DATABASE_VERSION = 1

        val TABLE_BANK_ACCOUNTS = "bankAccounts"
        val TABLE_BANK_CUSTOMERS = "bankCustomers"
        val TABLE_SSB_TRANSACTIONS = "ssbTransactions"
        val TABLE_SSB_ACCOUNTS = "ssbAccounts"
        val TABLE_SSB_REGISTERED_BENEFICIARIES = "ssbRegisteredBeneficiaries"
        val TABLE_BANK_CUSTOMERS_ADDRESSES = "bankCustomersAddresses"

        val CUSTOMER_ID = "customerId"
        val CUSTOMER_NAME = "customerName"
        val CUSTOMER_DOB = "customerDob"
        val MOBILE = "mobile"
        val EMAIL = "email"

        val ACCOUNT_NUMBER = "accountNumber"
        val ACCOUNT_TYPE = "accountType"
        val ACCOUNT_OPENED_ON = "accountOpenedOn"
        val BANK_BALANCE = "bankBalance"

        val TRANSACTION_ID = "transaction_id"
        val DATE = "date"
        val SENDER_ACCOUNT_NUMBER = "senderAccountNumber"
        val RECEIVER_ACCOUNT_NUMBER = "recAccountNumber"
        val REFERENCE_NUMBER = "referenceNumber"
        val TRANSACTION_MODE = "transactionMode"
        val TRANSACTION_PURPOSE = "transactionPurpose"
        val TRANSACTION_AMOUNT = "amount"

        val USERNAME = "userName"
        val PASSWORD = "password"
        val PIN_STATUS = "pinStatus"
        val PIN = "pin"

        val BENEFICIARY_ACCOUNT_NUMBER = "beneficiaryAccountNumber"
        val BENEFICIARY_NAME = "beneficiaryName"
        val BENEFICIARY_NICK_NAME = "beneficiaryNickName"
        val BENEFICIARY_IFSC = "beneficiaryIFSC"
        val BENEFICIARY_BANK = "beneficiaryBankName"
        val BENEFICIARY_BRANCH_NAME = "beneficiaryBranchName"

        val CUSTOMER_VILLAGE = "village"
        val CUSTOMER_CITY = "city"
        val CUSTOMER_DISTRICT = "district"
        val CUSTOMER_STATE = "state"
        val CUSTOMER_COUNTRY = "country"
        val CUSTOMER_PIN_CODE = "pinCode"

    }
}