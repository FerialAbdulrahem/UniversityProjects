package controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import engine.Game;
import engine.board.Cell;
import engine.board.CellType;
import engine.board.SafeZone;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import model.Colour;
import model.card.Card;
import model.card.Deck;
import model.card.standard.Standard;
import model.card.standard.Suit;
import model.player.Marble;
import model.player.Player;

public class Main extends Application implements EventHandler<ActionEvent> {

    private AnchorPane an;
    private Scene playerNameScene;
    private Label nameLabel;
    private TextField nameField;
    private Button startGame;

    // ─── Round tracking ───────────────────────────────────────────────────────────
    private static int uiRoundCounter = 0;
    private static int lastHandSize   = 4;
    // ─── Skip tracking for Ten/Queen discard effect ───────────────────────────────
    private static final java.util.Set<Colour> skippedPlayers = new java.util.HashSet<>();
    // ─── Inline info panel reference ─────────────────────────────────────────────
    private static MainScene activeMs = null;

    // ─── showInfo: display non-error messages inline instead of popup ─────────────
    private static void showInfo(String title, String message) {
        if (activeMs != null && activeMs.miscLabel != null) {
            Platform.runLater(() ->
                activeMs.miscLabel.setText("ℹ " + title + "\n" + message)
            );
        }
    }

    // ─── start ────────────────────────────────────────────────────────────────────

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Jackaroo");
        an = new AnchorPane();
        playerNameScene = new Scene(an, 400, 250);
        primaryStage.setScene(playerNameScene);
        primaryStage.show();

        an.setStyle("-fx-background-color: #f0f0f0;");

        Label welcomeLabel = new Label("Welcome to Jackaroo!");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        addControl(welcomeLabel, 30, 20, 340, 40);

        nameLabel = new Label("Enter Your Name:");
        nameLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        addControl(nameLabel, 30, 80, 150, 30);

        nameField = new TextField();
        nameField.setPromptText("Your name here");
        addControl(nameField, 30, 110, 320, 30);

        startGame = new Button("Start Game");
        startGame.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px;");
        startGame.setOnAction(this);
        addControl(startGame, 30, 160, 320, 40);

        nameField.setOnAction(e -> startGame.fire());

        startGame.setOnMouseClicked(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                String playerName = nameField.getText().trim();
                if (playerName.isEmpty()) {
                    displayAlert2("Error", "Please enter your name to start the game.");
                    return;
                }

                Game game;
                try {
                    game = new Game(playerName);
                } catch (IOException e1) {
                    displayAlert2("Error", "Failed to load cards: " + e1.getMessage());
                    return;
                }

                MainScene ms = new MainScene();
                activeMs = ms;
                Scene scene2 = ms.createGameScene();

                // Link safe zone cells
                for (int i = 0; i < ms.c.getSafeCellPairs().size(); i++) {
                    int playerIdx = i / 4;
                    int cellIdx   = i % 4;
                    Cell engineCell = game.getBoard().getSafeZones().get(playerIdx).getCells().get(cellIdx);
                    ms.c.safeCellPairs.get(i).setCell(engineCell);
                }

                // Link track cells
                for (int i = 0; i < ms.c.circleCellPairs.size(); i++) {
                    ms.c.circleCellPairs.get(i).setCell(game.getBoard().getTrack().get(i));
                }

                // Resolve FX colours per player order
                Color[] fxColors = new Color[4];
                for (int i = 0; i < 4; i++) {
                    fxColors[i] = colourToFX(game.getPlayers().get(i).getColour());
                }
                Color humanFX = fxColors[0];

                // Paint home zone circles with player colours
                for (int p = 0; p < 4; p++) {
                    for (int m = 0; m < 4; m++) {
                        ms.c.homeCellPairs.get(p * 4 + m).getCircle().setFill(fxColors[p]);
                    }
                }

                // Initialize safe zone circles with player color (faded)
                for (int p = 0; p < 4; p++) {
                    Color playerFaded = fxColors[p].deriveColor(0, 0.5, 1.5, 0.35);
                    for (int c = 0; c < 4; c++) {
                        javafx.scene.shape.Circle circle = ms.c.safeCellPairs.get(p * 4 + c).getCircle();
                        circle.setFill(playerFaded);
                        circle.setStroke(Color.BLACK);
                        circle.setStrokeWidth(1.0);
                    }
                }

                // Colour markers
                ms.miscCircles[2].setFill(humanFX);
                ms.miscCircles[3].setFill(fxColors[1]);
                ms.miscCircles[0].setFill(fxColors[2]);
                ms.miscCircles[1].setFill(fxColors[3]);

                // Player name labels
                ms.humanLabel.setText(playerName);
                ms.cpu1Label.setText(game.getPlayers().get(1).getName());
                ms.cpu2Label.setText(game.getPlayers().get(2).getName());
                ms.cpu3Label.setText(game.getPlayers().get(3).getName());

                refreshCardPanels(ms, game.getPlayers().get(0));
                updateGameUI(ms, game, fxColors);

                ms.c.setPlayerColor(humanFX);
                ms.c.makeMarbleSelectable();

                syncBoardFromEngine(ms, game, fxColors);
                setupCardHandlers(ms, game, fxColors, humanFX);

                ms.playButton.setOnAction(e -> handleHumanTurn(ms, game, fxColors, humanFX));

                // Keyboard shortcuts (1-4 to field marbles)
                scene2.setOnKeyPressed(e -> {
                    int key = -1;
                    switch (e.getCode()) {
                        case DIGIT1: case NUMPAD1: key = 0; break;
                        case DIGIT2: case NUMPAD2: key = 1; break;
                        case DIGIT3: case NUMPAD3: key = 2; break;
                        case DIGIT4: case NUMPAD4: key = 3; break;
                        case ESCAPE:
                            ms.c.clearMarbleSelections();
                            ms.actionPanel.setVisible(false);
                            deselectAllCards(ms);
                            break;
                        default: break;
                    }
                    if (key >= 0) {
                        CircleGrid.CircleCellPair pair = ms.c.homeCellPairs.get(key);
                        if (pair.getCircle().getFill().equals(humanFX)) {
                            ms.c.clearMarbleSelections();
                            pair.getCircle().setStroke(Color.WHITE);
                            pair.getCircle().setStrokeWidth(3);
                            ms.c.getSelectedMarbles().add(pair);
                        }
                    }
                });

                primaryStage.setTitle("Jackaroo");
                primaryStage.setScene(scene2);
                primaryStage.setMaximized(true);
                primaryStage.setMinWidth(800);
                primaryStage.setMinHeight(600);

                primaryStage.setOnShown(we -> {
                    double w = primaryStage.getWidth(), h = primaryStage.getHeight();
                    ms.screenWidth.set(w); ms.screenHeight.set(h);
                    ms.updateLayout(); ms.updateAllComponents();
                    ms.c.updateDimensions(w, h);
                });
                primaryStage.widthProperty().addListener((ob, ov, nv) -> {
                    ms.screenWidth.set(nv.doubleValue());
                    ms.updateLayout(); ms.updateAllComponents();
                });
                primaryStage.heightProperty().addListener((ob, ov, nv) -> {
                    ms.screenHeight.set(nv.doubleValue());
                    ms.updateLayout(); ms.updateAllComponents();
                });
                primaryStage.show();
            }
        });
    }

    // ─── HUMAN TURN ───────────────────────────────────────────────────────────────

    private static void handleHumanTurn(MainScene ms, Game game, Color[] fxColors, Color humanFX) {
        Colour humanColour = game.getPlayers().get(0).getColour();
        if (skippedPlayers.contains(humanColour)) {
            skippedPlayers.remove(humanColour);
            // Advance turn WITHOUT removing another card (Ten/Queen already discarded one)
            try {
                game.endPlayerTurn();
            } catch (Exception ignored) {}
            syncBoardFromEngine(ms, game, fxColors);
            updateGameUI(ms, game, fxColors);
            refreshCardPanels(ms, game.getPlayers().get(0));
            ms.c.makeMarbleSelectable();
            showInfo("Turn Skipped", "Your turn was skipped! You can play next turn.");
            // DO NOT call handleCPUTurns — CPUs already played (or are playing) in the current round loop
            return;
        }

        int cardIdx = getSelectedCardIndex(ms);
        if (cardIdx == -1) {
            showInfo("No Card", "Please select a card to play.");
            return;
        }

        Player human = game.getPlayers().get(0);
        Card card = human.getHand().get(cardIdx);
        String cardName = card.getName().toUpperCase();

        ArrayList<CircleGrid.CircleCellPair> uiSelected = ms.c.getSelectedMarbles();
        ArrayList<Marble> engineMarbles = resolveEngineMarbles(ms, game, uiSelected);

        try {
            game.selectCard(card);

            if (cardName.contains("SEVEN") && engineMarbles.size() == 2) {
                game.editSplitDistance(ms.getSplitDistance());
            }

            for (Marble m : engineMarbles) {
                game.selectMarble(m);
            }

            int[] handsBefore = new int[4];
            for (int pi = 0; pi < 4; pi++) handsBefore[pi] = game.getPlayers().get(pi).getHand().size();
            Colour tenTarget = cardName.contains("TEN") ? game.getNextPlayerColour() : null;

            game.playPlayerTurn();
            String playedName = card.getName();
            game.endPlayerTurn();

            // Detect Ten/Queen skip target
            if (cardName.contains("TEN") && tenTarget != null) {
                skippedPlayers.add(tenTarget);
            } else if (cardName.contains("QUEEN")) {
                for (int pi = 0; pi < 4; pi++) {
                    if (game.getPlayers().get(pi).getHand().size() < handsBefore[pi]) {
                        skippedPlayers.add(game.getPlayers().get(pi).getColour());
                        break;
                    }
                }
            }

            syncBoardFromEngine(ms, game, fxColors);
            deselectAllCards(ms);
            ms.c.clearMarbleSelections();
            ms.actionPanel.setVisible(false);
            updateFirePit(ms, game, playedName);
            refreshCardPanels(ms, human);
            updateGameUI(ms, game, fxColors);
            ms.c.makeMarbleSelectable();
            showInfo("Played", playedName);

            Colour winner = game.checkWin();
            if (winner != null) {
                displayAlert2("Game Over!", winner + " wins the game!");
                return;
            }

            handleCPUTurns(ms, game, fxColors);

        } catch (Exception ex) {
            game.deselectAll();
            displayAlert2("Invalid Move", ex.getMessage() != null ? ex.getMessage() : ex.toString());
        }
    }

    // ─── CPU TURNS ────────────────────────────────────────────────────────────────

    private static void handleCPUTurns(MainScene ms, Game game, Color[] fxColors) {
        Thread t = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                try { Thread.sleep(1200); } catch (InterruptedException ex) { ex.printStackTrace(); }

                final int pi = i;
                Platform.runLater(() -> {
                    Player cpu = game.getPlayers().get(pi);

                    // ── Ten/Queen skip: advance turn WITHOUT discarding another card ──
                    if (skippedPlayers.contains(cpu.getColour())) {
                        skippedPlayers.remove(cpu.getColour());
                        try {
                            game.endPlayerTurn();
                        } catch (Exception ignored) {}
                        syncBoardFromEngine(ms, game, fxColors);
                        updateGameUI(ms, game, fxColors);
                        showInfo("Turn Skipped", cpu.getName() + "'s turn was skipped!");
                        return;
                    }

                    String playedName = "";
                    try {
                        // Force field if CPU has marbles at home and an Ace/King
                        if (!cpu.getMarbles().isEmpty()) {
                            Card fieldCard = null;
                            for (Card c : cpu.getHand()) {
                                String n = c.getName().toUpperCase();
                                if (n.contains("ACE") || n.contains("KING")) { fieldCard = c; break; }
                            }
                            if (fieldCard != null) {
                                try {
                                    game.selectCard(fieldCard);
                                    game.playPlayerTurn();
                                    playedName = fieldCard.getName();
                                    game.endPlayerTurn();
                                    syncBoardFromEngine(ms, game, fxColors);
                                    updateFirePit(ms, game, playedName);
                                    updateGameUI(ms, game, fxColors);
                                    showInfo(cpu.getName() + " played", playedName);
                                    return;
                                } catch (Exception ex) {
                                    game.deselectAll();
                                }
                            }
                        }

                        // Normal CPU play — snapshot hands to detect Ten/Queen skip
                        int[] handsBeforeCPU = new int[4];
                        for (int hp = 0; hp < 4; hp++) handsBeforeCPU[hp] = game.getPlayers().get(hp).getHand().size();
                        Colour tenTargetCPU = null;

                        game.playPlayerTurn();
                        Card played = cpu.getSelectedCard();
                        if (played != null) {
                            playedName = played.getName();
                            String pn = playedName.toUpperCase();
                            if (pn.contains("TEN")) {
                                tenTargetCPU = game.getPlayers().get((pi + 1) % 4).getColour();
                            }
                        }
                        game.endPlayerTurn();

                        // Register skip targets
                        if (played != null) {
                            String pn = playedName.toUpperCase();
                            if (pn.contains("TEN") && tenTargetCPU != null) {
                                skippedPlayers.add(tenTargetCPU);
                            } else if (pn.contains("QUEEN")) {
                                for (int hp = 0; hp < 4; hp++) {
                                    if (game.getPlayers().get(hp).getHand().size() < handsBeforeCPU[hp]) {
                                        skippedPlayers.add(game.getPlayers().get(hp).getColour());
                                        break;
                                    }
                                }
                            }
                        }

                    } catch (Exception ex) {
                        // CPU can't play — discard first card, no skip for next
                        try {
                            if (cpu.getSelectedCard() == null && !cpu.getHand().isEmpty()) {
                                try { game.selectCard(cpu.getHand().get(0)); } catch (Exception ignored) {}
                            }
                            game.endPlayerTurn();
                        } catch (Exception e2) { /* ignore */ }
                    }

                    syncBoardFromEngine(ms, game, fxColors);
                    if (!playedName.isEmpty()) {
                        updateFirePit(ms, game, playedName);
                        showInfo(cpu.getName() + " played", playedName);
                    }
                    updateGameUI(ms, game, fxColors);

                    Colour winner = game.checkWin();
                    if (winner != null) displayAlert2("Game Over!", winner + " wins!");
                });

                try { Thread.sleep(400); } catch (InterruptedException ex) { ex.printStackTrace(); }
            }

            Platform.runLater(() -> {
                Player human = game.getPlayers().get(0);
                int currentHandSize = human.getHand().size();
                if (currentHandSize == 4 && lastHandSize < 4) {
                    uiRoundCounter++;
                    if (uiRoundCounter % 4 == 0) {
                        updateFirePit(ms, game, "🃏 New Cards");
                        showInfo("New Cards Dealt!", "Everyone has received a fresh hand of cards!");
                    }
                }
                lastHandSize = currentHandSize;
                refreshCardPanels(ms, human);
                ms.c.makeMarbleSelectable();
                showInfo("Your Turn", "CPUs have played. Select a card and marble!");
            });
        });
        t.setDaemon(true);
        t.start();
    }

    // ─── SYNC BOARD FROM ENGINE ───────────────────────────────────────────────────

    private static void syncBoardFromEngine(MainScene ms, Game game, Color[] fxColors) {
        Color empty = Color.web("#fad082");

        ArrayList<Cell> track = game.getBoard().getTrack();
        for (int i = 0; i < ms.c.circleCellPairs.size(); i++) {
            Cell cell = track.get(i);
            Marble marble = cell.getMarble();
            ms.c.circleCellPairs.get(i).getCircle().setFill(marble != null ? colourToFX(marble.getColour()) : empty);
        }

        for (int p = 0; p < 4; p++) {
            SafeZone sz = game.getBoard().getSafeZones().get(p);
            Color playerFaded = fxColors[p].deriveColor(0, 0.5, 1.5, 0.35);
            for (int c = 0; c < 4; c++) {
                Marble marble = sz.getCells().get(c).getMarble();
                javafx.scene.shape.Circle circle = ms.c.safeCellPairs.get(p * 4 + c).getCircle();
                circle.setFill(marble != null ? colourToFX(marble.getColour()) : playerFaded);
                circle.setStroke(Color.BLACK);
                circle.setStrokeWidth(1.0);
            }
        }

        for (int p = 0; p < 4; p++) {
            int inHome = game.getPlayers().get(p).getMarbles().size();
            for (int m = 0; m < 4; m++) {
                ms.c.homeCellPairs.get(p * 4 + m).getCircle()
                        .setFill(m < inHome ? fxColors[p] : empty);
            }
        }
    }

    // ─── Resolve engine Marble objects from UI circle selections ─────────────────

    private static ArrayList<Marble> resolveEngineMarbles(MainScene ms, Game game,
                                                           ArrayList<CircleGrid.CircleCellPair> uiSelected) {
        ArrayList<Marble> result = new ArrayList<>();
        for (CircleGrid.CircleCellPair pair : uiSelected) {
            if (ms.c.homeCellPairs.contains(pair)) continue;

            Cell cell = pair.getCell();
            if (cell != null && cell.getMarble() != null) {
                result.add(cell.getMarble());
                continue;
            }

            for (CircleGrid.CircleCellPair sp : ms.c.safeCellPairs) {
                if (sp == pair && sp.getCell() != null && sp.getCell().getMarble() != null) {
                    result.add(sp.getCell().getMarble());
                    break;
                }
            }
        }
        return result;
    }

    // ─── Card panel & action button setup ────────────────────────────────────────

    private static void setupCardHandlers(MainScene ms, Game game, Color[] fxColors, Color humanFX) {
        for (int i = 0; i < ms.cardPanels.length; i++) {
            final int idx = i;
            ms.cardPanels[i].setOnMouseClicked(e -> {
                if (!ms.cardPanels[idx].isVisible()) return;
                boolean wasSelected = ms.checkBoxes[idx].isSelected();
                deselectAllCards(ms);
                ms.c.clearMarbleSelections();
                ms.actionPanel.setVisible(false);
                if (!wasSelected) {
                    ms.checkBoxes[idx].setSelected(true);
                    ms.highlightCard(idx, true);
                    onCardSelected(ms, game, idx, humanFX);
                }
            });
        }

        // Discard button (Ten / Queen / any card when no marbles are out)
        ms.discardButton.setOnAction(e -> {
            int idx = getSelectedCardIndex(ms);
            if (idx == -1) { displayAlert2("Error", "Select a card first."); return; }
            Player human = game.getPlayers().get(0);
            Card card = human.getHand().get(idx);
            String name = card.getName().toUpperCase();
            long onTrack = ms.c.getCircleCellPairs().stream()
                    .filter(p -> p.getCircle().getFill().equals(humanFX)).count();
            // Also count marbles the human has in their safe zone
            Colour humanColourDiscard = game.getPlayers().get(0).getColour();
            long inSafeZone = game.getBoard().getSafeZones().stream()
                    .filter(sz -> sz.getColour() == humanColourDiscard)
                    .flatMap(sz -> sz.getCells().stream())
                    .filter(cell -> cell.getMarble() != null)
                    .count();
            boolean noMarblesOut = onTrack == 0 && inSafeZone == 0 && human.getMarbles().size() == 4;
            try {
                game.selectCard(card);
                if (noMarblesOut && !name.contains("TEN") && !name.contains("QUEEN")) {
                    // Non-playable discard: just end the turn (no skip added per rules)
                    game.endPlayerTurn();
                } else {
                    // Track hands before for Queen detection
                    int[] handsBefore = new int[4];
                    for (int pi = 0; pi < 4; pi++) handsBefore[pi] = game.getPlayers().get(pi).getHand().size();
                    Colour tenTarget = name.contains("TEN") ? game.getNextPlayerColour() : null;

                    game.playPlayerTurn();
                    game.endPlayerTurn();

                    if (name.contains("TEN") && tenTarget != null) {
                        skippedPlayers.add(tenTarget);
                    } else if (name.contains("QUEEN")) {
                        for (int pi = 0; pi < 4; pi++) {
                            if (game.getPlayers().get(pi).getHand().size() < handsBefore[pi]) {
                                skippedPlayers.add(game.getPlayers().get(pi).getColour());
                                break;
                            }
                        }
                    }
                }
                String playedName = card.getName();
                syncBoardFromEngine(ms, game, fxColors);
                deselectAllCards(ms);
                ms.actionPanel.setVisible(false);
                updateFirePit(ms, game, playedName);
                refreshCardPanels(ms, human);
                updateGameUI(ms, game, fxColors);
                ms.c.makeMarbleSelectable();
                showInfo("Discarded", playedName);
                handleCPUTurns(ms, game, fxColors);
            } catch (Exception ex) {
                game.deselectAll();
                displayAlert2("Cannot Discard", ex.getMessage() != null ? ex.getMessage() : ex.toString());
            }
        });

        // Split button (Seven with 2 marbles)
        ms.splitButton.setOnAction(e -> {
            ArrayList<CircleGrid.CircleCellPair> sel = ms.c.getSelectedMarbles();
            if (sel.size() < 2) {
                displayAlert2("Split Error", "Select exactly 2 of your marbles.");
                return;
            }
            int idx = getSelectedCardIndex(ms);
            if (idx == -1) { displayAlert2("Error", "Select the Seven card first."); return; }

            Player human = game.getPlayers().get(0);
            Card card = human.getHand().get(idx);
            ArrayList<Marble> engineMarbles = resolveEngineMarbles(ms, game, sel);

            if (engineMarbles.size() < 2) {
                displayAlert2("Split Error", "Both marbles must be on the track.");
                return;
            }

            try {
                game.selectCard(card);
                game.editSplitDistance(ms.getSplitDistance());
                game.selectMarble(engineMarbles.get(0));
                game.selectMarble(engineMarbles.get(1));
                game.playPlayerTurn();
                String playedName = card.getName();
                game.endPlayerTurn();

                syncBoardFromEngine(ms, game, fxColors);
                deselectAllCards(ms);
                ms.c.clearMarbleSelections();
                ms.actionPanel.setVisible(false);
                updateFirePit(ms, game, playedName);
                refreshCardPanels(ms, human);
                updateGameUI(ms, game, fxColors);
                ms.c.makeMarbleSelectable();
                showInfo("Played", playedName + " (split)");
                handleCPUTurns(ms, game, fxColors);
            } catch (Exception ex) {
                game.deselectAll();
                displayAlert2("Invalid Split", ex.getMessage() != null ? ex.getMessage() : ex.toString());
            }
        });
    }

    private static void onCardSelected(MainScene ms, Game game, int cardIdx, Color humanFX) {
        if (cardIdx >= game.getPlayers().get(0).getHand().size()) return;
        Card card = game.getPlayers().get(0).getHand().get(cardIdx);
        String name = card.getName().toUpperCase();
        long onTrack = ms.c.getCircleCellPairs().stream()
                .filter(p -> p.getCircle().getFill().equals(humanFX)).count();
        // Also count marbles the human has in their safe zone
        Colour humanColourCard = game.getPlayers().get(0).getColour();
        long inSafeZoneCard = game.getBoard().getSafeZones().stream()
                .filter(sz -> sz.getColour() == humanColourCard)
                .flatMap(sz -> sz.getCells().stream())
                .filter(cell -> cell.getMarble() != null)
                .count();
        boolean noMarblesOut = onTrack == 0 && inSafeZoneCard == 0 && game.getPlayers().get(0).getMarbles().size() == 4;

        // If marbles are ONLY in the safe zone and the card rank exceeds remaining steps,
        // the card cannot be played — force discard.
        boolean onlySafeZoneMarbles = onTrack == 0 && inSafeZoneCard > 0;
        if (onlySafeZoneMarbles && card instanceof Standard) {
            int rank = ((Standard) card).getRank();
            // Find max steps remaining for any marble in the safe zone (safe zone has 4 cells, indices 0-3)
            int maxStepsLeft = game.getBoard().getSafeZones().stream()
                    .filter(sz -> sz.getColour() == humanColourCard)
                    .flatMap(sz -> {
                        java.util.List<engine.board.Cell> cells = sz.getCells();
                        java.util.List<Integer> steps = new java.util.ArrayList<>();
                        for (int ci = 0; ci < cells.size(); ci++) {
                            if (cells.get(ci).getMarble() != null) {
                                steps.add(3 - ci); // cells 0..3, max steps = 3-index
                            }
                        }
                        return steps.stream();
                    })
                    .mapToInt(Integer::intValue).max().orElse(0);
            if (Math.abs(rank) > maxStepsLeft) {
                ms.c.makeMarbleSelectable();
                ms.showActionPanel("DISCARD_ANY", false);
                showInfo(card.getName(), "Card rank too high for safe zone — must discard.");
                return;
            }
        }

        if (noMarblesOut && !name.contains("ACE") && !name.contains("KING")) {
            ms.c.makeMarbleSelectable();
            ms.showActionPanel("DISCARD_ANY", false);
            showInfo(card.getName(), "No marbles on track — card will be discarded.");
        } else if (name.contains("JACK")) {
            ms.c.makeAllMarblesSelectable();
            ms.showActionPanel("", false);
            showInfo("Jack", "Select your marble then an opponent's marble to swap.");
        } else if (name.contains("SEVEN")) {
            ms.c.makeMarbleSelectable();
            ms.showActionPanel("SEVEN", true);
            showInfo("Seven", "Select 1 marble (normal) or 2 marbles + split distance.");
        } else if (name.contains("BURNER")) {
            // Burner: select only opponent marbles on the main track (not base, not safe, not home)
            ms.c.makeOpponentTrackMarblesSelectable();
            ms.showActionPanel("", false);
            showInfo("MarbleBurner", "Select an opponent's marble on track to send home.");
        } else if (name.contains("SAVER")) {
            // Saver: select only the player's own marbles on track (including base)
            ms.c.makeMarbleSelectable();
            ms.showActionPanel("", false);
            showInfo("MarbleSaver", "Select one of your marbles on track to send to safe zone.");
        } else if (name.contains("TEN")) {
            ms.c.makeMarbleSelectable();
            ms.showActionPanel("TEN", false);
            showInfo("Ten", "Move a marble 10 steps, OR use Discard to skip next player.");
        } else if (name.contains("QUEEN")) {
            ms.c.makeMarbleSelectable();
            ms.showActionPanel("QUEEN", false);
            showInfo("Queen", "Move a marble 12 steps, OR use Discard to skip a random player.");
        } else if (name.contains("FIVE")) {
            ms.c.makeTrackMarblesSelectable();
            ms.showActionPanel("", false);
            showInfo("Five", "Select any marble on track (yours or opponent's) to move 5 steps.");
        } else {
            ms.c.makeMarbleSelectable();
            ms.showActionPanel("", false);
            showInfo(card.getName(), card.getDescription());
        }
    }

    // ─── Card visual helpers ──────────────────────────────────────────────────────

    public static void refreshCardPanels(MainScene ms, Player human) {
        for (int i = 0; i < ms.cardPanels.length; i++) ms.hideCardPanel(i);
        for (int i = 0; i < Math.min(human.getHand().size(), 4); i++) {
            Card card = human.getHand().get(i);
            String rankStr, suitSymbol, desc = card.getDescription();
            Color suitColor;
            if (card instanceof Standard) {
                Standard sc = (Standard) card;
                rankStr    = rankToLabel(sc.getRank());
                suitSymbol = suitToSymbol(sc.getSuit());
                suitColor  = isRedSuit(sc.getSuit()) ? Color.CRIMSON : Color.BLACK;
            } else {
                rankStr = "W"; suitSymbol = "★"; suitColor = Color.DARKORANGE;
            }
            ms.updateCardPanel(i, rankStr, suitSymbol, suitColor, desc, card.getName());
        }
    }

    private static String rankToLabel(int r) {
        switch (r) { case 1: return "A"; case 11: return "J"; case 12: return "Q"; case 13: return "K"; default: return String.valueOf(r); }
    }
    private static String suitToSymbol(Suit s) {
        switch (s) { case HEART: return "♥"; case DIAMOND: return "♦"; case CLUB: return "♣"; case SPADE: return "♠"; default: return ""; }
    }
    private static boolean isRedSuit(Suit s) { return s == Suit.HEART || s == Suit.DIAMOND; }

    // ─── UI helpers ───────────────────────────────────────────────────────────────

    private static void updateGameUI(MainScene ms, Game game, Color[] fxColors) {
        ms.remainingCards1.setText(game.getPlayers().get(3).getName() + "\nCards: " + game.getPlayers().get(3).getHand().size());
        ms.remainingCards2.setText(game.getPlayers().get(1).getName() + "\nCards: " + game.getPlayers().get(1).getHand().size());
        ms.remainingCards3.setText(game.getPlayers().get(2).getName() + "\nCards: " + game.getPlayers().get(2).getHand().size());
        ms.deckRemainingLabel.setText("Rem: " + Deck.getPoolSize());
        ms.humanLabel.setText(game.getPlayers().get(0).getName());
        Colour active = game.getActivePlayerColour(), next = game.getNextPlayerColour();
        ms.currentPlayerLabel.setText("Current: " + active);
        ms.currentPlayerLabel.setTextFill(colourToFX(active));
        ms.nextPlayerLabel.setText("Next: " + next);
        ms.nextPlayerLabel.setTextFill(colourToFX(next));
    }

    private static int getSelectedCardIndex(MainScene ms) {
        for (int i = 0; i < ms.checkBoxes.length; i++) if (ms.checkBoxes[i].isSelected()) return i;
        return -1;
    }

    private static void deselectAllCards(MainScene ms) {
        for (int i = 0; i < ms.checkBoxes.length; i++) { ms.checkBoxes[i].setSelected(false); ms.highlightCard(i, false); }
    }

    private static void updateFirePit(MainScene ms, Game game, String cardName) {
        if (ms.firePitStack == null) return;
        java.util.List<model.card.Card> pit = game.getFirePit();
        if (pit.isEmpty() || cardName.isEmpty() || cardName.equals("🃏 New Cards")) {
            if (ms.firePitCardRank  != null) ms.firePitCardRank.setText("—");
            if (ms.firePitCardSuit  != null) ms.firePitCardSuit.setText("");
            if (ms.firePitCardName  != null) ms.firePitCardName.setText(cardName.isEmpty() ? "Empty" : cardName);
            return;
        }
        model.card.Card last = pit.get(pit.size() - 1);
        if (last == null) return;
        String rank = ""; String suit = ""; javafx.scene.paint.Color col = javafx.scene.paint.Color.web("#1a1a1a");
        if (last instanceof model.card.standard.Standard) {
            model.card.standard.Standard sc = (model.card.standard.Standard) last;
            int r = sc.getRank();
            switch(r) { case 1: rank="A"; break; case 11: rank="J"; break; case 12: rank="Q"; break; case 13: rank="K"; break; default: rank=String.valueOf(r); }
            switch(sc.getSuit()) {
                case HEART:   suit="♥"; col=javafx.scene.paint.Color.CRIMSON; break;
                case DIAMOND: suit="♦"; col=javafx.scene.paint.Color.CRIMSON; break;
                case CLUB:    suit="♣"; break;
                case SPADE:   suit="♠"; break;
            }
        } else {
            rank = "★"; suit = "Wild"; col = javafx.scene.paint.Color.DARKORANGE;
        }
        if (ms.firePitCardRank != null) { ms.firePitCardRank.setText(rank); ms.firePitCardRank.setTextFill(col); }
        if (ms.firePitCardSuit != null) { ms.firePitCardSuit.setText(suit); ms.firePitCardSuit.setTextFill(col); }
        if (ms.firePitCardName != null) ms.firePitCardName.setText(last.getName());
    }

    static Color colourToFX(Colour c) {
        switch (c) { case BLUE: return Color.BLUE; case GREEN: return Color.GREEN; case YELLOW: return Color.YELLOW; case RED: return Color.RED; default: return Color.BLACK; }
    }

    // ─── Start screen helpers ─────────────────────────────────────────────────────

    @Override
    public void handle(ActionEvent e) {
        if (e.getSource() == startGame && nameField.getText().isEmpty())
            try { displayAlert1("Error", "Enter your name to start the game."); }
            catch (FileNotFoundException e1) { displayAlert2("Error", "Enter your name to start the game."); }
    }

    public void addControl(Region c, int x, int y, int w, int h) {
        c.setLayoutX(x); c.setLayoutY(y); c.setPrefWidth(w); c.setPrefHeight(h);
        an.getChildren().add(c);
    }

    public void displayAlert1(String title, String message) throws FileNotFoundException {
        Stage window = new Stage();
        ImageView icon = new ImageView(new Image(new FileInputStream("Warning.png")));
        icon.setFitWidth(70); icon.setFitHeight(70);
        VBox layout = buildAlertLayout();
        Label t = new Label(title); t.setFont(Font.font("Arial", FontWeight.BOLD, 35)); t.setTextFill(Color.DARKSLATEBLUE);
        HBox box = new HBox(10); box.setAlignment(Pos.CENTER); box.getChildren().addAll(t, icon);
        Label m = new Label(message); m.setFont(Font.font(20)); m.setWrapText(true); m.setMaxWidth(500);
        Button ok = new Button("OK"); ok.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        ok.setOnAction(ev -> window.close());
        layout.getChildren().addAll(box, m, ok);
        window.setScene(new Scene(layout)); window.showAndWait();
    }

    public static void displayAlert2(String title, String message) {
        Stage window = new Stage();
        VBox layout = buildAlertLayout();
        Label t = new Label(title); t.setFont(Font.font("Arial", FontWeight.BOLD, 35)); t.setTextFill(Color.DARKSLATEBLUE);
        Label m = new Label(message); m.setFont(Font.font(20)); m.setWrapText(true); m.setMaxWidth(500);
        Button ok = new Button("OK"); ok.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        ok.setOnAction(ev -> window.close());
        layout.getChildren().addAll(t, m, ok);
        window.setScene(new Scene(layout)); window.showAndWait();
    }

    private static VBox buildAlertLayout() {
        VBox v = new VBox(20); v.setAlignment(Pos.CENTER); v.setPadding(new Insets(20));
        v.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(10), Insets.EMPTY)));
        v.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(1))));
        return v;
    }

    public static void main(String[] args) { launch(args); }
}