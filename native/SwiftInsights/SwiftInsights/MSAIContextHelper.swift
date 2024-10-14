import Foundation
import UIKit

let kMSAISessionFileName = "MSAISessions"
let kMSAISessionFileType = "plist"
let MSAISessionOperationsQueue = "com.microsoft.ApplicationInsights.sessionQueue"
let defaultSessionExpirationTime: UInt = 20
let kMSAIApplicationDidEnterBackgroundTime = "MSAIApplicationDidEnterBackgroundTime"
let kMSAIApplicationWasLaunched = "MSAIApplicationWasLaunched"

let MSAIUserChangedNotification = Notification.Name("MSAIUserChangedNotification")
let kMSAIUserInfo = "MSAIUserInfo"
let kMSAIPersistedUser = "MSAIPersistedUser"

let MSAISessionStartedNotification = Notification.Name("MSAISessionStartedNotification")
let MSAISessionEndedNotification = Notification.Name("MSAISessionEndedNotification")
let kMSAISessionInfo = "MSAISessionInfo"

class MSAIContextHelper {
    
    private var _appWillEnterForegroundObserver: Any?
    private var _appDidEnterBackgroundObserver: Any?
    private var _appWillTerminateObserver: Any?
    
    private var operationsQueue = DispatchQueue(label: MSAISessionOperationsQueue)
    
    var autoSessionManagementDisabled = false
    var appBackgroundTimeBeforeSessionExpires: UInt = defaultSessionExpirationTime
    
    static let sharedInstance: MSAIContextHelper = {
        let instance = MSAIContextHelper()
        instance.registerObservers()
        return instance
    }()
    
    private init() {
        registerObservers()
    }
    
    // MARK: - Users
    
    func newUser() -> MSAIUser {
        let user = MSAIUser()
        user.userId = msai_appAnonID()
        return user
    }
    
    func setUser(with configurationBlock: (MSAIUser) -> Void) {
        var currentUser = loadUser()
        
        if currentUser == nil {
            currentUser = newUser()
        }
        
        if let currentUser = currentUser {
            configurationBlock(currentUser)
            setCurrentUser(currentUser)
        }
    }
    
    func setCurrentUser(_ user: MSAIUser) {
        saveUser(user)
        sendUserChangedNotification(userInfo: [kMSAIUserInfo: user])
    }
    
    // MARK: - User persistence
    
    func saveUser(_ user: MSAIUser) {
        if let encodedObject = try? NSKeyedArchiver.archivedData(withRootObject: user, requiringSecureCoding: false) {
            UserDefaults.standard.set(encodedObject, forKey: kMSAIPersistedUser)
        }
    }
    
    func loadUser() -> MSAIUser? {
        guard let encodedObject = UserDefaults.standard.data(forKey: kMSAIPersistedUser),
              let user = try? NSKeyedUnarchiver.unarchiveTopLevelObjectWithData(encodedObject) as? MSAIUser else {
            return nil
        }
        return user
    }
    
    // MARK: - Sessions
    
    func newSession() -> MSAISession {
        return newSession(withId: nil)
    }
    
    func newSession(withId sessionId: String?) -> MSAISession {
        let session = MSAISession()
        session.sessionId = sessionId ?? msai_UUID()
        session.isNew = "false"
        
        if !UserDefaults.standard.bool(forKey: kMSAIApplicationWasLaunched) {
            session.isFirst = "true"
            UserDefaults.standard.set(true, forKey: kMSAIApplicationWasLaunched)
            UserDefaults.standard.synchronize()
        } else {
            session.isFirst = "false"
        }
        
        return session
    }
    
    // MARK: - Automatic Session Management
    
    func registerObservers() {
        let notificationCenter = NotificationCenter.default
        
        if _appDidEnterBackgroundObserver == nil {
            _appDidEnterBackgroundObserver = notificationCenter.addObserver(forName: UIApplication.didEnterBackgroundNotification, object: nil, queue: .main) { [weak self] _ in
                self?.updateDidEnterBackgroundTime()
            }
        }
        
        if _appWillEnterForegroundObserver == nil {
            _appWillEnterForegroundObserver = notificationCenter.addObserver(forName: UIApplication.willEnterForegroundNotification, object: nil, queue: .main) { [weak self] _ in
                self?.startNewSessionIfNeeded()
            }
        }
        
        if _appWillTerminateObserver == nil {
            _appWillTerminateObserver = notificationCenter.addObserver(forName: UIApplication.willTerminateNotification, object: nil, queue: .main) { [weak self] _ in
                self?.endSession()
            }
        }
    }
    
    func unregisterObservers() {
        let notificationCenter = NotificationCenter.default
        if let observer = _appDidEnterBackgroundObserver {
            notificationCenter.removeObserver(observer)
            _appDidEnterBackgroundObserver = nil
        }
        if let observer = _appWillEnterForegroundObserver {
            notificationCenter.removeObserver(observer)
            _appWillEnterForegroundObserver = nil
        }
        if let observer = _appWillTerminateObserver {
            notificationCenter.removeObserver(observer)
            _appWillTerminateObserver = nil
        }
    }
    
    func updateDidEnterBackgroundTime() {
        if autoSessionManagementDisabled { return }
        UserDefaults.standard.set(Date().timeIntervalSince1970, forKey: kMSAIApplicationDidEnterBackgroundTime)
        UserDefaults.standard.synchronize()
    }
    
    func startNewSessionIfNeeded() {
        if autoSessionManagementDisabled { return }
        
        if appBackgroundTimeBeforeSessionExpires == 0 {
            startNewSession()
            return
        }
        
        let appDidEnterBackgroundTime = UserDefaults.standard.double(forKey: kMSAIApplicationDidEnterBackgroundTime)
        let timeSinceLastBackground = Date().timeIntervalSince1970 - appDidEnterBackgroundTime
        
        if timeSinceLastBackground > Double(appBackgroundTimeBeforeSessionExpires) {
            startNewSession()
        }
    }
    
    // MARK: - Manual Session Management
    
    func renewSession(withId sessionId: String) {
        let session = newSession(withId: sessionId)
        let userInfo: [String: MSAISession] = [kMSAISessionInfo: session]
        sendSessionStartedNotification(userInfo: userInfo)
    }
    
    func startNewSession() {
        let newSessionId = msai_UUID()
        renewSession(withId: newSessionId)
    }
    
    func endSession() {
        sendSessionEndedNotification()
    }
    
    // MARK: - Notifications
    
    func sendUserChangedNotification(userInfo: [String: MSAIUser]) {
        DispatchQueue.main.async {
            NotificationCenter.default.post(name: MSAIUserChangedNotification, object: self, userInfo: userInfo)
        }
    }
    
    func sendSessionStartedNotification(userInfo: [String: MSAISession]) {
        DispatchQueue.main.async {
            NotificationCenter.default.post(name: MSAISessionStartedNotification, object: self, userInfo: userInfo)
        }
    }
    
    func sendSessionEndedNotification() {
        DispatchQueue.main.async {
            NotificationCenter.default.post(name: MSAISessionEndedNotification, object: self, userInfo: nil)
        }
    }
    
    // MARK: - Helper
    
    func unixTimestamp(from date: Date) -> String {
        return String(Int(date.timeIntervalSince1970))
    }
    
    func key(forTimestamp timestamp: String, inDictionary dict: [String: Any]) -> String? {
        for key in sortedKeys(ofDictionary: dict) {
            if let doubleTimestamp = Double(timestamp), let doubleKey = Double(key), doubleTimestamp >= doubleKey {
                return key
            }
        }
        return nil
    }
    
    func sortedKeys(ofDictionary dict: [String: Any]) -> [String] {
        return dict.keys.sorted { $0.compare($1, options: .numeric) == .orderedDescending }
    }
}
