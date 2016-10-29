package project1;

import com.google.common.util.concurrent.SettableFuture;
import net.sf.json.JSONObject;
import com.wrapper.spotify.JsonUtil;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.methods.AbstractRequest;
import com.wrapper.spotify.models.Page;
import com.wrapper.spotify.models.PlaylistTrack;
import com.wrapper.spotify.models.Track;
import com.wrapper.spotify.models.User;

import java.io.IOException;
import java.util.List;

public class CurrentUserTopRequest extends AbstractRequest {

  public CurrentUserTopRequest(Builder builder) {
    super(builder);
  }

  public SettableFuture<User> getAsync() {
    final SettableFuture<User> userFuture = SettableFuture.create();

    try {
      final JSONObject jsonObject = JSONObject.fromObject(getJson());

      userFuture.set(JsonUtil.createUser(jsonObject));
    } catch (Exception e) {
      userFuture.setException(e);
    }

    return userFuture;
  }

  public List<Track> get() throws IOException, WebApiException {
    JSONObject jsonObject = JSONObject.fromObject(getJson());

    return JsonUtil.createTracks(jsonObject);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder extends AbstractRequest.Builder<Builder> {

    public Builder accessToken(String accessToken) {
      return header("Authorization", "Bearer " + accessToken);
    }

    public CurrentUserTopRequest build() {
      path("/v1/me/top/tracks");
      return new CurrentUserTopRequest(this);
    }
  }

}
