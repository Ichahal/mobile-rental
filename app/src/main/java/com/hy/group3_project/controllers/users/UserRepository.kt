package com.hy.group3_project.controllers.users

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.hy.group3_project.models.users.User
import kotlinx.coroutines.tasks.await

class UserRepository(private val context: Context) {
    private val TAG = this.javaClass.simpleName
    private val db = Firebase.firestore

    private val COLLECTION_USERS = "Users"
    private var FIELD_ID = "id"
    private var FIELD_USER_FIRST_NAME = "firstName"
    private var FIELD_USER_LAST_NAME = "lastName"
    private var FIELD_USER_ROLE = "role"
    private var FIELD_PROPERTY_LIST = "properties"
//    private var FIELD_USER_PASSWORD = "password"
//    private var FIELD_USER_EMAIL = "email"

    var userAll: MutableLiveData<List<User>> = MutableLiveData<List<User>>()

    fun addUserToDB(user: User) {
        try {
            Log.d(TAG, "addUserToDB: $user")
            val data: MutableMap<String, Any> = HashMap()
            data[FIELD_ID] = user.id
            data[FIELD_USER_FIRST_NAME] = user.firstName
            data[FIELD_USER_LAST_NAME] = user.lastName
            data[FIELD_USER_ROLE] = user.role
            data[FIELD_PROPERTY_LIST] = user.showList()

            db.collection(COLLECTION_USERS)
                .document(user.id)
                .set(data)
                .addOnSuccessListener { docRef ->
                    Log.d(
                        TAG,
                        "addUserToDB: Document successfully added with ID : $docRef"
                    )
                }
                .addOnFailureListener { ex ->
                    Log.e(
                        TAG,
                        "addUserToDB: Exception occurred while adding a document : $ex",
                    )
                }
        } catch (ex: java.lang.Exception) {
            Log.d(
                TAG,
                "addUserToDB: Couldn't perform insert on Countries collection due to exception $ex"
            )
        }
    }

    suspend fun findUser(userId: String): User? {
        return try {
            val documentSnapshot = db.collection(COLLECTION_USERS)
                .document(userId)
                .get().await()
            val toObject = documentSnapshot.toObject(User::class.java)
            Log.d(TAG, toObject.toString())
            toObject

        } catch (ex: java.lang.Exception) {
            Log.e(TAG, "filterUser: Unable to filter user : $ex")
            null
        }
    }


    fun updateUser(userToUpdate: User) {
        val data: MutableMap<String, Any> = HashMap()
        data[FIELD_ID] = userToUpdate.id
        data[FIELD_USER_FIRST_NAME] = userToUpdate.firstName
        data[FIELD_USER_LAST_NAME] = userToUpdate.lastName
        data[FIELD_USER_ROLE] = userToUpdate.role
        data[FIELD_PROPERTY_LIST] = userToUpdate.showList()

        try {
            db.collection(COLLECTION_USERS).document(userToUpdate.id).update(data)
                .addOnSuccessListener { docRef ->
                    Log.d(TAG, "updateProperty: Document updated successfully : $docRef")
                }.addOnFailureListener { ex ->
                    Log.e(TAG, "updateProperty: Failed to update document : $ex")
                }
        } catch (ex: Exception) {
            Log.e(TAG, "updateProperty: Unable to update property due to exception : $ex")
        }
    }
}