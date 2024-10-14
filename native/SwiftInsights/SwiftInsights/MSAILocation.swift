import Foundation

class MSAILocation: NSObject, NSCoding {
    
    var ip: String?
    
    override init() {
        super.init()
    }
    
    func serializeToDictionary() -> [String: Any] {
        var dict = [String: Any]()
        if let ip = ip {
            dict["ai.location.ip"] = ip
        }
        return dict
    }
    
    // MARK: - NSCoding
    required init?(coder: NSCoder) {
        self.ip = coder.decodeObject(forKey: "self.ip") as? String
        super.init()
    }
    
    func encode(with coder: NSCoder) {
        coder.encode(ip, forKey: "self.ip")
    }
}
