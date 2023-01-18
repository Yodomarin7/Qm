package com.example.qm.usecase

import com.example.qm.repository.ContactREPO
import com.example.qm.repository.ContractREPO
import com.example.qm.source.room.ContactEntity

class SetContract(
    private val contactREPO: ContactREPO,
    private val contractREPO: ContractREPO
) {
    suspend fun run(contact: ContactEntity, currency: String): Boolean {
        val contractId = contractREPO.setRemoteContract(currency)

        return if(contractId != null) {
            contactREPO.setLocalContact(ContactEntity(
                0,
                contractId,
                contact.firstName,
                contact.lastName,
            ))
            true
        } else false
    }
}