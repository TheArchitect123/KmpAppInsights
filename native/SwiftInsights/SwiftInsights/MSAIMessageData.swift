import Foundation

class MSAIMessageData: NSObject, NSCoding {
    
    var envelopeTypeName: String
    var dataTypeName: String
    var version: NSNumber
    var properties: [String: Any]
    var message: String?
    var severityLevel: MSAISeverityLevel
    
    override init() {
        self.envelopeTypeName = "Microsoft.ApplicationInsights.Message"
        self.dataTypeName = "MessageData"
        self.version = 2
        self.properties = [:]
        self.severityLevel = .info
        super.init()
    }
    
    func serializeToDictionary() -> [String: Any] {
        var dict = [String: Any]()
        if let message = message {
            dict["message"] = message
        }
        dict["severityLevel"] = severityLevel.rawValue
        dict["properties"] = properties
        return dict
    }
    
    // MARK: - NSCoding
    required init?(coder: NSCoder) {
        self.message = coder.decodeObject(forKey: "self.message") as? String
        self.severityLevel = MSAISeverityLevel(rawValue: coder.decodeInt32(forKey: "self.severityLevel")) ?? .info
        super.init()
    }
    
    func encode(with coder: NSCoder) {
        coder.encode(message, forKey: "self.message")
        coder.encode(severityLevel.rawValue, forKey: "self.severityLevel")
    }
}
