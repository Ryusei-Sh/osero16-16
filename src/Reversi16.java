import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.Timer;

public class Reversi16 extends JPanel {
    static final int WIDTH = 1000; // 画面サイズ（幅）
    static final int HEIGHT = 1100; // 画面サイズ（高さ）
    int lm = 50; // 左側余白
    int tm = 200; // 上側余白
    int cs = 55; // マスのサイズ
    int turn = 1; // 手番（1:黒，2:白)
    private Timer timer; // タイマーオブジェクト
    private long elapsedTime; // 経過時間（ミリ秒単位）
    private long startTime; // 開始時間を記録する変数


    int ban[][] =  {{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 1, 2, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 2, 1, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}}; // 盤面
        

    boolean gameOver = false; // ゲーム終了フラグ

    // コンストラクタ（初期化処理）
    public Reversi16() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        addMouseListener(new MouseProc());

        // タイマーオブジェクトを初期化
        timer = new Timer(1000, e -> {
            elapsedTime = System.currentTimeMillis() - startTime;
            repaint(); // 描画を更新
        });

        // タイマーを開始
        timer.start();

        // ゲームの開始時間を記録
        startTime = System.currentTimeMillis();
    }

    // 画面描画
    protected void paintComponent(Graphics g) {
        // 背景
        g.setColor(Color.gray);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // ターン情報の表示
        String turnInfo;
        if (turn == 1) {
            turnInfo = "黒石のターン";
            g.setColor(Color.black);
        } else {
            turnInfo = "白石のターン";
            g.setColor(Color.white);
        }
        g.setFont(new Font("SansSerif", Font.BOLD, 50));
        g.drawString(turnInfo, 50, 150);


        // 経過時間を表示
        g.setFont(new Font("SansSerif", Font.BOLD, 40));
        g.setColor(Color.yellow);
        g.drawString("経過時間: " + elapsedTime / 1000 + "秒", 400, 50);


        // 盤面描画
        for (int i = 0; i < 16; i++) {
            int x = lm + cs * i;
            for (int j = 0; j < 16; j++) {
                int y = tm + cs * j;

                g.setColor(new Color(0, 170, 0));
                g.fillRect(x, y, cs, cs);
                g.setColor(Color.black);
                g.drawRect(x, y, cs, cs);

                if (ban[i][j] != 0) {
                    if (ban[i][j] == 1) {
                        g.setColor(Color.black);
                    } else {
                        g.setColor(Color.white);
                    }
                    g.fillOval(x + cs / 10, y + cs / 10, cs * 8 / 10, cs * 8 / 10);
                }
                // 自分のターンの時に相手の石をひっくり返せるマスに「○」マークを表示
                if (canPlace(i, j) && ban[i][j] == 0) {
                    if (turn == 1) {
                        g.setColor(Color.black);
                    } else if (turn == 2) {
                        g.setColor(Color.white);
                    } else if (turn == 0) {
                        g.drawString("hosi", x + cs, y + cs);
                    }
                    g.setFont(new Font("SansSerif", Font.BOLD, 30));
                    g.drawString("○", x + cs / 2 - 10, y + cs / 2 + 15);
                }
            }
        }
        // 石の個数を表示
        drawStoneCount(g);

        // ゲーム終了時に勝者を表示
        if (gameOver) {
            String winner;
            int blackCount = 0;
            int whiteCount = 0;

            // 石の個数を数える
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    if (ban[i][j] == 1) {
                        blackCount++;
                    } else if (ban[i][j] == 2) {
                        whiteCount++;
                    }
                }
            }

            if (blackCount > whiteCount) {
                winner = "黒石の勝利";
            } else if (blackCount < whiteCount) {
                winner = "白石の勝利";
            } else {
                winner = "引き分け";
            }
            g.setColor(Color.red);
            g.setFont(new Font("SansSerif", Font.BOLD, 150));
            g.drawString(winner, 100, 550);
        }
    }

    // 指定されたマスに石を置けるかどうかを判定するメソッド
    private boolean canPlace(int col, int row) {
        // 石を挟む方向の定義
        Direction[] directions = {
            new Direction(0, -1),   // 上
            new Direction(1, -1),   // 右上
            new Direction(1, 0),    // 右
            new Direction(1, 1),    // 右下
            new Direction(0, 1),    // 下
            new Direction(-1, 1),   // 左下
            new Direction(-1, 0),   // 左
            new Direction(-1, -1)   // 左上
        };

        // 相手の石を挟む処理
        for (Direction dir : directions) {
            int dx = dir.dx;
            int dy = dir.dy;
            int nx = col + dx;
            int ny = row + dy;

            if (nx >= 0 && nx < 16 && ny >= 0 && ny < 16 && ban[nx][ny] == 3 - turn) {
                // 相手の石が隣接している場合のみ挟める可能性がある
                nx += dx;
                ny += dy;

                while (nx >= 0 && nx < 16 && ny >= 0 && ny < 16) {
                    if (ban[nx][ny] == turn) {
                        // 挟める場合はtrueを返す
                        return true;
                    } else if (ban[nx][ny] == 0) {
                        break;
                    }

                    nx += dx;
                    ny += dy;
                }
            }
        }

        // 挟める場所がない場合はfalseを返す
        return false;
    }

    // ゲームが終了したかを判定するメソッド
    private boolean isGameOver() {
        int blackCount = 0;
        int whiteCount = 0;

        // 石の個数を数える
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (ban[i][j] == 1) {
                    blackCount++;
                } else if (ban[i][j] == 2) {
                    whiteCount++;
                }
            }
        }

        // 盤面が埋まった場合またはどちらかの石がなくなった場合はゲーム終了
        if (blackCount + whiteCount == 256 || blackCount == 0 || whiteCount == 0) {
            return true;
        }

        // どちらも石を置ける場所がない場合もゲーム終了
        boolean blackCanPlace = false;
        boolean whiteCanPlace = false;

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (ban[i][j] == 0) {
                    if (canPlace(i, j)) {
                        if (turn == 1) {
                            blackCanPlace = true;
                        } else if (turn == 2) {
                            whiteCanPlace = true;
                        }
                    }
                }
            }
        }

        return !blackCanPlace && !whiteCanPlace;
    }

    // 石の個数を表示する
    public void drawStoneCount(Graphics g) {
        // 現在の石の獲得数
        int blackCount = 0;
        int whiteCount = 0;

        // 石の個数を数える
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (ban[i][j] == 1) {
                    blackCount++;
                } else if (ban[i][j] == 2) {
                    whiteCount++;
                }
            }
        }

        // フォントと位置を設定して石の個数を表示
        g.setFont(new Font("SansSerif", Font.BOLD, 40));
        g.setColor(Color.black);
        g.drawString("黒石の個数: " + blackCount, getWidth() - 300, 100);
        g.setColor(Color.white);
        g.drawString("白石の個数: " + whiteCount, getWidth() - 300, 150);
    }

    // 石を挟む方向に対する座標の変化量を表すクラス
    class Direction {
        int dx;
        int dy;

        public Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }

    // クリック時の処理
    class MouseProc extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            if (gameOver) {
                return;
            }

            // クリックされた座標を取得
            int mx = e.getX();
            int my = e.getY();

            // 盤面の座標に変換
            int col = (mx - lm) / cs;
            int row = (my - tm) / cs;

            // クリックされたマスに石を置けるか判定
            if (col >= 0 && col < 16 && row >= 0 && row < 16 && canPlace(col, row)) {
                ban[col][row] = turn;

                // 相手の石をひっくり返す処理
                Direction[] directions = {
                    new Direction(0, -1),   // 上
                    new Direction(1, -1),   // 右上
                    new Direction(1, 0),    // 右
                    new Direction(1, 1),    // 右下
                    new Direction(0, 1),    // 下
                    new Direction(-1, 1),   // 左下
                    new Direction(-1, 0),   // 左
                    new Direction(-1, -1)   // 左上
                };

                for (Direction dir : directions) {
                    int dx = dir.dx;
                    int dy = dir.dy;
                    int nx = col + dx;
                    int ny = row + dy;

                    if (nx >= 0 && nx < 16 && ny >= 0 && ny < 16 && ban[nx][ny] == 3 - turn) {
                        nx += dx;
                        ny += dy;

                        while (nx >= 0 && nx < 16 && ny >= 0 && ny < 16) {
                            if (ban[nx][ny] == turn) {
                                // 自分の石があればひっくり返す
                                int cx = col;
                                int cy = row;

                                while (cx != nx || cy != ny) {
                                    ban[cx][cy] = turn;
                                    cx += dx;
                                    cy += dy;
                                }
                                break;
                            } else if (ban[nx][ny] == 0) {
                                break;
                            }

                            nx += dx;
                            ny += dy;
                        }
                    }
                }

                // ターンの切り替え
                turn = 3 - turn;

                // ゲーム終了判定
                gameOver = isGameOver();

                // 画面を再描画
                repaint();
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Reversi");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new Reversi16());
        frame.pack();
        frame.setVisible(true);
    }
}
