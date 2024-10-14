import Foundation

class MSAIRequestData: NSObject, NSCoding {
    
    var envelopeTypeName: String
    var dataTypeName: String
    var version: NSNumber
    var properties: [String: Any]
    var measurements: [String: Any]
    var requestDataId: String?
    var name: String?
    var startTime: String?
    var duration: String?
    var responseCode: String?
    var success: Bool
    var httpMethod: String?
    var url: String?
    
    override init() {
        self.envelopeTypeName = "Microsoft.ApplicationInsights.Request"
        self.dataTypeName = "RequestData"
        self.version = 2
        self.properties = [:]
        self.measurements = [:]
        self.success = false
        super.init()
    }
    
    func serializeToDictionary() -> [String: Any] {
        var dict: [String: Any] = [:]
        
        dict["id"] = self.requestDataId
        dict["name"] = self.name
        dict["startTime"] = self.startTime
        dict["duration"] = self.duration
        dict["responseCode"] = self.responseCode
        dict["success"] = self.success ? "true" : "false"
        dict["httpMethod"] = self.httpMethod
        dict["url"] = self.url
        dict["properties"] = self.properties
        dict["measurements"] = self.measurements
        
        return dict
    }
    
    // MARK: - NSCoding
    required init?(coder: NSCoder) {
        self.requestDataId = coder.decodeObject(forKey: "self.requestDataId") as? String
        self.startTime = coder.decodeObject(forKey: "self.startTime") as? String
        self.duration = coder.decodeObject(forKey: "self.duration") as? String
        self.responseCode = coder.decodeObject(forKey: "self.responseCode") as? String
        self.success = coder.decodeBool(forKey: "self.success")
        self.httpMethod = coder.decodeObject(forKey: "self.httpMethod") as? String
        self.url = coder.decodeObject(forKey: "self.url") as? String
        self.measurements = coder.decodeObject(forKey: "self.measurements") as? [String: Any] ?? [:]
        self.properties = [:] // This assumes properties need to be empty initially
        
        self.envelopeTypeName = "Microsoft.ApplicationInsights.Request"
        self.dataTypeName = "RequestData"
        self.version = 2
        
        super.init()
    }
    
    func encode(with coder: NSCoder) {
        coder.encode(self.requestDataId, forKey: "self.requestDataId")
        coder.encode(self.startTime, forKey: "self.startTime")
        coder.encode(self.duration, forKey: "self.duration")
        coder.encode(self.responseCode, forKey: "self.responseCode")
        coder.encode(self.success, forKey: "self.success")
        coder.encode(self.httpMethod, forKey: "self.httpMethod")
        coder.encode(self.url, forKey: "self.url")
        coder.encode(self.measurements, forKey: "self.measurements")
    }
}
