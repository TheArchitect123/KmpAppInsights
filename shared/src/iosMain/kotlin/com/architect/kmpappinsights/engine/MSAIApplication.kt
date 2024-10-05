package com.architect.kmpappinsights.engine

import platform.Foundation.NSCoder
import platform.Foundation.decodeObjectForKey
import platform.Foundation.encodeObject

data class MSAIApplication(
    var version: String? = null,
    var build: String? = null,
    var typeId: String? = null
) : MSAIObject(), Serializable {

    fun serializeToDictionary(): MSAIOrderedDictionary {
        val dict = super.serializeToDictionary()
        version?.let { dict["ai.application.ver"] = it }
        build?.let { dict["ai.application.build"] = it }
        typeId?.let { dict["ai.application.typeId"] = it }
        return dict
    }

    companion object {
        fun fromCoder(coder: NSCoder): MSAIApplication {
            val app = MSAIApplication()
            app.version = coder.decodeObjectForKey("self.version") as? String
            app.build = coder.decodeObjectForKey("self.build") as? String
            app.typeId = coder.decodeObjectForKey("self.typeId") as? String
            return app
        }
    }

    fun encodeWithCoder(coder: NSCoder) {
        coder.encodeObject(version, forKey = "self.version")
        coder.encodeObject(build, forKey = "self.build")
        coder.encodeObject(typeId, forKey = "self.typeId")
    }
}



