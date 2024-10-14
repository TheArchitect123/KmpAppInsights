import Foundation

let kMSAIUtcDateFormatter = "utcDateFormatter"

struct msai_info_t {
    var info_version: UInt8
    var msai_version: [CChar]
    var msai_build: [CChar]
}

class MSAIApplicationInsights {
    
    static let kMSAIInstrumentationKey = "MSAIInstrumentationKey"
    
    private var validInstrumentationKey = false
    private var startManagerIsInvoked = false
    private var managersInitialized = false
    private var _appClient: MSAIAppClient?
    private var appContext: MSAIContext?
    
    static let sharedInstance: MSAIApplicationInsights = {
        let instance = MSAIApplicationInsights()
        return instance
    }()
    
    private init() {
        self.serverURL = nil
        self.managersInitialized = false
        self._appClient = nil
        self.appStoreEnvironment = false
        self.startManagerIsInvoked = false
        
        msai_isAppStoreEnvironment()
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.0) {
            self.validateStartManagerIsInvoked()
        }
    }
    
    var serverURL: String?
    var appStoreEnvironment = false
    
#if MSAI_FEATURE_TELEMETRY
    var telemetryManagerDisabled = false
#endif /* MSAI_FEATURE_TELEMETRY */
    
    func setup() {
        if let instrumentationKey = Bundle.main.object(forInfoDictionaryKey: MSAIApplicationInsights.kMSAIInstrumentationKey) as? String {
            self.setupWithInstrumentationKey(instrumentationKey)
        }
    }
    
    func setupWithInstrumentationKey(_ instrumentationKey: String) {
        appContext = MSAIContext(instrumentationKey: instrumentationKey)
        initializeModules()
    }
    
    static func setup() {
        MSAIApplicationInsights.sharedInstance.setup()
    }
    
    static func setupWithInstrumentationKey(_ instrumentationKey: String) {
        MSAIApplicationInsights.sharedInstance.setupWithInstrumentationKey(instrumentationKey)
    }
    
    func start() {
        guard validInstrumentationKey else { return }
        
        if startManagerIsInvoked {
            print("[ApplicationInsights] Warning: start should only be invoked once! This call is ignored.")
            return
        }
        
        guard isSetUpOnMainThread() else { return }
        
        print("INFO: Starting MSAIManager")
        startManagerIsInvoked = true
        
        MSAISender.sharedSender.sendSavedData()
        
#if MSAI_FEATURE_TELEMETRY
        if !self.isTelemetryManagerDisabled() {
            if isAutoPageViewTrackingDisabled() {
                print("INFO: Auto page views disabled")
                MSAITelemetryManager.shared.autoPageViewTrackingDisabled = true
            }
            print("INFO: Starting MSAITelemetryManager")
            MSAITelemetryManager.shared.startManager()
        }
#endif /* MSAI_FEATURE_TELEMETRY */
        
        MSAICategoryContainer.activateCategory()
    }
    
    static func start() {
        MSAIApplicationInsights.sharedInstance.start()
    }
    
    func validateStartManagerIsInvoked() {
        if validInstrumentationKey && !appStoreEnvironment && !startManagerIsInvoked {
            print("[ApplicationInsights] ERROR: You did not call [MSAIApplicationInsights setup] to setup ApplicationInsights! The SDK is NOT running.")
        }
    }
    
    func isSetUpOnMainThread() -> Bool {
        if !Thread.isMainThread {
            print("[ApplicationInsights] ERROR: ApplicationInsights has to be setup on the main thread!")
            return false
        }
        return true
    }
    
    func initializeModules() {
        if managersInitialized {
            print("[ApplicationInsights] Warning: The SDK should only be initialized once! This call is ignored.")
            return
        }
        
        validInstrumentationKey = checkValidityOfInstrumentationKey(appContext?.instrumentationKey)
        
        guard isSetUpOnMainThread() else { return }
        
        startManagerIsInvoked = false
        
        if validInstrumentationKey {
            telemetryContext = MSAITelemetryContext(appContext: appContext!)
            MSAIEnvelopeManager.sharedManager.configure(with: telemetryContext!)
            MSAISender.sharedSender.configure(withAppClient: appClient)
            
#if MSAI_FEATURE_TELEMETRY
            print("INFO: Setup TelemetryManager")
#endif /* MSAI_FEATURE_TELEMETRY */
            
            managersInitialized = true
        } else if !appStoreEnvironment {
            print("[ApplicationInsights] ERROR: The Instrumentation Key is invalid! The SDK is disabled!")
        }
    }
    
    func checkValidityOfInstrumentationKey(_ instrumentationKey: String?) -> Bool {
        guard let instrumentationKey = instrumentationKey else { return false }
        let hexSet = CharacterSet(charactersIn: "0123456789abcdef-")
        let inStringSet = CharacterSet(charactersIn: instrumentationKey)
        let keyIsValid = instrumentationKey.count == 36 && hexSet.isSuperset(of: inStringSet)
        let internalKey = instrumentationKey.count == 40 && instrumentationKey.hasPrefix("AIF")
        return keyIsValid || internalKey
    }
    
    var telemetryContext: MSAITelemetryContext?
    
    var appClient: MSAIAppClient {
        if _appClient == nil {
            _appClient = MSAIAppClient(baseURL: URL(string: serverURL ?? MSAI_SERVER_URL)!)
        }
        return _appClient!
    }
    
#if MSAI_FEATURE_TELEMETRY
    func setTelemetryManagerDisabled(_ disabled: Bool) {
        MSAITelemetryManager.shared.telemetryManagerDisabled = disabled
        telemetryManagerDisabled = disabled
    }
    
    static func setTelemetryManagerDisabled(_ disabled: Bool) {
        MSAIApplicationInsights.sharedInstance.setTelemetryManagerDisabled(disabled)
    }
#endif /* MSAI_FEATURE_TELEMETRY */
}
