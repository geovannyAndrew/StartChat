import SwiftUI

@main
struct StartChatApp: App {
    var body: some Scene {
        WindowGroup {
            MainViewControllerRepresentable()
                .ignoresSafeArea()
        }
    }
}

struct MainViewControllerRepresentable: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> MainViewController {
        return MainViewController()
    }

    func updateUIViewController(_ uiViewController: MainViewController, context: Context) {
    }
}