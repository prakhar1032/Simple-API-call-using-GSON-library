package com.api.simpleapi


import android.app.Dialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CallAPILoginAsyncTask().execute()


    }


    private inner class  CallAPILoginAsyncTask(): AsyncTask<Any, Void, String>(){

        private lateinit var customProgressDialog:Dialog

        override fun onPreExecute() {
            super.onPreExecute()

            showProgressDialog()
        }

        override fun doInBackground(vararg params: Any?): String {
            var result: String

            var connection: HttpURLConnection? = null

            try {
                val url = URL("https://run.mocky.io/v3/e2b70eb0-6430-41b6-8337-99ccced4b550")
                connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.doOutput = true

                val httpResult: Int = connection.responseCode

                if(httpResult == HttpURLConnection.HTTP_OK ){
                    val inputStream = connection.inputStream

                    val reader = BufferedReader(InputStreamReader(inputStream))

                    val stringBuilder = StringBuilder()
                    var line: String?
                    try {
                        /**
                         * Reads a line of text.  A line is considered to be terminated by any one
                         * of a line feed ('\n'), a carriage return ('\r'), or a carriage return
                         * followed immediately by a linefeed.
                         */
                        while (reader.readLine().also { line = it } != null) {
                            stringBuilder.append(line + "\n")
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        try {
                            /**
                             * Closes this input stream and releases any system resources associated
                             * with the stream.
                             */
                            inputStream.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    result = stringBuilder.toString()
                }
                else {
                    /**
                     * Gets the HTTP response message, if any, returned along with the
                     * response code from a server.
                     */
                    result = connection.responseMessage
                }
            }catch (e: SocketTimeoutException) {
                result = "Connection Timeout"
            } catch (e: Exception) {
                result = "Error : " + e.message
            } finally {
                connection?.disconnect()
            }

            // You can notify with your result to onPostExecute.
            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            cancelProgressDialog()

            if (result != null) {
                Log.i("JSON response result",result)
            }

            /**
             *Creates a new with name/value mappings from the JSON string.
             */
//            val jsonObject = JSONObject(result)
//
//            // Returns the value mapped by {name} if it exists.
//            val message = jsonObject.optString("message")
//            Log.i("Message", message)
//
//            // Returns the value mapped by {name} if it exists.
//            val name = jsonObject.optString("name")
//            Log.i("Name", "$name")
//
//            val profileDetailsObject = jsonObject.optJSONObject("profile_details")
//
//            val rating = profileDetailsObject.optDouble("rating")
//            Log.i("Rating", "$rating")
//
//            val dataListArray = jsonObject.optJSONArray("data_list")
//            Log.i("Data List Size", "${dataListArray.length()}")
//
//            for (item in 0 until dataListArray.length()) {
//                Log.i("Value $item", "${dataListArray[item]}")
//
//                // Returns the value mapped by {name} if it exists.
//                val dataItemObject: JSONObject = dataListArray[item] as JSONObject
//
//                val id = dataItemObject.optString("id")
//                Log.i("ID", "$id")
//
//                val value = dataItemObject.optString("value")
//                Log.i("Value", "$value")
//            }

//            Map the json response with the Data Class using GSON.
            val responseData = Gson().fromJson(result, ResponseData::class.java)

            Log.i("Message", responseData.message)
            Log.i("User Id", "${responseData.user_id}")
            Log.i("Name", responseData.name)
            Log.i("Email", responseData.email)
            Log.i("Mobile", "${responseData.mobile}")

            // Profile Details
            Log.i("Is Profile Completed", "${responseData.profile_details.is_profile_completed}")
            Log.i("Rating", "${responseData.profile_details.rating}")

            // Data List Details.
            Log.i("Data List Size", "${responseData.data_list.size}")

            for (item in responseData.data_list.indices) {
                Log.i("Value $item", "${responseData.data_list[item]}")

                Log.i("ID", "${responseData.data_list[item].id}")
                Log.i("Value", "${responseData.data_list[item].value}")
            }
        }
        /**
         * Method is used to show the Custom Progress Dialog.
         */
        private fun showProgressDialog() {
            customProgressDialog = Dialog(this@MainActivity)

            /*Set the screen content from a layout resource.
            The resource will be inflated, adding all top-level views to the screen.*/
            customProgressDialog.setContentView(R.layout.dialog_custom_progress)

            //Start the dialog and display it on screen.
            customProgressDialog.show()
        }

        /**
         * This function is used to dismiss the progress dialog if it is visible to user.
         */
        private fun cancelProgressDialog() {
            customProgressDialog.dismiss()
        }

    }
}