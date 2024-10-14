import Foundation

class MSAIAvailabilityData: NSObject, NSCoding {
    var testRunId: String?
    var testTimeStamp: String?
    var testName: String?
    var duration: String?
    var result: Int
    var runLocation: String?
    var message: String?
    var dataSize: String?
    var properties: [String: Any]
    var measurements: [String: Any]
    
    override init() {
        self.result = 0
        self.properties = [:]
        self.measurements = [:]
        super.init()
    }
    
    func serializeToDictionary() -> [String: Any] {
        var dict = [String: Any]()
        if let testRunId = testRunId {
            dict["testRunId"] = testRunId
        }
        if let testTimeStamp = testTimeStamp {
            dict["testTimeStamp"] = testTimeStamp
        }
        if let testName = testName {
            dict["testName"] = testName
        }
        if let duration = duration {
            dict["duration"] = duration
        }
        dict["result"] = result
        if let runLocation = runLocation {
            dict["runLocation"] = runLocation
        }
        if let message = message {
            dict["message"] = message
        }
        if let dataSize = dataSize {
            dict["dataSize"] = dataSize
        }
        if !properties.isEmpty {
            dict["properties"] = properties
        }
        if !measurements.isEmpty {
            dict["measurements"] = measurements
        }
        return dict
    }
    
    // NSCoding
    required init?(coder: NSCoder) {
        self.testRunId = coder.decodeObject(forKey: "self.testRunId") as? String
        self.testTimeStamp = coder.decodeObject(forKey: "self.testTimeStamp") as? String
        self.testName = coder.decodeObject(forKey: "self.testName") as? String
        self.duration = coder.decodeObject(forKey: "self.duration") as? String
        self.result = coder.decodeInteger(forKey: "self.result")
        self.runLocation = coder.decodeObject(forKey: "self.runLocation") as? String
        self.message = coder.decodeObject(forKey: "self.message") as? String
        self.dataSize = coder.decodeObject(forKey: "self.dataSize") as? String
        self.measurements = coder.decodeObject(forKey: "self.measurements") as? [String: Any] ?? [:]
    }
    
    func encode(with coder: NSCoder) {
        coder.encode(testRunId, forKey: "self.testRunId")
        coder.encode(testTimeStamp, forKey: "self.testTimeStamp")
        coder.encode(testName, forKey: "self.testName")
        coder.encode(duration, forKey: "self.duration")
        coder.encode(result, forKey: "self.result")
        coder.encode(runLocation, forKey: "self.runLocation")
        coder.encode(message, forKey: "self.message")
        coder.encode(dataSize, forKey: "self.dataSize")
        coder.encode(measurements, forKey: "self.measurements")
    }
}
