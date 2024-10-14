import Foundation

class MSAIDomain: NSObject, NSCoding {
    
    var envelopeTypeName: String
    var dataTypeName: String
    
    override init() {
        self.envelopeTypeName = "Microsoft.ApplicationInsights.Domain"
        self.dataTypeName = "Domain"
        super.init()
    }
    
    func serializeToDictionary() -> [String: Any] {
        var dict = [String: Any]()
        // Add members to the dictionary as necessary
        return dict
    }
    
    // MARK: - NSCoding
    required init?(coder: NSCoder) {
        self.envelopeTypeName = coder.decodeObject(forKey: "_envelopeTypeName") as? String ?? "Microsoft.ApplicationInsights.Domain"
        self.dataTypeName = coder.decodeObject(forKey: "_dataTypeName") as? String ?? "Domain"
        super.init(coder: coder)
    }
    
    func encode(with coder: NSCoder) {
        super.encode(with: coder)
        coder.encode(envelopeTypeName, forKey: "_envelopeTypeName")
        coder.encode(dataTypeName, forKey: "_dataTypeName")
    }
}
