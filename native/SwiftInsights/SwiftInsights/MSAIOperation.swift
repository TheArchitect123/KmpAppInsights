import Foundation

class MSAIOperation: MSAIObject {
    
    var operationId: String?
    var name: String?
    var parentId: String?
    var rootId: String?
    var syntheticSource: String?
    var isSynthetic: String?
    
    override init() {
        super.init()
    }
    
    override func serializeToDictionary() -> [String: Any] {
        var dict = super.serializeToDictionary()
        
        if let operationId = operationId {
            dict["ai.operation.id"] = operationId
        }
        if let name = name {
            dict["ai.operation.name"] = name
        }
        if let parentId = parentId {
            dict["ai.operation.parentId"] = parentId
        }
        if let rootId = rootId {
            dict["ai.operation.rootId"] = rootId
        }
        if let syntheticSource = syntheticSource {
            dict["ai.operation.syntheticSource"] = syntheticSource
        }
        if let isSynthetic = isSynthetic {
            dict["ai.operation.isSynthetic"] = isSynthetic
        }
        
        return dict
    }
    
    // MARK: - NSCoding
    required init?(coder: NSCoder) {
        self.operationId = coder.decodeObject(forKey: "self.operationId") as? String
        self.name = coder.decodeObject(forKey: "self.name") as? String
        self.parentId = coder.decodeObject(forKey: "self.parentId") as? String
        self.rootId = coder.decodeObject(forKey: "self.rootId") as? String
        self.syntheticSource = coder.decodeObject(forKey: "self.syntheticSource") as? String
        self.isSynthetic = coder.decodeObject(forKey: "self.isSynthetic") as? String
        super.init(coder: coder)
    }
    
    override func encode(with coder: NSCoder) {
        super.encode(with: coder)
        coder.encode(operationId, forKey: "self.operationId")
        coder.encode(name, forKey: "self.name")
        coder.encode(parentId, forKey: "self.parentId")
        coder.encode(rootId, forKey: "self.rootId")
        coder.encode(syntheticSource, forKey: "self.syntheticSource")
        coder.encode(isSynthetic, forKey: "self.isSynthetic")
    }
}
