import Foundation

class MSAITelemetryData: NSObject, NSCoding {

    var version: String?
    var name: String?
    var properties: [String: Any]?

    // Serialization to Dictionary
    func serializeToDictionary() -> MSAIOrderedDictionary {
        let dict = super.serializeToDictionary()
        if let version = self.version {
            dict.setObject(version, forKey: "ver")
        }
        return dict
    }

    // MARK: - NSCoding
    
    required init?(coder: NSCoder) {
        super.init()
        self.version = coder.decodeObject(forKey: "self.version") as? String
        self.name = coder.decodeObject(forKey: "self.name") as? String
        self.properties = coder.decodeObject(forKey: "self.properties") as? [String: Any]
    }

    func encode(with coder: NSCoder) {
        super.encode(with: coder)
        coder.encode(self.version, forKey: "self.version")
        coder.encode(self.name, forKey: "self.name")
        coder.encode(self.properties, forKey: "self.properties")
    }
}
