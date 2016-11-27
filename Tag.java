package photo_renamer;

import java.io.Serializable;

/** A tag applied to ImageFile objects located in the system. */
class Tag implements Serializable {

    /** The serialVersionUID for this class. */
    private static final long serialVersionUID = 3707256125680272656L;
    /** The name of this tag. */
    String name;

    /**
     * An image tag.
     *
     * @param name the name of this tag
     */
    Tag(String name) {
        this.name = name;
    }

    /**
     * Return a string representation of this Tag.
     *
     * @return the name of this tag
     */
    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Return whether this Tag is equal to Tag comp.
     *
     * @param comp the Tag object being compared to
     * @return a boolean of whether this Tag is equal to Tag comp
     */
    @Override
    public boolean equals(Object comp) {
        boolean isEqual = false;
        if (comp != null && comp instanceof Tag) {
            isEqual = this.name.toLowerCase().equals(((Tag) comp).name.toLowerCase());
        }
        return isEqual;
    }
}
