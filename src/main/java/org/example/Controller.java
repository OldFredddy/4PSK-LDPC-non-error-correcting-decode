package org.example;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;

public class Controller {

    public List<Byte> decodeArr = new ArrayList<>();
    private long lastUpdate = 0;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private AnchorPane myPane;

    @FXML
    void initialize() {
        myPane.setOnDragOver(event -> {
            if (event.getGestureSource() != myPane && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        myPane.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                success = true;
                File file = db.getFiles().get(0);

                Task<List<Byte>> task = new Task<List<Byte>>() {
                    @Override
                    protected List<Byte> call() throws Exception {
                        try (FileInputStream fileInputStream = new FileInputStream(file)) {
                            byte[] fileData = fileInputStream.readAllBytes();

                            List<Byte> inputData = new ArrayList<>();
                            for (byte b : fileData) {
                                for (int i = 0; i < 8; i++) {
                                    inputData.add((byte) ((b >> i) & 1));
                                }
                            }

                            DataProcessor dataProcessor = new DataProcessor(Controller.this);
                            return dataProcessor.process(inputData);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null; // или обработайте ошибку соответствующим образом
                        }
                    }
                };

                task.setOnSucceeded(e -> {
                    decodeArr = task.getValue();
                    try {
                        saveProcessedData(decodeArr, file.getAbsolutePath());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });

                new Thread(task).start();
            }
            event.setDropCompleted(success);
            event.consume();
        });


    }

    private void saveProcessedData(List<Byte> processedData, String originalFilePath) throws IOException {
        Path originalPath = Paths.get(originalFilePath);
        String decodedFilePath = originalPath.getParent().resolve(originalPath.getFileName().toString() + "_decoded").toString();

        try (FileOutputStream fileOutputStream = new FileOutputStream(decodedFilePath);
             BufferedOutputStream outputStream = new BufferedOutputStream(fileOutputStream)) {
            byte currentByte = 0;
            int bitIndex = 0;

            for (Byte bit : processedData) {
                currentByte |= (bit << (7 - bitIndex % 8));
                bitIndex++;

                if (bitIndex % 8 == 0) {
                    outputStream.write(currentByte);
                    currentByte = 0;
                }
            }

            if (bitIndex % 8 != 0) {
                outputStream.write(currentByte);
            }
        }
        updateProgress(1);
    }

    // В классе Controller
    public void updateProgress(double progress) {
        // Ограничение частоты обновления прогресса, например, не чаще чем раз в 100 мс
        if (System.currentTimeMillis() - lastUpdate > 100) {
            Platform.runLater(() -> progressBar.setProgress(progress));
            lastUpdate = System.currentTimeMillis();
        }
    }

}
