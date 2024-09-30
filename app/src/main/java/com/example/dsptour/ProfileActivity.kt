package com.example.dsptour

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.util.UUID


class ProfileActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var profileImageView: ImageView
    private lateinit var editName: EditText
    private lateinit var editAge: EditText
    private lateinit var editAddress: EditText
    private lateinit var editContact: EditText
    private lateinit var editBio: EditText
    private lateinit var btnSaveProfile: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    private lateinit var imageActivityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile) // Ensure the correct layout name

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        // Initialize UI components
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        profileImageView = findViewById(R.id.profileImage)
        editName = findViewById(R.id.editName)
        editAge = findViewById(R.id.editAge)
        editAddress = findViewById(R.id.editAddress)
        editContact = findViewById(R.id.editContact)
        editBio = findViewById(R.id.editBio)
        btnSaveProfile = findViewById(R.id.btnSaveProfile)

        // Set up Toolbar and DrawerLayout
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> startActivity(Intent(this, HomeActivity::class.java))
                R.id.City -> startActivity(Intent(this, CityActivity::class.java))
                R.id.Temple -> startActivity(Intent(this, TempleActivity::class.java))
                R.id.Park -> startActivity(Intent(this, ParkActivity::class.java))
                R.id.setting -> startActivity(Intent(this, SettingActivity::class.java))
                R.id.Share -> {
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "Check out this amazing app!")
                        type = "text/plain"
                    }
                    startActivity(Intent.createChooser(shareIntent, "Share this app with:"))
                }
                R.id.logout -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Set up image picker
        imageActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageUri: Uri? = result.data?.data
                profileImageView.setImageURI(imageUri)
                uploadImageToFirebase(imageUri)
            }
        }

        profileImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imageActivityResultLauncher.launch(intent)
        }

        btnSaveProfile.setOnClickListener {
            saveProfileData()
        }

        loadProfileData()
    }

    private fun uploadImageToFirebase(imageUri: Uri?) {
        if (imageUri != null) {
            val userId = auth.currentUser?.uid ?: return
            val imageRef = storageReference.child("profile_images/$userId/${UUID.randomUUID()}")
            imageRef.putFile(imageUri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        saveImageUrlToFirestore(uri.toString())
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveImageUrlToFirestore(imageUrl: String) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId)
            .update("profileImage", imageUrl)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile image updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update profile image", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveProfileData() {
        val userId = auth.currentUser?.uid ?: return
        val name = editName.text.toString().trim()
        val age = editAge.text.toString().trim()
        val address = editAddress.text.toString().trim()
        val contact = editContact.text.toString().trim()
        val bio = editBio.text.toString().trim()

        val userMap = hashMapOf(
            "name" to name,
            "age" to age,
            "address" to address,
            "contact" to contact,
            "bio" to bio
        )

        firestore.collection("users").document(userId)
            .update(userMap as Map<String, Any>)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile data saved", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, SettingActivity::class.java))
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save profile data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadProfileImage(profileImageUrl: String) {
        Glide.with(this)
            .load(profileImageUrl)
            .apply(RequestOptions.circleCropTransform()) // Apply round shape
            .into(profileImageView)
    }

    private fun loadProfileData() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    editName.setText(document.getString("name") ?: "")
                    editAge.setText(document.getString("age") ?: "")
                    editAddress.setText(document.getString("address") ?: "")
                    editContact.setText(document.getString("contact") ?: "")
                    editBio.setText(document.getString("bio") ?: "")
                    val profileImageUrl = document.getString("profileImage")
                    if (profileImageUrl != null) {
                        loadProfileImage(profileImageUrl)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load profile data", Toast.LENGTH_SHORT).show()
            }
    }
}
