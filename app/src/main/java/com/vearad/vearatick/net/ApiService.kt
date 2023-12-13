
import com.vearad.vearatick.model.Events
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Url

interface ApiService {
    @GET
     fun getData(@Url url: String, @Header("Authorization") accessToken: String): Call<Events>
}
