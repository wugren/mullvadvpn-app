package net.mullvad.mullvadvpn.test.mockapi

import androidx.test.runner.AndroidJUnit4
import androidx.test.uiautomator.By
import junit.framework.TestCase.assertNotNull
import net.mullvad.mullvadvpn.test.common.extension.clickAgreeOnPrivacyDisclaimer
import net.mullvad.mullvadvpn.test.common.extension.clickAllowOnNotificationPermissionPromptIfApiLevel33AndAbove
import net.mullvad.mullvadvpn.test.common.extension.dismissChangelogDialogIfShown
import net.mullvad.mullvadvpn.test.common.extension.findObjectWithTimeout
import net.mullvad.mullvadvpn.test.mockapi.util.currentUtcTimeWithOffsetZero
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LogoutMockApiTest : MockApiTest() {

    @Test
    fun testLoginWithValidCredentialsToUnexpiredAccountAndLogout() {
        // Arrange
        val validAccountToken = "1234123412341234"
        apiDispatcher.apply {
            expectedAccountToken = validAccountToken
            accountExpiry = currentUtcTimeWithOffsetZero().plusMonths(1)
        }

        // Act
        app.launch(endpoint)
        device.clickAgreeOnPrivacyDisclaimer()
        device.clickAllowOnNotificationPermissionPromptIfApiLevel33AndAbove()
        device.dismissChangelogDialogIfShown()
        app.waitForLoginPrompt()
        app.attemptLogin(validAccountToken)
        device.findObjectWithTimeout(By.text("UNSECURED CONNECTION"))
        app.clickAccountCog()
        app.clickActionButtonByText("Log out")

        // Assert
        assertNotNull(device.findObjectWithTimeout(By.text("Login")))
    }
}
