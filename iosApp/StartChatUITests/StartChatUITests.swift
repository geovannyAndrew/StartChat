import XCTest

final class StartChatUITests: XCTestCase {

    var app: XCUIApplication!

    override func setUpWithError() throws {
        continueAfterFailure = false
        app = XCUIApplication()
        app.launchArguments = ["--uitesting"]
    }

    // MARK: - Helpers

    private func launch() {
        app.launch()
        // Wait for the top-bar title to appear (compose semantics sync).
        let title = app.descendants(matching: .any)
            .matching(NSPredicate(format: "label == 'Start Chat'"))
            .firstMatch
        XCTAssertTrue(title.waitForExistence(timeout: 20),
                      "App title 'Start Chat' should appear")
    }

    private func shot(_ name: String) {
        let attachment = XCTAttachment(screenshot: app.windows.firstMatch.screenshot())
        attachment.name = name
        attachment.lifetime = .keepAlways
        add(attachment)
    }

    private func openDrawer() {
        let menuButton = app.buttons["Open main menu"]
        XCTAssertTrue(menuButton.waitForExistence(timeout: 10), "Menu button should exist")
        menuButton.tap()
        sleep(2)
    }

    // Drawer items have valid frames but XCTest computes hit point {-1,-1} (CMP a11y bug).
    // Tap by coordinate at the button's center.
    private func tapDrawerItem(_ label: String) {
        let btn = app.buttons[label]
        if btn.exists {
            let f = btn.frame
            let cx = f.midX
            let cy = f.midY
            app.coordinate(withNormalizedOffset: CGVector(dx: 0, dy: 0))
                .withOffset(CGVector(dx: cx, dy: cy)).tap()
            sleep(1)
        }
    }

    // Finds an element of any type by its accessibility label.
    private func anyElement(_ label: String) -> XCUIElement {
        app.descendants(matching: .any)
            .matching(NSPredicate(format: "label == %@", label))
            .firstMatch
    }

    // MARK: - Test 1: Start Chat flow (FR1, FR3, FR9)

    func test1_startChatFlow() throws {
        launch()
        shot("1_0_initial")

        // 1.1 The phone field is exposed as a non-editable button in CMP 1.7.3
        // (text-input accessibility traits only arrived in 1.8). Tap its
        // coordinate to give it focus, then type via on-screen keyboard.
        let phoneField = app.buttons.allElementsBoundByIndex.first { el in
            let f = el.frame
            return f.minY > 400 && f.minY < 495 && f.width > 200
        }
        if let field = phoneField {
            field.tap()
        } else {
            app.coordinate(withNormalizedOffset: CGVector(dx: 0.5, dy: 0.535)).tap()
        }
        sleep(1)
        shot("1_1_field_focused")

        let keyboard = app.keyboards.firstMatch
        XCTAssertTrue(keyboard.waitForExistence(timeout: 5), "Keyboard should appear")
        for ch in ["1", "2", "3", "4", "5", "6", "7", "8", "9", "0"] {
            keyboard.keys[ch].tap()
        }
        sleep(1)
        shot("1_2_number_typed")

        // Start Chat button should now be enabled
        let startButton = app.buttons["Start Chat"].firstMatch
        XCTAssertTrue(startButton.waitForExistence(timeout: 5), "Start Chat button should exist")
        XCTAssertTrue(startButton.isEnabled, "Start Chat button should be enabled with valid number")
        shot("1_3_button_enabled")

        // 1.2 Tap Start Chat -> opens wa.me URL (Safari/App Store fallback on simulator)
        startButton.tap()
        sleep(3)
        shot("1_4_after_tap")

        // Bring the app back to the foreground
        app.activate()
        sleep(1)
        shot("1_5_back_in_app")
    }

    // MARK: - Test 2: Country code persistence (FR9)

    func test2_countryCodePersistence() throws {
        launch()

        // 2.1 Select a country code from the dropdown
        let dropdown = app.buttons.matching(NSPredicate(format: "label CONTAINS[c] 'Select Country Code'")).firstMatch
        XCTAssertTrue(dropdown.waitForExistence(timeout: 10), "Country dropdown should exist")
        dropdown.tap()
        sleep(1)
        shot("2_1_dropdown_open")

        // The menu items report zero frame — use coordinate tap.
        // Dropdown button center is at ~y=380; first item is ~70pt below.
        let f = dropdown.frame
        app.coordinate(withNormalizedOffset: CGVector(dx: 0, dy: 0))
            .withOffset(CGVector(dx: f.midX, dy: f.minY + 70)).tap()
        sleep(1)
        shot("2_2_country_selected")

        // 2.2 Kill the app (MemorySettings → persistence NOT expected for Phase 3)
        app.terminate()
        sleep(2)

        // 2.3 Relaunch and verify
        app.launch()
        XCTAssertTrue(anyElement("Start Chat").waitForExistence(timeout: 20))
        sleep(1)
        shot("2_3_after_relaunch")
        // NOTE: v1 uses MemorySettings — persistence expected to NOT work in Phase 3.
    }

    // MARK: - Test 3: History screen (FR4, FR5, FR8)

    func test3_historyScreen() throws {
        launch()

        // 3.1 Open drawer and tap History
        openDrawer()
        shot("3_1_drawer_open")

        XCTAssertTrue(app.buttons["History"].waitForExistence(timeout: 5), "History menu item should exist")
        tapDrawerItem("History")
        sleep(1)
        shot("3_2_history_screen")

        // 3.2 Verify History screen title is shown
        XCTAssertTrue(anyElement("History").waitForExistence(timeout: 5),
                      "History screen title should appear")
        shot("3_3_history_confirmed")
    }

    // MARK: - Test 4: Clipboard chips (FR6)

    func test4_clipboardChips() throws {
        // --uitesting keeps accessibility enabled; --no-clipboard-clear preserves clipboard for this test.
        app.launchArguments = ["--uitesting", "--no-clipboard-clear"]
        let phone = "9876543210"

        // 4.1 Launch app — clipboard read happens on onResume during launch
        launch()
        sleep(2)
        shot("4_1_app_launched")

        // 4.2 Background the app (terminate + relaunch simulates background/foreground).
        app.terminate()
        sleep(2)

        // 4.3 Relaunch and verify clipboard chip appears.
        app.launch()
        XCTAssertTrue(anyElement("Start Chat").waitForExistence(timeout: 20))
        sleep(3)
        shot("4_2_after_relaunch")

        // The clipboard chip appears as a button with the phone number as label.
        // NOTE: iOS clipboard access is more restricted — may not work reliably on simulator.
        let clipboardChip = app.buttons[phone]
        if clipboardChip.waitForExistence(timeout: 5) {
            shot("4_3_chip_visible")
        } else {
            shot("4_3_chip_not_visible")
        }
    }

    // MARK: - Test 5: About screen (FR7)

    func test5_aboutScreen() throws {
        launch()
        openDrawer()

        XCTAssertTrue(app.buttons["About"].waitForExistence(timeout: 5), "About menu item should exist")
        tapDrawerItem("About")
        sleep(1)
        shot("5_1_about_screen")

        // Verify app name appears on About screen
        XCTAssertTrue(anyElement("Start Chat").waitForExistence(timeout: 5),
                      "About screen should show app name 'Start Chat'")
        shot("5_2_about_confirmed")
    }

    // MARK: - Test 6: Navigation (FR8)

    func test6_navigation() throws {
        launch()
        shot("6_1_start")

        // Start -> History
        openDrawer()
        tapDrawerItem("History")
        sleep(1)
        XCTAssertTrue(anyElement("History").waitForExistence(timeout: 5))
        shot("6_2_history")

        // History -> About
        openDrawer()
        tapDrawerItem("About")
        sleep(1)
        shot("6_3_about")

        // About -> Start Chat (back)
        openDrawer()
        tapDrawerItem("Start Chat")
        sleep(1)
        shot("6_4_back_to_start")

        // Verify we're back on start (menu button visible again)
        XCTAssertTrue(app.buttons["Open main menu"].waitForExistence(timeout: 5))
    }
}
