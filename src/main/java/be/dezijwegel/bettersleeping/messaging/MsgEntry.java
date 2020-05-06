package be.dezijwegel.bettersleeping.messaging;

public class MsgEntry {

    private final String tag;
    private final String replacement;

    /**
     * Keeps track of a tag and its replacement for use in PlayerMessenger
     * @param tag the original tag
     * @param replacement the replacement value of this tag
     */
    public MsgEntry(String tag, String replacement)
    {
        this.tag = tag;
        this.replacement = replacement;
    }

    /**
     * Get the tag that should be replaced
     * @return the tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * Gets the value by which the tag should be replaced
     * @return the replacement
     */
    public String getReplacement() {
        return replacement;
    }
}
