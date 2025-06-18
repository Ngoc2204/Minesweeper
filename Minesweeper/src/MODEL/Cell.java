/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MODEL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import UI.Minesweeper;
import java.net.URL;

/**
 *
 * @author nguye
 */
public class Cell extends JButton {

    private int row, col;
    private boolean isMine;
    private boolean isRevealed;
    private boolean isFlagged;
    private int neighborMines;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.isMine = false;
        this.isRevealed = false;
        this.isFlagged = false;

        setPreferredSize(new Dimension(50, 50));
        setFont(new Font("Arial", Font.BOLD, 20));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    toggleFlag();
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    reveal();
                }
            }
        });
    }

    private void toggleFlag() {
        if (!isRevealed) {
            isFlagged = !isFlagged;
            if (isFlagged) {
                URL url = getClass().getResource("/minesweeper/img/flag.jpg");
                if (url != null) {
                    ImageIcon icon = new ImageIcon(url);
                    Image scaledImage = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                    setIcon(new ImageIcon(scaledImage));
                } else {
                    System.err.println("Không tìm thấy ảnh flag.png!");
                }
                setText("");
            } else {
                setIcon(null);
                setText("");
            }
        }
    }

    public void reveal() {
        if (isFlagged || isRevealed) {
            return;
        }
        isRevealed = true;

        if (isMine) {
            URL url = getClass().getResource("/minesweeper/img/mine.png");
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                Image scaledImage = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                setIcon(new ImageIcon(scaledImage));
            } else {
                System.err.println("Không tìm thấy ảnh mine.png!");
            }
            setBackground(Color.RED);
            ((Minesweeper) SwingUtilities.getWindowAncestor(this)).gameOver(false);
        } else {
            if (neighborMines > 0) {
                setText(String.valueOf(neighborMines));
                switch (neighborMines) {
                    case 1:
                        setForeground(Color.BLUE);
                        break;
                    case 2:
                        setForeground(new Color(0, 128, 0));
                        break;
                    case 3:
                        setForeground(Color.RED);
                        break;
                    case 4:
                        setForeground(new Color(128, 0, 128));
                        break;
                    default:
                        setForeground(Color.BLACK);
                        break;
                }
            }
            setEnabled(false);
            if (neighborMines == 0) {
                ((Minesweeper) SwingUtilities.getWindowAncestor(this)).revealNeighbors(row, col);
            }
            ((Minesweeper) SwingUtilities.getWindowAncestor(this)).checkWin();
        }
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean isMine) {
        this.isMine = isMine;
    }

    public void setNeighborMines(int count) {
        this.neighborMines = count;
    }

    public boolean isRevealed() {
        return isRevealed;
    }

    public boolean isFlagged() {
        return isFlagged;
    }

//    public void updateTheme(boolean darkMode) {
//        if (darkMode) {
//            this.setBackground(java.awt.Color.DARK_GRAY);
//            this.setForeground(java.awt.Color.WHITE);
//        } else {
//            this.setBackground(null); // hoặc màu mặc định
//            this.setForeground(java.awt.Color.BLACK);
//        }
//    }
    public void updateTheme(boolean isDarkMode) {
        if (isDarkMode) {
            setBackground(Color.DARK_GRAY);
            setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        } else {
            setBackground(Color.LIGHT_GRAY);
            setBorder(BorderFactory.createLineBorder(Color.GRAY));
        }
    }
}
