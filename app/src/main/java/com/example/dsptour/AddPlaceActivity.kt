package com.example.dsptour

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
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

class AddPlaceActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var placeImageView: ImageView
    private lateinit var editPlaceName: EditText
    private lateinit var editPlacePrice: EditText
    private lateinit var categoryRadioGroup: RadioGroup
    private lateinit var btnPickImage: Button
    private lateinit var btnAddPlace: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    private lateinit var imageActivityResultLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null // Variable to hold the selected image URI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_place) // Ensure the correct layout name

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        // Initialize UI components
        val toolbar = findViewById<Toolbar>(R.id.admin_toolbar)
        placeImageView = findViewById(R.id.place_image)
        editPlaceName = findViewById(R.id.et_place_name)
        editPlacePrice = findViewById(R.id.et_place_price)
        categoryRadioGroup = findViewById(R.id.category_radio_group)
        btnPickImage = findViewById(R.id.btn_pick_image)
        btnAddPlace = findViewById(R.id.btn_add_place)

        // Set up Toolbar and DrawerLayout
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.admin_drawer_layout)
        navigationView = findViewById(R.id.admin_nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> startActivity(Intent(this, AdminActivity::class.java))
                R.id.logout -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                R.id.nav_viewPlace -> {
                    startActivity(Intent(this, ViewPlaceActivity::class.java))
                    finish()
                }
                R.id.nav_confirm -> {
                    startActivity(Intent(this, BookingRequestActivity::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Set up image picker
        imageActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                selectedImageUri = result.data?.data // Save the image URI
                placeImageView.setImageURI(selectedImageUri) // Set the image in ImageView
            }
        }

        btnPickImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imageActivityResultLauncher.launch(intent)
        }

        btnAddPlace.setOnClickListener {
            uploadPlaceData()
        }
    }

    private fun uploadPlaceData() {
        val placeName = editPlaceName.text.toString().trim()
        val placePrice = editPlacePrice.text.toString().trim()
        val selectedCategoryId = categoryRadioGroup.checkedRadioButtonId
        val category = findViewById<RadioButton>(selectedCategoryId)?.text.toString()

        if (placeName.isEmpty() || placePrice.isEmpty()  || selectedImageUri == null) {
            Toast.makeText(this, "Please fill all fields and pick an image", Toast.LENGTH_SHORT).show()
            return
        }

        val placeRef = firestore.collection("places").document()
        val placeId = placeRef.id

        // Upload image to Firebase Storage
        val imageRef = storageReference.child("places/$placeId.jpg")
        val uploadTask = imageRef.putFile(selectedImageUri!!)
        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val placeData = hashMapOf(
                    "name" to placeName,
                    "price" to placePrice,
                    "category" to category,
                    "imageUri" to uri.toString(),
                    "placeId" to placeId
                )

                // Save place data to Firestore
                placeRef.set(placeData).addOnSuccessListener {
                    Toast.makeText(this, "Place added successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, ViewPlaceActivity::class.java))
                    finish()
                }.addOnFailureListener { e ->
                    Toast.makeText(this, "Error adding place: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Error uploading image: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
