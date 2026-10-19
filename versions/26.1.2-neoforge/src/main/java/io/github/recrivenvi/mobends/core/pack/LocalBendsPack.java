package io.github.recrivenvi.mobends.core.pack;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class LocalBendsPack implements IBendsPack {

    private BendsPackMeta metadata;
    private final File dataFile;
    private final LegacyBendsPackDefinition legacyDefinition;

    public LocalBendsPack(BendsPackMeta metadata) {
        this(metadata, null, null);
    }

    private LocalBendsPack(
            BendsPackMeta metadata, File dataFile, LegacyBendsPackDefinition legacyDefinition) {
        this.metadata = metadata;
        this.dataFile = dataFile;
        this.legacyDefinition = legacyDefinition;
    }

    @Override
    public String getKey() {
        return metadata.key;
    }

    @Override
    public String getDisplayName() {
        return metadata.displayName;
    }

    public String getAuthor() {
        return metadata.author == null ? "" : metadata.author;
    }

    public String getDescription() {
        return metadata.description == null ? "" : metadata.description;
    }

    public File getDataFile() {
        return dataFile;
    }

    public boolean isLegacyFormat() {
        return legacyDefinition != null;
    }

    LegacyBendsPackDefinition getLegacyDefinition() {
        return legacyDefinition;
    }

    public static LocalBendsPack readFromFile(File file) throws IOException {
        try (JsonReader fileReader = new JsonReader(new FileReader(file))) {
            BendsPackMeta meta = (new Gson()).fromJson(fileReader, BendsPackMeta.class);
            String filename = file.getName();
            String dataFilename =
                    filename.substring(0, filename.length() - ".bendsmeta".length()) + ".bends";
            return new LocalBendsPack(meta, new File(file.getParentFile(), dataFilename), null);
        }
    }

    public static LocalBendsPack readLegacyFromFile(File file) throws IOException {
        LegacyBendsPackParser.ParsedPack parsed = LegacyBendsPackParser.parse(file);
        return new LocalBendsPack(parsed.metadata, file, parsed.definition);
    }
}
