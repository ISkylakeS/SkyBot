/*
 * Skybot, a multipurpose discord bot
 *      Copyright (C) 2017 - 2018  Duncan "duncte123" Sterken & Ramid "ramidzkh" Khan & Maurice R S "Sanduhr32"
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ml.duncte123.skybot.utils

import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import ml.duncte123.skybot.DocumentationNeeded
import ml.duncte123.skybot.Settings
import ml.duncte123.skybot.config.Config
import ml.duncte123.skybot.unstable.utils.ComparatingUtils
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.util.*

class WebUtils {

    companion object {
        const val USER_AGENT = "Mozilla/5.0 dunctebot (SkyBot v" + Settings.version + ", https://bot.duncte123.me/)"
        private val client = OkHttpClient()

        /**
         * Reads contents from a website and returns it to a string
         *
         * @param url The url to read
         * @return The text contents
         * @throws IOException When something broke
         */
        @Throws(IOException::class)
        @JvmStatic
        fun getText(url: String): String {
            return getRequest(url).body()!!.string()
        }

        /**
         * Reads json data from a web page and returns it
         *
         * @param url The url to read
         * @return The text contents
         * @throws IOException When something broke
         */
        @Throws(IOException::class)
        @JvmStatic
        fun getJSONObject(url: String): JSONObject {
            return JSONObject(getText(url))
        }

        /**
         * Reads json data from a web page and returns it
         *
         * @param url The url to read
         * @return The text contents
         * @throws IOException When something broke
         */
        @Throws(IOException::class)
        @JvmStatic
        fun getJSONArray(url: String): JSONArray {
            return JSONArray(getText(url))
        }

        /**
         * Reads the contents of a url into an InputStream
         * @param url the url to read
         * @return the InputStream of the url
         * @throws IOException when things break
         */
        @Throws(IOException::class)
        @JvmStatic
        fun getInputStream(url: String): InputStream {
            return getRequest(url).body()?.byteStream()!!
        }

        /**
         * This makes a get request to the specified website
         *
         * @param url    The website to post to
         * @param accept What we will accept, [AcceptType]
         * @return The [Response] from the webserver
         */
        @JvmStatic
        fun getRequest(url: String, accept: AcceptType = AcceptType.TEXT_HTML): Response {
            return runBlocking {
                executeRequest(
                        Request.Builder()
                                .url(url)
                                .get()
                                .addHeader("User-Agent", USER_AGENT)
                                .addHeader("Accept", accept.type)
                                .addHeader("cache-control", "no-cache")
                                .build())
            }
        }

        /**
         * This makes a post request to the specified website
         *
         * @param url        The website to post to
         * @param postFields the params for the post (param name, param value)
         * @param accept     What we will accept, [AcceptType]
         * @return The [Response] from the webserver
         */
        @JvmStatic
        fun postRequest(url: String, postFields: Map<String, Any?>, accept: AcceptType = AcceptType.URLENCODED): Response {
            val postParams = StringBuilder()

            for ((key, value) in postFields) {
                postParams.append(key).append("=").append(value).append("&")
            }
            return runBlocking {
                executeRequest(
                        Request.Builder()
                                .url(url)
                                .post(RequestBody.create(MediaType.parse(AcceptType.URLENCODED.type), Config.replaceLast(postParams.toString(), "\\&", "")))
                                .addHeader("User-Agent", USER_AGENT)
                                .addHeader("Accept", accept.type)
                                .addHeader("cache-control", "no-cache")
                                .build())
            }
        }

        /**
         * This makes a post request to the specified website
         *
         * @param url    The website to post to
         * @param accept What we will accept, [AcceptType]
         * @return The [Response] from the webserver
         */
        @JvmStatic
        fun postRequest(url: String, accept: AcceptType = AcceptType.TEXT_JSON): Response {
            return postRequest(url, HashMap(), accept)
        }

        /**
         * This allows for JSON post requests to a website
         * @param url the website to post the json to
         * @param data the JSON data to post
         * @return The [Response] from the webserver
         */
        @JvmStatic
        fun postJSON(url: String, data: JSONObject): Response {
            return runBlocking {
                executeRequest(
                        Request.Builder()
                                .url(url)
                                .post(RequestBody.create(MediaType.parse("application/json"), data.toString()))
                                .addHeader("User-Agent", USER_AGENT)
                                .build())
            }
        }

        /**
         * Shortens a URL with the [goo.gl](https://goo.gl) api
         *
         * @param url The URL to shorten
         * @return The shortened URL. `null` if any error occurred
         */
        @JvmStatic
        fun shortenUrl(url: String): String {
            try {
                val jo = JSONObject()

                jo.put("longUrl", url)

                val response = postJSON("https://www.googleapis.com/urlshortener/v1/url?key=" + AirUtils.config.getString("apis.googl", "Google api key"), jo)
                return JSONObject(response.body()?.string()).get("id").toString()
            } catch (e: NullPointerException) {
                ComparatingUtils.checkEx(e)
            } catch (e: IOException) {
                ComparatingUtils.checkEx(e)
            }
            return ""
        }

        /**
         * This translates a string into a different language
         * @param sourceLang the source language (example: "nl")
         * @param targetLang the target language (example: "en")
         * @param input the user inpur (example: "Dit is een test")
         * @return the output of the api
         * THe examples above will output the following `["This is a test","Dit is een test",null,null,1]`
         */
        @JvmStatic
        fun translate(sourceLang: String, targetLang: String, input: String): JSONArray {
            return getJSONArray("https://translate.googleapis.com/translate_a/single?client=gtx&sl=$sourceLang&tl=$targetLang&dt=t&q=$input")
                    .getJSONArray(0).getJSONArray(0)
        }

        /**
         * Executes a web request
         * @param request the {@link Request Request} to execute
         * @returns the [Response] from the web server
         */
        @JvmStatic
        suspend fun executeRequest(request: Request): Response {
            return async { client.newCall(request).execute() }.await()

        }

        @DocumentationNeeded
        private fun postRawToService(service: WebUtils.Service, raw: String): JSONObject {
            val req = Request.Builder()
                    .post(RequestBody.create(MediaType.parse("text/plain"), raw))
                    .url(service.url)
                    .build()
            try {
                val res = client.newCall(req).execute()
                return if (!res.isSuccessful) JSONObject().put("key", "about.md") else JSONObject(res.body()!!.string())
            } catch (e: IOException) {
                ComparatingUtils.checkEx(e)
            }

            return JSONObject().put("key", "about.md")
        }

        @DocumentationNeeded
        fun hastebin(s: String): String = "hastebin.com/" + postRawToService(Service.HASTEBIN, s).getString("key") + ".kt"
        fun wastebin(s: String): String = "wastebin.party/" + postRawToService(Service.WASTEBIN, s).getString("key") + ".kt"
    }
    /**
     * This holds some variables that we will accept
     */
    enum class AcceptType(val type: String) {
        TEXT_PLAIN("text/plain"),
        TEXT_JSON("application/json"),
        TEXT_HTML("text/html"),
        TEXT_XML("application/xml"),
        URLENCODED("application/x-www-form-urlencoded")
    }

    enum class Service(val url: String) {
        HASTEBIN("https://hastebin.com/documents"),
        WASTEBIN("https://wastebin.party/documents")
    }
}

/*
 * Global wrapping fun for every class
 */
fun hastebin(s: String): String = WebUtils.hastebin(s)
fun wastebin(s: String): String = WebUtils.wastebin(s)