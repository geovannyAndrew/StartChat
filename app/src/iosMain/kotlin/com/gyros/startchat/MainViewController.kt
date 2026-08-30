package com.gyros.startchat

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import platform.UIKit.willEnterForegroundNotification
import platform.Foundation.NSNotificationCenter

class MainViewController : UIViewController {
    private var notificationObserver: Any? = null

    init {
        initKoin()
    }

    override fun viewDidLoad() {
        super.viewDidLoad()

        val composeViewController = ComposeUIViewController {
            MainNavHost(
                onNavigationIconClick = { }
            )
        }

        addChildViewController(composeViewController)
        view.addSubview(composeViewController.view)
        composeViewController.view.frame = view.bounds
        composeViewController.view.autoresizingMask =
            UIViewController.UIViewAutoresizingFlexibleWidth or UIViewController.UIViewAutoresizingFlexibleHeight
        composeViewController.didMove(toParentViewController = this)

        observeForeground()
    }

    private fun observeForeground() {
        val notificationCenter = NSNotificationCenter.defaultCenter
        notificationObserver = notificationCenter.addObserver(
            forName = willEnterForegroundNotification,
            `object` = null,
            queue = null
        ) { _ ->
            // Handle foreground event - clipboard check would go here
        }
    }

    override fun viewWillDisappear(animated: Boolean) {
        super.viewWillDisappear(animated)
        notificationObserver?.let {
            NSNotificationCenter.defaultCenter.removeObserver(it)
        }
    }
}