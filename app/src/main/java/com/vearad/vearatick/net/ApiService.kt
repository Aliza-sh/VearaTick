
import com.vearad.vearatick.model.Events
import com.vearad.vearatick.model.RegisterData
import com.vearad.vearatick.model.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiService {
    @GET
     fun getData(@Url url: String, @Header("Authorization") accessToken: String): Call<Events>

    @POST("register") // آدرس API endpoint برای ثبت نام
    fun registerUser(@Body registerData: RegisterData): Call<RegisterResponse>
}
