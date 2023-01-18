package com.example.qm.repository

import com.example.qm.model.Profile
import com.example.qm.source.firebase.Contact
import com.example.qm.source.room.ContactDao
import com.example.qm.source.room.ContactEntity
import com.google.firebase.firestore.FirebaseFirestoreException

class ContactREPO(
    private val remoteContact: Contact,
    private val contactDao: ContactDao
) {

    suspend fun setLocalContact(contactEntity: ContactEntity) {
        contactDao.insertOne(contactEntity)
    }

    suspend fun getProfile(contractId: String, contactId: String?): Profile? {
        var profile: Profile?
        val contactEntity = contactDao.getByContractId(contractId)

        profile = if(contactEntity?.firstName == null || contactEntity.lastName == null) {
            if(contactId != null) {
                profile = remoteContact.getProfile(contactId)

                contactDao.insertOne(ContactEntity(
                    0,
                    contractId,
                    profile?.firstname,
                    profile?.lastName,
                ))
                profile
            }
            else null
        } else {
            Profile(contactEntity.firstName, contactEntity.lastName)
        }

        return profile
    }
}