import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.entity.mime.content.FileBody
import org.apache.http.impl.client.HttpClients
import java.io.File

private const val API_KEY_HEADER = "CAFEBAZAAR-PISHKHAN-API-SECRET"

class CafeBazaarApiService(private val cafeBazaarApiKey: String) {
    private val httpClient = HttpClients.createDefault()

    fun requestUploadingRelease():Boolean {
        HttpPost("https://api.pishkhan.cafebazaar.ir/v1/apps/releases/").apply {
            addHeader(API_KEY_HEADER, cafeBazaarApiKey)
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
                addHeader(API_KEY_HEADER, cafeBazaarApiKey)
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
}