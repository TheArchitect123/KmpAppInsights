import Foundation

class MSAIBase: NSObject, NSCoding {
    var baseType: String?

    override init() {
        super.init()
    }

    func serializeToDictionary() -> [String: Any] {
        var dict = [String: Any]()
        if let baseType = baseType {
            dict["baseType"] = baseType
        }
        return dict
    }

    // NSCoding
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        self.baseType = coder.decodeObject(forKey: "self.baseType") as? String
    }

    func encode(with coder: NSCoder) {
        super.encode(with: coder)
        coder.encode(baseType, forKey: "self.baseType")
    }
}
