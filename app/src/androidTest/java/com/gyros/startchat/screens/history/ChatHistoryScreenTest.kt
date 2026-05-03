package com.gyros.startchat.screens.history

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.gyros.startchat.data.models.ChatHistoryEntry
import org.junit.Rule
import org.junit.Test

class ChatHistoryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun buildState(
        entries: List<ChatHistoryEntry> = emptyList(),
        onEntryClicked: ((ChatHistoryEntry) -> Unit)? = null
    ) = ChatHistoryViewModel.ChatHistoryState(
        entries = entries,
        onEntryClicked = onEntryClicked
    )

    // region Empty state

    @Test
    fun whenEntriesIsEmpty_emptyStateLabelIsDisplayed() {
        composeTestRule.setContent {
            ChatHistoryScreen(state = buildState())
        }

        composeTestRule.onNodeWithText("No chat history yet.").assertIsDisplayed()
    }

    @Test
    fun whenEntriesIsNotEmpty_emptyStateLabelDoesNotExist() {
        composeTestRule.setContent {
            ChatHistoryScreen(
                state = buildState(entries = listOf(ChatHistoryEntry("+14155552671", 1000L)))
            )
        }

        composeTestRule.onNodeWithText("No chat history yet.").assertDoesNotExist()
    }

    // endregion

    // region Entry list

    @Test
    fun whenEntriesIsNotEmpty_eachPhoneNumberIsDisplayed() {
        val entries = listOf(
            ChatHistoryEntry("+14155552671", 2000L),
            ChatHistoryEntry("+573212345678", 1000L)
        )

        composeTestRule.setContent {
            ChatHistoryScreen(state = buildState(entries = entries))
        }

        entries.forEach { entry ->
            composeTestRule.onNodeWithText(entry.phoneNumber).assertIsDisplayed()
        }
    }

    @Test
    fun whenEntryIsClicked_onEntryClickedIsCalledWithThatEntry() {
        val entry = ChatHistoryEntry("+14155552671", 1000L)
        var capturedEntry: ChatHistoryEntry? = null

        composeTestRule.setContent {
            ChatHistoryScreen(
                state = buildState(
                    entries = listOf(entry),
                    onEntryClicked = { capturedEntry = it }
                )
            )
        }

        composeTestRule.onNodeWithText(entry.phoneNumber).performClick()

        assertEquals(entry, capturedEntry) { "Expected $entry but got $capturedEntry" }
    }

    // endregion

    // region TopAppBar

    @Test
    fun navigationIconIsDisplayed() {
        composeTestRule.setContent {
            ChatHistoryScreen(state = buildState())
        }

        composeTestRule.onNodeWithContentDescription("Open main menu").assertIsDisplayed()
    }

    @Test
    fun clickingNavigationIconInvokesCallback() {
        var invoked = false

        composeTestRule.setContent {
            ChatHistoryScreen(
                state = buildState(),
                onNavigationIconClick = { invoked = true }
            )
        }

        composeTestRule.onNodeWithContentDescription("Open main menu").performClick()

        assert(invoked) { "onNavigationIconClick was not called" }
    }

    // endregion
}

private fun assertEquals(expected: Any?, actual: Any?, message: () -> String) {
    assert(expected == actual, message)
}
