import Foundation
import UIKit
import System
import Dispatch

// MARK: - NSString helpers

func msai_URLEncodedString(_ inputString: String) -> String {
    return inputString.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? inputString
}

func msai_URLDecodedString(_ inputString: String) -> String {
    return inputString.removingPercentEncoding ?? inputString
}

// Return ISO 8601 string representation of the date
func msai_utcDateString(_ date: Date) -> String {
    var dateFormatter: DateFormatter?
    
    if msai_isPreiOS7Environment() {
        let threadDictionary = Thread.current.threadDictionary
        
        if let formatter = threadDictionary[kMSAIUtcDateFormatter] as? DateFormatter {
            dateFormatter = formatter
        } else {
            let enUSPOSIXLocale = Locale(identifier: "en_US_POSIX")
            dateFormatter = DateFormatter()
            dateFormatter?.locale = enUSPOSIXLocale
            dateFormatter?.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
            dateFormatter?.timeZone = TimeZone(secondsFromGMT: 0)
            threadDictionary[kMSAIUtcDateFormatter] = dateFormatter
        }
        
        return dateFormatter?.string(from: date) ?? ""
    }
    
    var onceToken: DispatchOnceToken = DispatchOnceToken()
    onceToken.perform {
        let enUSPOSIXLocale = Locale(identifier: "en_US_POSIX")
        dateFormatter = DateFormatter()
        dateFormatter?.locale = enUSPOSIXLocale
        dateFormatter?.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        dateFormatter?.timeZone = TimeZone(secondsFromGMT: 0)
    }
    
    return dateFormatter?.string(from: date) ?? ""
}

func msai_base64String(_ data: Data) -> String {
    return data.base64EncodedString()
}

func msai_keychainMSAIServiceName() -> String {
    var serviceName: String?
    var predServiceName: DispatchOnceToken = DispatchOnceToken()
    
    predServiceName.perform {
        serviceName = "\(msai_mainBundleIdentifier()).MSAI"
    }
    
    return serviceName ?? ""
}

func msai_mainBundleIdentifier() -> String {
    return Bundle.main.object(forInfoDictionaryKey: "CFBundleIdentifier") as? String ?? ""
}

func msai_encodeInstrumentationKey(_ inputString: String?) -> String {
    return inputString != nil ? msai_URLEncodedString(inputString!) : msai_URLEncodedString(msai_mainBundleIdentifier())
}

func msai_osVersionBuild() -> String? {
    var size: size_t = 0
    var ret: Int32 = 0
    let name = "kern.osversion"
    
    // Fetch the expected length
    if sysctlbyname(name, nil, &size, nil, 0) == -1 {
        return nil
    }
    
    var result = malloc(size)
    ret = sysctlbyname(name, result, &size, nil, 0)
    
    if ret == -1 {
        free(result)
        return nil
    }
    
    let osBuild = String(cString: result!)
    free(result)
    
    let osVersion = UIDevice.current.systemVersion
    
    return "\(osVersion) (\(osBuild))"
}

func msai_osName() -> String {
    return UIDevice.current.systemName
}

func msai_appVersion() -> String {
    let build = Bundle.main.infoDictionary?["CFBundleVersion"] as? String ?? ""
    let version = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String
    
    if let version = version {
        return "\(version) (\(build))"
    } else {
        return build
    }
}

func msai_deviceType() -> String {
    switch UIDevice.current.userInterfaceIdiom {
    case .pad:
        return "Tablet"
    case .phone:
        return "Phone"
    default:
        return "Unknown"
    }
}

func msai_screenSize() -> String {
    let scale = UIScreen.main.scale
    let screenSize = UIScreen.main.bounds.size
    return "\(Int(screenSize.height * scale))x\(Int(screenSize.width * scale))"
}

func msai_sdkVersion() -> String {
    return "ios:\(String(cString: applicationinsights_library_info.msai_version))"
}

func msai_sdkBuild() -> String {
    return String(cString: applicationinsights_library_info.msai_build)
}

func msai_devicePlatform() -> String {
    var size: size_t = 0
    sysctlbyname("hw.machine", nil, &size, nil, 0)
    let answer = malloc(size)
    
    if sysctlbyname("hw.machine", answer, &size, nil, 0) != 0 {
        free(answer)
        return ""
    }
    
    let platform = String(cString: answer!)
    free(answer)
    return platform
}

func msai_deviceLanguage() -> String {
    return Bundle.main.preferredLocalizations.first ?? ""
}

func msai_deviceLocale() -> String {
    return Locale.current.identifier
}

func msai_UUID() -> String {
    return UUID().uuidString
}

func msai_appAnonID() -> String? {
    static var appAnonID: String?
    static var predAppAnonID: DispatchOnceToken = DispatchOnceToken()
    
    predAppAnonID.perform {
        let appAnonIDKey = "appAnonID"
        appAnonID = MSAIKeychainUtils.getPassword(forUsername: appAnonIDKey, andServiceName: msai_keychainMSAIServiceName(), error: nil)
        
        if appAnonID == nil {
            appAnonID = msai_UUID()
            if let appAnonID = appAnonID {
                DispatchQueue.global(qos: .background).async {
                    MSAIKeychainUtils.storeUsername(appAnonIDKey, andPassword: appAnonID, forServiceName: msai_keychainMSAIServiceName(), updateExisting: true, accessibility: kSecAttrAccessibleAlwaysThisDeviceOnly, error: nil)
                }
            }
        }
    }
    
    return appAnonID
}

func msai_isPreiOS7Environment() -> Bool {
    static var isPreiOS7Environment: Bool = true
    static var checkOS: DispatchOnceToken = DispatchOnceToken()
    
    checkOS.perform {
        if floor(NSFoundationVersionNumber) <= 993.00 {
            isPreiOS7Environment = true
        } else {
            isPreiOS7Environment = false
        }
    }
    
    return isPreiOS7Environment
}

func msai_isPreiOS8Environment() -> Bool {
    static var isPreiOS8Environment: Bool = true
    static var checkOS8: DispatchOnceToken = DispatchOnceToken()
    
    checkOS8.perform {
        if floor(NSFoundationVersionNumber) <= 1047.25 {
            isPreiOS8Environment = true
        } else {
            isPreiOS8Environment = false
        }
    }
    
    return isPreiOS8Environment
}

func msai_isRunningInAppExtension() -> Bool {
    static var isRunningInAppExtension: Bool = false
    static var checkAppExtension: DispatchOnceToken = DispatchOnceToken()
    
    checkAppExtension.perform {
        isRunningInAppExtension = (Bundle.main.executablePath?.range(of: ".appex/") != nil)
    }
    
    return isRunningInAppExtension
}

func msai_isAppStoreEnvironment() -> Bool {
#if !TARGET_IPHONE_SIMULATOR
    if Bundle.main.path(forResource: "embedded", ofType: "mobileprovision") == nil {
        return true
    }
#endif
    return false
}

func msai_isDebuggerAttached() -> Bool {
    static var debuggerIsAttached: Bool = false
    static var debuggerPredicate: DispatchOnceToken = DispatchOnceToken()
    
    debuggerPredicate.perform {
        var info = kinfo_proc()
        var info_size = MemoryLayout<kinfo_proc>.size
        var name = [CTL_KERN, KERN_PROC, KERN_PROC_PID, getpid()]
        
        if sysctl(&name, 4, &info, &info_size, nil, 0) == -1 {
            NSLog("[ApplicationInsights] ERROR: Checking for a running debugger via sysctl() failed: %s", strerror(errno))
            debuggerIsAttached = false
        }
        
        if !debuggerIsAttached && (info.kp_proc.p_flag & P_TRACED) != 0 {
            debuggerIsAttached = true
        }
    }
    
    return debuggerIsAttached
}
