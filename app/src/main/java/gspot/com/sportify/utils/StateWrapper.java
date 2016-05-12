package gspot.com.sportify.utils;

/**
 * Created by patrickhayes on 5/11/16.
 */
public class StateWrapper {

    /**
     * State Enum Class
     * ProfileActivity exists in 3 types of states:
     * - View Mine State: The user is viewing their own profile
     * - Edit State: The user is editing their own profile
     * - View Other State: The user is viewing someone else's profile
     * Depending on the state that ProfileActivity is in, the user
     * may or may not be able to edit particular parts of the page.
     */
    public enum State {
        VIEW_MINE("View Mine"),
        EDIT("Edit"),
        VIEW_OTHER("View Other");

        private final String name;

        State(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    private State mState;

    public StateWrapper(State state) {
        mState = state;
    }

    public State getState() {
        return mState;
    }

    public void setState(State state) {
        this.mState = state;
    }
}
