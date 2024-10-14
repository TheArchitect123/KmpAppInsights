import Foundation
import UIKit

class MSAITelemetryContext {
    
    var instrumentationKey: String?
    var application: MSAIApplication?
    var device: MSAIDevice?
    var location: MSAILocation?
    var user: MSAIUser?
    var internalData: MSAIInternal?
    var operation: MSAIOperation?
    var session: MSAISession?
    var tags: MSAIOrderedDictionary?
    
    // MARK: - Initialization
    
    init(appContext: MSAIContext) {
        
        let deviceContext = MSAIDevice()
        deviceContext.model = appContext.deviceModel
        deviceContext.type = appContext.deviceType
        deviceContext.osVersion = appContext.osVersion
        deviceContext.os = appContext.osName
        deviceContext.deviceId = msai_appAnonID()
        deviceContext.locale = msai_deviceLocale()
        deviceContext.language = msai_deviceLanguage()
        deviceContext.screenResolution = msai_screenSize()
        deviceContext.oemName = "Apple"
        
        let internalContext = MSAIInternal()
        internalContext.sdkVersion = msai_sdkVersion()
        
        let applicationContext = MSAIApplication()
        applicationContext.version = appContext.appVersion
        
        let sessionContext = MSAIContextHelper.sharedInstance.newSession()
        let userInfo: [String: Any] = [kMSAISessionInfo: sessionContext]
        MSAIContextHelper.sharedInstance.sendUserChangedNotification(userInfo: userInfo)
        
        let operationContext = MSAIOperation()
        var userContext = MSAIContextHelper.sharedInstance.loadUser()
        if userContext == nil {
            userContext = MSAIContextHelper.sharedInstance.newUser()
        }
        
        let locationContext = MSAILocation()
        
        self.instrumentationKey = appContext.instrumentationKey
        self.application = applicationContext
        self.device = deviceContext
        self.location = locationContext
        self.user = userContext
        self.internalData = internalContext
        self.operation = operationContext
        self.session = sessionContext
        self.tags = self.tags()
        
        configureUserTracking()
        configureNetworkStatusTracking()
        configureSessionTracking()
    }
    
    deinit {
        NotificationCenter.default.removeObserver(self)
    }
    
    // MARK: - Network Tracking
    
    func configureNetworkStatusTracking() {
        MSAIReachability.sharedInstance().startNetworkStatusTracking()
        self.device?.network = MSAIReachability.sharedInstance().descriptionForActiveReachabilityType()
        NotificationCenter.default.addObserver(self, selector: #selector(updateNetworkType(_:)), name: NSNotification.Name(rawValue: kMSAIReachabilityTypeChangedNotification), object: nil)
    }
    
    @objc func updateNetworkType(_ notification: Notification) {
        if let networkType = notification.userInfo?[kMSAIReachabilityUserInfoName] as? String {
            self.device?.network = networkType
        }
    }
    
    // MARK: - Session Tracking
    
    func configureSessionTracking() {
        NotificationCenter.default.addObserver(forName: NSNotification.Name(MSAISessionStartedNotification), object: nil, queue: nil) { [weak self] notification in
            if let session = notification.userInfo?[kMSAISessionInfo] as? MSAISession {
                self?.session = session
            }
        }
    }
    
    // MARK: - User Tracking
    
    func configureUserTracking() {
        NotificationCenter.default.addObserver(forName: NSNotification.Name(MSAIUserChangedNotification), object: nil, queue: nil) { [weak self] notification in
            if let user = notification.userInfo?[kMSAIUserInfo] as? MSAIUser {
                self?.user = user
            }
        }
    }
    
    // MARK: - Helper
    
    func contextDictionary() -> MSAIOrderedDictionary {
        let contextDictionary = MSAIOrderedDictionary()
        contextDictionary.addEntries(from: self.tags ?? [:])
        contextDictionary.addEntries(from: self.session?.serializeToDictionary() ?? [:])
        contextDictionary.addEntries(from: self.user?.serializeToDictionary() ?? [:])
        contextDictionary.addEntries(from: self.device?.serializeToDictionary() ?? [:])
        
        return contextDictionary
    }
    
    func tags() -> MSAIOrderedDictionary {
        if self.tags == nil {
            self.tags = self.application?.serializeToDictionary() ?? MSAIOrderedDictionary()
            self.tags?.addEntries(from: self.application?.serializeToDictionary() ?? [:])
            self.tags?.addEntries(from: self.location?.serializeToDictionary() ?? [:])
            self.tags?.addEntries(from: self.internalData?.serializeToDictionary() ?? [:])
            self.tags?.addEntries(from: self.operation?.serializeToDictionary() ?? [:])
        }
        return self.tags!
    }
}
