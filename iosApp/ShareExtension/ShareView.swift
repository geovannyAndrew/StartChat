import SwiftUI

struct ShareView: View {
    var previewText: String = ""
    var isLoaded: Bool = false
    var onConfirm: () -> Void = {
    }
    var onCancel: () -> Void = {
    }

    var body: some View {
        VStack(spacing: 16) {
            Text("Share to Start Chat")
                .font(.headline)

            if !previewText.isEmpty {
                Text(previewText)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                    .lineLimit(4)
                    .padding()
                    .background(Color(.systemGray6))
                    .cornerRadius(8)
            }

            if isLoaded {
                Button(action: onConfirm) {
                    Text("Open in Start Chat")
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.green)
                        .foregroundColor(.white)
                        .cornerRadius(10)
                }
            }

            Button(action: onCancel) {
                Text("Cancel")
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color(.systemGray5))
                    .foregroundColor(.primary)
                    .cornerRadius(10)
            }
        }
        .padding()
    }
}
