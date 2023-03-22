package com.webclues.IPPSOperator.service


import android.content.Context
import com.webclues.IPPSOperator.utility.Content
import com.webclues.IPPSOperator.utility.TinyDb
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class APIClient {


    companion object {

        fun getretrofit(context: Context): Retrofit {

            /*  if (retofit == null) {
                  val interceptor = HttpLoggingInterceptor()
                  interceptor.level = HttpLoggingInterceptor.Level.BODY
                  val okHttpClient = OkHttpClient.Builder()
                      .addInterceptor(object : Interceptor {
                          override fun intercept(chain: Interceptor.Chain): Response {

                              var request = chain.request()
                              var requestBuilder: Request.Builder = request.newBuilder()
                                  .addHeader(
                                      "Authorization", "Bearer " +
                                              TinyDb(context).getString(Content.AUTORIZATION_TOKEN)
                                  )
                                  .addHeader("Accept", "application/json")
                              request = requestBuilder.build()
                              return chain.proceed(request)


                          }

                      })
                      .connectTimeout(200, TimeUnit.SECONDS)
                      .writeTimeout(200, TimeUnit.SECONDS)
                      .readTimeout(200, TimeUnit.SECONDS)
                      .build()

                  retofit = Retrofit.Builder()
                      .baseUrl(Content.BASE_URL)
                      .client(okHttpClient)
                      .addConverterFactory(GsonConverterFactory.create())
                      .build()
              }*/

            /*   val okClient = OkHttpClient.Builder()
                   .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                   .addInterceptor { chain ->
                       val originalRequest = chain.request()
                       val requestBuilder = originalRequest.newBuilder()
                           .method(originalRequest.method(), originalRequest.body())
                       val request = requestBuilder.build()
                       chain.proceed(request)
                   }
                   .addNetworkInterceptor { chain ->
                       val original = chain.request()
                       val requestBuilder = original.newBuilder()
                           .addHeader(
                               "Authorization",
                               TinyDb(context).getString(Content.AUTORIZATION_TOKEN)
                           )
                           .addHeader("Accept", "application/json")
                       chain.proceed(requestBuilder.build())
                   }
                   .connectTimeout(200, TimeUnit.SECONDS)
                   .writeTimeout(200, TimeUnit.SECONDS)
                   .readTimeout(200, TimeUnit.SECONDS)
                   .build()

               retofit = Retrofit.Builder()
                   .baseUrl(Content.BASE_URL)
                   .client(okClient)
                   .addConverterFactory(GsonConverterFactory.create())
                   .build()

               return retofit!!*/


            val okClient = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor { chain ->
                    val originalRequest = chain.request()
                    val requestBuilder = originalRequest.newBuilder()
                        .method(originalRequest.method, originalRequest.body)
                    val request = requestBuilder.build()
                    chain.proceed(request)
                }
                .addNetworkInterceptor { chain ->
                    val original = chain.request()
                    val requestBuilder = original.newBuilder()
                        .addHeader(
                            "Authorization",
                              TinyDb(context).getString(Content.AUTORIZATION_TOKEN)
                        )
                        .addHeader("Accept", "application/json")
                    chain.proceed(requestBuilder.build())
                }
                .callTimeout(200, TimeUnit.MINUTES)
                .connectTimeout(200, TimeUnit.SECONDS)
                .readTimeout(200, TimeUnit.SECONDS)
                .writeTimeout(200, TimeUnit.SECONDS)
                .build();

            val retofit = Retrofit.Builder()
                .baseUrl(Content.BASE_URL)
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            /* val rssService: ApiInterface = retofit!!.create(ApiInterface::class.java)
             return rssService!!*/
            return retofit
        }

    }


}

