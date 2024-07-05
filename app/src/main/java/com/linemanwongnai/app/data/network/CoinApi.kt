
import retrofit2.http.GET

interface CoinApi {

    @GET("carlist")
    suspend fun getCars() : List<String>
}