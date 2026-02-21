import SwiftUI
import WidgetKit

@main
struct ZyvaComplicationsBundle: WidgetBundle {
    var body: some Widget {
        ZyvaRecoveryComplication()
        ZyvaStrainComplication()
        ZyvaSummaryComplication()
        ZyvaInlineComplication()
    }
}

struct ZyvaRecoveryComplication: Widget {
    let kind = "ZyvaRecovery"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: ZyvaComplicationProvider()) { entry in
            AccessoryCircularView(entry: entry)
        }
        .configurationDisplayName("Recovery")
        .description("Your current Recovery score.")
        .supportedFamilies([.accessoryCircular])
    }
}

struct ZyvaStrainComplication: Widget {
    let kind = "ZyvaStrain"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: ZyvaComplicationProvider()) { entry in
            AccessoryCornerView(entry: entry)
        }
        .configurationDisplayName("Strain")
        .description("Today's strain progress.")
        .supportedFamilies([.accessoryCorner])
    }
}

struct ZyvaSummaryComplication: Widget {
    let kind = "ZyvaSummary"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: ZyvaComplicationProvider()) { entry in
            AccessoryRectangularView(entry: entry)
        }
        .configurationDisplayName("Zyva Summary")
        .description("Recovery, Strain, and Sleep at a glance.")
        .supportedFamilies([.accessoryRectangular])
    }
}

struct ZyvaInlineComplication: Widget {
    let kind = "ZyvaInline"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: ZyvaComplicationProvider()) { entry in
            AccessoryInlineView(entry: entry)
        }
        .configurationDisplayName("Zyva Inline")
        .description("Recovery and Strain in text.")
        .supportedFamilies([.accessoryInline])
    }
}
