import SwiftUI

/// Animated organic blob visualization for the Longevity dashboard hero section.
/// Uses Fourier harmonics to create a natural-looking, morphing shape with
/// a radial green gradient and particle effects.
struct OrganicBlobView: View {
    let zyvaAge: Double
    let yearsYoungerOlder: Double
    var animationPhase: Double = 0
    var size: CGFloat = 260

    var body: some View {
        ZStack {
            // Outer glow
            OrganicShape(harmonics: 5, amplitude: 0.10, phase: animationPhase)
                .fill(
                    RadialGradient(
                        colors: [AppColors.longevityBlobGlow.opacity(0.3), .clear],
                        center: .center,
                        startRadius: size * 0.2,
                        endRadius: size * 0.55
                    )
                )
                .frame(width: size * 1.1, height: size * 1.1)
                .blur(radius: 25)

            // Main blob
            OrganicShape(harmonics: 5, amplitude: 0.10, phase: animationPhase)
                .fill(
                    RadialGradient(
                        colors: [
                            blobCenterColor.opacity(0.15),
                            blobEdgeColor.opacity(0.6),
                            blobEdgeColor.opacity(0.8),
                            blobEdgeColor.opacity(0.3),
                            .clear
                        ],
                        center: .center,
                        startRadius: 0,
                        endRadius: size * 0.48
                    )
                )
                .frame(width: size, height: size)

            // Inner glow ring
            OrganicShape(harmonics: 5, amplitude: 0.10, phase: animationPhase)
                .stroke(blobEdgeColor.opacity(0.5), lineWidth: 2)
                .frame(width: size * 0.92, height: size * 0.92)
                .blur(radius: 3)

            // Particle overlay
            particleCanvas
                .frame(width: size, height: size)
                .clipShape(OrganicShape(harmonics: 5, amplitude: 0.10, phase: animationPhase))

            // Center text
            VStack(spacing: 2) {
                Text(String(format: "%.1f", zyvaAge))
                    .font(.system(size: 48, weight: .bold, design: .rounded))
                    .foregroundStyle(AppColors.textPrimary)
                    .contentTransition(.numericText())

                Text("ZYVA AGE")
                    .font(.system(size: 12, weight: .semibold))
                    .foregroundStyle(AppColors.textSecondary)
                    .tracking(1.5)

                if abs(yearsYoungerOlder) > 0.1 {
                    Text(yearsLabel)
                        .font(.system(size: 15, weight: .medium))
                        .foregroundStyle(yearsYoungerOlder < 0 ? AppColors.longevityGreen : AppColors.longevityOrange)
                        .padding(.top, 2)
                }
            }
        }
    }

    private var yearsLabel: String {
        let absYears = abs(yearsYoungerOlder)
        let suffix = yearsYoungerOlder < 0 ? "years younger" : "years older"
        return String(format: "%.1f %@", absYears, suffix)
    }

    private var blobCenterColor: Color {
        paceColor.opacity(0.1)
    }

    private var blobEdgeColor: Color {
        yearsYoungerOlder < 0 ? AppColors.longevityGreen : AppColors.longevityOrange
    }

    private var paceColor: Color {
        yearsYoungerOlder < 0 ? AppColors.longevityGreen : AppColors.longevityOrange
    }

    private var particleCanvas: some View {
        Canvas { context, canvasSize in
            let center = CGPoint(x: canvasSize.width / 2, y: canvasSize.height / 2)
            let radius = min(canvasSize.width, canvasSize.height) / 2

            for i in 0..<30 {
                let seed = Double(i) * 137.5
                let angle = (seed + animationPhase * 20).truncatingRemainder(dividingBy: 360)
                let r = radius * (0.2 + 0.7 * sin(seed * 0.1 + animationPhase).magnitude)
                let x = center.x + CGFloat(r * cos(angle * .pi / 180))
                let y = center.y + CGFloat(r * sin(angle * .pi / 180))
                let particleSize = CGFloat(2 + 3 * sin(seed * 0.3 + animationPhase * 2).magnitude)
                let opacity = 0.3 + 0.5 * sin(seed * 0.2 + animationPhase * 3).magnitude

                context.fill(
                    Path(ellipseIn: CGRect(x: x - particleSize / 2, y: y - particleSize / 2, width: particleSize, height: particleSize)),
                    with: .color(AppColors.longevityGreen.opacity(opacity))
                )
            }
        }
    }
}

/// Custom Shape using polar coordinates with Fourier harmonics for organic morphing.
struct OrganicShape: Shape {
    let harmonics: Int
    let amplitude: Double
    var phase: Double

    var animatableData: Double {
        get { phase }
        set { phase = newValue }
    }

    func path(in rect: CGRect) -> Path {
        let center = CGPoint(x: rect.midX, y: rect.midY)
        let baseRadius = min(rect.width, rect.height) / 2

        var path = Path()
        let steps = 120

        for i in 0...steps {
            let angle = (Double(i) / Double(steps)) * 2 * .pi
            var r = baseRadius

            // Superimpose harmonics for organic shape
            for k in 1...harmonics {
                let freq = Double(k)
                let phaseShift = phase * (0.3 + freq * 0.15)
                let amp = amplitude / freq
                r += baseRadius * amp * sin(freq * angle + phaseShift)
            }

            let point = CGPoint(
                x: center.x + CGFloat(r * Darwin.cos(angle)),
                y: center.y + CGFloat(r * Darwin.sin(angle))
            )

            if i == 0 {
                path.move(to: point)
            } else {
                path.addLine(to: point)
            }
        }
        path.closeSubpath()
        return path
    }
}
