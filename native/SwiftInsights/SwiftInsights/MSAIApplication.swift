import Foundation

class MSAIApplication: NSObject, NSCoding {

    var version: String?
    var build: String?
    var typeId: String?

    override init() {
        super.init()
    }

    func serializeToDictionary() -> [String: Any] {
        var dict = [String: Any]()
        if let version = version {
            dict["ai.application.ver"] = version
        }
        if let build = build {
            dict["ai.application.build"] = build
        }
        if let typeId = typeId {
            dict["ai.application.typeId"] = typeId
        }
        return dict
    }

    // NSCoding
    required init?(coder: NSCoder) {
        self.version = coder.decodeObject(forKey: "self.version") as? String
        self.build = coder.decodeObject(forKey: "self.build") as? String
        self.typeId = coder.decodeObject(forKey: "self.typeId") as? String
    }

    func encode(with coder: NSCoder) {
        coder.encode(version, forKey: "self.version")
        coder.encode(build, forKey: "self.build")
        coder.encode(typeId, forKey: "self.typeId")
    }
}
