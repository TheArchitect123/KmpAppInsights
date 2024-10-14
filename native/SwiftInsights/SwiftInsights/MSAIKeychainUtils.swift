import Foundation
import Security

enum MSAIKeychainUtilsError: Error {
    case invalidInput
    case keychainError(OSStatus)
    case itemNotFound
}

class MSAIKeychainUtils {
    
    static let keychainUtilsErrorDomain = "MSAIKeychainUtilsErrorDomain"
    
    static func getPassword(forUsername username: String, serviceName: String) throws -> String? {
        guard !username.isEmpty, !serviceName.isEmpty else {
            throw MSAIKeychainUtilsError.invalidInput
        }
        
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrAccount as String: username,
            kSecAttrService as String: serviceName,
            kSecReturnData as String: true
        ]
        
        var result: AnyObject?
        let status = SecItemCopyMatching(query as CFDictionary, &result)
        
        guard status == errSecSuccess else {
            if status == errSecItemNotFound {
                throw MSAIKeychainUtilsError.itemNotFound
            }
            throw MSAIKeychainUtilsError.keychainError(status)
        }
        
        guard let data = result as? Data,
              let password = String(data: data, encoding: .utf8) else {
            throw MSAIKeychainUtilsError.keychainError(-1999)
        }
        
        return password
    }
    
    static func storeUsername(_ username: String, password: String, serviceName: String, updateExisting: Bool) throws {
        try storeUsername(username, password: password, serviceName: serviceName, updateExisting: updateExisting, accessibility: kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly)
    }
    
    static func storeUsername(_ username: String, password: String, serviceName: String, updateExisting: Bool, accessibility: CFTypeRef) throws {
        guard !username.isEmpty, !password.isEmpty, !serviceName.isEmpty else {
            throw MSAIKeychainUtilsError.invalidInput
        }
        
        var getError: Error?
        let existingPassword = try? getPassword(forUsername: username, serviceName: serviceName)
        
        if let existingPassword = existingPassword, updateExisting {
            let query: [String: Any] = [
                kSecClass as String: kSecClassGenericPassword,
                kSecAttrService as String: serviceName,
                kSecAttrAccount as String: username
            ]
            
            let attributes: [String: Any] = [
                kSecValueData as String: password.data(using: .utf8)!,
                kSecAttrAccessible as String: accessibility
            ]
            
            let status = SecItemUpdate(query as CFDictionary, attributes as CFDictionary)
            guard status == errSecSuccess else {
                throw MSAIKeychainUtilsError.keychainError(status)
            }
        } else {
            let query: [String: Any] = [
                kSecClass as String: kSecClassGenericPassword,
                kSecAttrService as String: serviceName,
                kSecAttrAccount as String: username,
                kSecValueData as String: password.data(using: .utf8)!,
                kSecAttrAccessible as String: accessibility
            ]
            
            let status = SecItemAdd(query as CFDictionary, nil)
            guard status == errSecSuccess else {
                throw MSAIKeychainUtilsError.keychainError(status)
            }
        }
    }
    
    static func deleteItem(forUsername username: String, serviceName: String) throws {
        guard !username.isEmpty, !serviceName.isEmpty else {
            throw MSAIKeychainUtilsError.invalidInput
        }
        
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrAccount as String: username,
            kSecAttrService as String: serviceName
        ]
        
        let status = SecItemDelete(query as CFDictionary)
        guard status == errSecSuccess else {
            throw MSAIKeychainUtilsError.keychainError(status)
        }
    }
    
    static func purgeItems(forServiceName serviceName: String) throws {
        guard !serviceName.isEmpty else {
            throw MSAIKeychainUtilsError.invalidInput
        }
        
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: serviceName
        ]
        
        let status = SecItemDelete(query as CFDictionary)
        guard status == errSecSuccess else {
            throw MSAIKeychainUtilsError.keychainError(status)
        }
    }
}
