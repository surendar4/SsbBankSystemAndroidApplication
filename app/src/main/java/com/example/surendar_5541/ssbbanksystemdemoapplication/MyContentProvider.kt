package com.example.surendar_5541.ssbbanksystemdemoapplication

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri

class MyContentProvider : ContentProvider() {
    private val AUTHORITY = "com.example.surendar_5541.ssbbanksystemdemoapplication.MyContentProvider"

    private val BANK_ACCOUNTS_URL = "content://$AUTHORITY/${DataBaseHandler.TABLE_BANK_ACCOUNTS}"
    private val BANK_CUSTOMERS_URL = "content://$AUTHORITY/${DataBaseHandler.TABLE_BANK_CUSTOMERS}"
    private val SSB_ACCOUNTS_URL = "content://$AUTHORITY/${DataBaseHandler.TABLE_SSB_ACCOUNTS}"
    private val SSB_BENEFICIARIES_URL = "content://$AUTHORITY/${DataBaseHandler.TABLE_SSB_REGISTERED_BENEFICIARIES}"
    private val SSB_TRANSACTIONS_URL = "content://$AUTHORITY/${DataBaseHandler.TABLE_SSB_TRANSACTIONS}"
    private val BANK_CUSTOMERS_ADDRESSES_URL = "content://$AUTHORITY/${DataBaseHandler.TABLE_BANK_CUSTOMERS_ADDRESSES}"

    val CONTENT_BANK_ACCOUNTS_URI: Uri = Uri.parse(BANK_ACCOUNTS_URL)
    val CONTENT_BANK_CUSTOMERS_URI: Uri = Uri.parse(BANK_CUSTOMERS_URL)
    val CONTENT_SSB_ACCOUNTS_URI: Uri = Uri.parse(SSB_ACCOUNTS_URL)
    val CONTENT_SSB_BENEFICIAFIES_URI: Uri = Uri.parse(SSB_BENEFICIARIES_URL)
    val CONTENT_SSB_TRANSACTIONS_URI: Uri = Uri.parse(SSB_TRANSACTIONS_URL)
    val CONTENT_BANK_ADDRESSES_URI: Uri = Uri.parse(BANK_CUSTOMERS_ADDRESSES_URL)

    private val BANK_CUSTOMERS = 1
    private val BANK_ACCOUNTS = 2
    private val SSB_ACCOUNTS = 3
    private val SSB_BENEFICIARIES = 6
    private val SSB_TRANSACTIONS = 7
    private val BANK_CUSTOMERS_ADDRESSES = 8
    private val SSB_TRANSACTIONS_ID = 9
    private val SSB_BENEFICIARY_ID = 5

    private val uri_Matcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    init {
        uri_Matcher.addURI(AUTHORITY, DataBaseHandler.TABLE_BANK_CUSTOMERS, BANK_CUSTOMERS)
        uri_Matcher.addURI(AUTHORITY, DataBaseHandler.TABLE_BANK_ACCOUNTS, BANK_ACCOUNTS)
        uri_Matcher.addURI(AUTHORITY, DataBaseHandler.TABLE_SSB_ACCOUNTS, SSB_ACCOUNTS)
        uri_Matcher.addURI(AUTHORITY, DataBaseHandler.TABLE_SSB_REGISTERED_BENEFICIARIES, SSB_BENEFICIARIES)
        uri_Matcher.addURI(AUTHORITY, DataBaseHandler.TABLE_SSB_TRANSACTIONS, SSB_TRANSACTIONS)
        uri_Matcher.addURI(AUTHORITY,DataBaseHandler.TABLE_BANK_CUSTOMERS_ADDRESSES,BANK_CUSTOMERS_ADDRESSES)
        uri_Matcher.addURI(AUTHORITY,DataBaseHandler.TABLE_SSB_TRANSACTIONS + "/#",SSB_TRANSACTIONS_ID)
        uri_Matcher.addURI(AUTHORITY,DataBaseHandler.TABLE_SSB_REGISTERED_BENEFICIARIES + "/#", SSB_BENEFICIARY_ID)
    }

    var db: DataBaseHandler? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val uriType: Int = uri_Matcher.match(uri)
        val sqDB = db!!.writableDatabase
        val id: Int
        when(uriType) {
            SSB_BENEFICIARIES -> id = sqDB.delete(DataBaseHandler.TABLE_SSB_REGISTERED_BENEFICIARIES,selection,selectionArgs)
            else -> throw UnsupportedOperationException("Query: Uri Unknown" + uri)
        }
        return id
    }

    override fun getType(uri: Uri): String? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val uriType: Int = uri_Matcher.match(uri)
        val sqDB: SQLiteDatabase = db!!.writableDatabase
        val id: Long
        when (uriType) {
            BANK_CUSTOMERS -> id = sqDB.insert(DataBaseHandler.TABLE_BANK_CUSTOMERS, null, values)
            BANK_ACCOUNTS -> id = sqDB.insert(DataBaseHandler.TABLE_BANK_ACCOUNTS, null, values)
            SSB_ACCOUNTS -> id = sqDB.insert(DataBaseHandler.TABLE_SSB_ACCOUNTS, null, values)
            SSB_BENEFICIARIES -> id = sqDB.insert(DataBaseHandler.TABLE_SSB_REGISTERED_BENEFICIARIES, null, values)
            SSB_TRANSACTIONS -> id = sqDB.insert(DataBaseHandler.TABLE_SSB_TRANSACTIONS, null, values)
            BANK_CUSTOMERS_ADDRESSES -> id = sqDB.insert(DataBaseHandler.TABLE_BANK_CUSTOMERS_ADDRESSES,null,values)
            else -> throw UnsupportedOperationException("Unknown Uri" + uri)
        }
        context.contentResolver.notifyChange(uri, null)
        return Uri.parse(DataBaseHandler.TABLE_BANK_CUSTOMERS + "/" + id)
    }

    override fun onCreate(): Boolean {
        db = DataBaseHandler(context)
        return false
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val queryBuilder = SQLiteQueryBuilder()
        val uriType = uri_Matcher.match(uri)
        when (uriType) {
            BANK_ACCOUNTS -> {
                queryBuilder.tables = "${DataBaseHandler.TABLE_BANK_CUSTOMERS} NATURAL JOIN ${DataBaseHandler.TABLE_BANK_ACCOUNTS}"
            }
            BANK_CUSTOMERS -> {
                queryBuilder.tables = DataBaseHandler.TABLE_BANK_CUSTOMERS
            }

            SSB_ACCOUNTS -> {
                queryBuilder.tables = DataBaseHandler.TABLE_SSB_ACCOUNTS
            }
            SSB_BENEFICIARIES -> {
                queryBuilder.tables = DataBaseHandler.TABLE_SSB_REGISTERED_BENEFICIARIES
            }

            SSB_TRANSACTIONS -> {
                queryBuilder.tables = DataBaseHandler.TABLE_SSB_TRANSACTIONS
            }

            SSB_BENEFICIARY_ID -> {
                queryBuilder.tables = DataBaseHandler.TABLE_SSB_REGISTERED_BENEFICIARIES
                queryBuilder.appendWhere("_id = ${uri.lastPathSegment.get(1)}")
            }



            else -> throw UnsupportedOperationException("Query: Uri Unknown" + uri)
        }

        val cursor = queryBuilder.query(db?.readableDatabase, projection, selection, selectionArgs, null, null, sortOrder)
        cursor.setNotificationUri(context.contentResolver, uri)
        return cursor
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        val uriType = uri_Matcher.match(uri)
        val rowsUpdated: Int
        val sqDB = db!!.writableDatabase
        when(uriType) {
            BANK_ACCOUNTS -> rowsUpdated = sqDB.update(DataBaseHandler.TABLE_BANK_ACCOUNTS, values, selection, selectionArgs)
            SSB_BENEFICIARIES -> rowsUpdated = sqDB.update(DataBaseHandler.TABLE_SSB_REGISTERED_BENEFICIARIES,values,selection,selectionArgs)
            SSB_ACCOUNTS -> rowsUpdated = sqDB.update(DataBaseHandler.TABLE_SSB_ACCOUNTS,values,selection,selectionArgs)

            else -> throw IllegalArgumentException("URI Unknown Uri:" + uri)
        }
        context.contentResolver.notifyChange(uri, null)
        return rowsUpdated
    }
}
