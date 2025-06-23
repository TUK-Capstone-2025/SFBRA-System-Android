package com.example.sfbra_system_android.ui.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.sfbra_system_android.R
import com.example.sfbra_system_android.data.viewmodels.ProfileUpdateViewModel
import com.example.sfbra_system_android.data.SharedPreferencesHelper
import com.example.sfbra_system_android.data.viewmodels.ProfileViewModel
import com.example.sfbra_system_android.ui.activities.LoginActivity
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.yalantis.ucrop.UCrop
import java.io.File

// 내 프로필 화면
class MyProfileFragment : Fragment() {
    private val userViewModel: ProfileViewModel by viewModels() // 사용자 정보를 가져오는 뷰 모델
    private val profileUpdateViewModel: ProfileUpdateViewModel by viewModels() // 프로필 정보를 업데이트하는 뷰 모델
    private lateinit var name: TextView
    private lateinit var nickname: TextView
    private lateinit var id: TextView
    private lateinit var profileImage: ImageView
    private var imageUri: Uri? = null
    private val REQUEST_IMAGE_PICK = 1001
    private val REQUEST_CROP_IMAGE = UCrop.REQUEST_CROP

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_profile, container, false)

        val changeAvatar = view.findViewById<TextView>(R.id.change_avatar)
        val changeNickname = view.findViewById<TextView>(R.id.change_nickname)
        val changeId = view.findViewById<TextView>(R.id.change_id)
        val changePassword = view.findViewById<TextView>(R.id.change_password)

        changeAvatar.setOnClickListener { pickImage() }
        changeNickname.setOnClickListener { changeNickname() }
        changeId.setOnClickListener { changeLoginID() }
        changePassword.setOnClickListener { changePassword() }

        name = view.findViewById(R.id.name_text)
        nickname = view.findViewById(R.id.nickname_text)
        id = view.findViewById(R.id.id_text)
        profileImage = view.findViewById(R.id.profile_image)

        getInformation()

        return view
    }

    // 이미지 선택 함수
    private fun pickImage() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    // UCrop 사용을 위한 content:// -> 실제 파일로 복사하기
    private fun copyUriToFile(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File.createTempFile("upload_", ".jpg", context.cacheDir)
            val outputStream = file.outputStream()

            inputStream?.copyTo(outputStream)

            inputStream?.close()
            outputStream.close()

            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // 이미지 선택 결과 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_IMAGE_PICK -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val sourceUri = data.data
                    if (sourceUri != null) {
                        val copiedFile = copyUriToFile(requireContext(), sourceUri)
                        if (copiedFile != null) {
                            val copiedUri = Uri.fromFile(copiedFile)
                            // 크롭용 임시 파일 생성
                            val destinationUri = Uri.fromFile(File(requireContext().cacheDir, "cropped_profile_image.jpg"))

                            UCrop.of(copiedUri, destinationUri)
                                .withAspectRatio(1f, 1f)
                                .withMaxResultSize(512, 512)
                                .start(requireContext(), this)
                        } else {
                            Toast.makeText(requireContext(), "이미지를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            REQUEST_CROP_IMAGE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val resultUri = UCrop.getOutput(data)
                    if (resultUri != null) {
                        imageUri = resultUri

                        // 서버로 전송
                        changeAvatar()
                    }
                } else if (resultCode == UCrop.RESULT_ERROR) {
                    val cropError = UCrop.getError(data!!)
                    cropError?.printStackTrace()
                    Toast.makeText(requireContext(), "이미지 자르기 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    // 프로필 사진 변경 함수
    private fun changeAvatar() {
        // 이미지 URI가 설정되지 않은 경우 처리
        if (imageUri == null) {
            Toast.makeText(requireContext(), "이미지를 먼저 선택해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        profileUpdateViewModel.changeAvatar(imageUri!!)

        profileUpdateViewModel.changeAvatarResponse.observe(viewLifecycleOwner, Observer { response ->
            if (response != null && response.success) {
                Toast.makeText(requireContext(), "프로필 사진을 변경하였습니다.", Toast.LENGTH_SHORT).show()
                getInformation()
                profileUpdateViewModel.clearLiveData()
            }
            else if (response != null){
                Toast.makeText(requireContext(), "프로필 사진 변경에 실패했습니다.", Toast.LENGTH_SHORT).show()
                profileUpdateViewModel.clearLiveData()
            }
        })
    }

    // 닉네임 변경 함수
    private fun changeNickname() {
        val editText = EditText(requireContext())
        AlertDialog.Builder(requireContext())
            .setTitle("닉네임 변경")
            .setView(editText)
            .setPositiveButton("변경") { _, _ ->
                val newNickname = editText.text.toString()
                // 닉네임 변경 요청
                profileUpdateViewModel.changeNickResponse.removeObservers(viewLifecycleOwner)
                profileUpdateViewModel.changeNickname(newNickname)

                // 닉네임 변경 후, 변경 완료된 시점에 사용자 정보를 새로 불러오기
                profileUpdateViewModel.changeNickResponse.observe(viewLifecycleOwner, Observer { response ->
                    if (response != null && response.success) {
                        Toast.makeText(requireContext(), "닉네임을 변경하였습니다.", Toast.LENGTH_SHORT).show()
                        getInformation()
                        profileUpdateViewModel.clearLiveData()
                    } else if (response != null) {
                        Toast.makeText(requireContext(), "닉네임 변경에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        profileUpdateViewModel.clearLiveData()
                    }
                })
            }
            .setNegativeButton("취소", null)
            .show()
    }

    // 아이디 변경 함수(중복,동일한 경우 변경 안됨)
    private fun changeLoginID() {
        val editText = EditText(requireContext())
        AlertDialog.Builder(requireContext())
            .setTitle("아이디 변경")
            .setView(editText)
            .setPositiveButton("변경") { _, _ ->
                val newUserId = editText.text.toString()
                // 아이디 변경 요청
                profileUpdateViewModel.changeIdResponse.removeObservers(viewLifecycleOwner)
                profileUpdateViewModel.changeUserId(newUserId)

                // 아이디 변경 후, 재로그인
                profileUpdateViewModel.changeIdResponse.observe(viewLifecycleOwner, Observer { response ->
                    if (response != null && response.success) {
                        Toast.makeText(requireContext(), "아이디를 변경하였습니다.\n다시 로그인해주십시오.", Toast.LENGTH_SHORT).show()

                        SharedPreferencesHelper.clearToken(requireContext()) // 토큰 초기화
                        startActivity(Intent(requireContext(), LoginActivity::class.java)) // 로그인 화면으로 이동
                        activity?.finish() // 액티비티 종료
                    } else if (response != null) {
                        val gson = Gson()
                        val jsonObject = gson.fromJson(response.message, JsonObject::class.java)

                        val errorMessage = jsonObject.get("message").asString
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                        profileUpdateViewModel.clearLiveData()
                    }
                })
            }
            .setNegativeButton("취소", null)
            .show()
    }

    // 비밀번호 변경 함수(동일한 경우 변경 안됨)
    private fun changePassword() {
        val layout = layoutInflater.inflate(R.layout.dialog_change_password, null)
        val currentPasswordInput = layout.findViewById<EditText>(R.id.current_password)
        val newPasswordInput = layout.findViewById<EditText>(R.id.new_password)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("비밀번호 변경")
            .setView(layout)
            .setPositiveButton("변경", null) // 여기서는 null로 설정하여 아무 동작도 하지 않음
            .setNegativeButton("취소", null)
            .create()

        dialog.show() // 다이얼로그를 표시

        // "변경" 버튼을 눌렀을 때를 따로 처리
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val currentPassword = currentPasswordInput.text.toString()
            val newPassword = newPasswordInput.text.toString()

            // 비밀번호가 동일한 경우, 토스트 표시 후 다이얼로그는 그대로 두기
            if (currentPassword == newPassword) {
                Toast.makeText(requireContext(), "비밀번호가 같습니다.", Toast.LENGTH_SHORT).show()
            } else {
                // 비밀번호가 다를 때 비밀번호 변경 요청
                profileUpdateViewModel.changePassResponse.removeObservers(viewLifecycleOwner)
                profileUpdateViewModel.changePassword(currentPassword, newPassword)

                profileUpdateViewModel.changePassResponse.observe(viewLifecycleOwner, Observer { response ->
                    if (response != null && response.success) {
                        Toast.makeText(requireContext(), "비밀번호를 변경하였습니다.", Toast.LENGTH_SHORT).show()
                        profileUpdateViewModel.clearLiveData()
                    } else if (response != null) {
                        Toast.makeText(requireContext(), "비밀번호 변경에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        profileUpdateViewModel.clearLiveData()
                    }
                })
                dialog.dismiss()  // 비밀번호 변경 완료 후 다이얼로그 닫음
            }
        }
    }

    // 사용자 정보 불러오기 함수
    fun getInformation() {
        // 사용자 정보 가져오기
        userViewModel.getUserInfo()

        // UI 업데이트
        userViewModel.userInfo.observe(viewLifecycleOwner, Observer { user ->
            if (user != null && user.success) {
                name.text = user.data.name
                nickname.text = user.data.nickname
                id.text = "id: ${user.data.userId}"

                val profileUrl = user.data.profileImageUrl
                if (!profileUrl.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(profileUrl)
                        .circleCrop()
                        .placeholder(R.drawable.ic_user)
                        .error(R.drawable.ic_user)
                        .into(profileImage)
                }
            } else {
                Toast.makeText(requireContext(), "사용자 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}