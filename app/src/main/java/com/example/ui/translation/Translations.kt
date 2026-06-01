package com.example.ui.translation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

enum class Language {
    ENGLISH,
    HINDI
}

enum class TextKey {
    APP_NAME,
    DASHBOARD_SUBTITLE,
    NAVI_MUMBAI,
    MUMBAI,
    MEMBER_DIRECTORY,
    CONTACT_US,
    BACKEND_MANAGE,
    NAME_LABEL,
    PHONE_LABEL,
    FEEDBACK_LABEL,
    SUBMIT_BUTTON,
    SUB_COMMUNITY_LABEL,
    CHOOSE_SUBC_PROMPT,
    SELECT_AVATAR,
    ADD_MEMBER_SUCCESS,
    DELETE_MEMBER,
    EMPTY_DIRECTORY,
    SAVE_MEMBER_BUTTON,
    FEEDBACK_INFO,
    ENTER_VALID_PHONE,
    ENTER_ALL_FIELDS,
    DIAL_PROMPT,
    LANGUAGE_LABEL,
    BACK_BUTTON,
    ENTER_NAME_PLACEHOLDER,
    ENTER_PHONE_PLACEHOLDER,
    ENTER_FEEDBACK_PLACEHOLDER,
    BACKEND_MANAGE_SUBTITLE,
    CLOSE_BUTTON,
    EMAIL_CLIENT_PROMPT,
    JALOR
}

object Translations {
    private val translations = mapOf(
        Language.ENGLISH to mapOf(
            TextKey.APP_NAME to "Kumawat Samaaj",
            TextKey.DASHBOARD_SUBTITLE to "Community Connect Portal",
            TextKey.NAVI_MUMBAI to "Kumawat Samaj - Navi Mumbai",
            TextKey.MUMBAI to "Kumawat Samaj-Mumbai",
            TextKey.MEMBER_DIRECTORY to "Member Directory",
            TextKey.CONTACT_US to "Contact Us & Feedback",
            TextKey.BACKEND_MANAGE to "Backend: Manage Members",
            TextKey.NAME_LABEL to "Name",
            TextKey.PHONE_LABEL to "Contact Number",
            TextKey.FEEDBACK_LABEL to "Feedback / Issue Details",
            TextKey.SUBMIT_BUTTON to "Submit Feedback",
            TextKey.SUB_COMMUNITY_LABEL to "Sub Community",
            TextKey.CHOOSE_SUBC_PROMPT to "Select Sub Community Category",
            TextKey.SELECT_AVATAR to "Choose Profile Avatar",
            TextKey.ADD_MEMBER_SUCCESS to "Member added successfully!",
            TextKey.DELETE_MEMBER to "Delete Member",
            TextKey.EMPTY_DIRECTORY to "No members registered yet in this directory.",
            TextKey.SAVE_MEMBER_BUTTON to "Add Member into Database",
            TextKey.FEEDBACK_INFO to "Fill the form to send community feedback",
            TextKey.ENTER_VALID_PHONE to "Please enter a valid 10-digit number",
            TextKey.ENTER_ALL_FIELDS to "Please fill all required fields",
            TextKey.DIAL_PROMPT to "Call Member",
            TextKey.LANGUAGE_LABEL to "Select Language • भाषा चुनें",
            TextKey.BACK_BUTTON to "Back",
            TextKey.ENTER_NAME_PLACEHOLDER to "e.g. Ramesh Kumawat",
            TextKey.ENTER_PHONE_PLACEHOLDER to "10-digit mobile number",
            TextKey.ENTER_FEEDBACK_PLACEHOLDER to "Describe feedback or report issue...",
            TextKey.BACKEND_MANAGE_SUBTITLE to "Insert new community members with photos into directories.",
            TextKey.CLOSE_BUTTON to "Close",
            TextKey.EMAIL_CLIENT_PROMPT to "Choose an Email app to send feedback to kumawatsamaj.co.in@gmail.com",
            TextKey.JALOR to "Kumawat Shikshan and Samajik Sansthan - Ahore (Jalor)"
        ),
        Language.HINDI to mapOf(
            TextKey.APP_NAME to "कुमावत समाज",
            TextKey.DASHBOARD_SUBTITLE to "सामुदायिक संपर्क पोर्टल",
            TextKey.NAVI_MUMBAI to "कुमावत समाज - नवी मुंबई",
            TextKey.MUMBAI to "कुमावत समाज-मुंबई",
            TextKey.MEMBER_DIRECTORY to "सदस्य निर्देशिका",
            TextKey.CONTACT_US to "हमसे संपर्क करें और फीडबैक",
            TextKey.BACKEND_MANAGE to "बैकएंड: सदस्यों का प्रबंधन",
            TextKey.NAME_LABEL to "नाम",
            TextKey.PHONE_LABEL to "संपर्क नंबर",
            TextKey.FEEDBACK_LABEL to "फीडबैक / समस्या विवरण",
            TextKey.SUBMIT_BUTTON to "फीडबैक सबमिट करें",
            TextKey.SUB_COMMUNITY_LABEL to "उप-समुदाय",
            TextKey.CHOOSE_SUBC_PROMPT to "उप-समुदाय श्रेणी चुनें",
            TextKey.SELECT_AVATAR to "प्रोफ़ाइल अवतार चुनें",
            TextKey.ADD_MEMBER_SUCCESS to "सदस्य सफलतापूर्वक जोड़ा गया!",
            TextKey.DELETE_MEMBER to "सदस्य हटाएं",
            TextKey.EMPTY_DIRECTORY to "इस निर्देशिका में अभी तक कोई सदस्य पंजीकृत नहीं है।",
            TextKey.SAVE_MEMBER_BUTTON to "डाटाबेस में सदस्य जोड़ें",
            TextKey.FEEDBACK_INFO to "फीडबैक भेजने के लिए फॉर्म भरें",
            TextKey.ENTER_VALID_PHONE to "कृपया एक वैध 10-अंकीय नंबर दर्ज करें",
            TextKey.ENTER_ALL_FIELDS to "कृपया सभी आवश्यक फ़ील्ड भरें",
            TextKey.DIAL_PROMPT to "सदस्य को कॉल करें",
            TextKey.LANGUAGE_LABEL to "भाषा चुनें • Select Language",
            TextKey.BACK_BUTTON to "पीछे",
            TextKey.ENTER_NAME_PLACEHOLDER to "जैसे: रमेश कुमावत",
            TextKey.ENTER_PHONE_PLACEHOLDER to "10-अंकीय मोबाइल नंबर",
            TextKey.ENTER_FEEDBACK_PLACEHOLDER to "फीडबैक या समस्या का वर्णन करें...",
            TextKey.BACKEND_MANAGE_SUBTITLE to "निर्देशिकाओं में तस्वीरों के साथ नए समुदाय के सदस्यों को जोड़ें।",
            TextKey.CLOSE_BUTTON to "बंद करें",
            TextKey.EMAIL_CLIENT_PROMPT to "kumawatsamaj.co.in@gmail.com पर फीडबैक भेजने के लिए एक ईमेल ऐप चुनें",
            TextKey.JALOR to "कुमावत शिक्षण एवं सामाजिक संस्थान - आहोर (जालौर)"
        )
    )

    fun getString(key: TextKey, language: Language): String {
        return translations[language]?.get(key) ?: translations[Language.ENGLISH]?.get(key) ?: ""
    }
}

// Composition heritable Local for screen updates 
val LocalLanguage = compositionLocalOf { Language.ENGLISH }
