import Foundation

/// Data contract class for type User.
class MSAIUser: NSObject, NSCoding {

    var accountAcquisitionDate: String?
    var accountId: String?
    var userAgent: String?
    var userId: String?
    var storeRegion: String?
    var authUserId: String?
    var anonUserAcquisitionDate: String?
    var authUserAcquisitionDate: String?
    
    override init() {
        super.init()
    }

    /// Adds all members of this class to a dictionary
    /// - Returns: Dictionary containing all members
    func serializeToDictionary() -> [String: Any] {
        var dict = [String: Any]()
        
        if let accountAcquisitionDate = accountAcquisitionDate {
            dict["ai.user.accountAcquisitionDate"] = accountAcquisitionDate
        }
        if let accountId = accountId {
            dict["ai.user.accountId"] = accountId
        }
        if let userAgent = userAgent {
            dict["ai.user.userAgent"] = userAgent
        }
        if let userId = userId {
            dict["ai.user.id"] = userId
        }
        if let storeRegion = storeRegion {
            dict["ai.user.storeRegion"] = storeRegion
        }
        if let authUserId = authUserId {
            dict["ai.user.authUserId"] = authUserId
        }
        if let anonUserAcquisitionDate = anonUserAcquisitionDate {
            dict["ai.user.anonUserAcquisitionDate"] = anonUserAcquisitionDate
        }
        if let authUserAcquisitionDate = authUserAcquisitionDate {
            dict["ai.user.authUserAcquisitionDate"] = authUserAcquisitionDate
        }
        
        return dict
    }
    
    // MARK: - NSCoding
    required init?(coder: NSCoder) {
        accountAcquisitionDate = coder.decodeObject(forKey: "self.accountAcquisitionDate") as? String
        accountId = coder.decodeObject(forKey: "self.accountId") as? String
        userAgent = coder.decodeObject(forKey: "self.userAgent") as? String
        userId = coder.decodeObject(forKey: "self.userId") as? String
        storeRegion = coder.decodeObject(forKey: "self.storeRegion") as? String
        authUserId = coder.decodeObject(forKey: "self.authUserId") as? String
        anonUserAcquisitionDate = coder.decodeObject(forKey: "self.anonUserAcquisitionDate") as? String
        authUserAcquisitionDate = coder.decodeObject(forKey: "self.authUserAcquisitionDate") as? String
    }
    
    func encode(with coder: NSCoder) {
        coder.encode(accountAcquisitionDate, forKey: "self.accountAcquisitionDate")
        coder.encode(accountId, forKey: "self.accountId")
        coder.encode(userAgent, forKey: "self.userAgent")
        coder.encode(userId, forKey: "self.userId")
        coder.encode(storeRegion, forKey: "self.storeRegion")
        coder.encode(authUserId, forKey: "self.authUserId")
        coder.encode(anonUserAcquisitionDate, forKey: "self.anonUserAcquisitionDate")
        coder.encode(authUserAcquisitionDate, forKey: "self.authUserAcquisitionDate")
    }
}
