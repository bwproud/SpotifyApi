package project1;
import javax.swing.JOptionPane;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import com.wrapper.spotify.*;
import com.wrapper.spotify.exceptions.*;
import com.wrapper.spotify.methods.*;
import com.wrapper.spotify.methods.authentication.*;
import com.wrapper.spotify.models.*;
public class Setup {

	static final String id = "b5d152e3a9d9445fad73eef0033a7c34";
	static final String secret = "9387a864238240e99e434f9cb8b93cbb";
	static final String rURI = "http://localhost:8080/callback";

	/**
	* Authorize user to interact with spotify api(use this authorization method if it is necessary to modify/create playlists) 
	*/
	public static Api authorizationCodeGrantAPI() throws IOException, WebApiException{
		Api api = Api.builder()
				.clientId(id)
				.clientSecret(secret)
				.redirectURI(rURI)
				.build();

		final List<String> scopes = Arrays.asList("playlist-modify-public","user-library-modify", "playlist-read-private", "playlist-modify-private", "user-read-private", "user-read-email", "user-top-read");
		final String state = "StateString";
		
		System.out.println(api.createAuthorizeURL(scopes, state));
		final String authToken = JOptionPane.showInputDialog("Use the link: in the terminal to authenticate. Enter the code here");			
		AuthorizationCodeCredentials auth = api.authorizationCodeGrant(authToken).build().get();
		api.setAccessToken(auth.getAccessToken());
		api.setRefreshToken(auth.getRefreshToken());
		System.out.println("Authenticated");
		return api;

	}
	
	/**
	* Authorize user to interact with spotify api(use this authorization method if it is just necessary to retrieve information) 
	*/
	public static Api clientCredentialsAPI() throws IOException, WebApiException{
		Api api = Api.builder()
				.clientId(id)
				.clientSecret(secret)
				.redirectURI(rURI)
				.build();
		final ClientCredentialsGrantRequest request = api.clientCredentialsGrant().build();
		final ClientCredentials creds = request.get();
		api.setAccessToken(creds.getAccessToken());
		return api;
	}

}