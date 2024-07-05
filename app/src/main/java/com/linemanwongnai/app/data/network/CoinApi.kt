
import retrofit2.http.GET

interface CoinApi {

    @GET("carlist")
    suspend fun getCoinList() : List<String>
}