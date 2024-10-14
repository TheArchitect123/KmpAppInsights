import Foundation

class MSAIEventData: NSObject, NSCoding {
    
    var envelopeTypeName: String
    var dataTypeName: String
    var version: NSNumber
    var properties: MSAIOrderedDictionary
    var measurements: MSAIOrderedDictionary
    var name: String?
    
    override init() {
        self.envelopeTypeName = "Microsoft.ApplicationInsights.Event"
        self.dataTypeName = "EventData"
        self.version = 2
        self.properties = MSAIOrderedDictionary()
        self.measurements = MSAIOrderedDictionary()
        super.init()
    }
    
    func serializeToDictionary() -> [String: Any] {
        var dict = [String: Any]()
        if let name = name {
            dict["name"] = name
        }
        dict["properties"] = properties
        dict["measurements"] = measurements
        return dict
    }
    
    // MARK: - NSCoding
    required init?(coder: NSCoder) {
        self.envelopeTypeName = coder.decodeObject(forKey: "self.envelopeTypeName") as? String ?? "Microsoft.ApplicationInsights.Event"
        self.dataTypeName = coder.decodeObject(forKey: "self.dataTypeName") as? String ?? "EventData"
        self.version = coder.decodeObject(forKey: "self.version") as? NSNumber ?? 2
        self.properties = coder.decodeObject(forKey: "self.properties") as? MSAIOrderedDictionary ?? MSAIOrderedDictionary()
        self.measurements = coder.decodeObject(forKey: "self.measurements") as? MSAIOrderedDictionary ?? MSAIOrderedDictionary()
        super.init()
    }
    
    func encode(with coder: NSCoder) {
        coder.encode(envelopeTypeName, forKey: "self.envelopeTypeName")
        coder.encode(dataTypeName, forKey: "self.dataTypeName")
        coder.encode(version, forKey: "self.version")
        coder.encode(properties, forKey: "self.properties")
        coder.encode(measurements, forKey: "self.measurements")
    }
}
