import SwiftUI
import StartChat

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
    func makeUIViewController(context: Context) -> UIViewController {
        return MainViewControllerKt.createMainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
    }
}
