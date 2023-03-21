import org.apache.http.HttpRequestInterceptor
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.entity.mime.content.FileBody
import org.apache.http.impl.client.HttpClientBuilder
import org.gradle.internal.impldep.com.google.gson.JsonObject
import java.io.File

private const val API_KEY_HEADER = "CAFEBAZAAR-PISHKHAN-API-SECRET"

class CafeBazaarApiService(private val cafeBazaarApiKey: String) {

    private val httpClient = HttpClientBuilder.create()
        .addInterceptorFirst(HttpRequestInterceptor { request, _ ->
            request.addHeader(API_KEY_HEADER, cafeBazaarApiKey)
        }).build()

    fun requestUploadingRelease():Boolean {
        HttpPost("https://api.pishkhan.cafebazaar.ir/v1/apps/releases/").apply {
        }.let { portReq ->
            httpClient.execute(portReq)
        }.let {
            println(it.statusLine)
            return it.statusLine.statusCode == 201
        }
    }

    fun startUploadingOnBazaar(apkPath: String): Boolean {
        val apk = File(apkPath)
        println("publishing ${apk.name} on Bazaar Market")
        HttpPost("https://api.pishkhan.cafebazaar.ir/v1/apps/releases/upload/")
            .apply {
                val contentEntity = MultipartEntityBuilder.create()
                    .addTextBody("architecture", "all")
                    .addPart("apk",FileBody(apk, ContentType.create("application/vnd.android.package-archive")))
                    .build()
                entity = contentEntity
            }.let {
                httpClient.execute(it)
            }.let {
                println(it.statusLine)
                return it.statusLine.statusCode in 200 until 202
            }
    }

    fun commit(changelogEn:String="",changelogFa:String="",developerNote:String="",stagedRolloutPercentage:Int=100,autoPublish:Boolean=false):Boolean{
        println("Committing the release")
        val stringedContentJson="""
            {
                "staged_rollout_percentage": $stagedRolloutPercentage,
                "changelog_fa": "$developerNote",
                "changelog_en": "$changelogEn",
                "developer_note": "$changelogFa",
                "auto_publish": $autoPublish
            }
        """.trimIndent()
        HttpPost("https://api.pishkhan.cafebazaar.ir/v1/apps/releases/commit").apply {
            entity=StringEntity(stringedContentJson,ContentType.APPLICATION_JSON)
        }.let {
            httpClient.execute(it)
        }.let {
            println(it.statusLine)
            return it.statusLine.statusCode in 200 until 202
        }
    }
}