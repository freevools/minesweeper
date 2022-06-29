package com.javarush.games.minesweeper;

import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;

public class MinesweeperGame extends Game {
    private static final int SIDE = 9;
    private final GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private int countFlags;
    private final static String MINE = "\uD83D\uDCA3";
    private final static String FLAG = "\uD83D\uDEA9";
    private boolean isGameStopped;
    private int countClosedTiles = SIDE * SIDE;
    private int score;



    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
    }

    private void createGame() {

        for (int y = 0; y < SIDE; y++) {   //очищение поля
            for (int x = 0; x < SIDE; x++) {
                setCellValue(x,y,"");
            }
        }

        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.GREENYELLOW);

            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
    }

    //считает количество соседей у клетки
    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }

    //установка количества мин вокруг каждого gameObject (не мины) поля gameField
    private void countMineNeighbors(){
        for (int x = 0; x < SIDE; x++) {
            for (int y = 0; y < SIDE; y++) {

                if (!gameField[x][y].isMine){ //если gameObject не мина

                    for (GameObject g : getNeighbors(gameField[x][y])) {    //List соседей gameObject
                        if (g.isMine)  gameField[x][y].countMineNeighbors++;
                    }
                }
            }
        }
    }
    //Открытие ячейки
    private void openTile(int x, int y){


        if (!isGameStopped && !gameField[y][x].isOpen && !gameField[y][x].isFlag) {

            GameObject gameObject = gameField[y][x];
            gameObject.isOpen = true;
            countClosedTiles--;
            setCellColor(x, y, Color.AQUA);

            if (gameObject.isMine) { //если мина, то присваеваем клетке значение и картинку мины

                setCellValue(gameObject.x, gameObject.y, MINE);   //GameOver
                setCellValueEx(x, y, Color.RED, MINE);
                gameOver();

            } else if (gameObject.countMineNeighbors != 0) {    //если мины рядом есть
                setCellNumber(x, y, gameObject.countMineNeighbors); //, пишем цифру
                score = score + 5;

            } else {

                setCellValue(x, y, "");
                for (GameObject g : getNeighbors(gameObject)) { //то открываем соседние

                    if (!g.isOpen) {
                        openTile(g.x, g.y);                          //рекурсия
                        score = score + 5;
                    }
                }
            }
            if (countClosedTiles == countMinesOnField && !gameField[y][x].isMine){
                win();
            }
            setScore(score);
        }

    }
    private void markTile(int x, int y){

        if (!gameField[y][x].isOpen){

            GameObject gameObject = gameField[y][x];

            if (countFlags != 0 && !gameObject.isFlag){

                countFlags--;
                setCellColor(x,y,Color.AQUA);
                setCellValue(x, y, FLAG);
                gameObject.isFlag = true;


            }else if (gameObject.isFlag) {

                countFlags++;
                gameObject.isFlag = false;
                setCellColor(x, y, Color.GREENYELLOW);
                setCellValue(x, y, "");
            }
        }
    }
    private void gameOver(){
        isGameStopped = true;
        showMessageDialog(Color.WHITE,"Game Over!",Color.BLACK,50);
    }
    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.WHITE,"Congratulation!",Color.BLACK,50);
    }
    private void restart(){
        isGameStopped = false;
        countClosedTiles = SIDE * SIDE;
        score = 0;
        countMinesOnField = 0;
        setScore(score);
        createGame();
    }


        @Override
    public void onMouseLeftClick(int x, int y) {
        if (isGameStopped) {
            restart();
            return;
        }
        openTile(x,y);
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        markTile(x,y);
    }

}