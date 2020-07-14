package com.company;

import java.util.Random;
import java.util.Scanner;

class Universe {
    private final int size;
    private boolean[][] map;
    private int aliveCells;
    public Universe (int size) {
        this.size = size;
        this.aliveCells = 0;
        map = this.createMap();
    }

    private boolean[][] createMap () {
        boolean[][] tempMap = new boolean[size][size];
        Random rand = new Random();

        for (int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                tempMap[i][j] = rand.nextBoolean();
                if(tempMap[i][j]) {
                    this.aliveCells++;
                }
            }
        }

        return tempMap;
    }

    @Override
    public String toString () {
        StringBuilder strMap = new StringBuilder();

        for (int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                if(map[i][j]) {
                    strMap.append('O');
                } else {
                    strMap.append(' ');
                }
            }
            strMap.append('\n');
        }

        return strMap.toString();
    }

    public int getSize () {
        return size;
    }

    public boolean[][] getMap() {
        return map;
    }

    public int getAlive () {
        return aliveCells;
    }

    public void setMap(boolean[][] nextGenMap, int aliveCells) {
        this.map = nextGenMap;
        this.aliveCells = aliveCells;
    }
}

class Generation {
    private final Universe currGen;

    Generation (Universe currGen) {
        this.currGen = currGen;
    }

    public void generateNextGen () {
        int size = currGen.getSize();

        boolean[][] pastMap = currGen.getMap();
        boolean[][] nextGenMap = new boolean[size][size];

        int totalAlive = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int cnt = getCount(pastMap, size, i, j);
                if ((pastMap[i][j] && (cnt == 3 || cnt == 2))
                        || (!pastMap[i][j] && cnt == 3)) {
                    totalAlive ++;
                    nextGenMap[i][j] = true;
                } else {
                    nextGenMap[i][j] = false;
                }
            }
        }
        currGen.setMap(nextGenMap, totalAlive);
    }

    private int getCount (boolean[][] pastMap, int size, int i, int j) {
        int c = 0;

        for(int posX = i - 1; posX <= i + 1; posX++) {
            for(int posY = j - 1; posY <= j + 1; posY++) {
                if(posX == i && posY == j) {
                    continue;
                }

                int x = (posX < 0 ? size - 1 : (posX >= size? 0 : posX));
                int y = (posY < 0 ? size - 1 : (posY >= size? 0 : posY));

                if(pastMap[x][y]){
                    c++;
                }
            }
        }

        return c;
    }

}

public class Main {

    public static void main(String[] args) {

        Universe game = new Universe (20);
            new GameOfLife(game);
    }
}
