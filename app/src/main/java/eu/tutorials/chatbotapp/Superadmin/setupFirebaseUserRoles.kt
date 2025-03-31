//package eu.tutorials.chatbotapp.Superadmin
//
//import com.google.firebase.auth.FirebaseUser
//import com.google.firebase.firestore.FieldValue
//import com.google.firebase.firestore.FirebaseFirestore
//
//// Add this to your app's initialization
//fun setupFirebaseUserRoles(user: FirebaseUser) {
//    val db = FirebaseFirestore.getInstance()
//    val userRef = db.collection("users").document(user.uid)
//
//    userRef.get().addOnSuccessListener { document ->
//        if (!document.exists()) {
//            // New user - set default role
//            val userData = hashMapOf(
//                "email" to user.email,
//                "isAdmin" to false,
//                "isSuperAdmin" to false,
//                "createdAt" to FieldValue.serverTimestamp()
//            )
//            userRef.set(userData)
//        }
//    }
//}