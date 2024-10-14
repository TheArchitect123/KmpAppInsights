import Foundation

enum MSAISessionState: Int {
    case start = 0
    case end = 1
}

class MSAISessionStateData: NSCoding {
    
    var envelopeTypeName: String?
    var dataTypeName: String?
    var version: NSNumber?
    var state: MSAISessionState
    
    override init() {
        envelopeTypeName = "Microsoft.ApplicationInsights.SessionState"
        dataTypeName = "SessionStateData"
        version = 2
        state = .start
    }
    
    // MARK: - Serialization
    
    override func serializeToDictionary() -> [String: Any] {
        var dict = super.serializeToDictionary()
        dict["state"] = state.rawValue
        return dict
    }
    
    // MARK: - NSCoding
    
    required init?(coder aDecoder: NSCoder) {
        envelopeTypeName = aDecoder.decodeObject(forKey: "envelopeTypeName") as? String
        dataTypeName = aDecoder.decodeObject(forKey: "dataTypeName") as? String
        state = MSAISessionState(rawValue: aDecoder.decodeInt32(forKey: "self.state")) ?? .start
        super.init(coder: aDecoder)
    }
    
    func encode(with aCoder: NSCoder) {
        aCoder.encode(envelopeTypeName, forKey: "envelopeTypeName")
        aCoder.encode(dataTypeName, forKey: "dataTypeName")
        aCoder.encode(state.rawValue, forKey: "self.state")
        super.encode(with: aCoder)
    }
}
