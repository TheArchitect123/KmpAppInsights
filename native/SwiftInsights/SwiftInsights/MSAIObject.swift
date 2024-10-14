import Foundation

class MSAIObject: NSObject, NSCoding {
    
    // Empty implementation for the base class
    func serializeToDictionary() -> [String: Any] {
        return [:]
    }
    
    func serializeToString() -> String {
        let dict = serializeToDictionary()
        var jsonString = ""
        do {
            let jsonData = try JSONSerialization.data(withJSONObject: dict, options: [])
            jsonString = String(data: jsonData, encoding: .utf8) ?? ""
        } catch {
            print("NSJSONSerialization error: \(error.localizedDescription)")
        }
        
        jsonString = jsonString.replacingOccurrences(of: "\"true\"", with: "true")
        jsonString = jsonString.replacingOccurrences(of: "\"false\"", with: "false")
        return jsonString
    }
    
    // MARK: - NSCoding
    func encode(with coder: NSCoder) {
        // Empty implementation
    }
    
    required init?(coder: NSCoder) {
        super.init()
    }
    
    override init() {
        super.init()
    }
}
