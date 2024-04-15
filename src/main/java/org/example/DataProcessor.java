package org.example;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataProcessor {

    private static final int SYNC_SIZE = 64; // Размер синхрокомбинации в битах
    private static final int DATA_BLOCK_SIZE = 114; // Размер блока данных в байтах
    private List<Byte> syncSequence = new ArrayList<>(); // Синхрокомбинация в битовом представлении
    private List<Byte> descrambledData = new ArrayList<>(); // Коллекция для хранения обработанных данных

    private Controller controller;

    public DataProcessor(Controller controller) {
        this.controller = controller;
        setSyncSequence();
    }

    private void setSyncSequence() {
        String syncBits = "1000100000000000001010110101110110110001110011000110011011111001";
        for (int i = 0; i < syncBits.length(); i++) {
            syncSequence.add((byte) (syncBits.charAt(i) == '1' ? 1 : 0));
        }
    }





    public List<Byte> process(List<Byte> inputData) {
        int pos = 0;
        int totalSize = inputData.size();
        while (pos <= inputData.size() - SYNC_SIZE) {
            boolean match = true;
            double progress = pos / (double) totalSize;
            controller.updateProgress(progress);
            for (int i = 0; i < SYNC_SIZE; i++) {
                if (!inputData.get(pos + i).equals(syncSequence.get(i))) {
                    match = false;
                    break;
                }
            }

            if (match) {
                int blockEnd = pos + SYNC_SIZE + DATA_BLOCK_SIZE * 8;
                if (blockEnd > inputData.size()) break;
                List<Byte> blockBits = inputData.subList(pos + SYNC_SIZE, blockEnd);
                AdditiveDescrambler additiveDescrambler = new AdditiveDescrambler();
                List<Byte> descrambledBlock = additiveDescrambler.descramble(blockBits);
                descrambledData.addAll(descrambledBlock);
                pos += SYNC_SIZE + DATA_BLOCK_SIZE * 8;
            } else {
                pos++;
            }
        }

        return descrambledData;
    }
}
