package src.checking;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameCheckingStrategy implements ICheckingStrategy{

    private File directory;
    private List<File> xmlFiles;

    public GameCheckingStrategy(File directory, List<File> xmlFiles) {
        this.directory = directory;
        this.xmlFiles = xmlFiles;
    }



    public boolean check() {
        String highestLevel = this.xmlFiles.get(this.xmlFiles.size() - 1).getName();
        if (getNumberPrefix(highestLevel) == null) {
            return false;
        }
        int highestLevelNum = Integer.parseInt(getNumberPrefix(highestLevel));
        String[] fileIndexes = new String[highestLevelNum];

        Arrays.fill(fileIndexes, "");
        boolean valid = true;

        try {
            FileWriter fileWriter = new FileWriter("log.txt");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            if (this.xmlFiles.size()== 0) {
                bufferedWriter.write("Game "+ this.directory.getName() + " - no maps found");
                bufferedWriter.close();
                return false;
            }
            // assumes the file names start with a number
            for (File file: this.xmlFiles) {
                String fileName = file.getName();

                if (getNumberPrefix(fileName) == null) {
                    return false;
                }

                String fileNumber = getNumberPrefix(fileName);
                int level = Integer.parseInt(fileNumber) - 1;
                fileIndexes[level] = fileIndexes[level] + fileName + " ";
            }
            for (int i = 0; i < fileIndexes.length; i++) {
                if (fileIndexes[i].split(" ").length > 1) {
                    bufferedWriter.write("Game " + this.directory.getName() + " - multiple maps at same level: " + fileIndexes[i]);
                    bufferedWriter.newLine();
                    valid = false;
                }
            }
            bufferedWriter.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return valid;
    }

    private String getNumberPrefix(String fileName) {
        Pattern pattern = Pattern.compile("^(\\d+).*");
        Matcher matcher = pattern.matcher(fileName);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return null;
    }
}
