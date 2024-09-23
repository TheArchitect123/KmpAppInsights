package com.architect.kmpappinsights.library

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Point
import android.net.ConnectivityManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.view.Display
import android.view.WindowManager
import com.architect.kmpappinsights.contracts.Application
import com.architect.kmpappinsights.contracts.Device
import com.architect.kmpappinsights.contracts.Internal
import com.architect.kmpappinsights.contracts.Operation
import com.architect.kmpappinsights.contracts.Session
import com.architect.kmpappinsights.contracts.User
import com.architect.kmpappinsights.logging.InternalLogging
import java.util.Locale
import java.util.UUID
import kotlin.concurrent.Volatile

/**
 * This class is holding all telemetryContext information.
 */
class TelemetryContext private constructor() {
    private val IKEY_LOCK = Any()

    /**
     * Synchronization LOCK for setting static context
     */
    private val INSTANCE_LOCK = Any()

    /**
     * The shared preferences INSTANCE for reading persistent context
     */
    private var settings: SharedPreferences? = null

    /**
     * Device telemetryContext.
     */
    @set:Synchronized
    var instrumentationKey: String? = null
        get() {
            synchronized(IKEY_LOCK) {
                return field
            }
        }
        set(instrumentationKey) {
            synchronized(IKEY_LOCK) {
                field = instrumentationKey
            }
        }

    /**
     * Device telemetryContext.
     */
    private val device = Device()

    /**
     * Session telemetryContext.
     */
    private val session = Session()

    /**
     * User telemetryContext.
     */
    private val user = User()

    /**
     * Application telemetryContext.
     */
    private val application = Application()

    /**
     * Internal telemetryContext.
     */
    private val internal = Internal()

    /**
     * The last session ID
     */
    private var lastSessionId: String? = null

    /**
     * The package name
     *
     * @see this.packageName
     */
    /**
     * The App ID for the envelope (defined as PackageInfo.packageName by CLL team)
     */
    var packageName: String? = null
        private set

    /**
     * Operation telemetryContext.
     */
    private val operation = Operation()

    fun resetContext() {
        // Reset device context

        deviceId = instance!!.deviceId
        deviceModel = instance!!.deviceModel
        deviceOemName = instance!!.deviceOemName
        deviceType = instance!!.deviceType
        osName = instance!!.osName
        osVersion = instance!!.osVersion
        networkType = instance!!.networkType

        // Reset session context
        sessionId = instance!!.sessionId

        // Reset user context
        userAcqusitionDate = instance!!.userAcqusitionDate
        userId = instance!!.userId
        accountId = instance!!.accountId

        // Reset internal context
        sdkVersion = instance!!.sdkVersion

        // Reset applicationContext
        appVersion = instance!!.appVersion

        // Reset other
        instrumentationKey = instance!!.instrumentationKey
    }

    /**
     * Constructs a new INSTANCE of the Telemetry telemetryContext tag keys
     *
     * @param context            the context for this telemetryContext
     * @param instrumentationKey the instrumentationkey for this application
     * @param user               a custom user object that will be assiciated with the telemetry data
     */
    protected constructor(context: Context, instrumentationKey: String?, user: User?) : this() {
        this.settings = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)

        configDeviceContext(context)
        configSessionContext()
        configUserContext(user)
        configInternalContext(context)
        configAppContext(context)

        this.lastSessionId = null
        this.instrumentationKey = instrumentationKey
    }

    /**
     * Renews the session context
     * The session ID is on demand. Additionally, the isFirst flag is set if no data was
     * found in settings and the isNew flag is set each time a new UUID is
     * generated.
     */
    fun renewSessionId() {
        val newId = UUID.randomUUID().toString()
        this.renewSessionId(newId)
    }

    /**
     * Renews the session context with a custom session ID.
     *
     * @param sessionId a custom session ID
     */
    fun renewSessionId(sessionId: String?) {
        this.sessionId = sessionId
        //normally, this should also be saved to SharedPrefs like isFirst.
        //The problem is that there are cases when committing the changes is too slow and we get
        //the wrong value. As isNew is only "true" when we start a new session, it is set in
        //TrackDataOperation directly before enqueueing the session event.
        isNewSession = "false"

        val editor = settings!!.edit()
        if (!settings!!.getBoolean(SESSION_IS_FIRST_KEY, false)) {
            editor.putBoolean(SESSION_IS_FIRST_KEY, true)
            editor.apply()
            isFirstSession = "true"
        } else {
            isFirstSession = "false"
        }
    }

    /**
     * Sets the session context
     */
    protected fun configSessionContext() {
        if (this.lastSessionId == null) {
            renewSessionId()
        } else {
            sessionId = lastSessionId
        }
    }

    /**
     * Sets the application telemetryContext tags
     *
     * @param appContext the android context
     */
    protected fun configAppContext(appContext: Context) {
        var version = "unknown"
        this.packageName = ""

        try {
            val manager = appContext.packageManager
            val info = manager
                .getPackageInfo(appContext.packageName, 0)

            if (info.packageName != null) {
                this.packageName = info.packageName
            }

            val appBuild = info.versionCode.toString()
            version = String.format("%s (%S)", info.versionName, appBuild)
        } catch (e: PackageManager.NameNotFoundException) {
            InternalLogging.warn(TAG, "Could not collect application context")
        } finally {
            appVersion = version
        }
    }

    /**
     * Sets the user Id. This method has been made protected to make sure it's not accessed from outside the SDK
     *
     * @param userId custom user id
     */
    protected fun configUserContext(userId: String?) {
        var userId = userId
        if (userId == null) {
            // No custom user Id is given, so get this info from settings
            userId =
                settings!!.getString(USER_ID_KEY, null)
            if (userId == null) {
                // No settings available, generate new user info
                userId = UUID.randomUUID().toString()
            }
        }

        this.userId = userId
        saveUserInfo()
    }

    /**
     * set the user for the user context associated with telemetry data.
     *
     * @param user The user object
     * In case the user object that is passed is null, a new user object will be generated.
     * If the user is missing a property, they will be generated, too.
     */
    protected fun configUserContext(user: User?) {
        if (user == null) {
            loadUserInfo()
        }

        if (user != null && user.id == null) {
            userId = UUID.randomUUID().toString()
        }
        saveUserInfo()
    }

    /**
     * Write user information to shared preferences.
     */
    protected fun saveUserInfo() {
        val editor = settings!!.edit()
        editor.putString(USER_ID_KEY, userId)
        editor.putString(USER_ACQ_KEY, userAcqusitionDate)
        editor.putString(USER_ACCOUNT_ID_KEY, accountId)
        editor.putString(USER_AUTH_USER_ID_KEY, authenticatedUserId)
        editor.putString(USER_AUTH_ACQ_DATE_KEY, authenticatedUserAcquisitionDate)
        editor.putString(USER_ANON_ACQ_DATE_KEY, anonymousUserAcquisitionDate)
        editor.apply()
    }

    /**
     * Load user information to shared preferences.
     *
     */
    protected fun loadUserInfo() {
        val user = User()

        val userId =
            settings!!.getString(USER_ID_KEY, null)
        this.userId = userId

        val acquisitionDateString =
            settings!!.getString(USER_ACQ_KEY, null)
        userAcqusitionDate = acquisitionDateString

        val accountId =
            settings!!.getString(USER_ACCOUNT_ID_KEY, null)
        this.accountId = accountId

        val authorizedUserId =
            settings!!.getString(USER_AUTH_USER_ID_KEY, null)
        user.authUserId = authorizedUserId

        val authUserAcqDate =
            settings!!.getString(USER_AUTH_ACQ_DATE_KEY, null)
        user.authUserAcquisitionDate = authUserAcqDate

        val anonUserAcqDate =
            settings!!.getString(USER_ANON_ACQ_DATE_KEY, null)
        user.anonUserAcquisitionDate = anonUserAcqDate
    }

    /**
     * Sets the device telemetryContext tags
     *
     * @param appContext the android Context
     */
    @SuppressLint("MissingPermission")
    protected fun configDeviceContext(appContext: Context) {
        osVersion = Build.VERSION.RELEASE
        osName = "Android" //used by the AI extension in Azure Portal to build stack traces
        deviceModel = Build.MODEL
        deviceOemName = Build.MANUFACTURER
        osLocale = Locale.getDefault().toString()
        updateScreenResolution(appContext)
        // get device ID
        val resolver = appContext.contentResolver
        val deviceIdentifier = Settings.Secure.getString(resolver, Settings.Secure.ANDROID_ID)
        if (deviceIdentifier != null) {
            deviceId =
                com.architect.kmpappinsights.library.Util.tryHashStringSha256(deviceIdentifier)
        }

        // check device type
        val telephonyManager =
            appContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (telephonyManager.phoneType != TelephonyManager.PHONE_TYPE_NONE) {
            deviceType = "Phone"
        } else {
            deviceType = "Tablet"
        }

        // check network type
        val connectivityManager =
            appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        if (activeNetwork != null) {
            val networkType = activeNetwork.type
            val networkString: String
            when (networkType) {
                ConnectivityManager.TYPE_WIFI -> networkString = "WiFi"
                ConnectivityManager.TYPE_MOBILE -> networkString = "Mobile"
                else -> {
                    networkString = "Unknown"
                    InternalLogging.warn(TAG, "Unknown network type:$networkType")
                }
            }
            this.networkType = networkString
        }

        // detect emulator
        if (com.architect.kmpappinsights.library.Util.isEmulator) {
            deviceModel = "[Emulator]" + device.model
        }
    }

    @SuppressLint("NewApi", "Deprecation")
    fun updateScreenResolution(context: Context?) {
        val resolutionString: String
        var width: Int
        var height: Int

        if (context != null) {
            val wm = context.getSystemService(
                Context.WINDOW_SERVICE
            ) as WindowManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                val size = Point()
                wm.defaultDisplay.getRealSize(size)
                width = size.x
                height = size.y
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                try {
                    //We have to use undocumented API here. Android 4.0 introduced soft buttons for
                    //back, home and menu, but there's no API present to get the real display size
                    //all available methods only return the size of the contentview.
                    val mGetRawW = Display::class.java.getMethod("getRawWidth")
                    val mGetRawH = Display::class.java.getMethod("getRawHeight")
                    val display = wm.defaultDisplay
                    width = mGetRawW.invoke(display) as Int
                    height = mGetRawH.invoke(display) as Int
                } catch (ex: Exception) {
                    val size = Point()
                    wm.defaultDisplay.getSize(size)
                    width = size.x
                    height = size.y
                    InternalLogging.warn(TAG, "Couldn't determine screen resolution: $ex")
                }
            } else {
                //Use old, and now deprecated API to get width and height of the display
                val d = wm.defaultDisplay
                width = d.width
                height = d.height
            }

            resolutionString = height.toString() + "x" + width.toString()

            screenResolution = resolutionString
        }
    }

    /**
     * Sets the internal package context
     */
    protected fun configInternalContext(appContext: Context?) {
        var sdkVersionString = ""
        if (appContext != null) {
            try {
                val bundle = appContext.packageManager
                    .getApplicationInfo(appContext.packageName, PackageManager.GET_META_DATA)
                    .metaData
                if (bundle != null) {
                    sdkVersionString =
                        bundle.getString("com.architect.kmpappinsights.library.sdkVersion", "")
                } else {
                    InternalLogging.warn(
                        TAG,
                        "Could not load sdk version from gradle.properties or manifest"
                    )
                }
            } catch (exception: PackageManager.NameNotFoundException) {
                InternalLogging.warn(TAG, "Error loading SDK version from manifest")
                Log.v(TAG, exception.toString())
            }
        }
        sdkVersion = "android:$sdkVersionString"
    }

    val contextTags: MutableMap<String?, String?>
        get() {
            val contextTags: MutableMap<String?, String?> = LinkedHashMap()

            synchronized(this.application) {
                application.addToHashMap(contextTags)
            }
            synchronized(this.internal) {
                internal.addToHashMap(contextTags)
            }
            synchronized(this.operation) {
                operation.addToHashMap(contextTags)
            }
            synchronized(this.device) {
                device.addToHashMap(contextTags)
            }
            synchronized(this.session) {
                session.addToHashMap(contextTags)
            }
            synchronized(this.user) {
                user.addToHashMap(contextTags)
            }

            return contextTags
        }

    var screenResolution: String?
        get() {
            synchronized(this.application) {
                return device.screenResolution
            }
        }
        set(screenResolution) {
            synchronized(this.application) {
                device.screenResolution = screenResolution
            }
        }

    var appVersion: String?
        get() {
            synchronized(this.application) {
                return application.ver
            }
        }
        set(appVersion) {
            synchronized(this.application) {
                application.ver = appVersion
            }
        }

    var userId: String?
        get() {
            synchronized(this.user) {
                return user.id
            }
        }
        set(userId) {
            synchronized(this.user) {
                user.id = userId
                if (this === instance) {
                    saveUserInfo()
                }
            }
        }

    var userAcqusitionDate: String?
        get() {
            synchronized(this.user) {
                return user.accountAcquisitionDate
            }
        }
        set(userAcqusitionDate) {
            synchronized(this.user) {
                user.authUserAcquisitionDate = userAcqusitionDate
                if (this === instance) {
                    saveUserInfo()
                }
            }
        }

    var accountId: String?
        get() {
            synchronized(this.user) {
                return user.accountId
            }
        }
        set(accountId) {
            synchronized(this.user) {
                user.accountId = accountId
                if (this === instance) {
                    saveUserInfo()
                }
            }
        }

    var authenticatedUserId: String?
        get() {
            synchronized(this.user) {
                return user.authUserId
            }
        }
        set(authenticatedUserId) {
            synchronized(this.user) {
                user.authUserId = authenticatedUserId
                if (this === instance) {
                    saveUserInfo()
                }
            }
        }

    var authenticatedUserAcquisitionDate: String?
        get() {
            synchronized(this.user) {
                return user.authUserAcquisitionDate
            }
        }
        set(authenticatedUserAcquisitionDate) {
            synchronized(this.user) {
                user.authUserAcquisitionDate = authenticatedUserAcquisitionDate
                if (this === instance) {
                    saveUserInfo()
                }
            }
        }

    var anonymousUserAcquisitionDate: String?
        get() {
            synchronized(this.user) {
                return user.anonUserAcquisitionDate
            }
        }
        set(anonymousUserAcquisitionDate) {
            synchronized(this.user) {
                user.anonUserAcquisitionDate = anonymousUserAcquisitionDate
                if (this === instance) {
                    saveUserInfo()
                }
            }
        }

    var sdkVersion: String?
        get() {
            synchronized(this.internal) {
                return internal.sdkVersion
            }
        }
        set(sdkVersion) {
            synchronized(this.internal) {
                internal.sdkVersion = sdkVersion
            }
        }

    var sessionId: String?
        get() {
            synchronized(this.session) {
                return session.id
            }
        }
        set(sessionId) {
            synchronized(this.session) {
                session.id = sessionId
            }
        }

    var isFirstSession: String?
        get() {
            synchronized(this.session) {
                return session.isFirst
            }
        }
        set(isFirst) {
            synchronized(this.session) {
                session.isFirst = isFirst
            }
        }

    var isNewSession: String?
        get() {
            synchronized(this.session) {
                return session.isNew
            }
        }
        set(isFirst) {
            synchronized(this.session) {
                session.isNew = isFirst
            }
        }

    var osVersion: String?
        get() {
            synchronized(this.device) {
                return device.osVersion
            }
        }
        set(osVersion) {
            synchronized(this.device) {
                device.osVersion = osVersion
            }
        }

    var osName: String?
        get() {
            synchronized(this.device) {
                return device.os
            }
        }
        set(osName) {
            synchronized(this.device) {
                device.os = osName
            }
        }

    var deviceModel: String?
        get() {
            synchronized(this.device) {
                return device.model
            }
        }
        set(deviceModel) {
            synchronized(this.device) {
                device.model = deviceModel
            }
        }

    var deviceOemName: String?
        get() {
            synchronized(this.device) {
                return device.oemName
            }
        }
        set(deviceOemName) {
            synchronized(this.device) {
                device.oemName = deviceOemName
            }
        }

    var osLocale: String?
        get() {
            synchronized(this.device) {
                return device.locale
            }
        }
        set(osLocale) {
            synchronized(this.device) {
                device.locale = osLocale
            }
        }

    var deviceId: String?
        get() = device.id
        set(deviceId) {
            synchronized(this.device) {
                device.id = deviceId
            }
        }

    var deviceType: String?
        get() = device.type
        set(deviceType) {
            synchronized(this.device) {
                device.type = deviceType
            }
        }

    var networkType: String?
        get() = device.network
        set(networkType) {
            synchronized(this.device) {
                device.network = networkType
            }
        }

    companion object {
        private const val SHARED_PREFERENCES_KEY = "APP_INSIGHTS_CONTEXT"
        private const val USER_ID_KEY = "USER_ID"
        private const val USER_ACQ_KEY = "USER_ACQ"
        private const val USER_ACCOUNT_ID_KEY = "USER_ACCOUNT_ID"
        private const val USER_AUTH_USER_ID_KEY = "USER_AUTH_USER_ID"
        private const val USER_ANON_ACQ_DATE_KEY = "USER_ANON_ACQ_DATE"
        private const val USER_AUTH_ACQ_DATE_KEY = "USER_AUTH_ACQ_DATE"
        private const val SESSION_IS_FIRST_KEY = "SESSION_IS_FIRST"
        private const val TAG = "TelemetryContext"

        /**
         * Volatile boolean for double checked synchronize block
         */
        @Volatile
        private var isTelemetryContextLoaded = false

        /**
         * The shared TelemetryContext instance.
         */
        private var instance: TelemetryContext? = null

        /**
         * Synchronization LOCK for setting static context
         */
        private val LOCK = Any()


        /**
         * @return the INSTANCE of persistence or null if not yet initialized
         */
        fun newInstance(): TelemetryContext? {
            var context: TelemetryContext? = null
            if (instance == null) {
                InternalLogging.error(
                    TAG,
                    "newInstance was called before calling ApplicationInsights.setup()"
                )
            } else {
                context = TelemetryContext()
                context.resetContext()
            }
            return context
        }

        /**
         * Initialize the INSTANCE of the telemetryContext
         *
         * @param context            the context for this telemetryContext
         * @param instrumentationKey the instrumentationkey for this application
         * @param user               a custom user object that will be assiciated with the telemetry data
         */
        fun initialize(context: Context, instrumentationKey: String?, user: User?) {
            if (!isTelemetryContextLoaded) {
                synchronized(LOCK) {
                    if (!isTelemetryContextLoaded) {
                        isTelemetryContextLoaded = true
                        instance = TelemetryContext(context, instrumentationKey, user)
                    }
                }
            }
        }

        val sharedInstance: TelemetryContext?
            /**
             * @return the INSTANCE of persistence or null if not yet initialized
             */
            get() {
                if (instance == null) {
                    InternalLogging.error(TAG, "getSharedInstance was called before initialization")
                }
                return instance
            }
    }
}
