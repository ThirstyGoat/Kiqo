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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Skill skill = (Skill) o;

        if (description != null ? !description.equals(skill.description)
                                : skill.description != null) {
            return false;
        }
        if (!shortName.equals(skill.shortName)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = shortName.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
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
