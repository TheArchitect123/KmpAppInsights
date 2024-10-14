import Foundation

class MSAIInternal: NSObject, NSCoding {
    
    var sdkVersion: String?
    var agentVersion: String?
    
    override init() {
        super.init()
    }
    
    func serializeToDictionary() -> [String: Any] {
        var dict = [String: Any]()
        if let sdkVersion = sdkVersion {
            dict["ai.internal.sdkVersion"] = sdkVersion
        }
        if let agentVersion = agentVersion {
            dict["ai.internal.agentVersion"] = agentVersion
        }
        return dict
    }
    
    // MARK: - NSCoding
    required init?(coder: NSCoder) {
        self.sdkVersion = coder.decodeObject(forKey: "self.sdkVersion") as? String
        self.agentVersion = coder.decodeObject(forKey: "self.agentVersion") as? String
        super.init()
    }
    
    func encode(with coder: NSCoder) {
        coder.encode(sdkVersion, forKey: "self.sdkVersion")
        coder.encode(agentVersion, forKey: "self.agentVersion")
    }
}
