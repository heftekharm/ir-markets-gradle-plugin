import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.entity.mime.content.FileBody
import org.apache.http.impl.client.HttpClients
import java.io.File

private const val API_KEY_HEADER = "CAFEBAZAAR-PISHKHAN-API-SECRET"

object ApiService {
    private val httpClient = HttpClients.createDefault()

    @JvmStatic
    fun requestUploadingRelease(bazaarKeyValue: String):Boolean {
        HttpPost("https://api.pishkhan.cafebazaar.ir/v1/apps/releases/").apply {
            addHeader(API_KEY_HEADER, bazaarKeyValue)
        }.let { portReq ->
            httpClient.execute(portReq)
        }.let {
            return it.statusLine.statusCode == 201
        }
    }

    @JvmStatic
    fun startUploadingOnBazaar(bazaarKeyValue: String, apkPath: String) {
        val apk = File(apkPath)
        println("publishing ${apk.name} on Bazaar Market")
        HttpPost("https://api.pishkhan.cafebazaar.ir/v1/apps/releases/upload/")
            .apply {
                addHeader(API_KEY_HEADER, bazaarKeyValue)
                val contentEntity = MultipartEntityBuilder.create()
                    .addTextBody("architecture", "all")
                    .addPart("apk",FileBody(apk, ContentType.create("application/vnd.android.package-archive")))
                    //.addBinaryBody("apk", apk)
                    .build()
                entity = contentEntity
            }.let {
                httpClient.execute(it)
            }.let {
                println(it.statusLine)
            }
    }
}