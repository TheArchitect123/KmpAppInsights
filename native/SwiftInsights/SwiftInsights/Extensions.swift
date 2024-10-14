import UIKit
import ObjectiveC
import zlib

extension UIViewController {

    static func swizzleViewWillAppear() {
        DispatchQueue.once(token: "com.applicationinsights.swizzleViewWillAppear") {
            let originalSelector = #selector(UIViewController.viewWillAppear(_:))
            let swizzledSelector = #selector(UIViewController.msai_viewWillAppear(_:))

            let originalMethod = class_getInstanceMethod(UIViewController.self, originalSelector)
            let swizzledMethod = class_getInstanceMethod(UIViewController.self, swizzledSelector)

            method_exchangeImplementations(originalMethod!, swizzledMethod!)
        }
    }

    @objc func msai_viewWillAppear(_ animated: Bool) {
        self.msai_viewWillAppear(animated)
        #if MSAI_FEATURE_TELEMETRY
        if !MSAITelemetryManager.shared.autoPageViewTrackingDisabled {
            if !msai_shouldTrackPageView(self) { return }
            let pageViewName = msai_pageViewName(for: self)
            MSAITelemetryManager.shared.trackPageView(pageViewName)
        }
        #endif
    }
}

func msai_shouldTrackPageView(_ viewController: UIViewController) -> Bool {
    let containerViewControllerClasses = ["UINavigationController", "UITabBarController", "UISplitViewController", "UIInputWindowController", "UIPageViewController"]
    
    for classString in containerViewControllerClasses {
        if let containerClass = NSClassFromString(classString) as? UIViewController.Type,
           viewController.isKind(of: containerClass) {
            return false
        }
    }
    return true
}

func msai_pageViewName(for viewController: UIViewController) -> String {
    let className = String(describing: type(of: viewController))
    
    if let title = viewController.title, !title.isEmpty {
        return "\(className) \(title)"
    }
    
    return className
}

class MSAICategoryContainer {
    static func activateCategory() {
        UIViewController.swizzleViewWillAppear()
    }
}

extension DispatchQueue {
    private static var _onceTracker = [String]()

    class func once(token: String, block: () -> Void) {
        objc_sync_enter(self)
        defer { objc_sync_exit(self) }

        if _onceTracker.contains(token) {
            return
        }
        _onceTracker.append(token)
        block()
    }
}

extension Data {
    func gzippedData(compressionLevel level: Float = -1.0) -> Data? {
        if self.isEmpty { return nil }
        
        var stream = z_stream()
        stream.next_in = UnsafeMutablePointer<Bytef>(mutating: (self as NSData).bytes.bindMemory(to: Bytef.self, capacity: self.count))
        stream.avail_in = uint(self.count)
        
        let compression = (level < 0.0) ? Z_DEFAULT_COMPRESSION : Int32(level * 9)
        guard deflateInit2_(&stream, compression, Z_DEFLATED, 31, 8, Z_DEFAULT_STRATEGY, ZLIB_VERSION, Int32(MemoryLayout<z_stream>.size)) == Z_OK else { return nil }
        
        var data = Data(count: 16384)
        while stream.avail_out == 0 {
            if stream.total_out >= data.count {
                data.count += 16384
            }
            stream.next_out = UnsafeMutablePointer<Bytef>(mutating: data.withUnsafeMutableBytes { $0.baseAddress!.assumingMemoryBound(to: Bytef.self) } + Int(stream.total_out))
            stream.avail_out = uInt(data.count) - uInt(stream.total_out)
            deflate(&stream, Z_FINISH)
        }
        
        deflateEnd(&stream)
        data.count = Int(stream.total_out)
        return data
    }

    func gunzippedData() -> Data? {
        if self.isEmpty { return nil }

        var stream = z_stream()
        stream.next_in = UnsafeMutablePointer<Bytef>(mutating: (self as NSData).bytes.bindMemory(to: Bytef.self, capacity: self.count))
        stream.avail_in = uint(self.count)
        
        var data = Data(count: self.count * 2)
        guard inflateInit2_(&stream, 47, ZLIB_VERSION, Int32(MemoryLayout<z_stream>.size)) == Z_OK else { return nil }
        
        var status: Int32
        repeat {
            if stream.total_out >= data.count {
                data.count += self.count / 2
            }
            stream.next_out = UnsafeMutablePointer<Bytef>(mutating: data.withUnsafeMutableBytes { $0.baseAddress!.assumingMemoryBound(to: Bytef.self) } + Int(stream.total_out))
            stream.avail_out = uInt(data.count) - uInt(stream.total_out)
            status = inflate(&stream, Z_SYNC_FLUSH)
        } while status == Z_OK
        
        guard inflateEnd(&stream) == Z_OK, status == Z_STREAM_END else { return nil }
        
        data.count = Int(stream.total_out)
        return data
    }
}
