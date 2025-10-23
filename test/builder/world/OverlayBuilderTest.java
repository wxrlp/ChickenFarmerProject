package builder.world;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import static org.junit.Assert.*;

public class OverlayBuilderTest {


    @Test
    public void testGetSectionCaseInsensitive() throws IOException {
        String content = ":TeStLaBeL:\n" +
                "text\n" +
                "end;";

        List<String> section = OverlayBuilder.getSection("testlabel", content);

        assertEquals(1, section.size());
        assertEquals("text", section.get(0));
    }

    @Test
    public void testGetSectionTrimsWhitespace() throws IOException {
        String content = "  :label:  \n" +
                "  text with spaces  \n" +
                "  end;  ";

        List<String> section = OverlayBuilder.getSection("label", content);

        assertEquals(1, section.size());
        assertEquals("text with spaces", section.get(0));
    }

    @Test
    public void testGetSectionEmptySection() throws IOException {
        String content = ":emptysection:\n" +
                "end;";

        List<String> section =
                OverlayBuilder.getSection("emptysection",
                        content);

        assertEquals(0, section.size());
    }

    @Test
    public void testGetSectionMultipleSections() throws IOException {
        String content = ":section1:\n" +
                "text1\n" +
                "end;\n" +
                ":section2:\n" +
                "text2\n" +
                "end;";

        List<String> section1 = OverlayBuilder.getSection("section1", content);
        List<String> section2 = OverlayBuilder.getSection("section2", content);

        assertEquals(1, section1.size());
        assertEquals("text1", section1.get(0));
        assertEquals(1, section2.size());
        assertEquals("text2", section2.get(0));
    }

    @Test(expected = IOException.class)
    public void testGetSectionNotFound() throws IOException {
        String content = ":existingsection:\n" +
                "text\n" +
                "end;";

        OverlayBuilder.getSection("nonexistent", content);
    }

    @Test
    public void testGetSectionWithMultipleLines() throws IOException {
        String content = ":multiline:\n" +
                "line1\n" +
                "line2\n" +
                "line3\n" +
                "line4\n" +
                "end;";

        List<String> section =
                OverlayBuilder.getSection("multiline", content);

        assertEquals(4, section.size());
    }

    @Test
    public void testExtractSpawnDetailsFromLineBasic() {
        String line = "x:100 y:200 duration:500";

        SpawnerDetails details =
                OverlayBuilder.extractSpawnDetailsFromLine(line);

        assertEquals(100, details.getX());
        assertEquals(200, details.getY());
        assertEquals(500, details.getDuration());
    }

    @Test
    public void testExtractSpawnDetailsFromLineZeroValues() {
        String line = "x:0 y:0 duration:0";

        SpawnerDetails details = OverlayBuilder.extractSpawnDetailsFromLine(line);

        assertEquals(0, details.getX());
        assertEquals(0, details.getY());
        assertEquals(0, details.getDuration());
    }

    @Test
    public void testExtractSpawnDetailsFromLineLargeValues() {
        String line = "x:9999 y:8888 duration:10000";

        SpawnerDetails details = OverlayBuilder.extractSpawnDetailsFromLine(line);

        assertEquals(9999, details.getX());
        assertEquals(8888, details.getY());
        assertEquals(10000, details.getDuration());
    }

    @Test
    public void testExtractPlayerDetailsFromLineBasic() {
        String line = "x:150 y:250 coins:10 food:5";

        PlayerDetails details = OverlayBuilder.extractPlayerDetailsFromLine(line);

        assertEquals(150, details.getX());
        assertEquals(250, details.getY());
        assertEquals(10, details.getStartingCoins());
        assertEquals(5, details.getStartingFood());
    }

    @Test
    public void testExtractPlayerDetailsFromLineZeroResources() {
        String line = "x:0 y:0 coins:0 food:0";

        PlayerDetails details = OverlayBuilder.extractPlayerDetailsFromLine(line);

        assertEquals(0, details.getStartingCoins());
        assertEquals(0, details.getStartingFood());
    }

    @Test
    public void testExtractPlayerDetailsFromLineLargeResources() {
        String line = "x:400 y:400 coins:1000 food:999";

        PlayerDetails details = OverlayBuilder.extractPlayerDetailsFromLine(line);

        assertEquals(1000, details.getStartingCoins());
        assertEquals(999, details.getStartingFood());
    }

    @Test
    public void testPlayerDetailsToString() {
        PlayerDetails details = OverlayBuilder.extractPlayerDetailsFromLine("x:100 y:200 coins:10 food:5");

        String string = details.toString();
        assertTrue(string.contains("100"));
        assertTrue(string.contains("200"));
        assertTrue(string.contains("10"));
        assertTrue(string.contains("5"));
    }

    @Test
    public void testGetEagleSpawnDetailsFromString() throws IOException {
        String content = ":eaglespawner:\n" +
                "x:100 y:200 duration:800\n" +
                "x:300 y:400 duration:800\n" +
                "end;";

        List<SpawnerDetails> eagles = OverlayBuilder.getEagleSpawnDetailsFromString(content);

        assertEquals(2, eagles.size());
        assertEquals(100, eagles.get(0).getX());
        assertEquals(200, eagles.get(0).getY());
        assertEquals(800, eagles.get(0).getDuration());
    }

    @Test
    public void testGetMagpieSpawnDetailsFromString() throws IOException {
        String content = ":magpiespawner:\n" +
                "x:500 y:600 duration:800\n" +
                "end;";

        List<SpawnerDetails> magpies = OverlayBuilder.getMagpieSpawnDetailsFromString(content);

        assertEquals(1, magpies.size());
        assertEquals(500, magpies.get(0).getX());
        assertEquals(600, magpies.get(0).getY());
    }

    @Test
    public void testGetPigeonSpawnDetailsFromString() throws IOException {
        String content = ":pigeonspawner:\n" +
                "x:700 y:800 duration:200\n" +
                "x:900 y:1000 duration:200\n" +
                "x:1100 y:1200 duration:200\n" +
                "end;";

        List<SpawnerDetails> pigeons = OverlayBuilder.getPigeonSpawnDetailsFromString(content);

        assertEquals(3, pigeons.size());
        assertEquals(700, pigeons.get(0).getX());
        assertEquals(1200, pigeons.get(2).getY());
    }

    @Test
    public void testGetSpawnerDetailsWithEmptySection() throws IOException {
        String content = ":eaglespawner:\n" +
                "end;";

        List<SpawnerDetails> eagles = OverlayBuilder.getEagleSpawnDetailsFromString(content);

        assertEquals(0, eagles.size());
    }

    @Test(expected = IOException.class)
    public void testGetSpawnerDetailsWithMissingSection() throws IOException {
        String content = ":othersection:\n" +
                "data\n" +
                "end;";

        OverlayBuilder.getEagleSpawnDetailsFromString(content);
    }

    @Test
    public void testGetPlayerDetailsFromFile() throws IOException {
        String content = ":chickenfarmer:\n" +
                "x:320 y:240 coins:15 food:10\n" +
                "end;";

        PlayerDetails player = OverlayBuilder.getPlayerDetailsFromFile(content);

        assertEquals(320, player.getX());
        assertEquals(240, player.getY());
        assertEquals(15, player.getStartingCoins());
        assertEquals(10, player.getStartingFood());
    }

    @Test
    public void testGetPlayerDetailsFromFileCaseInsensitive() throws IOException {
        String content = ":ChIcKeNfArMeR:\n" +
                "x:100 y:100 coins:5 food:5\n" +
                "end;";

        PlayerDetails player = OverlayBuilder.getPlayerDetailsFromFile(content);

        assertNotNull(player);
        assertEquals(100, player.getX());
    }

    @Test
    public void testGetCabbageSpawnDetailsFromString() throws IOException {
        String content = ":cabbages:\n" +
                "x:100 y:200\n" +
                "x:300 y:400\n" +
                "end;";

        List<CabbageDetails> cabbages = OverlayBuilder.getCabbageSpawnDetailsFromString(content);

        assertEquals(2, cabbages.size());
        assertEquals(100, cabbages.get(0).getX());
        assertEquals(200, cabbages.get(0).getY());
        assertEquals(300, cabbages.get(1).getX());
        assertEquals(400, cabbages.get(1).getY());
    }

    @Test
    public void testGetCabbageSpawnDetailsEmptySection() throws IOException {
        String content = ":cabbages:\n" +
                "end;";

        List<CabbageDetails> cabbages = OverlayBuilder.getCabbageSpawnDetailsFromString(content);

        assertEquals(0, cabbages.size());
    }

    @Test
    public void testCabbageDetailsToString() throws IOException {
        String content = ":cabbages:\n" +
                "x:500 y:600\n" +
                "end;";

        List<CabbageDetails> cabbages = OverlayBuilder.getCabbageSpawnDetailsFromString(content);
        String string = cabbages.get(0).toString();

        assertTrue(string.contains("500"));
        assertTrue(string.contains("600"));
    }


    @Test
    public void testMultipleSpawnersOfSameType() throws IOException {
        String content = ":eaglespawner:\n" +
                "x:100 y:100 duration:800\n" +
                "x:200 y:200 duration:1000\n" +
                "x:300 y:300 duration:1200\n" +
                "x:400 y:400 duration:1400\n" +
                "end;";

        List<SpawnerDetails> eagles = OverlayBuilder.getEagleSpawnDetailsFromString(content);

        assertEquals(4, eagles.size());
        assertEquals(800, eagles.get(0).getDuration());
        assertEquals(1000, eagles.get(1).getDuration());
        assertEquals(1200, eagles.get(2).getDuration());
        assertEquals(1400, eagles.get(3).getDuration());
    }


    @Test
    public void testSectionsWithExtraWhitespace() throws IOException {
        String content = "  :chickenfarmer:  \n" +
                "  x:100 y:200 coins:5 food:3  \n" +
                "  end;  \n";

        PlayerDetails player = OverlayBuilder.getPlayerDetailsFromFile(content);

        assertEquals(100, player.getX());
        assertEquals(200, player.getY());
    }




    @Test
    public void testCompleteDetailsFile() throws IOException {
        String content = ":chickenfarmer:\n" +
                "x:400 y:400 coins:20 food:15\n" +
                "end;\n" +
                ":eaglespawner:\n" +
                "x:100 y:100 duration:800\n" +
                "end;\n" +
                ":magpiespawner:\n" +
                "x:700 y:700 duration:800\n" +
                "end;\n" +
                ":pigeonspawner:\n" +
                "x:200 y:200 duration:200\n" +
                "end;\n" +
                ":cabbages:\n" +
                "x:500 y:500\n" +
                "end;";

        PlayerDetails player =
                OverlayBuilder.getPlayerDetailsFromFile(content);
        List<SpawnerDetails> eagles =
                OverlayBuilder.getEagleSpawnDetailsFromString(content);
        List<SpawnerDetails> magpies =
                OverlayBuilder.getMagpieSpawnDetailsFromString(content);
        List<SpawnerDetails> pigeons =
                OverlayBuilder.getPigeonSpawnDetailsFromString(content);
        List<CabbageDetails> cabbages =
                OverlayBuilder.getCabbageSpawnDetailsFromString(content);

        assertEquals(400, player.getX());
        assertEquals(1, eagles.size());
        assertEquals(1, magpies.size());
        assertEquals(1, pigeons.size());
        assertEquals(1, cabbages.size());
    }

}