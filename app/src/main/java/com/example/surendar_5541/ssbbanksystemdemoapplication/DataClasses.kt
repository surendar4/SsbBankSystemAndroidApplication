package com.example.surendar_5541.ssbbanksystemdemoapplication

data class BankCustomer(val customerId: String,val customerName: String,val dateOfBirth: String,val mobile: Number,val email: String)

data class BankAccount(val accountNumber: Number,val customerId: String,val accountType: String,val accountOpenedOn: Long,val balance: Float)
data class SsbAccount(val customerId: String,val username: String,val password: ByteArray,val pinStatus: Int ,val pin: ByteArray)
data class BeneficiaryAccount(val customerId: String?,val nickName: String,val name:String,val accountNumber: Number,val ifscCode: String,val bankName: String?,val branchName: String?)
data class Transaction(val date: Long,val senderAccountNumber:Number,val receiverAccountNumber:Number,val transactionMode: String,val purpose: String,val referenceNumber: String,val amount:Float)
data class Statement(val date: Long,val referenceNumber: String,val amount: String,val balance: Float)
data class BankCustomerAddress(val customerId: String,val village: String,val city: String,val district: String,val state: String,val country: String,val pinCode: Long)

