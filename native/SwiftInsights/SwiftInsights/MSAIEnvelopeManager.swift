import Foundation

class MSAIEnvelopeManager {

    private var telemetryContext: MSAITelemetryContext?

    // MARK: - Initialize and configure singleton instance
    static let sharedManager: MSAIEnvelopeManager = {
        let instance = MSAIEnvelopeManager()
        return instance
    }()

    func configure(with telemetryContext: MSAITelemetryContext) {
        self.telemetryContext = telemetryContext
    }

    // MARK: - Create envelope objects
    func envelope() -> MSAIEnvelope {
        let envelope = MSAIEnvelope()
        envelope.appId = msai_mainBundleIdentifier()
        envelope.appVer = telemetryContext?.application?.version
        envelope.time = msai_utcDateString(Date())
        envelope.iKey = telemetryContext?.instrumentationKey

        if let deviceContext = telemetryContext?.device {
            envelope.deviceId = deviceContext.deviceId
            envelope.os = deviceContext.os
            envelope.osVer = deviceContext.osVersion
        }

        envelope.tags = telemetryContext?.contextDictionary
        return envelope
    }

    func envelope(for telemetryData: MSAITelemetryData) -> MSAIEnvelope {
        telemetryData.version = schemaVersion as NSNumber

        let data = MSAIData()
        data.baseData = telemetryData
        data.baseType = telemetryData.dataTypeName 

        let envelope = self.envelope()
        envelope.data = data
        envelope.name = telemetryData.envelopeTypeName

        return envelope
    }
}
