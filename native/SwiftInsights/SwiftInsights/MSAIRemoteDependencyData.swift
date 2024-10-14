import Foundation

class MSAIRemoteDependencyData: NSObject, NSCoding {
    
    var envelopeTypeName: String
    var dataTypeName: String
    var version: NSNumber
    var kind: MSAIDataPointType
    var dependencyKind: MSAIDependencyKind
    var success: Bool
    var async: Bool?
    var dependencySource: MSAIDependencySourceType
    var properties: [String: Any]
    var name: String?
    var value: NSNumber?
    var count: NSNumber?
    var min: NSNumber?
    var max: NSNumber?
    var stdDev: NSNumber?
    var commandName: String?
    var dependencyTypeName: String?
    
    override init() {
        self.envelopeTypeName = "Microsoft.ApplicationInsights.RemoteDependency"
        self.dataTypeName = "RemoteDependencyData"
        self.version = 2
        self.kind = .measurement
        self.dependencyKind = .other
        self.success = true
        self.dependencySource = .undefined
        self.properties = [:]
        super.init()
    }
    
    func serializeToDictionary() -> [String: Any] {
        var dict: [String: Any] = [:]
        
        dict["name"] = self.name
        dict["kind"] = self.kind.rawValue
        dict["value"] = self.value
        dict["count"] = self.count
        dict["min"] = self.min
        dict["max"] = self.max
        dict["stdDev"] = self.stdDev
        dict["dependencyKind"] = self.dependencyKind.rawValue
        dict["success"] = self.success ? "true" : "false"
        dict["async"] = self.async != nil ? (self.async! ? "true" : "false") : nil
        dict["dependencySource"] = self.dependencySource.rawValue
        dict["commandName"] = self.commandName
        dict["dependencyTypeName"] = self.dependencyTypeName
        dict["properties"] = self.properties
        
        return dict
    }
    
    // MARK: - NSCoding
    required init?(coder: NSCoder) {
        self.kind = MSAIDataPointType(rawValue: coder.decodeInteger(forKey: "self.kind")) ?? .measurement
        self.value = coder.decodeObject(forKey: "self.value") as? NSNumber
        self.count = coder.decodeObject(forKey: "self.count") as? NSNumber
        self.min = coder.decodeObject(forKey: "self.min") as? NSNumber
        self.max = coder.decodeObject(forKey: "self.max") as? NSNumber
        self.stdDev = coder.decodeObject(forKey: "self.stdDev") as? NSNumber
        self.dependencyKind = MSAIDependencyKind(rawValue: coder.decodeInteger(forKey: "self.dependencyKind")) ?? .other
        self.success = coder.decodeBool(forKey: "self.success")
        self.async = coder.decodeBool(forKey: "self.async")
        self.dependencySource = MSAIDependencySourceType(rawValue: coder.decodeInteger(forKey: "self.dependencySource")) ?? .undefined
        self.commandName = coder.decodeObject(forKey: "self.commandName") as? String
        self.dependencyTypeName = coder.decodeObject(forKey: "self.dependencyTypeName") as? String
        self.properties = coder.decodeObject(forKey: "self.properties") as? [String: Any] ?? [:]
        
        self.envelopeTypeName = "Microsoft.ApplicationInsights.RemoteDependency"
        self.dataTypeName = "RemoteDependencyData"
        self.version = 2
        
        super.init()
    }
    
    func encode(with coder: NSCoder) {
        coder.encode(self.kind.rawValue, forKey: "self.kind")
        coder.encode(self.value, forKey: "self.value")
        coder.encode(self.count, forKey: "self.count")
        coder.encode(self.min, forKey: "self.min")
        coder.encode(self.max, forKey: "self.max")
        coder.encode(self.stdDev, forKey: "self.stdDev")
        coder.encode(self.dependencyKind.rawValue, forKey: "self.dependencyKind")
        coder.encode(self.success, forKey: "self.success")
        coder.encode(self.async, forKey: "self.async")
        coder.encode(self.dependencySource.rawValue, forKey: "self.dependencySource")
        coder.encode(self.commandName, forKey: "self.commandName")
        coder.encode(self.dependencyTypeName, forKey: "self.dependencyTypeName")
        coder.encode(self.properties, forKey: "self.properties")
    }
}

// Supporting Enums
enum MSAIDataPointType: Int {
    case measurement = 0
}

enum MSAIDependencyKind: Int {
    case other = 0
}

enum MSAIDependencySourceType: Int {
    case undefined = 0
}
