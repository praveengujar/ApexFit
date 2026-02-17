import SwiftUI

enum TrendPeriod: String, CaseIterable, Identifiable {
    case week = "7D"
    case month = "30D"
    case quarter = "90D"

    var id: String { rawValue }

    var label: String { rawValue }

    var days: Int {
        switch self {
        case .week: return 7
        case .month: return 30
        case .quarter: return 90
        }
    }

    var startDate: Date {
        Date().daysAgo(days)
    }
}

struct PeriodPicker: View {
    @Binding var period: TrendPeriod

    var body: some View {
        Picker("Period", selection: $period) {
            ForEach(TrendPeriod.allCases) { p in
                Text(p.rawValue).tag(p)
            }
        }
        .pickerStyle(.segmented)
    }
}
