package com.company;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.Rectangle2D;

class GameOfLife extends JFrame {
    //Constants
    private final int GRID_PX = 800;
    private final int SPACING = 1;
    private final int DEFAULT_SIZE = 20;
    private final int MIN_SIZE = 4;
    private final int MAX_SIZE = 200;

    private int gen = 0;
    private Universe currMap;
    private boolean flag = false;

    private int size = 20;
    private double cellSize = 40;

    private final JLabel generationLabel;
    private final JLabel aliveLabel;
    private final JButton startButton = new JButton("Start");
    private final JButton pauseButton = new JButton("Pause");
    private final JButton resetButton = new JButton("Reset");
    private final JButton setButton = new JButton("Set");
    private final JSlider sizeSlider = new JSlider(MIN_SIZE, MAX_SIZE, DEFAULT_SIZE);
    private final JTextField sizeInput = new JTextField(Integer.toString(size));

    public GameOfLife (Universe initMap) {
        this.currMap = initMap;

        generationLabel = new JLabel("Generation #" + this.gen);
        aliveLabel = new JLabel("Alive: " + currMap.getAlive());

        this.setTitle("Game of Life");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(GRID_PX + 15, GRID_PX + 115);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setVisible(true);

        Board board = new Board();
        board.setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new GridLayout(1, 2));
        JPanel bottomPanel = new JPanel();
        JPanel textPanel = new JPanel();
        JPanel sizePanel = new JPanel();
        JPanel setPanel = new JPanel();

        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        textPanel.setLayout(new BoxLayout(textPanel,  BoxLayout.PAGE_AXIS));
        setPanel.setLayout(new BoxLayout(setPanel,  BoxLayout.LINE_AXIS));
        sizePanel.setLayout(new BoxLayout(sizePanel,  BoxLayout.PAGE_AXIS));

        sizePanel.setBorder(BorderFactory.createEmptyBorder(2,0, 0,0));
        textPanel.setBorder(BorderFactory.createEmptyBorder(2,5, 0,0));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 300, 2, 300));

        setButton.setMaximumSize(new Dimension(60, 20));
        sizeInput.setMaximumSize(new Dimension(50, 23));
        sizeInput.setMinimumSize(new Dimension(50, 23));
        sizeInput.setHorizontalAlignment(JTextField.CENTER);

        textPanel.add(generationLabel);
        textPanel.add(aliveLabel);

        setPanel.add(sizeInput);
        setPanel.add(setButton);

        sizePanel.add(sizeSlider);
        sizePanel.add(setPanel);

        topPanel.add(textPanel);
        topPanel.add(sizePanel);

        bottomPanel.add(startButton);
        bottomPanel.add(pauseButton);
        bottomPanel.add(resetButton);

        this.setContentPane(board);
        this.getContentPane().add(topPanel, BorderLayout.PAGE_START);
        this.getContentPane().add(bottomPanel, BorderLayout.PAGE_END);

        Thread genSim = new Thread(() -> {

            while (true) {
                do {
                    try {
                        Thread.sleep(250L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (flag);

                Generation mapGen = new Generation(currMap);
                mapGen.generateNextGen();
                gen++;

                updateInfo (board);
            }
        });

        sizeSlider.addChangeListener(e -> {
            JSlider src = (JSlider) e.getSource();
            int newSize = src.getValue();
            updateMapSize(newSize, board);
            flag = true;
        });

        startButton.addActionListener(e -> {
            if (!genSim.isAlive()) {
                genSim.start();
            }
            flag = false;
        });

        pauseButton.addActionListener(e ->  flag = true);

        resetButton.addActionListener(e -> {
            currMap = new Universe(size);
            gen = 0;

            updateInfo (board);

            flag = true;
        });

        sizeInput.addActionListener(e -> updateMapAccordingToText(board));

        setButton.addActionListener(e -> {
            String command = e.getActionCommand();
            if(command.equals("Set")) {
                updateMapAccordingToText(board);
            }
        });
    }

    private class Board extends JPanel {
        public void paintComponent (Graphics g) {
            g.setColor (Color.DARK_GRAY);
            g.fillRect(0,45,GRID_PX + 1, GRID_PX + 1);
            boolean[][] gameMap = currMap.getMap();
            Graphics2D g2 = (Graphics2D) g;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    Rectangle2D rect = new Rectangle2D.Double((double) i * cellSize, (double) j * cellSize + 45 + 1,
                            cellSize - SPACING, cellSize - SPACING);
                    if (!gameMap[i][j])
                        g2.setColor (Color.WHITE);
                    else
                        g2.setColor (Color.DARK_GRAY);
                    g2.fill(rect);
                }
            }
        }
    }

    private void updateInfo (Board board) {
        generationLabel.setText("Generation #" + gen);
        aliveLabel.setText("Alive: " + currMap.getAlive());
        setContentPane(board);
    }

    private void updateMapSize (int size, Board board) {
        this.size = size;
        currMap = new Universe(size);
        cellSize = (double) GRID_PX / size;
        sizeInput.setText(Integer.toString(size));
        updateInfo(board);
    }

    private void updateMapAccordingToText (Board board) {
        int newSize = Integer.parseInt(sizeInput.getText());
        if(this.size != newSize) {
            updateMapSize(newSize, board);
        }
    }

}

