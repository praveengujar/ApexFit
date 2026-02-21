import SwiftUI

struct WelcomeView: View {
    var onContinue: () -> Void

    @State private var logoAppeared = false
    @State private var contentAppeared = false

    var body: some View {
        ZStack {
            AppColors.backgroundPrimary
                .ignoresSafeArea()

            VStack(spacing: 0) {
                Spacer()

                // Logo and App Name
                VStack(spacing: AppTheme.spacingMD) {
                    Image("ZyvaLogo")
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(width: 120, height: 120)
                        .scaleEffect(logoAppeared ? 1 : 0.5)
                        .opacity(logoAppeared ? 1 : 0)

                    Text("Zyva")
                        .font(AppTypography.metricLarge)
                        .foregroundStyle(AppColors.textPrimary)

                    Text("Your Personal Health Performance Platform")
                        .font(AppTypography.bodyLarge)
                        .foregroundStyle(AppColors.textSecondary)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal, AppTheme.spacingXL)
                }
                .padding(.bottom, AppTheme.spacingXXL)

                // Feature bullets
                VStack(spacing: AppTheme.spacingLG) {
                    FeatureBullet(
                        icon: "heart.text.square.fill",
                        color: AppColors.recoveryGreen,
                        title: "Recovery Tracking",
                        description: "Know when your body is ready to perform"
                    )

                    FeatureBullet(
                        icon: "flame.fill",
                        color: AppColors.recoveryRed,
                        title: "Strain Monitoring",
                        description: "Optimize your training load day by day"
                    )

                    FeatureBullet(
                        icon: "moon.fill",
                        color: AppColors.lavender,
                        title: "Sleep Analysis",
                        description: "Deep insights into your sleep quality"
                    )

                    FeatureBullet(
                        icon: "brain.head.profile.fill",
                        color: AppColors.teal,
                        title: "AI Coach",
                        description: "Personalized guidance based on your data"
                    )
                }
                .padding(.horizontal, AppTheme.spacingXL)
                .opacity(contentAppeared ? 1 : 0)
                .offset(y: contentAppeared ? 0 : 20)

                Spacer()

                // Get Started button
                Button(action: onContinue) {
                    Text("Get Started")
                        .font(AppTypography.heading3)
                        .foregroundStyle(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: AppTheme.minimumTapTarget + 8)
                        .background(AppColors.primaryBlue)
                        .clipShape(RoundedRectangle(cornerRadius: AppTheme.cornerRadiusMedium))
                }
                .padding(.horizontal, AppTheme.spacingLG)
                .padding(.bottom, AppTheme.spacingXL)
                .opacity(contentAppeared ? 1 : 0)
            }
        }
        .onAppear {
            withAnimation(.easeOut(duration: 0.6)) {
                logoAppeared = true
            }
            withAnimation(.easeOut(duration: 0.6).delay(0.3)) {
                contentAppeared = true
            }
        }
    }
}

// MARK: - Feature Bullet

private struct FeatureBullet: View {
    let icon: String
    let color: Color
    let title: String
    let description: String

    var body: some View {
        HStack(spacing: AppTheme.spacingMD) {
            Image(systemName: icon)
                .font(.system(size: 24))
                .foregroundStyle(color)
                .frame(width: 40, alignment: .center)

            VStack(alignment: .leading, spacing: 2) {
                Text(title)
                    .font(AppTypography.labelLarge)
                    .foregroundStyle(AppColors.textPrimary)

                Text(description)
                    .font(AppTypography.bodySmall)
                    .foregroundStyle(AppColors.textSecondary)
            }

            Spacer()
        }
    }
}

#Preview {
    WelcomeView {
        print("Continue tapped")
    }
}
