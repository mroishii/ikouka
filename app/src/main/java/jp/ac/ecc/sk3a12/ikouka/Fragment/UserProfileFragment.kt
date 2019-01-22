package jp.ac.ecc.sk3a12.ikouka.Fragment

import android.content.Context
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView

import jp.ac.ecc.sk3a12.ikouka.R
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter



/**
 * A simple [Fragment] subclass.
 * Use the [UserProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class UserProfileFragment : DialogFragment() {
    private lateinit var parent: Context
    private var userId: String = ""

    //FirebaseAuth
    private lateinit var mAuth: FirebaseAuth
    //Firestore
    private lateinit var mDb: FirebaseFirestore

    //LayoutElements
    private lateinit var username: TextView
    private lateinit var email: TextView
    private lateinit var image: CircleImageView
    private lateinit var qrCode: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString("userId")

        }
        mAuth = FirebaseAuth.getInstance()
        mDb = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Set-up header
        view.findViewById<ImageView>(R.id.header_exit).setOnClickListener {
            this.dismiss()
        }

        //Layout Elements
        username = view.findViewById(R.id.userProfileUsername)
        email = view.findViewById(R.id.userProfileEmail)
        image = view.findViewById(R.id.userProfileImage)
        qrCode = view.findViewById(R.id.userProfileQR)

        mDb.collection("Users").document(userId).get()
                .addOnSuccessListener {
                    username.text = it.getString("userName")
                    email.text = it.getString("email")
                    if (it.getString("image") != "default") {
                        Glide.with(parent)
                                .load(it.getString("image"))
                                .into(image as ImageView)
                    }

                    //generate qr code
                    val multiFormatWriter = MultiFormatWriter()
                    try {
                        val bitMatrix = multiFormatWriter.encode(userId, BarcodeFormat.QR_CODE, 200, 200)
                        val barcodeEncoder = BarcodeEncoder()
                        val bitmap = barcodeEncoder.createBitmap(bitMatrix)
                        qrCode.setImageBitmap(bitmap)
                    } catch (e: WriterException) {
                        e.printStackTrace()
                    }

                }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserProfileFragment.
         */
        @JvmStatic
        fun newInstance(context: Context, userId: String) =
                UserProfileFragment().apply {
                    arguments = Bundle().apply {
                        putString("userId", userId)
                    }
                    parent = context
                }
    }
}
