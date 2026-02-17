import SwiftUI
import WidgetKit

@main
struct ApexFitComplicationsBundle: WidgetBundle {
    var body: some Widget {
        ApexFitRecoveryComplication()
        ApexFitStrainComplication()
        ApexFitSummaryComplication()
        ApexFitInlineComplication()
    }
}

struct ApexFitRecoveryComplication: Widget {
    let kind = "ApexFitRecovery"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: ApexFitComplicationProvider()) { entry in
            AccessoryCircularView(entry: entry)
        }
        .configurationDisplayName("Recovery")
        .description("Your current Recovery score.")
        .supportedFamilies([.accessoryCircular])
    }
}

struct ApexFitStrainComplication: Widget {
    let kind = "ApexFitStrain"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: ApexFitComplicationProvider()) { entry in
            AccessoryCornerView(entry: entry)
        }
        .configurationDisplayName("Strain")
        .description("Today's strain progress.")
        .supportedFamilies([.accessoryCorner])
    }
}

struct ApexFitSummaryComplication: Widget {
    let kind = "ApexFitSummary"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: ApexFitComplicationProvider()) { entry in
            AccessoryRectangularView(entry: entry)
        }
        .configurationDisplayName("ApexFit Summary")
        .description("Recovery, Strain, and Sleep at a glance.")
        .supportedFamilies([.accessoryRectangular])
    }
}

struct ApexFitInlineComplication: Widget {
    let kind = "ApexFitInline"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: ApexFitComplicationProvider()) { entry in
            AccessoryInlineView(entry: entry)
        }
        .configurationDisplayName("ApexFit Inline")
        .description("Recovery and Strain in text.")
        .supportedFamilies([.accessoryInline])
    }
}
