
package gspot.com.sportify.Model;
import com.fasterxml.jackson.annotation.*;

/**
 * Created by yunfanyang on 5/1/16.
 */
public class SportType {

    @JsonIgnore
    public String mSID;

    @JsonProperty("name")
    public String mName;

    @JsonProperty("description")
    public String mDescription;
}