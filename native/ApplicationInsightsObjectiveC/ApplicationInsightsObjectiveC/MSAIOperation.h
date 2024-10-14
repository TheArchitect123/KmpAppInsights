#import "MSAIObject.h"

@interface MSAIOperation : MSAIObject <NSCoding>

@property (nonatomic, copy) NSString *operationId;
@property (nonatomic, copy) NSString *name;
@property (nonatomic, copy) NSString *parentId;
@property (nonatomic, copy) NSString *rootId;
@property (nonatomic, copy) NSString *syntheticSource;
@property (nonatomic, copy) NSString *isSynthetic;

@end
