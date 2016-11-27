package photo_renamer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

/** A class for testing TagManager. */
public class TagManagerTest {
    /** The TagManager being tested. */
    private TagManager tagManager;
    /** The path where the tested TagManager database is stored. */
    private String path = System.getProperty("user.dir") + "/TagManagerTest.txt";

    @Before
    /* Set up a new TagManager for testing. */
    public void setUp() throws Exception {
        tagManager = new TagManager(path);
    }

    @Test
    /* Test retrieving the list of managed Tags from a new, empty TagManager. */
    public void testEmpty() throws Exception {
        Object result = tagManager.tags;
        ArrayList<Tag> expected = new ArrayList<>();
        assertEquals(expected, result);
    }

    @Test
    /* Test adding a single new Tag to the TagManager. */
    public void testAddOne() throws Exception {
        Tag red = new Tag("red");
        tagManager.addTag(red);
        Object result = tagManager.tags;
        ArrayList<Tag> expected = new ArrayList<>(Collections.singletonList(red));
        assertEquals(expected, result);
    }

    @Test
    /* Test adding a multiple new Tags to the TagManager. */
    public void testAddMultiple() throws Exception {
        Tag blue = new Tag("blue");
        Tag green = new Tag("green");
        tagManager.addTag(blue);
        tagManager.addTag(green);
        Object result = tagManager.tags;
        ArrayList<Tag> expected = new ArrayList<>(Arrays.asList(blue, green));
        assertEquals(expected, result);
    }

    @Test
    /* Test adding an identical Tag object in the TagManager twice. */
    public void testAddExactDuplicate() throws Exception {
        Tag pink = new Tag("pink");
        tagManager.addTag(pink);
        tagManager.addTag(pink);
        Object result = tagManager.tags;
        ArrayList<Tag> expected = new ArrayList<>(Collections.singletonList(pink));
        assertEquals(expected, result);
    }

    @Test
    /* Test adding two different Tags with the same name attribute in the TagManager. */
    public void testAddNameDuplicate() throws Exception {
        Tag blue = new Tag("blue");
        Tag bleu = new Tag("blue");
        tagManager.addTag(blue);
        tagManager.addTag(bleu);
        Object result = tagManager.tags;
        ArrayList<Tag> expected = new ArrayList<>(Collections.singletonList(blue));
        assertEquals(expected, result);
    }

    @Test
    /* Test adding Tags with the same name, but different cases. */
    public void testAddCasingDuplicate() throws Exception {
        Tag orange = new Tag("orange");
        Tag redYellow = new Tag("Orange");
        tagManager.addTag(orange);
        tagManager.addTag(redYellow);
        Object result = tagManager.tags;
        ArrayList<Tag> expected = new ArrayList<>(Collections.singletonList(orange));
        assertEquals(expected, result);
    }

    @Test
    /* Test removing a Tag from an empty TagManager. */
    public void testRemoveNonexistent() throws Exception {
        Tag cat = new Tag("cat");
        tagManager.removeTag(cat);
        Object result = tagManager.tags;
        ArrayList<Tag> expected = new ArrayList<>();
        assertEquals(expected, result);
    }

    @Test
    /* Test removing a Tag that exists within the TagManager. */
    public void testRemoveExistent() throws Exception {
        Tag cat = new Tag("cat");
        tagManager.addTag(cat);
        tagManager.removeTag(cat);
        Object result = tagManager.tags;
        ArrayList<Tag> expected = new ArrayList<>();
        assertEquals(expected, result);
    }

    @Test
    /* Test removing a Tag that exists with alternate casing within the TagManager. */
    public void testRemoveExistentCasing() throws Exception {
        Tag cat = new Tag("cat");
        Tag bulldozer = new Tag("CAT");
        tagManager.addTag(cat);
        tagManager.removeTag(bulldozer);
        Object result = tagManager.tags;
        ArrayList<Tag> expected = new ArrayList<>();
        assertEquals(expected, result);
    }

    @Test
    /* Test Tag removal with other Tags remaining within the TagManager. */
    public void testRemoveSingle() throws Exception {
        Tag cat = new Tag("cat");
        Tag dog = new Tag("dog");
        tagManager.addTag(cat);
        tagManager.addTag(dog);
        tagManager.removeTag(cat);
        Object result = tagManager.tags;
        ArrayList<Tag> expected = new ArrayList<>(Collections.singletonList(dog));
        assertEquals(expected, result);
    }

    @Test
    /* Test writing to and reading from a TagManager database file. */
    public void testWriteRead() throws Exception {
        Tag cat = new Tag("cat");
        Tag dog = new Tag("dog");
        tagManager.addTag(cat);
        tagManager.addTag(dog);
        TagManager tagBoss = new TagManager(path);
        Object result = tagBoss.tags;
        ArrayList<Tag> expected = new ArrayList<>(Arrays.asList(cat, dog));
        assertEquals(expected, result);
    }

    @After
    /* Delete system artifacts generated by TagManager testing. */
    public void tearDown() throws Exception {
        File database = new File(path);
        database.delete();
    }

}