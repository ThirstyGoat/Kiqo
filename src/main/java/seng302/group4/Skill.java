package seng302.group4;

/**
 * Created by leroy on 25/03/15.
 */
public class Skill {
    private String shortName;
    private String description;

    public Skill(String shortName, String description) {
        this.shortName = shortName;
        this.description = description;
    }

    public String getShortName() {
        return shortName;
    }

    public String getDescription() {
        return description;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
