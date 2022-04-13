import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main {

    public static void main(String[] args) {
        StringBuilder log = new StringBuilder();
        makeDir("src", "c://Games", log);
        makeDir("res", "c://Games", log);
        makeDir("savegames", "c://Games", log);
        makeDir("temp", "c://Games", log);
        makeDir("main", "c://Games/src", log);
        makeDir("test", "c://Games/src", log);
        makeFile("Main.java", "c://Games/src/main", log);
        makeFile("Utils.java", "c://Games/src/main", log);
        makeDir("drawables", "c://Games/res", log);
        makeDir("vectors", "c://Games/res", log);
        makeDir("icons", "c://Games/res", log);
        makeFile("temp.txt", "c://Games/temp", log);
        try (FileWriter writer = new FileWriter("c://Games/temp/temp.txt")) {
            writer.write(String.valueOf(log));
            writer.flush();
        } catch (IOException ex) {
            ex.getMessage();
        }

        GameProgress save1 = new GameProgress(100, 1, 1, 10);
        GameProgress save2 = new GameProgress(74, 3, 4, 1030);
        GameProgress save3 = new GameProgress(96, 5, 7, 14580);

        saveGame("c://Games/savegames/save1.dat", save1);
        saveGame("c://Games/savegames/save2.dat", save2);
        saveGame("c://Games/savegames/save3.dat", save3);

        String[] listToZip = new String[]{"c://Games/savegames/save1.dat", "c://Games/savegames/save2.dat", "c://Games/savegames/save3.dat"};
        zipFiles("c://Games/savegames/saves.zip", listToZip);

        for (String save : listToZip) {
            File fileToDel = new File(save);
            fileToDel.delete();
        }

        openZip("c://Games/savegames/saves.zip", "c://Games/savegames/");

        System.out.println(openProgress("c://Games/savegames/save2.dat"));
    }


    public static void makeDir(String name, String path, StringBuilder sb) {
        File dir = new File(path + "/" + name);
        if (dir.mkdir()) sb.append("Каталог " + name + " добавлен по адресу: " + path);
        sb.append('\n');
    }

    public static void makeFile(String name, String path, StringBuilder sb) {
        File myFile = new File(path + "/" + name);
        try {
            if (myFile.createNewFile()) sb.append("Файл " + name + " добавлен по адресу: " + path);
            sb.append('\n');
        } catch (IOException ex) {
            sb.append(ex.getMessage());
            sb.append('\n');
        }
    }

    public static void saveGame(String path, GameProgress save) {
        try (FileOutputStream fos = new FileOutputStream(path);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(save);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void zipFiles(String path, String[] savesToZip) {
        try (FileOutputStream fos = new FileOutputStream(path);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            for (String save : savesToZip) {
                FileInputStream fis = new FileInputStream(save);
                File fileToZip = new File(save);
                ZipEntry entry = new ZipEntry(fileToZip.getName());
                zos.putNextEntry(entry);
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                fis.close();
                zos.write(buffer);
                // zos.write(Files.readAllBytes(fileToZip.toPath()));
                zos.closeEntry();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void openZip(String pathToFile, String pathToUnpack) {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(pathToFile))) {
            ZipEntry entry;
            String name;
            while ((entry = zis.getNextEntry()) != null) {
                name = entry.getName();
                FileOutputStream fos = new FileOutputStream(pathToUnpack + "/" + name);
                for (int i = zis.read(); i != -1; i = zis.read()) {
                    fos.write(i);
                }
                fos.flush();
                zis.closeEntry();
                fos.close();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static GameProgress openProgress(String path) {
        GameProgress gameProgress = null;
        try (FileInputStream fis = new FileInputStream(path);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            gameProgress = (GameProgress) ois.readObject();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return gameProgress;
    }
}
