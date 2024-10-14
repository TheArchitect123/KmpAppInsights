#import "MSAIPersistence.h"
@class MSAIEnvelope;

NS_ASSUME_NONNULL_BEGIN

/**
 * Notification that will be send on the main thread to notifiy observers of a successfully saved bundle.
 * This is typically used to trigger sending to the server.
 */
FOUNDATION_EXPORT NSString *const MSAIPersistenceSuccessNotification;

@interface MSAIPersistence ()

///-----------------------------------------------------------------------------
/// @name Save/delete bundle of data
///-----------------------------------------------------------------------------

/**
 *  A queue which makes file system operations thread safe.
 */
@property (nonatomic, strong) dispatch_queue_t persistenceQueue;

/**
 *  Determines how many files (regular prio) can be on disk at a time.
 */
@property NSUInteger maxFileCount;

/**
 *  An array with all file paths, that have been requested by the sender. If the
 *  triggers a delete, the appropriate path should also be removed here. We keep to
 *  track of requested bundles to make sure, that bundles get sent twice at the same
 *  time by differend http operations.
 */
@property (nonatomic, strong) NSMutableArray *requestedBundlePaths;

/**
 * Saves the bundle and sends out a MSAIPersistenceSuccessNotification in case of success
 * for all types
 * @param bundle a bundle of tracked events that will be serialized and saved.
 * @param type The type of the bundle we want to save.
 * @param completionBlock An optional block that will be executed after we have tried to save the bundle.
 *
 * @warning: The data within the array needs to implement NSCoding.
 */
- (void)persistBundle:(NSData *)bundle ofType:(MSAIPersistenceType)type withCompletionBlock:(nullable void (^)(BOOL success))completionBlock;

/**
 *  Saves the bundle to disk.
 *
 *  @param bundle            the bundle, which should be saved to disk
 *  @param type              the persistence type of the bundle (high prio/regular prio)
 *  @param sendNotifications a flag which determines if a notification should be sent if saving was successful
 *  @param completionBlock   a block which is executed after the bundle has been stored
 */
- (void)persistBundle:(NSData *)bundle ofType:(MSAIPersistenceType)type enableNotifications:(BOOL)sendNotifications withCompletionBlock:(void (^)(BOOL success))completionBlock;

/**
 *  Deletes the file for the given path.
 *
 *  @param path the path of the file, which should be deleted
 */
- (void)deleteFileAtPath:(NSString *)path ;

/**
 *  Determines whether the persistence layer is able to write more files to disk.
 *
 *  @return YES if the maxFileCount has not been reached, yet (otherwise NO).
 */
- (BOOL)isFreeSpaceAvailable;

///-----------------------------------------------------------------------------
/// @name Get a bundle of saved data
///-----------------------------------------------------------------------------

/**
 * Get a bundle of previously saved data from disk and deletes it using dispatch_sync.
 *
 * @warning Make sure nextBundle is not called from the main thread.
 *
 * It will return bundles of MSAIPersistenceType first.
 * Between bundles of the same MSAIPersistenceType, the order is arbitrary.
 * Returns 'nil' if no bundle is available
 *
 * @return a bundle of data that's ready to be sent to the server
 */

/**
 *  Returns the path for the next item to send. The requested path is reserved as long
 *  as leaveUpRequestedPath: gets called.
 *
 *  @see giveBackRequestedPath:
 *
 *  @return the path of the item, which should be sent next
 */
- (NSString *)requestNextPath;

/**
 *  Release a requested path. This method should be called after sending a file failed.
 *
 *  @param path the path that should be available for sending again.
 */
- (void)giveBackRequestedPath:(NSString *)path;

/**
 *  Return the bundle for a given path.
 *
 *  @param path the path of the bundle.
 *
 *  @return an array with all envelope objects.
 */
- (NSArray *)bundleAtPath:(NSString *)path;

/**
 *  Return the json data for a given path
 *
 *  @param path the path of the file
 *
 *  @return a data object which contains telemetry data in json representation
 */
- (NSData *)dataAtPath:(NSString *)path;

///-----------------------------------------------------------------------------
/// @name Getting a path
///-----------------------------------------------------------------------------

/**
 *  Returns a folder path for items of a given type.
 *
 *  @param type the file type, which the directory holds
 *
 *  @return a folder path for items of a given type
 */
- (NSString *)folderPathForPersistenceType:(MSAIPersistenceType)type;

@end
NS_ASSUME_NONNULL_END
