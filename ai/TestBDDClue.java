package ai;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javaff.data.TotalOrderPlan;
import javaff.data.UngroundProblem;
import javaff.parser.PDDL21parser;
import javaff.parser.ParseException;
import jdd.bdd.BDD;

/**
 * This class...
 *
 * @author Aaron Kay
 */
public class TestBDDClue {
  private static Random random = new Random();
  private static final BDD bdd = new BDD(100);

  //rooms
  private static final int peacock = bdd.createVar();
  private static final int study = bdd.createVar();
  private static final int hall = bdd.createVar();
  private static final int lounge = bdd.createVar();
  private static final int library = bdd.createVar();
  private static final int diningRoom = bdd.createVar();
  private static final int billiardRoom = bdd.createVar();
  private static final int conservatory = bdd.createVar();
  private static final int ballroom = bdd.createVar();

  private static final int kitchen = bdd.createVar();
  //suspects
  private static final int plum = bdd.createVar();
  private static final int scarlet = bdd.createVar();
  private static final int mustard = bdd.createVar();
  private static final int white = bdd.createVar();
  private static final int green = bdd.createVar();

  //weapon
  private static final int rope = bdd.createVar();
  private static final int leadPipe = bdd.createVar();
  private static final int knife = bdd.createVar();
  private static final int wrench = bdd.createVar();
  private static final int candlestick = bdd.createVar();
  private static final int revolver = bdd.createVar();

  public static void main_helper(String[] args) {
    boolean[][] exclusions = new boolean[24][25];

    exclusions[0][0] = true;
    exclusions[0][1] = true;
    exclusions[0][2] = true;
    exclusions[0][3] = true;
    exclusions[0][4] = true;
    exclusions[0][5] = false;
    exclusions[0][6] = true;
    exclusions[0][7] = true;
    exclusions[0][8] = true;
    exclusions[0][9] = true;
    exclusions[0][10] = true;
    exclusions[0][11] = true;
    exclusions[0][12] = true;
    exclusions[0][13] = true;
    exclusions[0][14] = true;
    exclusions[0][15] = true;
    exclusions[0][16] = true;
    exclusions[0][17] = true;
    exclusions[0][18] = false;
    exclusions[0][19] = true;
    exclusions[0][20] = true;
    exclusions[0][21] = true;
    exclusions[0][22] = true;
    exclusions[0][23] = true;
    exclusions[0][24] = true;
    exclusions[1][0] = true;
    exclusions[1][1] = true;
    exclusions[1][2] = true;
    exclusions[1][3] = true;
    exclusions[1][4] = false;
    exclusions[1][5] = false;
    exclusions[1][6] = true;
    exclusions[1][7] = true;
    exclusions[1][8] = true;
    exclusions[1][9] = true;
    exclusions[1][10] = true;
    exclusions[1][11] = false;
    exclusions[1][12] = true;
    exclusions[1][13] = true;
    exclusions[1][14] = true;
    exclusions[1][15] = true;
    exclusions[1][16] = true;
    exclusions[1][17] = false;
    exclusions[1][18] = false;
    exclusions[1][19] = true;
    exclusions[1][20] = true;
    exclusions[1][21] = true;
    exclusions[1][22] = true;
    exclusions[1][23] = true;
    exclusions[1][24] = true;
    exclusions[2][0] = true;
    exclusions[2][1] = true;
    exclusions[2][2] = true;
    exclusions[2][3] = true;
    exclusions[2][4] = false;
    exclusions[2][5] = false;
    exclusions[2][6] = true;
    exclusions[2][7] = true;
    exclusions[2][8] = true;
    exclusions[2][9] = true;
    exclusions[2][10] = true;
    exclusions[2][11] = false;
    exclusions[2][12] = true;
    exclusions[2][13] = true;
    exclusions[2][14] = true;
    exclusions[2][15] = true;
    exclusions[2][16] = true;
    exclusions[2][17] = false;
    exclusions[2][18] = false;
    exclusions[2][19] = true;
    exclusions[2][20] = true;
    exclusions[2][21] = true;
    exclusions[2][22] = true;
    exclusions[2][23] = true;
    exclusions[2][24] = true;
    exclusions[3][0] = true;
    exclusions[3][1] = true;
    exclusions[3][2] = true;
    exclusions[3][3] = true;
    exclusions[3][4] = false;
    exclusions[3][5] = false;
    exclusions[3][6] = true;
    exclusions[3][7] = true;
    exclusions[3][8] = true;
    exclusions[3][9] = true;
    exclusions[3][10] = true;
    exclusions[3][11] = false;
    exclusions[3][12] = true;
    exclusions[3][13] = true;
    exclusions[3][14] = true;
    exclusions[3][15] = true;
    exclusions[3][16] = true;
    exclusions[3][17] = false;
    exclusions[3][18] = false;
    exclusions[3][19] = true;
    exclusions[3][20] = true;
    exclusions[3][21] = true;
    exclusions[3][22] = true;
    exclusions[3][23] = true;
    exclusions[3][24] = true;
    exclusions[4][0] = true;
    exclusions[4][1] = true;
    exclusions[4][2] = true;
    exclusions[4][3] = true;
    exclusions[4][4] = false;
    exclusions[4][5] = false;
    exclusions[4][6] = true;
    exclusions[4][7] = true;
    exclusions[4][8] = true;
    exclusions[4][9] = true;
    exclusions[4][10] = true;
    exclusions[4][11] = false;
    exclusions[4][12] = true;
    exclusions[4][13] = true;
    exclusions[4][14] = true;
    exclusions[4][15] = true;
    exclusions[4][16] = true;
    exclusions[4][17] = false;
    exclusions[4][18] = false;
    exclusions[4][19] = true;
    exclusions[4][20] = true;
    exclusions[4][21] = true;
    exclusions[4][22] = true;
    exclusions[4][23] = true;
    exclusions[4][24] = true;
    exclusions[5][0] = true;
    exclusions[5][1] = true;
    exclusions[5][2] = true;
    exclusions[5][3] = true;
    exclusions[5][4] = false;
    exclusions[5][5] = false;
    exclusions[5][6] = true;
    exclusions[5][7] = true;
    exclusions[5][8] = true;
    exclusions[5][9] = true;
    exclusions[5][10] = true;
    exclusions[5][11] = false;
    exclusions[5][12] = true;
    exclusions[5][13] = true;
    exclusions[5][14] = true;
    exclusions[5][15] = true;
    exclusions[5][16] = true;
    exclusions[5][17] = false;
    exclusions[5][18] = false;
    exclusions[5][19] = false;
    exclusions[5][20] = true;
    exclusions[5][21] = true;
    exclusions[5][22] = true;
    exclusions[5][23] = true;
    exclusions[5][24] = true;
    exclusions[6][0] = true;
    exclusions[6][1] = true;
    exclusions[6][2] = true;
    exclusions[6][3] = true;
    exclusions[6][4] = false;
    exclusions[6][5] = false;
    exclusions[6][6] = false;
    exclusions[6][7] = true;
    exclusions[6][8] = true;
    exclusions[6][9] = true;
    exclusions[6][10] = false;
    exclusions[6][11] = false;
    exclusions[6][12] = false;
    exclusions[6][13] = false;
    exclusions[6][14] = false;
    exclusions[6][15] = false;
    exclusions[6][16] = false;
    exclusions[6][17] = false;
    exclusions[6][18] = false;
    exclusions[6][19] = false;
    exclusions[6][20] = false;
    exclusions[6][21] = false;
    exclusions[6][22] = false;
    exclusions[6][23] = true;
    exclusions[6][24] = true;
    exclusions[7][0] = false;
    exclusions[7][1] = false;
    exclusions[7][2] = false;
    exclusions[7][3] = false;
    exclusions[7][4] = false;
    exclusions[7][5] = false;
    exclusions[7][6] = false;
    exclusions[7][7] = false;
    exclusions[7][8] = false;
    exclusions[7][9] = false;
    exclusions[7][10] = false;
    exclusions[7][11] = false;
    exclusions[7][12] = false;
    exclusions[7][13] = false;
    exclusions[7][14] = false;
    exclusions[7][15] = false;
    exclusions[7][16] = false;
    exclusions[7][17] = false;
    exclusions[7][18] = false;
    exclusions[7][19] = false;
    exclusions[7][20] = false;
    exclusions[7][21] = false;
    exclusions[7][22] = false;
    exclusions[7][23] = false;
    exclusions[7][24] = true;
    exclusions[8][0] = true;
    exclusions[8][1] = false;
    exclusions[8][2] = false;
    exclusions[8][3] = false;
    exclusions[8][4] = false;
    exclusions[8][5] = false;
    exclusions[8][6] = false;
    exclusions[8][7] = false;
    exclusions[8][8] = false;
    exclusions[8][9] = false;
    exclusions[8][10] = false;
    exclusions[8][11] = false;
    exclusions[8][12] = false;
    exclusions[8][13] = false;
    exclusions[8][14] = false;
    exclusions[8][15] = false;
    exclusions[8][16] = false;
    exclusions[8][17] = true;
    exclusions[8][18] = true;
    exclusions[8][19] = true;
    exclusions[8][20] = true;
    exclusions[8][21] = true;
    exclusions[8][22] = true;
    exclusions[8][23] = false;
    exclusions[8][24] = true;
    exclusions[9][0] = true;
    exclusions[9][1] = true;
    exclusions[9][2] = true;
    exclusions[9][3] = true;
    exclusions[9][4] = true;
    exclusions[9][5] = true;
    exclusions[9][6] = true;
    exclusions[9][7] = false;
    exclusions[9][8] = true;
    exclusions[9][9] = true;
    exclusions[9][10] = true;
    exclusions[9][11] = true;
    exclusions[9][12] = true;
    exclusions[9][13] = true;
    exclusions[9][14] = true;
    exclusions[9][15] = false;
    exclusions[9][16] = false;
    exclusions[9][17] = true;
    exclusions[9][18] = true;
    exclusions[9][19] = true;
    exclusions[9][20] = true;
    exclusions[9][21] = true;
    exclusions[9][22] = true;
    exclusions[9][23] = false;
    exclusions[9][24] = false;
    exclusions[10][0] = true;
    exclusions[10][1] = true;
    exclusions[10][2] = true;
    exclusions[10][3] = true;
    exclusions[10][4] = true;
    exclusions[10][5] = true;
    exclusions[10][6] = true;
    exclusions[10][7] = false;
    exclusions[10][8] = true;
    exclusions[10][9] = true;
    exclusions[10][10] = true;
    exclusions[10][11] = true;
    exclusions[10][12] = true;
    exclusions[10][13] = true;
    exclusions[10][14] = true;
    exclusions[10][15] = false;
    exclusions[10][16] = false;
    exclusions[10][17] = true;
    exclusions[10][18] = true;
    exclusions[10][19] = true;
    exclusions[10][20] = true;
    exclusions[10][21] = true;
    exclusions[10][22] = true;
    exclusions[10][23] = true;
    exclusions[10][24] = true;
    exclusions[11][0] = true;
    exclusions[11][1] = true;
    exclusions[11][2] = true;
    exclusions[11][3] = true;
    exclusions[11][4] = true;
    exclusions[11][5] = true;
    exclusions[11][6] = true;
    exclusions[11][7] = false;
    exclusions[11][8] = true;
    exclusions[11][9] = true;
    exclusions[11][10] = true;
    exclusions[11][11] = true;
    exclusions[11][12] = true;
    exclusions[11][13] = true;
    exclusions[11][14] = true;
    exclusions[11][15] = false;
    exclusions[11][16] = false;
    exclusions[11][17] = true;
    exclusions[11][18] = true;
    exclusions[11][19] = true;
    exclusions[11][20] = true;
    exclusions[11][21] = true;
    exclusions[11][22] = true;
    exclusions[11][23] = true;
    exclusions[11][24] = true;
    exclusions[12][0] = true;
    exclusions[12][1] = true;
    exclusions[12][2] = true;
    exclusions[12][3] = true;
    exclusions[12][4] = true;
    exclusions[12][5] = true;
    exclusions[12][6] = true;
    exclusions[12][7] = false;
    exclusions[12][8] = true;
    exclusions[12][9] = true;
    exclusions[12][10] = true;
    exclusions[12][11] = true;
    exclusions[12][12] = true;
    exclusions[12][13] = true;
    exclusions[12][14] = true;
    exclusions[12][15] = false;
    exclusions[12][16] = false;
    exclusions[12][17] = true;
    exclusions[12][18] = true;
    exclusions[12][19] = true;
    exclusions[12][20] = true;
    exclusions[12][21] = true;
    exclusions[12][22] = true;
    exclusions[12][23] = true;
    exclusions[12][24] = true;
    exclusions[13][0] = true;
    exclusions[13][1] = true;
    exclusions[13][2] = true;
    exclusions[13][3] = true;
    exclusions[13][4] = true;
    exclusions[13][5] = true;
    exclusions[13][6] = true;
    exclusions[13][7] = false;
    exclusions[13][8] = true;
    exclusions[13][9] = true;
    exclusions[13][10] = true;
    exclusions[13][11] = true;
    exclusions[13][12] = true;
    exclusions[13][13] = true;
    exclusions[13][14] = true;
    exclusions[13][15] = false;
    exclusions[13][16] = false;
    exclusions[13][17] = true;
    exclusions[13][18] = true;
    exclusions[13][19] = true;
    exclusions[13][20] = true;
    exclusions[13][21] = true;
    exclusions[13][22] = true;
    exclusions[13][23] = true;
    exclusions[13][24] = true;
    exclusions[14][0] = true;
    exclusions[14][1] = true;
    exclusions[14][2] = true;
    exclusions[14][3] = true;
    exclusions[14][4] = true;
    exclusions[14][5] = true;
    exclusions[14][6] = true;
    exclusions[14][7] = false;
    exclusions[14][8] = false;
    exclusions[14][9] = false;
    exclusions[14][10] = false;
    exclusions[14][11] = false;
    exclusions[14][12] = false;
    exclusions[14][13] = false;
    exclusions[14][14] = false;
    exclusions[14][15] = false;
    exclusions[14][16] = false;
    exclusions[14][17] = true;
    exclusions[14][18] = true;
    exclusions[14][19] = true;
    exclusions[14][20] = true;
    exclusions[14][21] = true;
    exclusions[14][22] = true;
    exclusions[14][23] = false;
    exclusions[14][24] = false;
    exclusions[15][0] = true;
    exclusions[15][1] = false;
    exclusions[15][2] = false;
    exclusions[15][3] = false;
    exclusions[15][4] = false;
    exclusions[15][5] = false;
    exclusions[15][6] = false;
    exclusions[15][7] = false;
    exclusions[15][8] = false;
    exclusions[15][9] = false;
    exclusions[15][10] = false;
    exclusions[15][11] = false;
    exclusions[15][12] = false;
    exclusions[15][13] = false;
    exclusions[15][14] = false;
    exclusions[15][15] = false;
    exclusions[15][16] = false;
    exclusions[15][17] = true;
    exclusions[15][18] = true;
    exclusions[15][19] = true;
    exclusions[15][20] = true;
    exclusions[15][21] = true;
    exclusions[15][22] = true;
    exclusions[15][23] = false;
    exclusions[15][24] = true;
    exclusions[16][0] = false;
    exclusions[16][1] = false;
    exclusions[16][2] = false;
    exclusions[16][3] = false;
    exclusions[16][4] = false;
    exclusions[16][5] = false;
    exclusions[16][6] = false;
    exclusions[16][7] = false;
    exclusions[16][8] = false;
    exclusions[16][9] = true;
    exclusions[16][10] = true;
    exclusions[16][11] = true;
    exclusions[16][12] = true;
    exclusions[16][13] = true;
    exclusions[16][14] = true;
    exclusions[16][15] = false;
    exclusions[16][16] = false;
    exclusions[16][17] = false;
    exclusions[16][18] = false;
    exclusions[16][19] = false;
    exclusions[16][20] = false;
    exclusions[16][21] = false;
    exclusions[16][22] = false;
    exclusions[16][23] = false;
    exclusions[16][24] = true;
    exclusions[17][0] = true;
    exclusions[17][1] = true;
    exclusions[17][2] = true;
    exclusions[17][3] = true;
    exclusions[17][4] = true;
    exclusions[17][5] = true;
    exclusions[17][6] = false;
    exclusions[17][7] = false;
    exclusions[17][8] = false;
    exclusions[17][9] = true;
    exclusions[17][10] = true;
    exclusions[17][11] = true;
    exclusions[17][12] = true;
    exclusions[17][13] = true;
    exclusions[17][14] = true;
    exclusions[17][15] = false;
    exclusions[17][16] = false;
    exclusions[17][17] = false;
    exclusions[17][18] = false;
    exclusions[17][19] = false;
    exclusions[17][20] = false;
    exclusions[17][21] = false;
    exclusions[17][22] = false;
    exclusions[17][23] = true;
    exclusions[17][24] = true;
    exclusions[18][0] = true;
    exclusions[18][1] = true;
    exclusions[18][2] = true;
    exclusions[18][3] = true;
    exclusions[18][4] = true;
    exclusions[18][5] = true;
    exclusions[18][6] = false;
    exclusions[18][7] = false;
    exclusions[18][8] = false;
    exclusions[18][9] = true;
    exclusions[18][10] = true;
    exclusions[18][11] = true;
    exclusions[18][12] = true;
    exclusions[18][13] = true;
    exclusions[18][14] = true;
    exclusions[18][15] = false;
    exclusions[18][16] = false;
    exclusions[18][17] = false;
    exclusions[18][18] = true;
    exclusions[18][19] = true;
    exclusions[18][20] = true;
    exclusions[18][21] = true;
    exclusions[18][22] = true;
    exclusions[18][23] = true;
    exclusions[18][24] = true;
    exclusions[19][0] = true;
    exclusions[19][1] = true;
    exclusions[19][2] = true;
    exclusions[19][3] = true;
    exclusions[19][4] = true;
    exclusions[19][5] = true;
    exclusions[19][6] = false;
    exclusions[19][7] = false;
    exclusions[19][8] = false;
    exclusions[19][9] = true;
    exclusions[19][10] = true;
    exclusions[19][11] = true;
    exclusions[19][12] = true;
    exclusions[19][13] = true;
    exclusions[19][14] = true;
    exclusions[19][15] = true;
    exclusions[19][16] = false;
    exclusions[19][17] = false;
    exclusions[19][18] = true;
    exclusions[19][19] = true;
    exclusions[19][20] = true;
    exclusions[19][21] = true;
    exclusions[19][22] = true;
    exclusions[19][23] = true;
    exclusions[19][24] = true;
    exclusions[20][0] = true;
    exclusions[20][1] = true;
    exclusions[20][2] = true;
    exclusions[20][3] = true;
    exclusions[20][4] = true;
    exclusions[20][5] = true;
    exclusions[20][6] = false;
    exclusions[20][7] = false;
    exclusions[20][8] = false;
    exclusions[20][9] = true;
    exclusions[20][10] = true;
    exclusions[20][11] = true;
    exclusions[20][12] = true;
    exclusions[20][13] = true;
    exclusions[20][14] = true;
    exclusions[20][15] = true;
    exclusions[20][16] = false;
    exclusions[20][17] = false;
    exclusions[20][18] = true;
    exclusions[20][19] = true;
    exclusions[20][20] = true;
    exclusions[20][21] = true;
    exclusions[20][22] = true;
    exclusions[20][23] = true;
    exclusions[20][24] = true;
    exclusions[21][0] = true;
    exclusions[21][1] = true;
    exclusions[21][2] = true;
    exclusions[21][3] = true;
    exclusions[21][4] = true;
    exclusions[21][5] = true;
    exclusions[21][6] = false;
    exclusions[21][7] = false;
    exclusions[21][8] = false;
    exclusions[21][9] = true;
    exclusions[21][10] = true;
    exclusions[21][11] = true;
    exclusions[21][12] = true;
    exclusions[21][13] = true;
    exclusions[21][14] = true;
    exclusions[21][15] = true;
    exclusions[21][16] = false;
    exclusions[21][17] = false;
    exclusions[21][18] = true;
    exclusions[21][19] = true;
    exclusions[21][20] = true;
    exclusions[21][21] = true;
    exclusions[21][22] = true;
    exclusions[21][23] = true;
    exclusions[21][24] = true;
    exclusions[22][0] = true;
    exclusions[22][1] = true;
    exclusions[22][2] = true;
    exclusions[22][3] = true;
    exclusions[22][4] = true;
    exclusions[22][5] = true;
    exclusions[22][6] = false;
    exclusions[22][7] = false;
    exclusions[22][8] = false;
    exclusions[22][9] = true;
    exclusions[22][10] = true;
    exclusions[22][11] = true;
    exclusions[22][12] = true;
    exclusions[22][13] = true;
    exclusions[22][14] = true;
    exclusions[22][15] = true;
    exclusions[22][16] = false;
    exclusions[22][17] = false;
    exclusions[22][18] = true;
    exclusions[22][19] = true;
    exclusions[22][20] = true;
    exclusions[22][21] = true;
    exclusions[22][22] = true;
    exclusions[22][23] = true;
    exclusions[22][24] = true;
    exclusions[23][0] = true;
    exclusions[23][1] = true;
    exclusions[23][2] = true;
    exclusions[23][3] = true;
    exclusions[23][4] = true;
    exclusions[23][5] = true;
    exclusions[23][6] = true;
    exclusions[23][7] = false;
    exclusions[23][8] = true;
    exclusions[23][9] = true;
    exclusions[23][10] = true;
    exclusions[23][11] = true;
    exclusions[23][12] = true;
    exclusions[23][13] = true;
    exclusions[23][14] = true;
    exclusions[23][15] = true;
    exclusions[23][16] = true;
    exclusions[23][17] = false;
    exclusions[23][18] = true;
    exclusions[23][19] = true;
    exclusions[23][20] = true;
    exclusions[23][21] = true;
    exclusions[23][22] = true;
    exclusions[23][23] = true;
    exclusions[23][24] = true;

    for (int j = 0; j < 25; ++j) {
      for (int i = 0; i < 24; ++i) {
        if (!exclusions[i][j]) {
          System.out.print("O");
        }
        else {
          System.out.print(" ");
        }
      }
      System.out.print("\n");
    }


    StringBuilder variables = new StringBuilder();
    variables.append("(:objects\n");
    for (int i = 0; i < 24; ++i) {
      for (int j = 0; j < 25; ++j) {
        if (!exclusions[i][j]) {
          variables.append("sq-").append(i).append("-").append(j).append("\n");
        }
//        System.out.println("exclusions[" + i + "][" + j + "]");

      }
    }
    for (AICard card : AICard.getRooms()) {
      variables.append(card.name()).append("\n");
    }
    variables.append(" - square\nleft right up down - direction)\n");
    System.out.print(variables.toString());

    StringBuilder init = new StringBuilder();
    init.append(" (:init\n");
    for (int i = 0; i < 24; ++i) {
      for (int j = 0; j < 25; ++j) {
        if (!exclusions[i][j]) {
          if (i > 0 && !exclusions[i-1][j]) {
            init.append("(adj sq-").append(i).append("-").append(j).append(" left sq-").append(i - 1).append("-").append(j).append(")\n");
          }
          if (i < 23 && !exclusions[i+1][j]) {
            init.append("(adj sq-").append(i).append("-").append(j).append(" right sq-").append(i + 1).append("-").append(j).append(")\n");
          }
          if (j > 0 && !exclusions[i][j-1]) {
            init.append("(adj sq-").append(i).append("-").append(j).append(" down sq-").append(i).append("-").append(j - 1).append(")\n");
          }
          if (j < 24 && !exclusions[i][j+1]) {
            init.append("(adj sq-").append(i).append("-").append(j).append(" up sq-").append(i).append("-").append(j + 1).append(")\n");
          }
        }
      }
    }
    init.append("" +
        "(adj STUDY down sq-6-4)\n" +
        "(adj sq-6-4 up STUDY)\n" +
        "(adj HALL left sq-8-4)\n" +
        "(adj sq-8-4 right HALL)\n" +
        "(adj HALL down sq-11-7)\n" +
        "(adj HALL down sq-12-7)\n" +
        "(adj sq-11-7 up HALL)\n" +
        "(adj sq-12-7 up HALL)\n" +
        "(adj LOUNGE down sq-17-6)\n" +
        "(adj sq-17-6 up LOUNGE)\n" +
        "(adj LIBRARY right sq-7-8)\n" +
        "(adj sq-7-8 left LIBRARY)\n" +
        "(adj LIBRARY down sq-3-11)\n" +
        "(adj sq-3-11 up LIBRARY)\n" +
        "(adj DINING_ROOM up sq-17-8)\n" +
        "(adj sq-17-8 down DINING_ROOM)\n" +
        "(adj DINING_ROOM left sq-15-12)\n" +
        "(adj sq-15-12 right DINING_ROOM)\n" +
        "(adj BILLIARD_ROOM up sq-1-11)\n" +
        "(adj sq-1-11 down BILLIARD_ROOM)\n" +
        "(adj CONSERVATORY right sq-5-17)\n" +
        "(adj sq-5-17 left CONSERVATORY)\n" +
        "(adj BALLROOM left sq-7-17)\n" +
        "(adj sq-7-17 right BALLROOM)\n" +
        "(adj BALLROOM right sq-16-17)\n" +
        "(adj sq-16-17 left BALLROOM)\n" +
        "(adj BALLROOM up sq-9-16)\n" +
        "(adj BALLROOM up sq-14-16)\n" +
        "(adj sq-9-16 down BALLROOM)\n" +
        "(adj sq-14-16 down BALLROOM)\n" +
        "(adj KITCHEN up sq-19-17)\n" +
        "(adj sq-19-17 down KITCHEN)\n");
    System.out.print(init.toString());
  }

  public static void main(String[] args) {
    List<Plan> plans = Planner.plan(new Point(7, 16), null, Arrays.asList(AICard.KITCHEN, AICard.DINING_ROOM));
    for (Plan plan : plans) {
      System.out.println("Plan(" + plan.size() + ") to get to: " + plan.getDestination().name());
      for (Step step : plan.getSteps()) {
        System.out.println(step.toString());
      }
    }
  }

  public static void main_planningonly(String[] args) {

    long startTime = System.currentTimeMillis();
    ByteArrayOutputStream domainWriteStream = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(domainWriteStream);
    ps.println(Planner.DOMAIN_PDDL);
    ps.flush();
    PDDL21parser domainParser = new PDDL21parser(new ByteArrayInputStream(domainWriteStream.toByteArray()));
    boolean domainParsed = false;
    try {
      domainParsed = domainParser.parseDomain();
    }
    catch (ParseException e) {
      e.printStackTrace();
    }
    System.out.println("Domain parsed: " + domainParsed);

    ByteArrayOutputStream problemOutputStream = new ByteArrayOutputStream();
    ps = new PrintStream(problemOutputStream);
    ps.print(Planner.getProblem(null, AICard.STUDY, AICard.KITCHEN));
    ps.flush();
    ByteArrayInputStream problemInputStream = new ByteArrayInputStream(problemOutputStream.toByteArray());
    PDDL21parser problemParser = new PDDL21parser(problemInputStream);
    boolean problemParsed = false;
    try {
      problemParsed = problemParser.parseProblem();
    }
    catch (ParseException e) {
      e.printStackTrace();
    }
    System.out.println("Problem parsed: " + problemParsed);
    if (!problemParsed) {
      System.exit(1);
    }
    UngroundProblem unground = PDDL21parser.getUngroundProblem();

    Planner.getPlan(startTime);

    PDDL21parser.reset();
    try {
      new PDDL21parser(new ByteArrayInputStream(domainWriteStream.toByteArray())).parseDomain();
    }
    catch (ParseException e) {
      e.printStackTrace();
    }
    problemOutputStream = new ByteArrayOutputStream();
    ps = new PrintStream(problemOutputStream);
    ps.print(Planner.getProblem(new Point(16, 0), null, AICard.KITCHEN));
        ps.flush();
    problemInputStream = new ByteArrayInputStream(problemOutputStream.toByteArray());
    problemParser = new PDDL21parser(problemInputStream);
    problemParsed = false;
    try {
      problemParsed = problemParser.parseProblem();
    }
    catch (ParseException e) {
      e.printStackTrace();
    }
    System.out.println("Second Problem parsed: " + problemParsed);
    if (!problemParsed) {
      System.exit(1);
    }
    unground = PDDL21parser.getUngroundProblem();

    TotalOrderPlan top = Planner.getPlan(startTime);


  }

  public static void main_beforeMovement(String[] args) {
    ClueSolver solver = new ClueSolver(new Point(16, 0), Arrays.asList(AICard.HALL, AICard.GREEN, AICard.BILLIARD_ROOM, AICard.KITCHEN, AICard.WRENCH));
    solver.printSuggestions();
    AICard room = solver.chooseRoom(AICard.CONSERVATORY);
    System.out.println("Room: " + (room == null ? null : room.name()));
    AICard[] suggestion = solver.getSuggestion(room);
    System.out.println("Suggest: " + suggestion[0].name() + " with the " + suggestion[1].name());
    solver.showCard(AICard.CONSERVATORY);
    room = solver.chooseRoom(AICard.LOUNGE);
    System.out.println("Room: " + (room == null ? null : room.name()));
    suggestion = solver.getSuggestion(room);
    System.out.println("Suggest: " + suggestion[0].name() + " with the " + suggestion[1].name());
    solver.showCard(AICard.LOUNGE);
    room = solver.chooseRoom(AICard.CONSERVATORY);
    System.out.println("Room: " + (room == null ? null : room.name()));
    room = solver.chooseRoom(AICard.DINING_ROOM);
    System.out.println("Room: " + (room == null ? null : room.name()));
    suggestion = solver.getSuggestion(room);
    System.out.println("Suggest: " + suggestion[0].name() + " with the " + suggestion[1].name());
//    solver.showNoProofSuggestion(room, suggestion[0], suggestion[1]);
//    solver.printSuggestions();
    solver.showCard(AICard.DINING_ROOM);
    room = solver.chooseRoom(AICard.HALL, AICard.BALLROOM, AICard.LOUNGE);
    System.out.println("Room: " + (room == null ? null : room.name()));
    suggestion = solver.getSuggestion(room);
    System.out.println("Suggest: " + suggestion[0].name() + " with the " + suggestion[1].name());
    solver.showCard(AICard.LEAD_PIPE);
    room = solver.chooseRoom(AICard.STUDY, AICard.LIBRARY, AICard.LOUNGE, AICard.DINING_ROOM);
    System.out.println("Room: " + (room == null ? null : room.name()));

    suggestion = solver.getSuggestion(room);
    System.out.println("Suggest: " + suggestion[0].name() + " with the " + suggestion[1].name());

    solver.showCard(AICard.STUDY);
    room = solver.chooseRoom(AICard.KITCHEN);
    System.out.println("Room: " + (room == null ? null : room.name()));

    suggestion = solver.getSuggestion(room);
    System.out.println("Suggest: " + suggestion[0].name() + " with the " + suggestion[1].name());
    solver.showCard(AICard.REVOLVER);

    room = solver.chooseRoom(AICard.STUDY);
    System.out.println("Room: " + (room == null ? null : room.name()));
    List<AICard> rooms = solver.chooseRoomDirection(AICard.BALLROOM, AICard.DINING_ROOM, AICard.CONSERVATORY, AICard.LOUNGE, AICard.BILLIARD_ROOM, AICard.HALL, AICard.LIBRARY, AICard.STUDY);
    printRooms(rooms);

    room = solver.chooseRoom(AICard.BALLROOM);
    System.out.println("Room: " + (room == null ? null : room.name()));
    suggestion = solver.getSuggestion(room);
    System.out.println("Suggest: " + suggestion[0].name() + " with the " + suggestion[1].name());
    solver.showCard(AICard.BALLROOM);

    room = solver.chooseRoom(AICard.CONSERVATORY, AICard.BILLIARD_ROOM, AICard.KITCHEN);
    System.out.println("Room: " + (room == null ? null : room.name()));
    suggestion = solver.getSuggestion(room);
    System.out.println("Suggest: " + suggestion[0].name() + " with the " + suggestion[1].name());
    solver.showCard(AICard.PEACOCK);

    room = solver.chooseRoom(AICard.CONSERVATORY, AICard.LIBRARY, AICard.BALLROOM);
    System.out.println("Room: " + (room == null ? null : room.name()));
    suggestion = solver.getSuggestion(room);
    System.out.println("Suggest: " + suggestion[0].name() + " with the " + suggestion[1].name());
//    solver.showCard(AICard.PEACOCK);
    solver.showNoProofSuggestion(room, suggestion[0], suggestion[1]);
//    solver.printSuggestions();
    AICard[] accusation = solver.getAccusation();
    System.out.println("Accusation: " + (accusation == null ? null : accusation[1].name() + " did it in the " + accusation[0].name() + " with the " + accusation[2].name()));
  }

  public static void main4(String[] args) {
    ClueSolver solver = new ClueSolver(new Point(16, 0), Arrays.asList(AICard.HALL, AICard.GREEN, AICard.BILLIARD_ROOM, AICard.KITCHEN, AICard.WRENCH));
    solver.printSuggestions();
    AICard room = solver.chooseRoom(AICard.CONSERVATORY);
    System.out.println("Room: " + (room == null ? null : room.name()));
    AICard[] suggestion = solver.getSuggestion(room);
    System.out.println("Suggest: " + suggestion[0].name() + " with the " + suggestion[1].name());
    solver.showCard(AICard.CONSERVATORY);
    room = solver.chooseRoom(AICard.LOUNGE);
    System.out.println("Room: " + (room == null ? null : room.name()));
    suggestion = solver.getSuggestion(room);
    System.out.println("Suggest: " + suggestion[0].name() + " with the " + suggestion[1].name());
    solver.showCard(AICard.LOUNGE);
    room = solver.chooseRoom(AICard.CONSERVATORY);
    System.out.println("Room: " + (room == null ? null : room.name()));
    room = solver.chooseRoom(AICard.DINING_ROOM);
    System.out.println("Room: " + (room == null ? null : room.name()));
    suggestion = solver.getSuggestion(room);
    System.out.println("Suggest: " + suggestion[0].name() + " with the " + suggestion[1].name());
//    solver.showNoProofSuggestion(room, suggestion[0], suggestion[1]);
//    solver.printSuggestions();
    solver.showCard(AICard.DINING_ROOM);
    room = solver.chooseRoom(AICard.HALL, AICard.BALLROOM, AICard.LOUNGE);
    System.out.println("Room: " + (room == null ? null : room.name()));
    suggestion = solver.getSuggestion(room);
    System.out.println("Suggest: " + suggestion[0].name() + " with the " + suggestion[1].name());
    solver.showCard(AICard.LEAD_PIPE);
    room = solver.chooseRoom(AICard.STUDY, AICard.LIBRARY, AICard.LOUNGE, AICard.DINING_ROOM);
    System.out.println("Room: " + (room == null ? null : room.name()));

    suggestion = solver.getSuggestion(room);
    System.out.println("Suggest: " + suggestion[0].name() + " with the " + suggestion[1].name());

    solver.showCard(AICard.STUDY);
    room = solver.chooseRoom(AICard.KITCHEN);
    System.out.println("Room: " + (room == null ? null : room.name()));

    suggestion = solver.getSuggestion(room);
    System.out.println("Suggest: " + suggestion[0].name() + " with the " + suggestion[1].name());
    solver.showCard(AICard.REVOLVER);

    room = solver.chooseRoom(AICard.STUDY);
    System.out.println("Room: " + (room == null ? null : room.name()));
    List<AICard> rooms = solver.chooseRoomDirection(AICard.BALLROOM, AICard.DINING_ROOM, AICard.CONSERVATORY, AICard.LOUNGE, AICard.BILLIARD_ROOM, AICard.HALL, AICard.LIBRARY, AICard.STUDY);
    printRooms(rooms);

    room = solver.chooseRoom(AICard.BALLROOM);
    System.out.println("Room: " + (room == null ? null : room.name()));
    suggestion = solver.getSuggestion(room);
    System.out.println("Suggest: " + suggestion[0].name() + " with the " + suggestion[1].name());
    solver.showCard(AICard.BALLROOM);

    room = solver.chooseRoom(AICard.CONSERVATORY, AICard.BILLIARD_ROOM, AICard.KITCHEN);
    System.out.println("Room: " + (room == null ? null : room.name()));
    suggestion = solver.getSuggestion(room);
    System.out.println("Suggest: " + suggestion[0].name() + " with the " + suggestion[1].name());
    solver.showCard(AICard.PEACOCK);

    room = solver.chooseRoom(AICard.CONSERVATORY, AICard.LIBRARY, AICard.BALLROOM);
    System.out.println("Room: " + (room == null ? null : room.name()));
    suggestion = solver.getSuggestion(room);
    System.out.println("Suggest: " + suggestion[0].name() + " with the " + suggestion[1].name());
//    solver.showCard(AICard.PEACOCK);
    solver.showNoProofSuggestion(room, suggestion[0], suggestion[1]);
//    solver.printSuggestions();
    AICard[] accusation = solver.getAccusation();
    System.out.println("Accusation: " + (accusation == null ? null : accusation[1].name() + " did it in the " + accusation[0].name() + " with the " + accusation[2].name()));
  }

  private static void printRooms(List<AICard> rooms) {
    System.out.print("Rooms: ");
    for (AICard each : rooms) {
      System.out.print(each.name() + ", ");
    }
    System.out.print("\n");
  }

  @SuppressWarnings("UnusedDeclaration")
  public static void main3(String[] args) {
    ClueSolver solver = new ClueSolver(new Point(16, 0), Arrays.asList(AICard.LOUNGE, AICard.HALL, AICard.BALLROOM, AICard.KITCHEN, AICard.PEACOCK));
    solver.printSuggestions();
    AICard room = solver.chooseRoom(AICard.LOUNGE, AICard.DINING_ROOM);
    System.out.println("Room: " + (room == null ? null : room.name()));
    AICard[] suggestion = solver.getSuggestion(room);
    System.out.println("Suggest: " + suggestion[0].name() + " with the " + suggestion[1].name());
    solver.showCard(AICard.WRENCH);
    room = solver.chooseRoom(AICard.CONSERVATORY);
    System.out.println("Room: " + (room == null ? null : room.name()));
    suggestion = solver.getSuggestion(room);
    System.out.println("Suggest: " + suggestion[0].name() + " with the " + suggestion[1].name());
    solver.showCard(AICard.CONSERVATORY);
    room = solver.chooseRoom(AICard.LOUNGE);
    System.out.println("Room: " + (room == null ? null : room.name()));
    suggestion = solver.getSuggestion(room);
    System.out.println("Suggest: " + suggestion[0].name() + " with the " + suggestion[1].name());
    solver.showNoProofSuggestion(room, suggestion[0], suggestion[1]);
    solver.printSuggestions();
    room = solver.chooseRoom(AICard.CONSERVATORY);
    System.out.println("Room: " + (room == null ? null : room.name()));
    room = solver.chooseRoom(AICard.DINING_ROOM);
    System.out.println("Room: " + (room == null ? null : room.name()));
    suggestion = solver.getSuggestion(room);
    System.out.println("Suggest: " + suggestion[0].name() + " with the " + suggestion[1].name());
    solver.showCard(AICard.DINING_ROOM);

    room = solver.chooseRoom(AICard.LOUNGE, AICard.HALL, AICard.STUDY, AICard.LIBRARY, AICard.BILLIARD_ROOM, AICard.CONSERVATORY, AICard.BALLROOM, AICard.KITCHEN);
    System.out.println("Room: " + (room == null ? null : room.name()));

    suggestion = solver.getSuggestion(room);
    System.out.println("Suggest: " + suggestion[0].name() + " with the " + suggestion[1].name());

    solver.showNoProofSuggestion(room, suggestion[0], suggestion[1]);
    solver.printSuggestions();
    AICard[] accusation = solver.getAccusation();
    System.out.println("Accusation: " + (accusation == null ? null : accusation[1].name() + " did it in the " + accusation[0].name() + " with the " + accusation[2].name()));
  }

  @SuppressWarnings("UnusedDeclaration")
  public static void main2(String[] args) {
    int[] rooms = new int[]{study, hall, lounge, library, diningRoom, billiardRoom, conservatory, ballroom, kitchen};
    int[] suspects = new int[]{scarlet, mustard, white, green, peacock, plum};
    int[] weapons = new int[]{rope, leadPipe, knife, wrench, candlestick, revolver};
    String[] roomsString = new String[]{"study", "hall", "lounge", "library", "diningRoom", "billiardRoom", "conservatory", "ballroom", "kitchen"};
    String[] suspectsString = new String[]{"scarlet", "mustard", "white", "green", "peacock", "plum"};
    String[] weaponsString = new String[]{"rope", "leadPipe", "knife", "wrench", "candlestick", "revolver"};
    List<Integer> roomConditions = buildConditions(bdd, rooms);
    List<Integer> suspectConditions = buildConditions(bdd, suspects);
    List<Integer> weaponConditions = buildConditions(bdd, weapons);

    int[] roomCards = new int[]{hall, lounge, study};
    int[] suspectCards = new int[]{mustard};
    int[] weaponCards = new int[]{knife};

    int roomKB = buildInitialKB(bdd, roomConditions, roomCards);
    int suspectKB = buildInitialKB(bdd, suspectConditions, suspectCards);
    int weaponKB = buildInitialKB(bdd, weaponConditions, weaponCards);

    List<Integer> possibleRooms = new ArrayList<Integer>(rooms.length);
    List<Integer> possibleSuspects = new ArrayList<Integer>(suspects.length);
    List<Integer> possibleWeapons = new ArrayList<Integer>(weapons.length);
    printSuggestions(bdd, rooms, roomsString, suspects, suspectsString, weapons, weaponsString, roomKB, suspectKB, weaponKB, possibleRooms, possibleSuspects, possibleWeapons);

    System.out.println("Chosen suspect: " + getPossibility(suspectsString, possibleSuspects));

    suspectKB = bdd.ref(bdd.and(suspectKB, bdd.not(white)));
    printSuggestions(bdd, rooms, roomsString, suspects, suspectsString, weapons, weaponsString, roomKB, suspectKB, weaponKB, possibleRooms, possibleSuspects, possibleWeapons);

    roomKB = bdd.ref(bdd.and(roomKB, bdd.not(conservatory)));
    printSuggestions(bdd, rooms, roomsString, suspects, suspectsString, weapons, weaponsString, roomKB, suspectKB, weaponKB, possibleRooms, possibleSuspects, possibleWeapons);

    System.out.println("Chosen suspect: " + getPossibility(suspectsString, possibleSuspects));

    suspectKB = bdd.ref(bdd.and(suspectKB, bdd.not(peacock)));
    printSuggestions(bdd, rooms, roomsString, suspects, suspectsString, weapons, weaponsString, roomKB, suspectKB, weaponKB, possibleRooms, possibleSuspects, possibleWeapons);

    System.out.println("Chosen suspect: " + getPossibility(suspectsString, possibleSuspects));

    suspectKB = bdd.ref(bdd.and(suspectKB, bdd.not(green)));
    printSuggestions(bdd, rooms, roomsString, suspects, suspectsString, weapons, weaponsString, roomKB, suspectKB, weaponKB, possibleRooms, possibleSuspects, possibleWeapons);

    System.out.println("Chosen suspect: " + getPossibility(suspectsString, possibleSuspects));

    suspectKB = bdd.ref(bdd.and(suspectKB, scarlet));
    printSuggestions(bdd, rooms, roomsString, suspects, suspectsString, weapons, weaponsString, roomKB, suspectKB, weaponKB, possibleRooms, possibleSuspects, possibleWeapons);

    System.out.println("Chosen suspect: " + getPossibility(suspectsString, possibleSuspects));

    roomKB = bdd.ref(bdd.and(roomKB, bdd.not(kitchen)));
    printSuggestions(bdd, rooms, roomsString, suspects, suspectsString, weapons, weaponsString, roomKB, suspectKB, weaponKB, possibleRooms, possibleSuspects, possibleWeapons);

    System.out.println("Chosen weapon: " + getPossibility(weaponsString, possibleWeapons));

    weaponKB = bdd.ref(bdd.and(weaponKB, bdd.not(wrench)));
    printSuggestions(bdd, rooms, roomsString, suspects, suspectsString, weapons, weaponsString, roomKB, suspectKB, weaponKB, possibleRooms, possibleSuspects, possibleWeapons);

    System.out.println("Chosen weapon: " + getPossibility(weaponsString, possibleWeapons));

    weaponKB = bdd.ref(bdd.and(weaponKB, bdd.not(candlestick)));
    printSuggestions(bdd, rooms, roomsString, suspects, suspectsString, weapons, weaponsString, roomKB, suspectKB, weaponKB, possibleRooms, possibleSuspects, possibleWeapons);

    System.out.println("Chosen weapon: " + getPossibility(weaponsString, possibleWeapons));

    weaponKB = bdd.ref(bdd.and(weaponKB, leadPipe));
    printSuggestions(bdd, rooms, roomsString, suspects, suspectsString, weapons, weaponsString, roomKB, suspectKB, weaponKB, possibleRooms, possibleSuspects, possibleWeapons);

    System.out.println("Chosen weapon: " + getPossibility(weaponsString, possibleWeapons));

    roomKB = bdd.ref(bdd.and(roomKB, bdd.not(library)));
    printSuggestions(bdd, rooms, roomsString, suspects, suspectsString, weapons, weaponsString, roomKB, suspectKB, weaponKB, possibleRooms, possibleSuspects, possibleWeapons);

    roomKB = bdd.ref(bdd.and(roomKB, bdd.not(billiardRoom)));
    printSuggestions(bdd, rooms, roomsString, suspects, suspectsString, weapons, weaponsString, roomKB, suspectKB, weaponKB, possibleRooms, possibleSuspects, possibleWeapons);

    roomKB = bdd.ref(bdd.and(roomKB, bdd.not(ballroom)));
    printSuggestions(bdd, rooms, roomsString, suspects, suspectsString, weapons, weaponsString, roomKB, suspectKB, weaponKB, possibleRooms, possibleSuspects, possibleWeapons);

  }

  private static List<Integer> buildConditions(BDD myCards, int[] rooms) {
    List<Integer> roomConditions = new ArrayList<Integer>();
    for (int i = 0; i < rooms.length; ++i) {
      int notJ = myCards.getOne();
      for (int j = 0; j < rooms.length; ++j) {
        if (i == j) {
          continue;
        }
        notJ = myCards.and(notJ, myCards.not(rooms[j]));
      }
      roomConditions.add(myCards.ref(myCards.biimp(rooms[i], notJ)));
    }
    return roomConditions;
  }

  private static int buildInitialKB(BDD myCards, List<Integer> roomConditions, int[] roomCards) {
    int roomKB = myCards.getOne();
    for (int room : roomCards) {
      roomKB = myCards.and(roomKB, myCards.not(room));
    }
    for (int condition : roomConditions) {
      roomKB = myCards.and(roomKB, condition);
    }
    return roomKB;
  }

  private static void printSuggestions(BDD myCards, int[] rooms, String[] roomsString, int[] suspects, String[] suspectsString, int[] weapons, String[] weaponsString, int roomKB, int suspectKB, int weaponKB, List<Integer> possibleRooms, List<Integer> possibleSuspects, List<Integer> possibleWeapons) {
    System.out.println("roomKB: " + roomKB);
    System.out.println("suspectKB: " + suspectKB);
    System.out.println("weaponKB: " + weaponKB);
    possibleRooms.clear();
    possibleSuspects.clear();
    possibleWeapons.clear();
    for (int i = 0; i < rooms.length; ++i) {
      int roomState = myCards.and(roomKB, rooms[i]);
//      System.out.println("roomKB and " + roomsString[i] + ": " + roomState);
      if (roomState != 0) {
        possibleRooms.add(i);
      }
    }
    for (int i = 0; i < suspects.length; ++i) {
      int suspectState = myCards.and(suspectKB, suspects[i]);
//      System.out.println("suspectKB and " + suspectsString[i] + ": " + suspectState);
      if (suspectState != 0) {
        possibleSuspects.add(i);
      }
    }
    for (int i = 0; i < weapons.length; ++i) {
      int weaponState = myCards.and(weaponKB, weapons[i]);
//      System.out.println("weaponKB and " + weaponsString[i] + ": " + weaponState);
      if (weaponState != 0) {
        possibleWeapons.add(i);
      }
    }

    System.out.println("Possible rooms:");
    for (int i : possibleRooms) {
      System.out.println(roomsString[i]);
    }
    System.out.println("Possible suspects:");
    for (int i : possibleSuspects) {
      System.out.println(suspectsString[i]);
    }
    System.out.println("Possible weapons:");
    for (int i : possibleWeapons) {
      System.out.println(weaponsString[i]);
    }
  }

  private static String getPossibility(String[] strings, List<Integer> indexes) {
    return strings[indexes.get(random.nextInt(indexes.size()))];
  }
}
