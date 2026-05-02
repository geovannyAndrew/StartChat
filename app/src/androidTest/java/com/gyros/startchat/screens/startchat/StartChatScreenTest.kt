package com.gyros.startchat.screens.startchat

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.gyros.startchat.data.models.CountryCode
import org.junit.Rule
import org.junit.Test

class StartChatScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockCountryCode = CountryCode(
        code = "US",
        name = "United States",
        dialCode = "+1",
        flag = "US"
    )

    private fun buildState(
        countryCodes: List<CountryCode>? = listOf(mockCountryCode),
        selectedCountryCode: CountryCode? = mockCountryCode,
        phoneNumber: String = "",
        onStartChat: ((CountryCode?, String) -> Unit)? = null,
        onEditTextChange: (String) -> Unit = {},
        numbersOnClipBoard: List<String>? = null
    ) = StartChatViewModel.StartChatState(
        countryCodes = countryCodes,
        selectedCountryCode = selectedCountryCode,
        phoneNumber = phoneNumber,
        onStartChat = onStartChat,
        onEditTextChange = onEditTextChange,
        numbersOnClipBoard = numbersOnClipBoard
    )

    // region TopAppBar

    @Test
    fun whenNotDialog_navigationIconIsDisplayed() {
        composeTestRule.setContent {
            StartChatScreen(state = buildState(), isDialog = false)
        }

        composeTestRule.onNodeWithContentDescription("Open main menu").assertIsDisplayed()
    }

    @Test
    fun whenIsDialog_navigationIconDoesNotExist() {
        composeTestRule.setContent {
            StartChatScreen(state = buildState(), isDialog = true)
        }

        composeTestRule.onNodeWithContentDescription("Open main menu").assertDoesNotExist()
    }

    @Test
    fun whenNotDialog_clickingNavigationIconInvokesCallback() {
        var invoked = false

        composeTestRule.setContent {
            StartChatScreen(
                state = buildState(),
                isDialog = false,
                onNavigationIconClick = { invoked = true }
            )
        }

        composeTestRule.onNodeWithContentDescription("Open main menu").performClick()

        assert(invoked) { "onNavigationIconClick was not called" }
    }

    // endregion

    // region Clipboard section

    @Test
    fun whenClipboardNumbersIsNull_clipboardSectionIsAbsent() {
        composeTestRule.setContent {
            StartChatScreen(state = buildState(numbersOnClipBoard = null))
        }

        composeTestRule.onNodeWithText("Use Number from Clipboard:").assertDoesNotExist()
    }

    @Test
    fun whenClipboardNumbersIsEmpty_clipboardSectionIsAbsent() {
        composeTestRule.setContent {
            StartChatScreen(state = buildState(numbersOnClipBoard = emptyList()))
        }

        composeTestRule.onNodeWithText("Use Number from Clipboard:").assertDoesNotExist()
    }

    @Test
    fun whenClipboardNumbersHasEntries_clipboardSectionHeaderIsDisplayed() {
        composeTestRule.setContent {
            StartChatScreen(state = buildState(numbersOnClipBoard = listOf("1234567890")))
        }

        composeTestRule.onNodeWithText("Use Number from Clipboard:").assertIsDisplayed()
    }

    @Test
    fun whenClipboardNumbersHasEntries_eachNumberIsDisplayed() {
        val numbers = listOf("1234567890", "+14155552671")

        composeTestRule.setContent {
            StartChatScreen(state = buildState(numbersOnClipBoard = numbers))
        }

        numbers.forEach { number ->
            composeTestRule.onNodeWithText(number).assertIsDisplayed()
        }
    }

    @Test
    fun whenClipboardNumberClicked_onEditTextChangeIsCalledWithThatNumber() {
        val number = "1234567890"
        var captured: String? = null

        composeTestRule.setContent {
            StartChatScreen(
                state = buildState(
                    numbersOnClipBoard = listOf(number),
                    onEditTextChange = { captured = it }
                )
            )
        }

        composeTestRule.onNodeWithText(number).performClick()

        assert(captured == number) { "Expected $number but got $captured" }
    }

    // endregion

    // region Country code section

    @Test
    fun whenCountryCodesIsNull_countryCodeLabelIsDisplayed() {
        composeTestRule.setContent {
            StartChatScreen(state = buildState(countryCodes = null))
        }

        composeTestRule.onNodeWithText("Number with country code:").assertIsDisplayed()
    }

    @Test
    fun whenCountryCodesIsNotNull_countryCodeLabelDoesNotExist() {
        composeTestRule.setContent {
            StartChatScreen(state = buildState(countryCodes = listOf(mockCountryCode)))
        }

        composeTestRule.onNodeWithText("Number with country code:").assertDoesNotExist()
    }

    @Test
    fun whenNoCountrySelected_dropdownShowsSelectCountryCodeHint() {
        composeTestRule.setContent {
            StartChatScreen(
                state = buildState(
                    countryCodes = listOf(mockCountryCode),
                    selectedCountryCode = null
                )
            )
        }

        composeTestRule.onNodeWithText("Select Country Code").assertIsDisplayed()
    }

    @Test
    fun whenCountrySelected_dropdownShowsCountryName() {
        composeTestRule.setContent {
            StartChatScreen(
                state = buildState(selectedCountryCode = mockCountryCode)
            )
        }

        composeTestRule.onNodeWithText(mockCountryCode.name).assertIsDisplayed()
    }

    // endregion

    // region Start Chat button
    // isDialog=true avoids "Start Chat" text collision with TopAppBar title

    @Test
    fun whenOnStartChatIsNull_startChatButtonIsDisabled() {
        composeTestRule.setContent {
            StartChatScreen(
                state = buildState(onStartChat = null),
                isDialog = true
            )
        }

        composeTestRule.onNodeWithText("Start Chat").assertIsNotEnabled()
    }

    @Test
    fun whenOnStartChatIsNotNull_startChatButtonIsEnabled() {
        composeTestRule.setContent {
            StartChatScreen(
                state = buildState(onStartChat = { _, _ -> }),
                isDialog = true
            )
        }

        composeTestRule.onNodeWithText("Start Chat").assertIsEnabled()
    }

    @Test
    fun whenStartChatButtonClicked_onStartChatIsCalledWithCountryCodeAndPhoneNumber() {
        val phoneNumber = "1234567890"
        var capturedCountryCode: CountryCode? = CountryCode("", "", "", "")
        var capturedPhone: String? = null

        composeTestRule.setContent {
            StartChatScreen(
                state = buildState(
                    selectedCountryCode = mockCountryCode,
                    phoneNumber = phoneNumber,
                    onStartChat = { cc, phone ->
                        capturedCountryCode = cc
                        capturedPhone = phone
                    }
                ),
                isDialog = true
            )
        }

        composeTestRule.onNodeWithText("Start Chat").performClick()

        assert(capturedCountryCode == mockCountryCode) {
            "Expected $mockCountryCode but got $capturedCountryCode"
        }
        assert(capturedPhone == phoneNumber) {
            "Expected $phoneNumber but got $capturedPhone"
        }
    }

    // endregion
}
