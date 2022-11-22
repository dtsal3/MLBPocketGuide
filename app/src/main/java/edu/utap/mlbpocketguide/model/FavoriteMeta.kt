package edu.utap.mlbpocketguide.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class FavoriteMeta(
    // Auth information
    var ownerUid: String = "",
    var playerName : String = "",
    @ServerTimestamp val timeStamp: Timestamp? = null,
    @DocumentId var firestoreID: String = ""
)
