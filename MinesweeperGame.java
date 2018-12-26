package com.javarush.games.minesweeper;

import com.javarush.engine.cell.*;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private int countFlags;
    private boolean isGameStopped;
    private int countClosedTiles = SIDE * SIDE;
    private int score;

    private void createGame() {
        countMinesOnField = 0;
        for (int x = 0; x < gameField[0].length; x++) {
            for (int y = 0; y < gameField.length; y++) {
                boolean b = (getRandomNumber(10) < 1) ? true : false;
                gameField[y][x] = new GameObject(x, y, b);
                if (b) countMinesOnField++;
                setCellColor(x, y, Color.ORANGE);
                setCellValue(x, y, "");
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
    }

    private void restart() {
        isGameStopped = false;
        countClosedTiles = SIDE * SIDE;
        score = 0;
        countMinesOnField = 0;
        countFlags = 0;
        createGame();
        setScore(score);
    }

    private List<GameObject> getNeighbors(GameObject o) {
        List<GameObject> list = new ArrayList<>();
        int x1, y1; // координаты какого-то соседа
        for (int y = -1; y <= 1; y++) {
            for (int x = -1; x <= 1; x++) {
                x1 = o.x + x;
                y1 = o.y + y;
                if (x1 >= 0 && x1 < SIDE && y1 >= 0 && y1 < SIDE) {
                    if (x != 0 || y != 0) list.add(gameField[y1][x1]);
                }
            }
        }
        return list;
    }

    private void countMineNeighbors() {
        for (int y = 0; y < gameField[0].length; y++) {
            for (int x = 0; x < gameField.length; x++) {
                if (!gameField[y][x].isMine) {
                    List<GameObject> list = getNeighbors(gameField[y][x]);
                    for (GameObject o : list) {
                        if (o.isMine) gameField[y][x].countMineNeighbors++;
                    }
                }
            }
        }
    }

    private void openTile(int x, int y) {
        if (isGameStopped || gameField[y][x].isOpen || gameField[y][x].isFlag) return;
        else {
            gameField[y][x].isOpen = true;
            countClosedTiles--;

            if (gameField[y][x].isMine) {
                setCellValueEx(x, y, Color.RED, MINE);
                gameOver();
                return;
            } else {
                score = score + 5;
                setScore(score);
                setCellColor(x, y, Color.LIGHTGREEN);
                if (gameField[y][x].countMineNeighbors != 0) setCellNumber(x, y, gameField[y][x].countMineNeighbors);
                else setCellValue(x, y, "");
            }

            if (countClosedTiles == countMinesOnField) {
                win();
                return;
            }

            if (!gameField[y][x].isMine && gameField[y][x].countMineNeighbors == 0) {
                List<GameObject> list = getNeighbors(gameField[y][x]);
                for (GameObject o : list) {
                    if (!o.isOpen) openTile(o.x, o.y);
                }
            }
        }
    }

    private void markTile(int x, int y) {
        if (gameField[y][x].isOpen) return;
        if (countFlags == 0 && gameField[y][x].isFlag == false) return;
        if (!gameField[y][x].isFlag) {
            gameField[y][x].isFlag = true;
            countFlags--;
            setCellValue(x, y, FLAG);
            setCellColor(x, y, Color.YELLOW);
        } else {
            gameField[y][x].isFlag = false;
            countFlags++;
            setCellValue(x, y, "");
            setCellColor(x, y, Color.ORANGE);

        }
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.AZURE, "Да ты крут!", Color.BLACK, 60);
    }


    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.AZURE, "Проиграл что ли? Хм...", Color.BLACK, 60);
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        super.onMouseRightClick(x, y);
        markTile(x, y);
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        super.onMouseLeftClick(x, y);
        if (isGameStopped) restart();
        else openTile(x, y);
    }

    @Override
    public void initialize() {
        super.initialize();
        setScreenSize(SIDE, SIDE);
        createGame();
    }
}
