import Foundation
import UIKit

// Notification message which MSAIApplicationInsights is listening to, to retry requesting updates from the server
let MSAINetworkDidBecomeReachableNotification = "MSAINetworkDidBecomeReachable"
let MSAI_SERVER_URL = "https://dc.services.visualstudio.com/v2/track"


final class MSAITelemetryManager {
    
    static let sharedManager = MSAITelemetryManager()
    private let telemetryEventQueue = DispatchQueue(label: "com.microsoft.ApplicationInsights.telemetryEventQueue", attributes: .concurrent)
    private let commonPropertiesQueue = DispatchQueue(label: "com.microsoft.ApplicationInsights.commonPropertiesQueue", attributes: .concurrent)
    
    var commonProperties: [String: Any] = [:]
    private var telemetryManagerDisabled = false
    private var managerInitialised = false
    private var appDidEnterBackgroundObserver: NSObjectProtocol?
    private var appWillResignActiveObserver: NSObjectProtocol?
    private var sessionStartedObserver: NSObjectProtocol?
    private var sessionEndedObserver: NSObjectProtocol?

    private init() { }
    
    // MARK: - Start manager
    func startManager() {
        telemetryEventQueue.sync(flags: .barrier) {
            if telemetryManagerDisabled { return }
            registerObservers()
            managerInitialised = true
        }
    }
    
    private func registerObservers() {
        let center = NotificationCenter.default
        
        appDidEnterBackgroundObserver = center.addObserver(forName: UIApplication.didEnterBackgroundNotification, object: nil, queue: OperationQueue.main) { _ in
            MSAIChannel.shared.persistDataItemQueue()
        }
        
        appWillResignActiveObserver = center.addObserver(forName: UIApplication.willResignActiveNotification, object: nil, queue: OperationQueue.main) { _ in
            MSAIChannel.shared.persistDataItemQueue()
        }
        
        sessionStartedObserver = center.addObserver(forName: NSNotification.Name.MSAISessionStartedNotification, object: nil, queue: OperationQueue.main) { _ in
            self.trackSessionStart()
        }
        
        sessionEndedObserver = center.addObserver(forName: NSNotification.Name.MSAISessionEndedNotification, object: nil, queue: OperationQueue.main) { _ in
            self.trackSessionEnd()
        }
    }
    
    private func unregisterObservers() {
        NotificationCenter.default.removeObserver(self)
        sessionStartedObserver = nil
        sessionEndedObserver = nil
    }
    
    // MARK: - Common Properties
    func setCommonProperties(_ properties: [String: Any]) {
        commonPropertiesQueue.async(flags: .barrier) {
            self.commonProperties = properties
        }
    }
    
    func getCommonProperties() -> [String: Any] {
        var properties: [String: Any] = [:]
        commonPropertiesQueue.sync {
            properties = self.commonProperties
        }
        return properties
    }
    
    // MARK: - Track Data
    func trackEvent(withName eventName: String, properties: [String: Any]? = nil, measurements: [String: Any]? = nil) {
        telemetryEventQueue.async {
            guard self.managerInitialised else { return }
            
            let eventData = MSAIEventData()
            eventData.name = eventName
            eventData.properties = properties
            eventData.measurements = measurements
            self.trackDataItem(eventData)
        }
    }
    
    func trackTrace(withMessage message: String, properties: [String: Any]? = nil) {
        telemetryEventQueue.async {
            guard self.managerInitialised else { return }
            
            let messageData = MSAIMessageData()
            messageData.message = message
            messageData.properties = properties
            self.trackDataItem(messageData)
        }
    }
    
    func trackMetric(withName metricName: String, value: Double, properties: [String: Any]? = nil) {
        telemetryEventQueue.async {
            guard self.managerInitialised else { return }
            
            let metricData = MSAIMetricData()
            let dataPoint = MSAIDataPoint()
            dataPoint.count = 1
            dataPoint.kind = .measurement
            dataPoint.max = value
            dataPoint.name = metricName
            dataPoint.value = value
            
            metricData.metrics = [dataPoint]
            metricData.properties = properties
            self.trackDataItem(metricData)
        }
    }
    
    func trackPageView(_ pageName: String, duration: TimeInterval = 0, properties: [String: Any]? = nil) {
        let durationString = durationString(from: duration)
        
        telemetryEventQueue.async {
            guard self.managerInitialised else { return }
            
            let pageViewData = MSAIPageViewData()
            pageViewData.name = pageName
            pageViewData.duration = durationString
            pageViewData.properties = properties
            self.trackDataItem(pageViewData)
        }
    }
    
    // Helper for duration formatting
    private func durationString(from duration: TimeInterval) -> String {
        let milliseconds = Int((duration.truncatingRemainder(dividingBy: 1)) * 1000)
        let durationInt = Int(duration)
        let seconds = durationInt % 60
        let minutes = (durationInt / 60) % 60
        let hours = (durationInt / 3600) % 24
        let days = durationInt / 86400
        
        return String(format: "%01d.%02d:%02d:%02d.%03d", days, hours, minutes, seconds, milliseconds)
    }
    
    // MARK: - Track Data Item
    private func trackDataItem(_ dataItem: MSAITelemetryData) {
        if !MSAIChannel.shared.isQueueBusy() {
            addCommonProperties(to: dataItem)
            let envelope = MSAIEnvelopeManager.shared.envelope(forTelemetryData: dataItem)
            let dict = envelope.serializeToDictionary()
            MSAIChannel.shared.enqueueDictionary(dict)
        } else {
            MSAILog("The data pipeline is saturated right now and the data item named \(dataItem.name ?? "") was dropped.")
        }
    }
    
    private func addCommonProperties(to dataItem: MSAITelemetryData) {
        var mergedProperties = getCommonProperties()
        if let itemProperties = dataItem.properties {
            mergedProperties.merge(itemProperties) { (_, new) in new }
        }
        dataItem.properties = mergedProperties
    }
    
    // MARK: - Session Management
    private func trackSessionStart() {
        let sessionState = MSAISessionStateData()
        sessionState.state = .start
        trackDataItem(sessionState)
    }
    
    private func trackSessionEnd() {
        let sessionState = MSAISessionStateData()
        sessionState.state = .end
        trackDataItem(sessionState)
    }
}
