import Foundation

class MSAISession: NSObject, NSCoding {
    
    var sessionId: String?
    var isFirst: String?
    var isNew: String?
    
    override init() {
        super.init()
    }
    
    // MARK: - Serialization

    func serializeToDictionary() -> [String: Any] {
        var dict = [String: Any]()
        if let sessionId = sessionId {
            dict["ai.session.id"] = sessionId
        }
        if let isFirst = isFirst {
            dict["ai.session.isFirst"] = isFirst
        }
        if let isNew = isNew {
            dict["ai.session.isNew"] = isNew
        }
        return dict
    }
    
    // MARK: - NSCoding
    
    required init?(coder aDecoder: NSCoder) {
        super.init()
        sessionId = aDecoder.decodeObject(forKey: "self.sessionId") as? String
        isFirst = aDecoder.decodeObject(forKey: "self.isFirst") as? String
        isNew = aDecoder.decodeObject(forKey: "self.isNew") as? String
    }
    
    func encode(with aCoder: NSCoder) {
        aCoder.encode(sessionId, forKey: "self.sessionId")
        aCoder.encode(isFirst, forKey: "self.isFirst")
        aCoder.encode(isNew, forKey: "self.isNew")
    }
}
