#import "MSAICategoryContainer.h"
#import <objc/runtime.h>
#import "MSAITelemetryManager.h"
#import "MSAITelemetryManagerPrivate.h"

@implementation UIViewController(PageViewLogging)

+ (void)swizzleViewWillAppear {
  static dispatch_once_t onceToken;
  dispatch_once(&onceToken, ^{
    Class class = [self class];
    
    SEL originalSelector = @selector(viewWillAppear:);
    SEL swizzledSelector = @selector(msai_viewWillAppear:);
    
    Method originalMethod = class_getInstanceMethod(class, originalSelector);
    Method swizzledMethod = class_getInstanceMethod(class, swizzledSelector);
    
    method_exchangeImplementations(originalMethod, swizzledMethod);
  });
}

#pragma mark - Method Swizzling

- (void)msai_viewWillAppear:(BOOL)animated {
  [self msai_viewWillAppear:animated];
#if MSAI_FEATURE_TELEMETRY
  if(![MSAITelemetryManager sharedManager].autoPageViewTrackingDisabled) {
    
    if (!msai_shouldTrackPageView(self)) {
      return;;
    }
    
    NSString *pageViewName = msai_pageViewNameForViewController(self);
    
    [[MSAITelemetryManager sharedManager] trackPageView:pageViewName];
  }
#endif /* MSAI_FEATURE_TELEMETRY */
}

@end

BOOL msai_shouldTrackPageView(UIViewController *viewController) {
  NSArray *containerViewControllerClasses = @ [@"UINavigationController", @"UITabBarController", @"UISplitViewController", @"UIInputWindowController", @"UIPageViewController"];
  
  // Check if current class is a kind of one of the known container view controller classes.
  for (NSString *classString in containerViewControllerClasses) {
    Class aContainerClass = NSClassFromString(classString);
    if ([viewController isKindOfClass:aContainerClass]) {
      return NO;
    }
  }
  return YES;
}

NSString* msai_pageViewNameForViewController(UIViewController *viewController) {
  NSString *className = NSStringFromClass([viewController class]);
  
  if (viewController.title && (viewController.title.length > 0)) {
    return [NSString stringWithFormat:@"%@ %@", className, viewController.title];
  }
  return className;
}

@implementation MSAICategoryContainer

+ (void)activateCategory {
  [UIViewController swizzleViewWillAppear];
}

@end


#pragma mark - GZIP library


//
//  GZIP.m
//
//  Version 1.0.3
//
//  Created by Nick Lockwood on 03/06/2012.
//  Copyright (C) 2012 Charcoal Design
//
//  Distributed under the permissive zlib License
//  Get the latest version from here:
//
//  https://github.com/nicklockwood/GZIP
//
//  This software is provided 'as-is', without any express or implied
//  warranty.  In no event will the authors be held liable for any damages
//  arising from the use of this software.
//
//  Permission is granted to anyone to use this software for any purpose,
//  including commercial applications, and to alter it and redistribute it
//  freely, subject to the following restrictions:
//
//  1. The origin of this software must not be misrepresented; you must not
//  claim that you wrote the original software. If you use this software
//  in a product, an acknowledgment in the product documentation would be
//  appreciated but is not required.
//
//  2. Altered source versions must be plainly marked as such, and must not be
//  misrepresented as being the original software.
//
//  3. This notice may not be removed or altered from any source distribution.
//

#import <zlib.h>

static const NSUInteger ChunkSize = 16384;

@implementation NSData (GZIP)

- (NSData *)gzippedDataWithCompressionLevel:(float)level
{
  if ([self length])
  {
    z_stream stream;
    stream.zalloc = Z_NULL;
    stream.zfree = Z_NULL;
    stream.opaque = Z_NULL;
    stream.avail_in = (uint)[self length];
    stream.next_in = (Bytef *)[self bytes];
    stream.total_out = 0;
    stream.avail_out = 0;
    
    int compression = (level < 0.0f)? Z_DEFAULT_COMPRESSION: (int)(roundf(level * 9));
    if (deflateInit2(&stream, compression, Z_DEFLATED, 31, 8, Z_DEFAULT_STRATEGY) == Z_OK)
    {
      NSMutableData *data = [NSMutableData dataWithLength:ChunkSize];
      while (stream.avail_out == 0)
      {
        if (stream.total_out >= [data length])
        {
          data.length += ChunkSize;
        }
        stream.next_out = (uint8_t *)[data mutableBytes] + stream.total_out;
        stream.avail_out = (uInt)([data length] - stream.total_out);
        deflate(&stream, Z_FINISH);
      }
      deflateEnd(&stream);
      data.length = stream.total_out;
      return data;
    }
  }
  return nil;
}

- (NSData *)gzippedData
{
  return [self gzippedDataWithCompressionLevel:-1.0f];
}

- (NSData *)gunzippedData
{
  if ([self length])
  {
    z_stream stream;
    stream.zalloc = Z_NULL;
    stream.zfree = Z_NULL;
    stream.avail_in = (uint)[self length];
    stream.next_in = (Bytef *)[self bytes];
    stream.total_out = 0;
    stream.avail_out = 0;
    
    NSMutableData *data = [NSMutableData dataWithLength:(NSUInteger)([self length] * 1.5)];
    if (inflateInit2(&stream, 47) == Z_OK)
    {
      int status = Z_OK;
      while (status == Z_OK)
      {
        if (stream.total_out >= [data length])
        {
          data.length += [self length] / 2;
        }
        stream.next_out = (uint8_t *)[data mutableBytes] + stream.total_out;
        stream.avail_out = (uInt)([data length] - stream.total_out);
        status = inflate (&stream, Z_SYNC_FLUSH);
      }
      if (inflateEnd(&stream) == Z_OK)
      {
        if (status == Z_STREAM_END)
        {
          data.length = stream.total_out;
          return data;
        }
      }
    }
  }
  return nil;
}

@end
