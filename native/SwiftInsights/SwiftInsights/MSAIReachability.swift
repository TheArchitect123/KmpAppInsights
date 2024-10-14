import SystemConfiguration
import CoreTelephony

extension Notification.Name {
    static let reachabilityTypeChanged = Notification.Name("MSAIReachabilityTypeChangedNotification")
}

enum MSAIReachabilityType: Int {
    case none, wifi, wwan, gprs, edge, _3G, lte
}

class MSAIReachability {
    
    static let shared = MSAIReachability()
    
    private let singletonQueue = DispatchQueue(label: "com.microsoft.ApplicationInsights.singletonQueue")
    private let networkQueue = DispatchQueue(label: "com.microsoft.ApplicationInsights.networkQueue")
    
    private var reachabilityRef: SCNetworkReachability?
    private var reachabilityType: MSAIReachabilityType = .none
    private var running: Bool = false
    
    var radioInfo: CTTelephonyNetworkInfo?
    
    private init() {
        if #available(iOS 7.0, *) {
            radioInfo = CTTelephonyNetworkInfo()
        }
        configureReachability()
    }
    
    deinit {
        stopNetworkStatusTracking()
    }
    
    private func configureReachability() {
        singletonQueue.sync {
            var zeroAddress = sockaddr_in()
            zeroAddress.sin_len = UInt8(MemoryLayout<sockaddr_in>.size)
            zeroAddress.sin_family = sa_family_t(AF_INET)
            
            reachabilityRef = withUnsafePointer(to: &zeroAddress) {
                $0.withMemoryRebound(to: sockaddr.self, capacity: 1) {
                    SCNetworkReachabilityCreateWithAddress(nil, $0)
                }
            }
        }
    }
    
    func startNetworkStatusTracking() {
        singletonQueue.async {
            guard !self.running, let reachability = self.reachabilityRef else { return }
            
            var context = SCNetworkReachabilityContext(version: 0, info: UnsafeMutableRawPointer(Unmanaged.passUnretained(self).toOpaque()), retain: nil, release: nil, copyDescription: nil)
            
            if SCNetworkReachabilitySetCallback(reachability, { (_, _, info) in
                guard let info = info else { return }
                let instance = Unmanaged<MSAIReachability>.fromOpaque(info).takeUnretainedValue()
                instance.notify()
            }, &context) {
                
                if SCNetworkReachabilitySetDispatchQueue(reachability, self.networkQueue) {
                    if #available(iOS 7.0, *) {
                        self.registerRadioObserver()
                    }
                    self.running = true
                } else {
                    SCNetworkReachabilitySetCallback(reachability, nil, nil)
                }
            }
        }
    }
    
    func stopNetworkStatusTracking() {
        singletonQueue.async {
            if #available(iOS 7.0, *) {
                self.unregisterRadioObserver()
            }
            
            if let reachability = self.reachabilityRef {
                SCNetworkReachabilitySetCallback(reachability, nil, nil)
                SCNetworkReachabilitySetDispatchQueue(reachability, nil)
                self.running = false
            }
        }
    }
    
    private func registerRadioObserver() {
        NotificationCenter.default.addObserver(forName: .CTRadioAccessTechnologyDidChange, object: nil, queue: nil) { [weak self] _ in
            self?.notify()
        }
    }
    
    private func unregisterRadioObserver() {
        NotificationCenter.default.removeObserver(self, name: .CTRadioAccessTechnologyDidChange, object: nil)
    }
    
    private func notify() {
        singletonQueue.async {
            self.reachabilityType = self.activeReachabilityType()
            let notificationDict: [String: Any] = [
                "kName": self.descriptionForReachabilityType(self.reachabilityType),
                "kType": self.reachabilityType.rawValue
            ]
            DispatchQueue.main.async {
                NotificationCenter.default.post(name: .reachabilityTypeChanged, object: nil, userInfo: notificationDict)
            }
        }
    }
    
    private func activeReachabilityType() -> MSAIReachabilityType {
        guard let reachability = reachabilityRef else { return .none }
        var flags = SCNetworkReachabilityFlags()
        
        if !SCNetworkReachabilityGetFlags(reachability, &flags) {
            return .none
        }
        
        if !flags.contains(.reachable) {
            return .none
        }
        
        var reachabilityType: MSAIReachabilityType = .none
        
        if !flags.contains(.connectionRequired) {
            reachabilityType = .wifi
        }
        
        if flags.contains(.connectionOnDemand) || flags.contains(.connectionOnTraffic) {
            if !flags.contains(.interventionRequired) {
                reachabilityType = .wifi
            }
        }
        
        if flags.contains(.isWWAN) {
            reachabilityType = .wwan
            if let radioInfo = radioInfo, let currentRadioAccessTechnology = radioInfo.currentRadioAccessTechnology {
                reachabilityType = wwanType(for: currentRadioAccessTechnology)
            }
        }
        
        return reachabilityType
    }
    
    private func wwanType(for technology: String) -> MSAIReachabilityType {
        switch technology {
        case CTRadioAccessTechnologyGPRS, CTRadioAccessTechnologyCDMA1x:
            return .gprs
        case CTRadioAccessTechnologyEdge:
            return .edge
        case CTRadioAccessTechnologyWCDMA, CTRadioAccessTechnologyHSDPA, CTRadioAccessTechnologyHSUPA,
             CTRadioAccessTechnologyCDMAEVDORev0, CTRadioAccessTechnologyCDMAEVDORevA,
             CTRadioAccessTechnologyCDMAEVDORevB, CTRadioAccessTechnologyeHRPD:
            return ._3G
        case CTRadioAccessTechnologyLTE:
            return .lte
        default:
            return .none
        }
    }
    
    func descriptionForReachabilityType(_ reachabilityType: MSAIReachabilityType) -> String {
        switch reachabilityType {
        case .wifi:
            return "WIFI"
        case .wwan:
            return "WWAN"
        case .gprs:
            return "GPRS"
        case .edge:
            return "EDGE"
        case ._3G:
            return "3G"
        case .lte:
            return "LTE"
        default:
            return "None"
        }
    }
    
    func descriptionForActiveReachabilityType() -> String {
        return descriptionForReachabilityType(activeReachabilityType())
    }
}
