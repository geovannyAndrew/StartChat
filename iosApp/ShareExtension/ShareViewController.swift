import SwiftUI
import UIKit

class ShareViewController: UIViewController {
    private let storeSuiteName = "group.com.gyros.startchat"
    private let keyPendingText = "pendingSharedText"
    private let keyPendingTimestamp = "pendingSharedTimestamp"

    private var sharedText: String?
    private let hostingController: UIHostingController<ShareView>

    init() {
        hostingController = UIHostingController(rootView: ShareView())
        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        loadSharedContent()
    }

    private func loadSharedContent() {
        guard let extensionItems = extensionContext?.inputItems as? [NSExtensionItem] else {
            showError()
            return
        }

        guard let item = extensionItems.first else {
            showError()
            return
        }

        let dispatchGroup = DispatchGroup()

        if let textAttachment = item.attachments?.first(where: { $0.hasItemConformingToTypeIdentifier("public.plain-text") }) {
            dispatchGroup.enter()
            textAttachment.loadItem(forTypeIdentifier: "public.plain-text", options: nil) { [weak self] item, _ in
                if let text = item as? String {
                    self?.sharedText = text
                }
                dispatchGroup.leave()
            }
        } else if let urlAttachment = item.attachments?.first(where: { $0.hasItemConformingToTypeIdentifier("public.url") }) {
            dispatchGroup.enter()
            urlAttachment.loadItem(forTypeIdentifier: "public.url", options: nil) { [weak self] item, _ in
                if let url = item as? URL {
                    self?.sharedText = url.absoluteString
                }
                dispatchGroup.leave()
            }
        }

        dispatchGroup.notify(queue: .main) { [weak self] in
            guard let self = self else {
                return
            }
            if let text = self.sharedText {
                self.hostingController.rootView = ShareView(
                    previewText: String(text.prefix(200)),
                    isLoaded: true,
                    onConfirm: { self.confirm() },
                    onCancel: { self.cancel() }
                )
            } else {
                self.showError()
            }
        }

        addChild(hostingController)
        view.addSubview(hostingController.view)
        hostingController.view.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
                                        hostingController.view.topAnchor.constraint(equalTo: view.topAnchor),
                                        hostingController.view.bottomAnchor.constraint(equalTo: view.bottomAnchor),
                                        hostingController.view.leadingAnchor.constraint(equalTo: view.leadingAnchor),
                                        hostingController.view.trailingAnchor.constraint(equalTo: view.trailingAnchor)
                                    ])
        hostingController.didMove(toParent: self)
    }

    private func confirm() {
        guard let text = sharedText else {
            return
        }

        let defaults = UserDefaults(suiteName: storeSuiteName)
        defaults?.set(text, forKey: keyPendingText)
        defaults?.set(Int64(Date().timeIntervalSince1970 * 1000), forKey: keyPendingTimestamp)

        extensionContext?.completeRequest(returningItems: nil, completionHandler: nil)
    }

    private func cancel() {
        extensionContext?.cancelRequest(withError: NSError(domain: "com.gyros.startchat.share", code: 0, userInfo: nil))
    }

    private func showError() {
        hostingController.rootView = ShareView(
            previewText: "Unable to load shared content",
            isLoaded: false,
            onConfirm: {},
            onCancel: { [weak self] in self?.cancel() }
        )
    }
}
