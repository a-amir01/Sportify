package gspot.com.sportify.Model;

/**
 * Class to represent a sport on a users profile. Purpose: to let other user know that this user
 * plays this sport and they have this skill level and these experiences or preferences.
 * Created by patrickhayes and Don Vo on 5/3/16.
 */
public class MySport {

    //Enum class increases readability
    public static enum SkillLevel {
        BEGINNER("Beginner"),
        INTERMEDIATE("Intermediate"),
        ADVANCED("Advanced");

        private final String skillLevel;

        SkillLevel(String skillLevel) {
            this.skillLevel = skillLevel;
        }

        public String getSkillLevel() {
            return skillLevel;
        }

        @Override
        public String toString() {
            return this.skillLevel;
        }
    }

    private SkillLevel mSkillLevel;
    private String mBio;
    private String mSport;

    public MySport() {
        this.mSkillLevel = SkillLevel.BEGINNER;
        this.mBio = "This is where you should put information about how you like to play this sport";
        this.mSport = "Basketball";
    }

    public MySport(String sport) {
        this.mSport = sport;
        this.mSkillLevel = SkillLevel.BEGINNER;
        this.mBio = "This is where you should put information about how you like to play this sport";
    }

    public SkillLevel getmSkillLevel() {
        return mSkillLevel;
    }

    public String skillLevelToString() {
        return mSkillLevel.toString();
    }

    public String getmBio() {
        return mBio;
    }

    public String getmSport() {
        return mSport;
    }

    public void setmSkillLevel(SkillLevel mSkillLevel) {
        this.mSkillLevel = mSkillLevel;
    }

    public void setmBio(String mBio) {
        this.mBio = mBio;
    }
}