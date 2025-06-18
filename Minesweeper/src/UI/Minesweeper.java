/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package UI;

import MODEL.Cell;
import java.awt.GridLayout;
import java.util.Random;
import javax.swing.JOptionPane;
import MODEL.SoundPlayer;
import java.sql.*;

/**
 *
 * @author nguye
 */
public class Minesweeper extends javax.swing.JFrame {

    private boolean isDarkMode = false; // Mặc định là Light Mode
    public final int ROWS = 10;
    public final int COLS = 10;
    public final int MINES = 10;
    public Cell[][] cells = new Cell[ROWS][COLS];
    private javax.swing.Timer timer;
    private int seconds;

    /**
     * Creates new form Minesweeper
     */
    public Minesweeper() {
        initComponents();
    }



    private void initBoard() {
        pnlBoard.setLayout(new GridLayout(ROWS, COLS)); // Dùng lưới
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                cells[i][j] = new Cell(i, j);
                pnlBoard.add(cells[i][j]);
            }
        }
        pnlBoard.revalidate();
        pnlBoard.repaint();

        placeMines();
        calculateNeighborMines();

    }

    private void startTimer() {
        if (timer != null) {
            timer.stop();
        }
        seconds = 0;

        lblTimer.setText("Timer: 0 s");

        lblTimer.setVisible(true);
        lblTimer.repaint();

        timer = new javax.swing.Timer(1000, new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                seconds++;
                lblTimer.setText("Timer: " + seconds + " s");
                lblTimer.repaint();
            }
        });
        timer.start();
    }

    private void placeMines() {
        Random rand = new Random();
        int minesPlaced = 0;
        while (minesPlaced < MINES) {
            int r = rand.nextInt(ROWS);
            int c = rand.nextInt(COLS);
            if (!cells[r][c].isMine()) {
                cells[r][c].setMine(true);
                minesPlaced++;
            }
        }
    }

    private void calculateNeighborMines() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                cells[i][j].setNeighborMines(countMinesAround(i, j));
            }
        }
    }

    private int countMinesAround(int row, int col) {
        int count = 0;
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i >= 0 && i < ROWS && j >= 0 && j < COLS) {
                    if (cells[i][j].isMine()) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private void checkNameBeforeStart() {
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên người chơi trước khi bắt đầu!");
            pnlBoard.setEnabled(false);
        } else {
            pnlBoard.setEnabled(true);
        }
    }

    public void resetGame() {
        getContentPane().removeAll();
        initComponents();
        initBoard();
        revalidate();
        repaint();
    }

    public void gameOver(boolean win) {
        stopTimer();
        String message = win ? "Bạn đã chiến thắng!" : "Bạn đã thua!";
        if (win) {
            saveScore();
        }
        int option = JOptionPane.showConfirmDialog(this, message + " Bạn có muốn chơi lại không?", "Kết quả", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            pnlBoard.removeAll();
            initBoard();
            startTimer();
        } else {
            HomeFrame gameForm = new HomeFrame();
            gameForm.setVisible(true);
            this.dispose();
        }
    }

    private void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    private void saveScore() {
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            return;
        }

        try {
            String url = "jdbc:sqlserver://localhost:1433;databaseName=MINESWEEPER;encrypt=false";
            String user = "sa";
            String password = "123";
            Connection conn = DriverManager.getConnection(url, user, password);

            String selectPlayer = "SELECT PlayerID FROM Players WHERE PlayerName = ?";
            PreparedStatement selectStmt = conn.prepareStatement(selectPlayer);
            selectStmt.setString(1, name);
            ResultSet rs = selectStmt.executeQuery();

            int playerId;
            if (rs.next()) {
                playerId = rs.getInt("PlayerID");
            } else {
                String insertPlayer = "INSERT INTO Players (PlayerName) VALUES (?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertPlayer, Statement.RETURN_GENERATED_KEYS);
                insertStmt.setString(1, name);
                insertStmt.executeUpdate();
                ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    playerId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Lỗi khi tạo Player.");
                }
                insertStmt.close();
            }
            String insertScore = "INSERT INTO Scores (PlayerID, TimeTakenInSeconds) VALUES (?, ?)";
            PreparedStatement insertScoreStmt = conn.prepareStatement(insertScore);
            insertScoreStmt.setInt(1, playerId);
            insertScoreStmt.setInt(2, seconds);
            insertScoreStmt.executeUpdate();

            rs.close();
            selectStmt.close();
            insertScoreStmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void revealNeighbors(int row, int col) {
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i >= 0 && i < ROWS && j >= 0 && j < COLS) {
                    if (i == row && j == col) {
                        continue;
                    }

                    Cell neighbor = cells[i][j];
                    if (!neighbor.isRevealed() && !neighbor.isFlagged()) {
                        neighbor.reveal();
                    }
                }
            }
        }
    }

    public void checkWin() {
        int revealedCount = 0;
        int totalCells = ROWS * COLS;

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (cells[i][j].isRevealed()) {
                    revealedCount++;
                }
            }
        }

        if (revealedCount == totalCells - MINES) {
            gameOver(true);
        }
    }

    private void toggleMode() {
        isDarkMode = !isDarkMode;

        if (isDarkMode) {
            getContentPane().setBackground(java.awt.Color.DARK_GRAY);
            pnlBoard.setBackground(java.awt.Color.BLACK);
            txtName.setBackground(java.awt.Color.GRAY);
            txtName.setForeground(java.awt.Color.WHITE);
            lblTimer.setForeground(java.awt.Color.WHITE);
            jLabel2.setForeground(java.awt.Color.WHITE);
            lbBack.setForeground(java.awt.Color.WHITE);
            btnPLay.setForeground(java.awt.Color.WHITE);
            toggleModeBtn.setText("Light Mode");
        } else {
            getContentPane().setBackground(java.awt.Color.WHITE);
            pnlBoard.setBackground(java.awt.Color.WHITE);
            txtName.setBackground(java.awt.Color.WHITE);
            txtName.setForeground(java.awt.Color.BLACK);
            lblTimer.setForeground(java.awt.Color.BLACK);
            jLabel2.setForeground(java.awt.Color.BLACK);
            lbBack.setForeground(java.awt.Color.BLACK);
            btnPLay.setForeground(java.awt.Color.BLACK);
            toggleModeBtn.setText("Dark Mode");
        }

        // Làm tương tự với các ô trong bảng nếu cần
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (cells[i][j] != null) {
                    cells[i][j].updateTheme(isDarkMode);
                }
            }
        }

        repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbBack = new javax.swing.JLabel();
        pnlBoard = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        btnPLay = new javax.swing.JButton();
        lblTimer = new javax.swing.JLabel();
        toggleModeBtn = new javax.swing.JToggleButton();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Tất cả chúng ta đều là tôn ngộ không");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lbBack.setFont(new java.awt.Font("Goudy Stout", 0, 24)); // NOI18N
        lbBack.setText("Back");
        lbBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbBackMouseClicked(evt);
            }
        });
        getContentPane().add(lbBack, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 600, -1, -1));

        pnlBoard.setBackground(new java.awt.Color(255, 255, 255));
        pnlBoard.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout pnlBoardLayout = new javax.swing.GroupLayout(pnlBoard);
        pnlBoard.setLayout(pnlBoardLayout);
        pnlBoardLayout.setHorizontalGroup(
            pnlBoardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 493, Short.MAX_VALUE)
        );
        pnlBoardLayout.setVerticalGroup(
            pnlBoardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 445, Short.MAX_VALUE)
        );

        getContentPane().add(pnlBoard, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 200, -1, 447));

        jLabel2.setFont(new java.awt.Font("Goudy Stout", 0, 18)); // NOI18N
        jLabel2.setText("Your Name:");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 160, -1, -1));

        txtName.setFont(new java.awt.Font("Goudy Stout", 0, 18)); // NOI18N
        getContentPane().add(txtName, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 160, 186, 30));

        btnPLay.setBackground(new java.awt.Color(0, 153, 153));
        btnPLay.setFont(new java.awt.Font("Goudy Stout", 1, 18)); // NOI18N
        btnPLay.setText("start");
        btnPLay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPLayActionPerformed(evt);
            }
        });
        getContentPane().add(btnPLay, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 160, 150, 30));

        lblTimer.setFont(new java.awt.Font("Goudy Old Style", 1, 24)); // NOI18N
        lblTimer.setText("Timer: 0 s");
        getContentPane().add(lblTimer, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 220, 140, 60));

        toggleModeBtn.setFont(new java.awt.Font("Goudy Old Style", 1, 18)); // NOI18N
        toggleModeBtn.setText("Dark/Light Mode");
        toggleModeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleModeBtnActionPerformed(evt);
            }
        });
        getContentPane().add(toggleModeBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 560, -1, 30));

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/minesweeper/img/11zon_resized.png"))); // NOI18N
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 850, 650));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnPLayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPLayActionPerformed
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "Vui lòng nhập tên người chơi!");
            return;
        }
        pnlBoard.removeAll();
        initBoard();
        startTimer();
    }//GEN-LAST:event_btnPLayActionPerformed

    private void lbBackMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbBackMouseClicked
        HomeFrame gameForm = new HomeFrame();
        // Hiển thị form Minesweeper
        gameForm.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_lbBackMouseClicked

    private void toggleModeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleModeBtnActionPerformed
        toggleMode(); // TODO add your handling code here:
    }//GEN-LAST:event_toggleModeBtnActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Minesweeper.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Minesweeper.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Minesweeper.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Minesweeper.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        try {
            boolean nimbusSet = false;
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    nimbusSet = true;
                    break;
                }
            }
            if (!nimbusSet) {
                javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getCrossPlatformLookAndFeelClassName());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Minesweeper().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPLay;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel lbBack;
    private javax.swing.JLabel lblTimer;
    private javax.swing.JPanel pnlBoard;
    private javax.swing.JToggleButton toggleModeBtn;
    private javax.swing.JTextField txtName;
    // End of variables declaration//GEN-END:variables
}
