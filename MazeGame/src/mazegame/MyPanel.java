package mazegame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Queue;
import javax.swing.JPanel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.Timer;
import javax.swing.JButton;

public class MyPanel extends JPanel {

    private static final int MAZE_SIZE = 20;
    private static final int CELL_SIZE = 20;

    private int[][] maze;
    private int redDroidRow;
    private int redDroidCol;
    private int greenDroidRow;
    private int greenDroidCol;
    private boolean gameStarted;
    private boolean gameFinished;
    private int mazeRows;
    private int mazeCols;
    private int redDroidCount = 0;
    
    private JButton startButton;
    private JButton stopButton;
    private JButton randomizeButton;
    private JButton randomizeRedDroidButton;
    private JButton randomizeGreenDroidButton;
    private JButton addRedDroidButton;
    private JButton reduceRedDroidButton;

    public MyPanel() {
        maze = generateMaze();
        shuffleRedDroidPosition();
        shuffleGreenDroidPosition();
        
        setPreferredSize(new Dimension(650, 450));
        startDroidMovement();
    }

    private int[][] generateMaze() {
        int[][] maze = new int[MAZE_SIZE][MAZE_SIZE];

        // Menginisialisasi labirin dengan dinding
        for (int row = 0; row < MAZE_SIZE; row++) {
            for (int col = 0; col < MAZE_SIZE; col++) {
                maze[row][col] = 1; // Tembok
            }
        }

        // Hasilkan labirin dengan backtracking rekursif
        generateMazeRecursive(1, 1, maze);

        // Hubungkan semua sel menggunakan Depth-First Search
        connectCellsUsingDFS(maze);
        
        // Atur posisi droid merah ke sel 0
        maze[redDroidRow][redDroidCol] = 0;

        // Atur posisi droid hijau ke sel 0
        maze[greenDroidRow][greenDroidCol] = 0;

        return maze;
    }

    private void generateMazeRecursive(int row, int col, int[][] maze) {
        // Tandai sel saat ini sebagai dikunjungi (0)
        maze[row][col] = 0;

        // Dapatkan daftar acak dari semua tetangga
        int[][] neighbors = getShuffledNeighbors(row, col);

        // Kunjungi setiap tetangga dalam urutan acak
        for (int[] neighbor : neighbors) {
            int nextRow = neighbor[0];
            int nextCol = neighbor[1];

            // Periksa apakah tetangga adalah sel yang valid
            if (isValidCell(nextRow, nextCol, maze)) {
                // Periksa apakah tetangga adalah sel yang belum dikunjungi (1)
                if (maze[nextRow][nextCol] == 1) {
                    // Hapus dinding antara sel saat ini dan tetangga
                    int wallRow = row + (nextRow - row) / 2;
                    int wallCol = col + (nextCol - col) / 2;
                    maze[wallRow][wallCol] = 0;

                    // Kunjungi tetangga secara rekursif
                    generateMazeRecursive(nextRow, nextCol, maze);
                }
            }
        }
    }

    private void connectCellsUsingDFS(int[][] maze) {
        boolean[][] visited = new boolean[MAZE_SIZE][MAZE_SIZE];
        dfs(0, 0, maze, visited);
    }

    private void dfs(int row, int col, int[][] maze, boolean[][] visited) {
        visited[row][col] = true;
        int[][] neighbors = {
                {row - 2, col},
                {row, col + 2},
                {row + 2, col},
                {row, col - 2}
        };

        // Kocok tetangga menggunakan algoritma Fisher-Yates
        for (int i = neighbors.length - 1; i > 0; i--) {
            int j = (int) (Math.random() * (i + 1));
            int[] temp = neighbors[i];
            neighbors[i] = neighbors[j];
            neighbors[j] = temp;
        }

        for (int[] neighbor : neighbors) {
            int nextRow = neighbor[0];
            int nextCol = neighbor[1];

            if (isValidCell(nextRow, nextCol, maze) && !visited[nextRow][nextCol]) {
                int wallRow = row + (nextRow - row) / 2;
                int wallCol = col + (nextCol - col) / 2;
                maze[wallRow][wallCol] = 0;
                dfs(nextRow, nextCol, maze, visited);
            }
        }
    }

    private int[][] getShuffledNeighbors(int row, int col) {
        int[][] neighbors = {
                {row - 2, col},
                {row, col + 2},
                {row + 2, col},
                {row, col - 2}
        };

        List<int[]> neighborList = new ArrayList<>();
        for (int[] neighbor : neighbors) {
            int nextRow = neighbor[0];
            int nextCol = neighbor[1];

            if (isValidCell(nextRow, nextCol, maze)) {
                neighborList.add(neighbor);
            }
        }

        Collections.shuffle(neighborList);

        int[][] shuffledNeighbors = new int[neighborList.size()][2];
        for (int i = 0; i < neighborList.size(); i++) {
            shuffledNeighbors[i] = neighborList.get(i);
        }

        return shuffledNeighbors;
    }

        private boolean isValidCell(int row, int col, int[][] maze) {
            return row >= 0 && row < MAZE_SIZE && col >= 0 && col < MAZE_SIZE;
        }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Menggambar seluruh sel di labirin
        for (int row = 0; row < MAZE_SIZE; row++) {
            for (int col = 0; col < MAZE_SIZE; col++) {
                int x = col * CELL_SIZE;
                int y = row * CELL_SIZE;

                // Menggambar jalan
                if (maze[row][col] == 0) {
                    g.setColor(Color.WHITE);
                    g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                }
                // Menggambar tembok
                else if (maze[row][col] == 1) {
                    g.setColor(Color.BLACK);
                    g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                }
                // Menggambar droid merah tambahan
                else if (maze[row][col] == 2) {
                    g.setColor(Color.RED);
                    g.fillOval(x, y, CELL_SIZE, CELL_SIZE);
                }
            }
        }
        
        // Menggambar droid merah
        g.setColor(Color.RED);
        g.fillOval(redDroidCol * CELL_SIZE, redDroidRow * CELL_SIZE, CELL_SIZE, CELL_SIZE);

        // Menggambar droid hijau
        g.setColor(Color.GREEN);
        g.fillOval(greenDroidCol * CELL_SIZE, greenDroidRow * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        
        int buttonWidth = 180;
        int buttonHeight = 30;
        int buttonMargin = 10;
        int sideMargin = 450;
        int topMargin = 20;

        startButton = new JButton("Mulai");
        startButton.setBounds(sideMargin, topMargin, buttonWidth, buttonHeight);
        add(startButton);

        stopButton = new JButton("Berhenti");
        stopButton.setBounds(sideMargin, topMargin + buttonHeight + buttonMargin, buttonWidth, buttonHeight);
        add(stopButton);

        randomizeButton = new JButton("Acak Peta");
        randomizeButton.setBounds(sideMargin, topMargin + (buttonHeight + buttonMargin) * 2, buttonWidth, buttonHeight);
        add(randomizeButton);

        randomizeRedDroidButton = new JButton("Acak Droid Merah");
        randomizeRedDroidButton.setBounds(sideMargin, topMargin + (buttonHeight + buttonMargin) * 3, buttonWidth, buttonHeight);
        add(randomizeRedDroidButton);

        randomizeGreenDroidButton = new JButton("Acak Droid Hijau");
        randomizeGreenDroidButton.setBounds(sideMargin, topMargin + (buttonHeight + buttonMargin) * 4, buttonWidth, buttonHeight);
        add(randomizeGreenDroidButton);

        addRedDroidButton = new JButton("Tambah Droid Merah");
        addRedDroidButton.setBounds(sideMargin, topMargin + (buttonHeight + buttonMargin) * 5, buttonWidth, buttonHeight);
        add(addRedDroidButton);

        reduceRedDroidButton = new JButton("Kurangi Droid Merah");
        reduceRedDroidButton.setBounds(sideMargin, topMargin + (buttonHeight + buttonMargin) * 6, buttonWidth, buttonHeight);
        add(reduceRedDroidButton);

        // Aksi untuk tombol Mulai, Berhenti, Acak Peta, Acak Droid Merah, Acak Droid Hijau, Tambah Droid Merah, Pandangan Droid Merah, Pandangan Droid Hijau dan Jarak Pandang Droid Hijau
        // Kode aksi ketika tombol Mulai ditekan
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameStarted && !gameFinished) {
                    gameStarted = true;
                }
            }
        });

        // Kode aksi ketika tombol Berhenti ditekan
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameStarted && !gameFinished) {
                    gameStarted = false;
                }
            }
        });

        // Kode aksi ketika tombol Acak Peta ditekan
        randomizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameStarted) {
                    maze = generateMaze();
                    repaint();
                }
            }
        });

        // Kode aksi ketika tombol Acak Droid Merah ditekan
        randomizeRedDroidButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameStarted) {
                    shuffleRedDroidPosition();
                    repaint();
                }
            }
        });

        // Kode aksi ketika tombol Acak Droid Hijau ditekan
        randomizeGreenDroidButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameStarted) {
                    shuffleGreenDroidPosition();
                    repaint();
                }
            }
        });

        // Kode aksi ketika tombol Tambah Droid Merah ditekan
        addRedDroidButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addRedDroid();
                repaint();
            }
        });

        // Kode aksi ketika tombol Kurangi Droid Merah ditekan
        reduceRedDroidButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                reduceRedDroid();
                repaint();
            }
        });

        // Periksa apakah game sudah selesai dan tampilkan pesan
        if (gameFinished) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Game Over!", 150, 250);
        }
    }

    private boolean isCellVisible(int row, int col) {
        return maze[row][col] == 0;
    }
    
    private void startDroidMovement() {
        Timer timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameStarted && !gameFinished) {
                    moveRedDroid();
                    moveGreenDroid();
                    repaint();

                    // Periksa apakah droid merah telah menangkap droid hijau
                    if (redDroidRow == greenDroidRow && redDroidCol == greenDroidCol) {
                        gameFinished = true;
                        gameStarted = false;
                    }
                }
            }
        });

        timer.start();
    }
    
    private void moveRedDroid() {
        int[][] distance = new int[MAZE_SIZE][MAZE_SIZE];
        boolean[][] visited = new boolean[MAZE_SIZE][MAZE_SIZE];

        // Inisialisasi larik jarak dengan nilai maksimum
        for (int row = 0; row < MAZE_SIZE; row++) {
            for (int col = 0; col < MAZE_SIZE; col++) {
                distance[row][col] = Integer.MAX_VALUE;
            }
        }

        // Lakukan BFS untuk menemukan jarak dari setiap sel ke droid hijau
        bfs(distance, visited, greenDroidRow, greenDroidCol);

        // Periksa apakah droid hijau terlihat
        boolean greenDroidVisible = distance[redDroidRow][redDroidCol] != Integer.MAX_VALUE;

        if (greenDroidVisible) {
            // Temukan sel terjauh dari droid hijau
            int farthestRow = redDroidRow;
            int farthestCol = redDroidCol;
            int maxDistance = Integer.MIN_VALUE;

            for (int row = 0; row < MAZE_SIZE; row++) {
                for (int col = 0; col < MAZE_SIZE; col++) {
                    if (maze[row][col] == 0 && distance[row][col] > maxDistance) {
                        farthestRow = row;
                        farthestCol = col;
                        maxDistance = distance[row][col];
                    }
                }
            }

            // Pindahkan droid merah ke sel terjauh menggunakan BFS
            bfs(distance, visited, farthestRow, farthestCol);

            // Temukan sel tetangga dengan jarak minimum
            int minDistance = distance[redDroidRow][redDroidCol];

            if (isValidCell(redDroidRow - 1, redDroidCol) && maze[redDroidRow - 1][redDroidCol] == 0 && distance[redDroidRow - 1][redDroidCol] < minDistance) {
                redDroidRow--; // bergerak ke atas
            } else if (isValidCell(redDroidRow, redDroidCol - 1) && maze[redDroidRow][redDroidCol - 1] == 0 && distance[redDroidRow][redDroidCol - 1] < minDistance) {
                redDroidCol--; // bergerak ke kiri
            } else if (isValidCell(redDroidRow + 1, redDroidCol) && maze[redDroidRow + 1][redDroidCol] == 0 && distance[redDroidRow + 1][redDroidCol] < minDistance) {
                redDroidRow++; // bergerak ke bawah
            } else if (isValidCell(redDroidRow, redDroidCol + 1) && maze[redDroidRow][redDroidCol + 1] == 0 && distance[redDroidRow][redDroidCol + 1] < minDistance) {
                redDroidCol++; // bergerak ke kanan
            }
        }

        // Cetak labirin yang diperbarui
        printMaze();

        // Periksa apakah droid merah telah menangkap droid hijau
        if (redDroidRow == greenDroidRow && redDroidCol == greenDroidCol) {
            System.out.println("Game over: Red droid caught the green droid!");
            gameFinished = true;
            gameStarted = false;
        }
    }
    
    private void bfs(int[][] distance, boolean[][] visited, int startRow, int startCol) {
        // Inisialisasi antrian untuk BFS
        Queue<int[]> queue = new LinkedList<>();

        // Setel jarak sel awal ke 0 dan tandai sebagai telah dikunjungi
        distance[startRow][startCol] = 0;
        visited[startRow][startCol] = true;

        // Tambahkan sel awal ke antrian
        queue.offer(new int[]{startRow, startCol});

        // Melakukan BFS
        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            int row = cell[0];
            int col = cell[1];

            // Periksa sel tetangga
            int[][] neighbors = {
                    {row - 1, col},
                    {row, col - 1},
                    {row + 1, col},
                    {row, col + 1}
            };

            for (int[] neighbor : neighbors) {
                int nextRow = neighbor[0];
                int nextCol = neighbor[1];

                // Periksa apakah tetangga adalah sel yang valid
                if (isValidCell(nextRow, nextCol) && maze[nextRow][nextCol] == 0 && !visited[nextRow][nextCol]) {
                    // Perbarui jarak dan tandai tetangga sebagai telah dikunjungi
                    distance[nextRow][nextCol] = distance[row][col] + 1;
                    visited[nextRow][nextCol] = true;

                    // Tambahkan tetangga ke antrean untuk eksplorasi lebih lanjut
                    queue.offer(new int[]{nextRow, nextCol});
                }
            }
        }
    }

    private void addRedDroid() {
        int row, col;
        do {
            row = (int) (Math.random() * (MAZE_SIZE - 2)) + 1;
            col = (int) (Math.random() * (MAZE_SIZE - 2)) + 1;
        } while (maze[row][col] != 0);

        maze[row][col] = 2; // Tandai droid merah dengan angka 2
        redDroidCount++;
    }

    private void reduceRedDroid() {
        if (redDroidCount > 0) {
            int row, col;
            do {
                row = (int) (Math.random() * (MAZE_SIZE - 2)) + 1;
                col = (int) (Math.random() * (MAZE_SIZE - 2)) + 1;
            } while (maze[row][col] != 2); // Hanya kurangi droid merah (angka 2)

            maze[row][col] = 0; // Kembalikan sel ke angka 0 (kosong)
            redDroidCount--;
        }
    } 
    
    private void moveGreenDroid() {
    int[][] distance = new int[MAZE_SIZE][MAZE_SIZE];
    boolean[][] visited = new boolean[MAZE_SIZE][MAZE_SIZE];

    // Inisialisasi larik jarak dengan nilai maksimum
    for (int row = 0; row < MAZE_SIZE; row++) {
        for (int col = 0; col < MAZE_SIZE; col++) {
            distance[row][col] = Integer.MAX_VALUE;
        }
    }

    // Lakukan BFS untuk menemukan jarak dari setiap sel ke droid merah
    bfs(distance, visited, redDroidRow, redDroidCol);

    // Temukan sel tetangga dengan jarak maksimum
    int maxDistance = Integer.MIN_VALUE;
    int maxDistanceRow = greenDroidRow;
    int maxDistanceCol = greenDroidCol;

    if (isValidCell(greenDroidRow - 1, greenDroidCol) && maze[greenDroidRow - 1][greenDroidCol] == 0 && distance[greenDroidRow - 1][greenDroidCol] > maxDistance) {
        maxDistance = distance[greenDroidRow - 1][greenDroidCol];
        maxDistanceRow = greenDroidRow - 1;
        maxDistanceCol = greenDroidCol;
    }

    if (isValidCell(greenDroidRow, greenDroidCol - 1) && maze[greenDroidRow][greenDroidCol - 1] == 0 && distance[greenDroidRow][greenDroidCol - 1] > maxDistance) {
        maxDistance = distance[greenDroidRow][greenDroidCol - 1];
        maxDistanceRow = greenDroidRow;
        maxDistanceCol = greenDroidCol - 1;
    }

    if (isValidCell(greenDroidRow + 1, greenDroidCol) && maze[greenDroidRow + 1][greenDroidCol] == 0 && distance[greenDroidRow + 1][greenDroidCol] > maxDistance) {
        maxDistance = distance[greenDroidRow + 1][greenDroidCol];
        maxDistanceRow = greenDroidRow + 1;
        maxDistanceCol = greenDroidCol;
    }

    if (isValidCell(greenDroidRow, greenDroidCol + 1) && maze[greenDroidRow][greenDroidCol + 1] == 0 && distance[greenDroidRow][greenDroidCol + 1] > maxDistance) {
        maxDistance = distance[greenDroidRow][greenDroidCol + 1];
        maxDistanceRow = greenDroidRow;
        maxDistanceCol = greenDroidCol + 1;
    }

    // Gerakkan droid hijau menuju sel tetangga dengan jarak maksimum
    greenDroidRow = maxDistanceRow;
    greenDroidCol = maxDistanceCol;
}


    private boolean isValidCell(int row, int col) {
        return row >= 0 && row < MAZE_SIZE && col >= 0 && col < MAZE_SIZE;
    }

    private void shuffleRedDroidPosition() {
        do {
            redDroidRow = (int) (Math.random() * (MAZE_SIZE - 2)) + 1;
            redDroidCol = (int) (Math.random() * (MAZE_SIZE - 2)) + 1;
        } while (maze[redDroidRow][redDroidCol] != 0);
    }
    
    private void shuffleGreenDroidPosition() {
        do {
            greenDroidRow = (int) (Math.random() * (MAZE_SIZE - 2)) + 1;
            greenDroidCol = (int) (Math.random() * (MAZE_SIZE - 2)) + 1;
        } while (maze[greenDroidRow][greenDroidCol] != 0);
        
        setLayout(null);
    }
    
    public void printMaze() {
        for (int i = 0; i < mazeRows; i++) {
            for (int j = 0; j < mazeCols; j++) {
                if (i == redDroidRow && j == redDroidCol) {
                    System.out.print(" R "); // Posisi droid merah
                } else if (i == greenDroidRow && j == greenDroidCol) {
                    System.out.print(" G "); // Posisi droid hijau
                } else if (maze[i][j] == 1) {
                    System.out.print(" # "); // Dinding
                } else {
                    System.out.print(" . "); // Jalan
                }
            }
            System.out.println(); // Pindah ke baris berikutnya
        }
    }
}