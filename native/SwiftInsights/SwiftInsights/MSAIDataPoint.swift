import Foundation

class MSAIDataPoint: NSObject, NSCoding {
    
    var name: String?
    var kind = MSAIDataPointType.measurement
    var value: NSNumber?
    var count: NSNumber?
    var min: NSNumber?
    var max: NSNumber?
    var stdDev: NSNumber?
    
    override init() {
        super.init()
        kind = .measurement
    }
    
    func serializeToDictionary() -> [String: Any] {
        var dict = [String: Any]()
        if let name = name {
            dict["name"] = name
        }
        dict["kind"] = kind.rawValue
        if let value = value {
            dict["value"] = value
        }
        if let count = count {
            dict["count"] = count
        }
        if let min = min {
            dict["min"] = min
        }
        if let max = max {
            dict["max"] = max
        }
        if let stdDev = stdDev {
            dict["stdDev"] = stdDev
        }
        return dict
    }
    
    // MARK: - NSCoding
    required init?(coder: NSCoder) {
        super.init()
        self.name = coder.decodeObject(forKey: "self.name") as? String
        self.kind = MSAIDataPointType(rawValue: Int(coder.decodeInt32(forKey: "self.kind"))) ?? MSAIDataPointType.measurement
        self.value = coder.decodeObject(forKey: "self.value") as? NSNumber
        self.count = coder.decodeObject(forKey: "self.count") as? NSNumber
        self.min = coder.decodeObject(forKey: "self.min") as? NSNumber
        self.max = coder.decodeObject(forKey: "self.max") as? NSNumber
        self.stdDev = coder.decodeObject(forKey: "self.stdDev") as? NSNumber
    }
    
    func encode(with coder: NSCoder) {
        coder.encode(name, forKey: "self.name")
        coder.encode(kind.rawValue, forKey: "self.kind")
        coder.encode(value, forKey: "self.value")
        coder.encode(count, forKey: "self.count")
        coder.encode(min, forKey: "self.min")
        coder.encode(max, forKey: "self.max")
        coder.encode(stdDev, forKey: "self.stdDev")
    }
}
