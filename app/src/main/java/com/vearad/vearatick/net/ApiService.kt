
import com.vearad.vearatick.model.Events
import com.vearad.vearatick.model.LoginData
import com.vearad.vearatick.model.LoginResponse
import com.vearad.vearatick.model.MiniSiteData
import com.vearad.vearatick.model.RegisterData
import com.vearad.vearatick.model.RegisterResponse
import com.vearad.vearatick.model.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiService {

    @POST("register") // آدرس API endpoint برای ثبت نام
    fun registerUser(@Body registerData: RegisterData): Call<RegisterResponse>
    @POST("login") // آدرس API endpoint برای ثبت نام
    fun loginUser(@Body loginData: LoginData): Call<LoginResponse>
    @GET
    fun getMiniSite(@Url url: String, @Header("Authorization") accessToken: String): Call<MiniSiteData>
    @GET
    fun getUser(@Url url: String, @Header("Authorization") accessToken: String): Call<UserResponse>
    @GET
    fun getData(@Url url: String, @Header("Authorization") accessToken: String): Call<Events>
}
